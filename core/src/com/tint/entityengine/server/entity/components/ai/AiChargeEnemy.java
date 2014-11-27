package com.tint.entityengine.server.entity.components.ai;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.EntityGrid;
import com.tint.entityengine.entity.components.CollisionComponent;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.server.GameServer;
import com.tint.entityengine.server.ServerClient;
import com.tint.entityengine.server.entity.components.ServerPlayerComponent;

public class AiChargeEnemy implements AiController {

	public static final ComponentMapper<ServerPlayerComponent> playerMapper = ComponentMapper.getFor(ServerPlayerComponent.class);
	
	private PositionComponent position;
	private Entity entity;
	private GameServer gameServer;
	private float speed = 1;
	
	private int attackSpeed;
	private transient int lastAttackTick;
	
	@Override
	public void update(int tick) {
		if(gameServer.getClients().size() == 0)
			return;
		
		Entity chasePlayer = gameServer.getClients().get(0).getEntity();
		PositionComponent playerPos = Mappers.position.get(chasePlayer);
		
		float dx = playerPos.getX() - position.getX();
		float dy = playerPos.getY() - position.getY();
		
		if(lastAttackTick + attackSpeed <= tick) {
			if(dx * dx + dy * dy < 500) {
				
				Mappers.health.get(chasePlayer).addHp(-10);
				lastAttackTick = tick;
				
			} else {
				
				//Normalize vector between the player and enemy to get the direction
				float length = (float) Math.sqrt(dx * dx + dy * dy);
				dx /= length;
				dy /= length;
				speed = 3;
				//Push away from obstacles
				ImmutableArray<Entity> nearbyEntities = EntityGrid.get(position);
				for(int i = 0; i < nearbyEntities.size(); i++) {
					Entity e = nearbyEntities.get(i);
					CollisionComponent collision = Mappers.dynamicCollision.get(e);
					
					if(collision != null) {
						PositionComponent otherPos = Mappers.position.get(e);
						
						//Don't push away from players
						if(e == entity || playerMapper.get(e) != null)
							continue;
						
						float distX = position.getX() - otherPos.getX();
						float distY = position.getY() - otherPos.getY();
						
						float totalDist = (float) distX * distX + distY * distY;
						distX /= totalDist;
						distY /= totalDist;
						
						float pushRange = Math.max(collision.getWidth(), collision.getHeight()) * 5;
						pushRange *= pushRange;
						
						if(pushRange > totalDist) {
							dx += distX * (1 - totalDist / pushRange) * 20;
							dy += distY * (1 - totalDist / pushRange) * 20;
						}
					}
				}
				
				//Normalize the movement vector
				length = (float) Math.sqrt(dx * dx + dy * dy);
				dx /= length;
				dy /= length;
				position.add(dx * speed, dy * speed, -1, true, entity);
			}
		}
	}

	@Override
	public void init(GameServer gameServer, Entity entity) {
		this.position = Mappers.position.get(entity);
		this.entity = entity;
		this.gameServer = gameServer;
	}
	
	public Entity getNearestPlayerPosition() {
		Entity playerEntity = null;
		float currentDxSq = Float.MAX_VALUE, currentDySq = Float.MAX_VALUE;
		synchronized (gameServer.getClients()) {
			for(ServerClient client : gameServer.getClients()) {
				PositionComponent playerPos = Mappers.position.get(client.getEntity());
				float dx = playerPos.getX() - position.getX();
				float dy = playerPos.getY() - position.getY();
				
				dx *= dx;
				dy *= dy;
				
				if(dx + dy < currentDxSq + currentDySq) {
					currentDxSq = dx;
					currentDySq = dy;
					playerEntity = client.getEntity();
				}
			}
		}
		return playerEntity;
	}
	
}
