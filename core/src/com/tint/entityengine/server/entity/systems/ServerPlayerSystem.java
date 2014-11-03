package com.tint.entityengine.server.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.server.GameServer;
import com.tint.entityengine.server.entity.components.ServerPlayerComponent;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.*;

public class ServerPlayerSystem extends IteratingSystem {

	private static final float DIAG_MOD = (float) Math.sqrt(0.5);
	private ComponentMapper<ServerPlayerComponent> playerMapper = ComponentMapper.getFor(ServerPlayerComponent.class);
	
	private GameServer gameServer;

	@SuppressWarnings("unchecked")
	public ServerPlayerSystem(GameServer gameServer) {
		super(Family.getFor(ServerPlayerComponent.class));
		this.gameServer = gameServer;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ServerPlayerComponent pl = playerMapper.get(entity);
		PositionComponent pos = Mappers.position.get(entity);
		float newX = pos.getX();
		float newY = pos.getY();
		
		float modifier = 1;
		
		//XOR if only one of the keys on one axis is pressed
		if((pl.getKey(KEY_UP) ^ pl.getKey(KEY_DOWN)) &&
				(pl.getKey(KEY_LEFT) ^ pl.getKey(KEY_RIGHT))) {
			modifier = DIAG_MOD;
		}
		
		if(pl.getKey(KEY_UP))
			newY += pl.getSpeed() * modifier;
		
		if(pl.getKey(KEY_DOWN))
			newY -= pl.getSpeed() * modifier;
		
		if(pl.getKey(KEY_LEFT))
			newX -= pl.getSpeed() * modifier;
		
		if(pl.getKey(KEY_RIGHT))
			newX += pl.getSpeed() * modifier;
		
		//TODO Check if new position is valid
		
		if(newX > 700)
			newX = 700;
		
		pos.set(newX, newY, gameServer.getTicks());
	}

}
