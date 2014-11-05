package com.tint.entityengine.entity.components.renderers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tint.entityengine.GameState;

public interface Renderer {
	public void initialize(GameState gs);
	public void render(SpriteBatch batch, Entity e, float tickTime);
}
