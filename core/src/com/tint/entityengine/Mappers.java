package com.tint.entityengine;

import com.badlogic.ashley.core.ComponentMapper;
import com.tint.entityengine.entity.components.HealthComponent;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.components.RenderComponent;

public class Mappers {

	public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
	public static final ComponentMapper<RenderComponent> render = ComponentMapper.getFor(RenderComponent.class);
	public static final ComponentMapper<HealthComponent> health = ComponentMapper.getFor(HealthComponent.class);
	
}
