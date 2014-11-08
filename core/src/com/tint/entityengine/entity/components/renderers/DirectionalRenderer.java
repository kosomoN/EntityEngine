package com.tint.entityengine.entity.components.renderers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.tint.entityengine.Assets;
import com.tint.entityengine.GameState;
import com.tint.entityengine.entity.components.PositionComponent;

public class DirectionalRenderer implements Renderer {

	public static final int N = 0, E = 1, S = 2, W = 3;
	
	private transient Animation[] anims;
	private transient Animation[] walkingAnims;
	
	private transient GameState gs;
	
	private String customAnimString;
	private boolean hasChanged = false;
	
	private transient Animation[] customAnim;
	private transient Vector2 customAnimOffset;
	private transient int customAnimStart;
	
	//Just to make the animations not exactly synchronized
	private transient float animTimeOffset;
	private transient Vector2 offset;
	//Used for direction
	private transient float oldX, oldY;
	private transient int dir;
	public String animFile;
	
	@Override
	public void initialize(GameState gs) {
		this.gs = gs;
		anims = Assets.getDirectionAnims(animFile);
		walkingAnims = Assets.getDirectionAnims(animFile + "Walking");
		
		offset = Assets.getAnimOffset(animFile);
		if(anims == null) {
			throw new RuntimeException("Animation is null");
		}
		
		animTimeOffset = (float) Math.random();
	}
	
	@Override
	public void render(SpriteBatch batch, Entity e, PositionComponent pos, float tickTime) {
		float lerpX = pos.getLerpX(tickTime);
		float lerpY = pos.getLerpY(tickTime);
		
		float dx = lerpX - oldX;
		float dy = lerpY - oldY;
		
		//If the entities has moved. Slight floating-point error was causing bugs
		if(Math.abs(dx) > 0.01 || Math.abs(dy) > 0.01 ) {
			oldX = lerpX;
			oldY = lerpY;
			
			//Calculate direction. Remove some to show sideways when going diagonally
			if(Math.abs(dx) >= Math.abs(dy) - 0.01) {
				if(dx > 0)
					dir = W;
				else
					dir = E;
			} else {
				if(dy > 0)
					dir = N;
				else
					dir = S;
			}

			if(walkingAnims != null && customAnim == null) {
				batch.draw(walkingAnims[dir].getKeyFrame(tickTime * GameState.TICK_LENGTH + animTimeOffset, true), lerpX - offset.x, lerpY - offset.y);
				return;
			}
		}

		if(customAnim != null) {
			float animTime = (gs.getTick() - customAnimStart) * GameState.TICK_LENGTH;
			if(customAnim[dir].getPlayMode() == PlayMode.LOOP_PINGPONG) {
				//Ping pong animations are double the length minus one frame
				if(customAnim[dir].isAnimationFinished(animTime / 2 + customAnim[dir].getFrameDuration()))
					customAnim = null;
			} else if(customAnim[dir].isAnimationFinished(animTime))
				customAnim = null;
			
			if(customAnim != null) {
				batch.draw(customAnim[dir].getKeyFrame(animTime, true), lerpX - customAnimOffset.x, lerpY - customAnimOffset.y);
				return;
			}
		}

		batch.draw(anims[dir].getKeyFrame(tickTime * GameState.TICK_LENGTH + animTimeOffset, true), lerpX - offset.x, lerpY - offset.y);
	}
	
	public void playCustomAnim(String anim) {
		customAnimString = anim;
		customAnim = Assets.getDirectionAnims(anim);
		customAnimOffset = Assets.getAnimOffset(anim);
		customAnimStart = gs.getTick();
	}

	@Override
	public void updatePacket(Renderer renderer) {
		DirectionalRenderer newRenderer = (DirectionalRenderer) renderer;
		
		if(newRenderer.customAnimString != null) 
			playCustomAnim(newRenderer.customAnimString);
		
		if(!newRenderer.animFile.equals(animFile))
			initialize(gs);
	}
	
	public void setCustomAnimString(String customAnim) {
		this.customAnimString = customAnim;
		hasChanged = true;
	}

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}

	@Override
	public void resetChanged() {
		hasChanged = false;
	}
}
