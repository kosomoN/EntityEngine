package com.tint.entityengine.server.entity.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.CollisionComponent;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.components.StaticCollisionComponent;

public class ServerCollisionSystem extends EntitySystem {

	private ImmutableArray<Entity> dynamicEntities, staticEntities;

	public ServerCollisionSystem() {
		super();
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		//No broad-phase checking or "sleeping" for now
		for(int i = 0; i < dynamicEntities.size(); i++) {
			Entity dynEnt = dynamicEntities.get(i);
			PositionComponent dynPos = Mappers.position.get(dynEnt);
			CollisionComponent dynCollision = Mappers.dynamicCollision.get(dynEnt);
			
			for(int j = 0; j < staticEntities.size(); j++) {
				Entity staticEnt = staticEntities.get(j);
				PositionComponent staticPos = Mappers.position.get(staticEnt);
				StaticCollisionComponent staticCollision = Mappers.staticCollision.get(staticEnt);
				
				if(dynPos.getX() + dynCollision.getOffset(0, 0) < staticPos.getX() + staticCollision.getOffset(2, 0))
					continue;
				
				if(dynPos.getY() + dynCollision.getOffset(0, 1) < staticPos.getY() + staticCollision.getOffset(2, 1))
					continue;
				
				if(dynPos.getX() + dynCollision.getOffset(2, 0) > staticPos.getX() + staticCollision.getOffset(0, 0))
					continue;
				
				if(dynPos.getY() + dynCollision.getOffset(2, 1) > staticPos.getY() + staticCollision.getOffset(0, 1))
					continue;
				
				float overlapX = dynPos.getX() - staticPos.getX();
				if(overlapX < 0)
					overlapX += (dynCollision.getWidth() / 2 + staticCollision.getWidth() / 2);
				else
					overlapX -= (dynCollision.getWidth() / 2 + staticCollision.getWidth() / 2);
				
				
				float overlapY = dynPos.getY() - staticPos.getY();
				if(overlapY < 0)
					overlapY += (dynCollision.getHeight() / 2 + staticCollision.getHeight() / 2);
				else
					overlapY -= (dynCollision.getHeight() / 2 + staticCollision.getHeight() / 2);
				
				
				if(Math.abs(overlapX) < Math.abs(overlapY)) {
					dynPos.add(-overlapX, 0);
				} else {
					dynPos.add(0, -overlapY);
				}
			}
		}
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		dynamicEntities = engine.getEntitiesFor(Family.getFor(CollisionComponent.class));
		staticEntities = engine.getEntitiesFor(Family.getFor(StaticCollisionComponent.class));
	}
}
