package com.tint.entityengine.entity.components.renderers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.tint.entityengine.Assets;
import com.tint.entityengine.GameState;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.PositionComponent;

public class DirectionalRenderer implements Renderer {

	private static final int N = 0, E = 1, S = 2, W = 3;
	
	private transient Animation[] anims;
	private transient Animation[] walkingAnims;
	
	//Just to make the animations not exactly synchronized
	private transient float animTimeOffset;
	private transient Vector2 offset;
	//Used for direction
	private transient float oldX, oldY;
	private transient int dir;
	public String animFile;
	
	@Override
	public void initialize(GameState gs) {
		anims = Assets.getDirectionAnims(animFile);
		walkingAnims = Assets.getDirectionAnims(animFile + "Walking");
		
		offset = Assets.getAnimOffset(animFile);
		if(anims == null) {
			throw new RuntimeException("Animation is null");
		}
		
		animTimeOffset = (float) Math.random();
	}
	
	@Override
	public void render(SpriteBatch batch, Entity e, float tickTime) {
		PositionComponent pos = Mappers.position.get(e);
		float lerpX = pos.getLerpX(tickTime);
		float lerpY = pos.getLerpY(tickTime);
		
		//If the entities has moved. Slight floating-point error was causing bugs
		if(Math.abs(lerpX - oldX) > 0.01 || Math.abs(lerpY - oldY) > 0.01 ) {
			float dx = lerpX - oldX;
			float dy = lerpY - oldY;
			
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
			
			if(walkingAnims != null) {
				batch.draw(walkingAnims[dir].getKeyFrame(tickTime * GameState.TICK_LENGTH + animTimeOffset, true), lerpX - offset.x, lerpY - offset.y);
				return;
			}
		}
		
		batch.draw(anims[dir].getKeyFrame(tickTime * GameState.TICK_LENGTH + animTimeOffset, true), lerpX - offset.x, lerpY - offset.y);
	}
}
