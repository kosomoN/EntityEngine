package com.tint.entityengine;

import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.*;

import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.entity.components.PositionComponent;

public class ClientPlayer {
	private static final float DIAG_MOD = (float) Math.sqrt(0.5);

	public long serverEntityId;
	private Entity entity;
	private PositionComponent pos;
	private GameState gs;
	private float speed = 5;
	
	public ClientPlayer(GameState gs) {
		this.gs = gs;
	}
	
	public void setPlayerEntity(Entity entity) {
		this.entity = entity;
		
		pos = Mappers.position.get(entity);
		pos.isPlayer = true;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void update() {
		
		float newX = pos.getX();
		float newY = pos.getY();
		
		float modifier = 1;
		
		InputProcessor in = gs.getInputProcessor();
		
		//XOR if only one of the keys on one axis is pressed
		if((in.getKey(KEY_UP) ^ in.getKey(KEY_DOWN)) &&
				(in.getKey(KEY_LEFT) ^ in.getKey(KEY_RIGHT))) {
			modifier = DIAG_MOD;
		}
		
		if(in.getKey(KEY_UP))
			newY += speed * modifier;
		
		if(in.getKey(KEY_DOWN))
			newY -= speed * modifier;
		
		if(in.getKey(KEY_LEFT))
			newX -= speed * modifier;
		
		if(in.getKey(KEY_RIGHT))
			newX += speed * modifier;
		
		//TODO Check if new position is valid
	
		pos.set(newX, newY, gs.getTick());
	}
	
}
