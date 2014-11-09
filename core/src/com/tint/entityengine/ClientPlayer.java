package com.tint.entityengine;

import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_ATTACK;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_DOWN;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_LEFT;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_RIGHT;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.KEY_UP;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.tint.entityengine.entity.components.CollisionComponent;
import com.tint.entityengine.entity.components.HealthComponent;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.components.StaticCollisionComponent;
import com.tint.entityengine.entity.components.renderers.PlayerRenderer;
import com.tint.entityengine.entity.components.renderers.PlayerRenderer.PlayerState;

public class ClientPlayer {
	private static final float DIAG_MOD = (float) Math.sqrt(0.5);
	private ImmutableArray<Entity> staticEntities;

	public long serverEntityId;
	private Entity entity;
	private PositionComponent pos;
	private HealthComponent health;
	private CollisionComponent collision;
	private GameState gs;
	private float speed = 5;
	
	public ClientPlayer(GameState gs) {
		this.gs = gs;
		
		staticEntities = gs.getEngine().getEntitiesFor(Family.getFor(StaticCollisionComponent.class));
	}
	
	public void setPlayerEntity(Entity entity) {
		this.entity = entity;
		
		pos = Mappers.position.get(entity);
		pos.isPlayer = true;
		
		health = Mappers.health.get(entity);
		collision = Mappers.dynamicCollision.get(entity);
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
		
		if(in.getKey(KEY_ATTACK)) {
			((PlayerRenderer) Mappers.render.get(entity).renderer).setPlayerState(PlayerState.ATTACKING);
		}
		
		for(int j = 0; j < staticEntities.size(); j++) {
			Entity staticEnt = staticEntities.get(j);
			PositionComponent staticPos = Mappers.position.get(staticEnt);
			StaticCollisionComponent staticCollision = Mappers.staticCollision.get(staticEnt);
			
			if(testX + collision.getOffset(0, 0) < staticPos.getX() + staticCollision.getOffset(2, 0))
				continue;
			
			if(testY + collision.getOffset(0, 1) < staticPos.getY() + staticCollision.getOffset(2, 1))
				continue;
			
			if(testX + collision.getOffset(2, 0) > staticPos.getX() + staticCollision.getOffset(0, 0))
				continue;
			
			if(testY + collision.getOffset(2, 1) > staticPos.getY() + staticCollision.getOffset(0, 1))
				continue;
			
			float overlapX = testX - staticPos.getX();
			if(overlapX < 0)
				overlapX += (collision.getWidth() / 2 + staticCollision.getWidth() / 2);
			else
				overlapX -= (collision.getWidth() / 2 + staticCollision.getWidth() / 2);
			
			
			float overlapY = testY - staticPos.getY();
			if(overlapY < 0)
				overlapY += (collision.getHeight() / 2 + staticCollision.getHeight() / 2);
			else
				overlapY -= (collision.getHeight() / 2 + staticCollision.getHeight() / 2);
			
			
			if(Math.abs(overlapX) < Math.abs(overlapY)) {
				testX -= overlapX;
			} else {
				testY -= overlapY;
			}
		}
		
		newX = testX;
		newY = testY;
		pos.set(newX, newY, gs.getTick());
	}
	
	public PositionComponent getPos() {
		return pos;
	}
	
}
