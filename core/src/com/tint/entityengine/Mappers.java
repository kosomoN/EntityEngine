package com.tint.entityengine;

import com.badlogic.ashley.core.ComponentMapper;
import com.tint.entityengine.entity.components.PositionComponent;

public class Mappers {

	public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);

	
}
