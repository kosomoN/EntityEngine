package com.tint.entityengine.server.entity.components.ai;

import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.server.GameServer;

public class AiChargeEnemy implements AiController {

	private PositionComponent position;
	private Entity entity;
	private GameServer gameServer;
	
	public AiChargeEnemy(Entity entity, GameServer gameServer) {
		this.position = Mappers.position.get(entity);
		this.entity = entity;
		this.gameServer = gameServer;
	}
	
	@Override
	public void update(int tick) {
		if(gameServer.getClients().size() == 0)
			return;
		
		PositionComponent playerPos = Mappers.position.get(gameServer.getClients().get(0).getEntity());
		
		float dx = playerPos.getX() - position.getX();
		float dy = playerPos.getY() - position.getY();
		float angle = (float) Math.atan2(dy, dx);
		
		position.add((float) Math.cos(angle) * 3, (float) Math.sin(angle) * 3, -1, true, entity);
		
		if(dx * dx + dy * dy < 500) {
			Mappers.health.get(gameServer.getClients().get(0).getEntity()).addHp(-1);
		}
	}
	
}
