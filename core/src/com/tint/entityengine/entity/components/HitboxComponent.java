package com.tint.entityengine.entity.components;

import com.badlogic.ashley.core.Component;
import com.tint.entityengine.GameMap;
import com.tint.entityengine.server.entity.components.Networked;

public class HitboxComponent extends Component implements Networked {

	private transient boolean init;
	private int width, height;
	private transient float[][] hitboxOffset = new float[4][2];;
	
	public HitboxComponent(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public HitboxComponent() {
		
	}
	
	public float getOffset(int x, int y) {
		if(!init) {
			hitboxOffset = new float[4][2];
			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < 2; j++) {
					if(j == 0)
						if(i == 0 || i == 3)
							hitboxOffset[i][j] = (float) (-getWidth() / 2f / GameMap.TILE_SIZE);
						else
							hitboxOffset[i][j] = (float) (getWidth() / 2f / GameMap.TILE_SIZE);
					else
						if(i == 0 || i == 1)
							hitboxOffset[i][j] = (float) (-getHeight() / 2f / GameMap.TILE_SIZE);
						else
							hitboxOffset[i][j] = (float) (getHeight() / 2f / GameMap.TILE_SIZE);
				}
			}
			init = true;
		}
		return hitboxOffset[x][y];
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	@Override
	public boolean hasChanged() {
		return false;
	}

	@Override
	public void resetChanged() {
		
	}

}
