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
	private float[][] hitboxOffset = new float[4][2];
	
	public ClientPlayer(GameState gs) {
		this.gs = gs;
	}
	
	public void setPlayerEntity(Entity entity) {
		this.entity = entity;
		
		pos = Mappers.position.get(entity);
		pos.isPlayer = true;
		
		health = Mappers.health.get(entity);
		
		entity.add(new HitboxComponent(32, 32));
		hitbox = Mappers.hitbox.get(entity);
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 2; j++) {
				if(j == 0)
					if(i == 0 || i == 3)
						hitboxOffset[i][j] = -hitbox.getWidth() / GameMap.TILE_SIZE;
					else
						hitboxOffset[i][j] = hitbox.getWidth() / GameMap.TILE_SIZE;
				else
					if(i == 0 || i == 1)
						hitboxOffset[i][j] = -hitbox.getHeight() / GameMap.TILE_SIZE;
					else
						hitboxOffset[i][j] = hitbox.getHeight() / GameMap.TILE_SIZE;
			}
		}
		
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
		
		// Checks all 4 corners, < 0 means it's blocked
		for(int i = 0; i < 4; i++) {
			float x = (float) (testX /  GameMap.TILE_SIZE);
			float y = (float) (testY / (float) GameMap.TILE_SIZE);
			float tileX = (float) (x + hitboxOffset[i][0]);
			float tileY = (float) (y + hitboxOffset[i][1]);
			
			if(gs.getMap().isOnMap(tileX, tileY)) {
				System.out.println(tileX + ", " + tileY);
				newX = testX;
				newY = testY;
			} else {
				if(tileX >= 1.5f && tileX < gs.getMap().getWidth())
					newX = testX;
				
				if(tileY >= 1.5f && tileY < gs.getMap().getHeight())
					newY = testY;
			}
				/*
				if(gs.getMap().getTile((int) tileX, (int) tileY, 0) < 0) {
					float xOverlap = tileX % 1.0f * GameMap.TILE_SIZE;
					float yOverlap = tileY % 1.0f * GameMap.TILE_SIZE;
					
					if(hitboxOffset[i][0] < 0)
						xOverlap = -(GameMap.TILE_SIZE - xOverlap);
					
					if(hitboxOffset[i][1] < 0)
						yOverlap = -(GameMap.TILE_SIZE - yOverlap);
					
					//Fix player getting stuck in walls
					if(Math.abs(xOverlap) == Math.abs(yOverlap) && yOverlap > 0) {
						testX -= xOverlap;
					} else if(Math.abs(xOverlap) < Math.abs(yOverlap))
						testX -= xOverlap;
					else
						testY -= yOverlap;
				}
			} else {
				System.out.println("Outside of Map");
				if(tileX < 1)
					testX += 2 * speed * modifier;
				else if(tileX + 0.5f > gs.getMap().getWidth() * GameMap.TILE_SIZE)
					testX -= 2 * speed * modifier;
				
				if(tileY < 1)
					testY += 2 * speed * modifier;
				else if(tileY + 0.5f > gs.getMap().getHeight() * GameMap.TILE_SIZE)
					testY -= 2 * speed * modifier;
			}*/
		}
		
		pos.set(newX, newY, gs.getTick());
	}
	
	public PositionComponent getPos() {
		return pos;
	}
	
}
