package com.tint.entityengine;

import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_DOWN;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_LEFT;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_RIGHT;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_UP;

import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.entity.components.HealthComponent;
import com.tint.entityengine.entity.components.HitboxComponent;
import com.tint.entityengine.entity.components.PositionComponent;

public class ClientPlayer {
	private static final float DIAG_MOD = (float) Math.sqrt(0.5);

	public long serverEntityId;
	private Entity entity;
	private PositionComponent pos;
	private HealthComponent health;
	private HitboxComponent hitbox;
	private GameState gs;
	private float speed = 5;
	
	public ClientPlayer(GameState gs) {
		this.gs = gs;
	}
	
	public void setPlayerEntity(Entity entity) {
		this.entity = entity;
		
		pos = Mappers.position.get(entity);
		pos.isPlayer = true;
		
		health = Mappers.health.get(entity);
		hitbox = Mappers.hitbox.get(entity);
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void update() {
		float newX = pos.getX();
		float newY = pos.getY();
		
		float modifier = 1;
		
		InputProcessor in = gs.getInputProcessor();
		
		//XOR if only one of the keys on one axis is pressed
		if((in.getKey(KEY_UP) ^ in.getKey(KEY_DOWN)) &&
				(in.getKey(KEY_LEFT) ^ in.getKey(KEY_RIGHT))) {
			modifier = DIAG_MOD;
		}
		
		float testX = newX;
		float testY = newY;
		
		if(in.getKey(KEY_UP))
			testY += speed * modifier;
		
		if(in.getKey(KEY_DOWN))
			testY -= speed * modifier;
		
		if(in.getKey(KEY_LEFT))
			testX -= speed * modifier;
		
		if(in.getKey(KEY_RIGHT))
			testX += speed * modifier;
		
		for(int i = 0; i < 4; i++) {
			float x = (float) (testX / GameMap.TILE_SIZE);
			float y = (float) (testY / GameMap.TILE_SIZE);
			float tileX = (float) (x + hitbox.getOffset(i, 0));
			float tileY = (float) (y + hitbox.getOffset(i, 1));
			
			if(gs.getMap().isOnMap(tileX, tileY)) {
				if(gs.getMap().isBlocked((int) tileX, (int) tileY, 1)) {
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
				else if(tileX >= gs.getMap().getWidth())
					newX = gs.getMap().getWidth() - 16;
				else
					newX = testX;
				
				if(tileY < 0)
					newY = 16;
				else if(tileY >= gs.getMap().getHeight())
					newY = gs.getMap().getHeight() - 16;
				else
					newY = testY;
				break;
			}
		}
		
		pos.set(newX, newY, gs.getTick());
	}
	
	public PositionComponent getPos() {
		return pos;
	}
	
}
