package com.tint.entityengine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Assets {

	public static Texture loadTexture(String path) {
		return new Texture(Gdx.files.internal(path));
	}
	
	public static void load() {
		// Load all textures
	}

}
