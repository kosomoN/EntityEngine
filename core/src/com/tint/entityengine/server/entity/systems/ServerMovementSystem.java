package com.tint.entityengine.server.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tint.entityengine.entity.components.PositionComponent;

public class ServerMovementSystem extends IteratingSystem {

	private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

	public ServerMovementSystem() {
		super(Family.getFor(PositionComponent.class));
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PositionComponent p = pm.get(entity);
		p.updateOld();
		p.set(p.getX() + 1, p.getY() + 1);
	}
}
