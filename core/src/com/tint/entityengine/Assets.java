package com.tint.entityengine;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Assets {

	private static Map<String, TextureRegion> textureRegions = new HashMap<String, TextureRegion>();
	private static Map<String, Animation[]> dirAnims = new HashMap<String, Animation[]>();
	private static Map<String, Vector2> renderOffsets = new HashMap<String, Vector2>();
	private static TextureAtlas atlas;
	
	public static void loadAssets() throws Exception {
		atlas = new TextureAtlas("graphics/entities/Entities.atlas");
		
		for(AtlasRegion region : atlas.getRegions()) {

			String[] splits = region.name.split(" ");
			
			textureRegions.put(splits[0], region);
			
			if(splits[1].equals("anim"))
				renderOffsets.put(splits[0], new Vector2(Integer.parseInt(splits[2]), Integer.parseInt(splits[3])));
			else
				renderOffsets.put(splits[0], new Vector2(Integer.parseInt(splits[1]), Integer.parseInt(splits[2])));
			
			//Load animations. Every anim contains for directions and index shows animation frames
			if(region.name.contains("anim")) {
				Animation[] anims = new Animation[4];
				
				//Second last number is frame time
				int animTime = Integer.parseInt(splits[splits.length - 2]);
				//Last number is width in sprites
				int widthSprites = Integer.parseInt(splits[splits.length - 1]);
				
				boolean pingPong = Boolean.parseBoolean(splits[4]);
				
				int spriteWidth = region.getRegionWidth() / widthSprites;
				int spriteHeight = region.getRegionHeight() / 4;
				
				TextureRegion[] sprites = new TextureRegion[widthSprites];
				for(int i = 0; i < 4; i++) {
					for(int j = 0; j < widthSprites; j++) {
						sprites[j] = new TextureRegion(region, j * spriteWidth, i * spriteHeight, spriteWidth, spriteHeight);
					}
					anims[i] = new Animation(animTime / 1000f, sprites.clone());
					
					if(pingPong)
						anims[i].setPlayMode(PlayMode.LOOP_PINGPONG);
				}
				
				dirAnims.put(splits[0], anims);
			}
		}
	}
	
	public static Animation[] getDirectionAnims(String animFile) {
		return dirAnims.get(animFile);
	}

	public static Vector2 getAnimOffset(String animFile) {
		return renderOffsets.get(animFile);
	}

	public static TextureRegion getTexture(String textureName) {
		return textureRegions.get(textureName);
	}
}
