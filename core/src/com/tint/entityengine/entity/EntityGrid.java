package com.tint.entityengine.entity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.tint.entityengine.entity.components.CollisionComponent;
import com.tint.entityengine.entity.components.PositionComponent;

public class EntityGrid {

	private static Engine engine;

	public static void init(Engine engine) {
		EntityGrid.engine = engine;
	}
	
	public static ImmutableArray<Entity> get(PositionComponent positionComponent) {
		return engine.getEntitiesFor(Family.getFor(CollisionComponent.class));
	}

}
