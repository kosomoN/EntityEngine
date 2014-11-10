package com.tint.entityengine.server.entity.systems;

import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_ATTACK;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_DOWN;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_LEFT;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_RIGHT;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_UP;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.AttackHitbox;
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
		
		if(pl.getKey(KEY_ATTACK))
			attack(entity, pos, testX - newX, testY - newY); //Will be replaced with weapons
		else
			hasAttacked = false;

		newX = testX;
		newY = testY;
		pos.set(newX, newY, gameServer.getTicks(), true, entity);
	}

	private boolean hasAttacked = false;
	//Delta used for direction
	private void attack(Entity player, PositionComponent pos, float dx, float dy) {
		if(!hasAttacked) {
			hasAttacked = true;
			
			ImmutableArray<Entity> hittableEntites = gameServer.getEngine().getEntitiesFor(Family.getFor(AttackHitbox.class));
			
			//TODO Add broad-phase
			for(int i = 0; i < hittableEntites.size(); i++) {
				Entity e = hittableEntites.get(i);
				if(e != player) {
					AttackHitbox hitbox = Mappers.attackHitbox.get(e);
					PositionComponent hitPos = Mappers.position.get(e);
					
					float minX = 0, minY = 0, maxX = 0, maxY = 0;
					
					
					if(dx > 0) {
						minX = pos.getX();
						maxX = pos.getX() + 40;
						
						minY = pos.getY() - 17;
						maxY = pos.getY() + 17;
						
					} else if(dx < 0) {
						
					} else {
						if(dy > 0) {
							
						} else {
							
						}
					}
					
					if(hitPos.getX() + hitbox.getWidth() / 2 + hitbox.getOffsetX() < minX)
						continue;
					
					if(hitPos.getY() + hitbox.getHeight() / 2 + hitbox.getOffsetY() < minY)
						continue;
					
					if(hitPos.getX() - hitbox.getWidth() / 2 + hitbox.getOffsetX() > maxX)
						continue;
					
					if(hitPos.getY() - hitbox.getHeight() / 2 + hitbox.getOffsetY() > maxY)
						continue;
					
					Mappers.health.get(e).addHp(-10);
				}
			}
		}
	}

}
