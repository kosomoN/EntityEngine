package com.tint.entityengine.entity.components.renderers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.tint.entityengine.Assets;
import com.tint.entityengine.GameState;
import com.tint.entityengine.entity.components.PositionComponent;

public class TextureRenderer implements Renderer {

	public String textureFile;
	private transient TextureRegion texture;
	private transient Vector2 offset;
	private boolean hasChanged;
	
	@Override
	public void initialize(GameState gs) {
		texture = Assets.getTexture(textureFile);
		if(texture == null) {
			throw new RuntimeException("Texture is null: " + textureFile);
		}
		offset = Assets.getAnimOffset(textureFile);
	}

	@Override
	public void render(SpriteBatch batch, Entity e, PositionComponent pos, float tickTime) {
		batch.draw(texture, pos.getLerpX(tickTime) - offset.x, pos.getLerpY(tickTime) - offset.y);
	}

	@Override
	public void updatePacket(Renderer renderer) {
		TextureRenderer newRenderer = (TextureRenderer) renderer;
		if(!newRenderer.textureFile.equals(textureFile))
			initialize(null);
	}
	
	public void setTextureFile(String textureFile) {
		this.textureFile = textureFile;
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
