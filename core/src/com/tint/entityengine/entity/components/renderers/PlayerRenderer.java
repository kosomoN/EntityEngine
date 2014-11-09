package com.tint.entityengine.entity.components.renderers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.tint.entityengine.Assets;
import com.tint.entityengine.GameState;
import com.tint.entityengine.entity.components.PositionComponent;

public class PlayerRenderer implements Renderer {

	public enum PlayerState { STANDING, WALKING, ATTACKING }
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	
	private transient GameState gs;
	private transient PlayerState state = PlayerState.STANDING;
	
	// Animations and files
	private transient Animation[] baseStandingAnims;
	private transient Animation[] baseWalkingAnims;
	private transient Animation[] baseAttackingAnims;
	private String[] animFiles;
	
	// Positioning
	private transient Vector2 standAnimOffset;
	private transient Vector2 walkAnimOffset;
	private transient Vector2 attackAnimOffset;
	private transient float oldX, oldY;
	private transient int dir;
	
	// Time related
	private transient float lastTickTime;
	private transient float attackTime = 100;
	
	@Override
	public void initialize(GameState gs) {
		this.gs = gs;
		
		baseStandingAnims = Assets.getDirectionAnims(animFiles[0]);
		baseWalkingAnims = Assets.getDirectionAnims(animFiles[1]);
		baseAttackingAnims = Assets.getDirectionAnims(animFiles[2]);
		for(Animation a : baseAttackingAnims)
			a.setPlayMode(Animation.PlayMode.NORMAL);
		
		standAnimOffset = Assets.getAnimOffset(animFiles[0]);
		walkAnimOffset = Assets.getAnimOffset(animFiles[1]);
		attackAnimOffset = Assets.getAnimOffset(animFiles[2]);
	}

	@Override
	public void render(SpriteBatch batch, Entity e, PositionComponent posComp, float tickTime) {
		attackTime += tickTime - lastTickTime;
		lastTickTime = tickTime;
		
		float lerpX = posComp.getLerpX(tickTime);
		float lerpY = posComp.getLerpY(tickTime);
		
		float dx = lerpX - oldX;
		float dy = lerpY - oldY;
		
		//If the entities has moved. Slight floating-point error was causing bugs
		if(Math.abs(dx) > 0.01 || Math.abs(dy) > 0.01 ) {
			setPlayerState(PlayerState.WALKING);
			
			oldX = lerpX;
			oldY = lerpY;
			
			//Calculate direction. Remove some to show sideways when going diagonally
			if(Math.abs(dx) >= Math.abs(dy) - 0.01) {
				if(dx > 0)
					dir = WEST;
				else
					dir = EAST;
			} else {
				if(dy > 0)
					dir = NORTH;
				else
					dir = SOUTH;
			}
		} else {
			setPlayerState(PlayerState.STANDING);
		}
		
		switch(state) {
		case STANDING:
			batch.draw(baseStandingAnims[dir].getKeyFrame(tickTime * GameState.TICK_LENGTH, true), lerpX - standAnimOffset.x, lerpY - standAnimOffset.y);
			break;
			
		case WALKING:
			batch.draw(baseWalkingAnims[dir].getKeyFrame(tickTime * GameState.TICK_LENGTH, true), lerpX - walkAnimOffset.x, lerpY - walkAnimOffset.y);
			break;
			
		case ATTACKING:
			batch.draw(baseAttackingAnims[dir].getKeyFrame(attackTime * GameState.TICK_LENGTH, false), lerpX - attackAnimOffset.x, lerpY - attackAnimOffset.y);
			break;
		}
	}

	@Override
	public void updatePacket(Renderer renderer) {
		PlayerRenderer newRenderer = (PlayerRenderer) renderer;
		
		if(newRenderer.baseStandingAnims == null)
			initialize(gs);
	}
	
	public void setPlayerState(PlayerState state) {
		if(baseAttackingAnims != null) {
			if(state == PlayerState.ATTACKING) {
				this.state = state;
				if(attackTime * GameState.TICK_LENGTH >= baseAttackingAnims[dir].getAnimationDuration())
					attackTime = 0;
			} else if(this.state == PlayerState.ATTACKING) {
				if(attackTime * GameState.TICK_LENGTH >= baseAttackingAnims[dir].getAnimationDuration())
					this.state = state;
			} else {
				this.state = state;
			}
		}
	}
	
	public void setPlayerAnims(String[] animFiles) {
		this.animFiles = animFiles;
	}
	
	@Override
	public boolean hasChanged() {
		return false;
	}

	@Override
	public void resetChanged() {
		
	}

}
