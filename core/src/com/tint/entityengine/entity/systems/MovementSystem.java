package com.tint.entityengine.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.tint.entityengine.entity.components.PositionComponent;

public class MovementSystem extends IteratingSystem {

	private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

	public MovementSystem() {
		super(Family.getFor(PositionComponent.class));
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PositionComponent p = pm.get(entity);
		p.updateOld();
		p.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
	}
}
