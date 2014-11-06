package com.tint.entityengine.server.entity.systems;

import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_DOWN;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_LEFT;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_RIGHT;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_UP;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tint.entityengine.GameMap;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.HitboxComponent;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.server.GameServer;
import com.tint.entityengine.server.entity.components.ServerPlayerComponent;

public class ServerPlayerSystem extends IteratingSystem {

	private static final float DIAG_MOD = (float) Math.sqrt(0.5);
	private ComponentMapper<ServerPlayerComponent> playerMapper = ComponentMapper.getFor(ServerPlayerComponent.class);
	
	private GameServer gameServer;

	public ServerPlayerSystem(GameServer gameServer) {
		super(Family.getFor(ServerPlayerComponent.class));
		this.gameServer = gameServer;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ServerPlayerComponent pl = playerMapper.get(entity);
		PositionComponent pos = Mappers.position.get(entity);
		float newX = pos.getX();
		float newY = pos.getY();
		
		float modifier = 1;
		
		//XOR if only one of the keys on one axis is pressed
		if((pl.getKey(KEY_UP) ^ pl.getKey(KEY_DOWN)) &&
				(pl.getKey(KEY_LEFT) ^ pl.getKey(KEY_RIGHT))) {
			modifier = DIAG_MOD;
		}
		
		float testX = newX;
		float testY = newY;
		
		if(pl.getKey(KEY_UP))
			testY += pl.getSpeed() * modifier;
		
		if(pl.getKey(KEY_DOWN))
			testY -= pl.getSpeed() * modifier;
		
		if(pl.getKey(KEY_LEFT))
			testX -= pl.getSpeed() * modifier;
		
		if(pl.getKey(KEY_RIGHT))
			testX += pl.getSpeed() * modifier;
		
		HitboxComponent hitbox = Mappers.hitbox.get(entity);
		
		for(int i = 0; i < 4; i++) {
			float x = (float) (testX / GameMap.TILE_SIZE);
			float y = (float) (testY / GameMap.TILE_SIZE);
			float tileX = (float) (x + hitbox.getOffset(i, 0));
			float tileY = (float) (y + hitbox.getOffset(i, 1));
			
			if(gameServer.getMap().isOnMap(tileX, tileY)) {
				System.out.println((int) tileX + ", " + (int) tileY + ": " + gameServer.getMap().getTile((int) tileX, (int) tileY, 0));
				if(gameServer.getMap().isBlocked((int) tileX, (int) tileY, 1)) {
					float xOverlap = tileX % 1.0f * GameMap.TILE_SIZE;
					float yOverlap = tileY % 1.0f * GameMap.TILE_SIZE;
					
					if(hitbox.getOffset(i, 0) < 0)
						xOverlap = -(GameMap.TILE_SIZE - xOverlap);
					
					if(hitbox.getOffset(i, 1) < 0)
						yOverlap = -(GameMap.TILE_SIZE - yOverlap);
					
					//Fix player getting stuck in walls
					if(Math.abs(xOverlap) == Math.abs(yOverlap) && yOverlap > 0) {
						testX -= xOverlap;
					} else if(Math.abs(xOverlap) < Math.abs(yOverlap))
						testX -= xOverlap;
					else
						testY -= yOverlap;
				}
				
				newX = testX;
				newY = testY;
			} else {
				if(tileX < 0)
					newX = 16;
				else if(tileX >= gameServer.getMap().getWidth())
					newX = gameServer.getMap().getWidth() - 16;
				else
					newX = testX;
				
				if(tileY < 0)
					newY = 16;
				else if(tileY >= gameServer.getMap().getHeight())
					newY = gameServer.getMap().getHeight() - 16;
				else
					newY = testY;
				break;
			}
		}
		
		pos.set(newX, newY, gameServer.getTicks());
	}

}
