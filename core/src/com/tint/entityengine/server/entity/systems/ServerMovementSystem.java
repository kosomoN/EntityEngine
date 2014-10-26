package com.tint.entityengine.server.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.server.GameServer;

public class ServerMovementSystem extends IteratingSystem {

	private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
	private GameServer gameServer;

	public ServerMovementSystem(GameServer gameServer) {
		super(Family.getFor(PositionComponent.class));
		this.gameServer = gameServer;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PositionComponent p = pm.get(entity);
		p.updateOld();
		p.set((float) Math.cos(gameServer.getTicks() / 10.0 + entity.getId()) * 400 + 600, (float) Math.sin(gameServer.getTicks() / 10.0 + entity.getId()) * 400 + 400);
	}
}
