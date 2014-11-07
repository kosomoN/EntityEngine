package com.tint.entityengine.entity.components.renderers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tint.entityengine.GameState;
import com.tint.entityengine.entity.components.PositionComponent;

public interface Renderer {
	public void initialize(GameState gs);
	public void render(SpriteBatch batch, Entity e, PositionComponent posComp, float tickTime);
}
