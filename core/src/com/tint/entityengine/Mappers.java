package com.tint.entityengine;

import com.badlogic.ashley.core.ComponentMapper;
import com.tint.entityengine.entity.components.AttackHitbox;
import com.tint.entityengine.entity.components.HealthComponent;
import com.tint.entityengine.entity.components.CollisionComponent;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.components.RenderComponent;
import com.tint.entityengine.entity.components.StaticCollisionComponent;

public class Mappers {

	public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
	public static final ComponentMapper<RenderComponent> render = ComponentMapper.getFor(RenderComponent.class);
	public static final ComponentMapper<HealthComponent> health = ComponentMapper.getFor(HealthComponent.class);
	public static final ComponentMapper<CollisionComponent> dynamicCollision = ComponentMapper.getFor(CollisionComponent.class);
	public static final ComponentMapper<StaticCollisionComponent> staticCollision = ComponentMapper.getFor(StaticCollisionComponent.class);
	public static final ComponentMapper<AttackHitbox> attackHitbox = ComponentMapper.getFor(AttackHitbox.class);
}
