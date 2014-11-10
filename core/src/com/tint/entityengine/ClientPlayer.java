package com.tint.entityengine;

import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.*;

import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.components.renderers.PlayerRenderer;
import com.tint.entityengine.entity.components.renderers.PlayerRenderer.PlayerState;

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

		float testX = newX;
		float testY = newY;
		
		if(in.getKey(KEY_UP))
			testY += speed * modifier;
		
		if(in.getKey(KEY_DOWN))
			testY -= speed * modifier;
		
		if(in.getKey(KEY_LEFT))
			testX -= speed * modifier;
		
		if(in.getKey(KEY_RIGHT))
			testX += speed * modifier;
		
		if(in.getKey(KEY_ATTACK))
			((PlayerRenderer) Mappers.render.get(entity).renderer).setPlayerState(PlayerState.ATTACKING);
		
		newX = testX;
		newY = testY;
		pos.set(newX, newY, gs.getTick(), true, entity);
	}
	
	public PositionComponent getPos() {
		return pos;
	}
	
}
