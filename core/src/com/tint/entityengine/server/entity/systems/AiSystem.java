package com.tint.entityengine.server.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tint.entityengine.server.GameServer;
import com.tint.entityengine.server.entity.components.AiComponent;

public class AiSystem extends IteratingSystem {

	private ComponentMapper<AiComponent> aiMapper = ComponentMapper.getFor(AiComponent.class);
	private GameServer server;
	
	public AiSystem(GameServer server) {
		super(Family.getFor(AiComponent.class));
		this.server = server;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		aiMapper.get(entity).aiController.update(server.getTicks());
	}
}
