package com.tint.entityengine.entity.components;

import com.badlogic.ashley.core.Component;
import com.tint.entityengine.server.entity.components.Networked;

public class StaticCollisionComponent extends Component implements Networked {

	private transient boolean init;
	private int width, height;
	private transient float[][] hitboxOffset = new float[4][2];
	
	public StaticCollisionComponent(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public float getOffset(int corner, int coordinate) {
		if(!init) {
			hitboxOffset = new float[4][2];
			
			hitboxOffset[0][0] = getWidth() / 2f;
			hitboxOffset[0][1] = getHeight() / 2f;
			
			hitboxOffset[1][0] = getWidth() / 2f;
			hitboxOffset[1][1] = -getHeight() / 2f;
			
			hitboxOffset[2][0] = -getWidth() / 2f;
			hitboxOffset[2][1] = -getHeight() / 2f;
			
			hitboxOffset[3][0] = -getWidth() / 2f;
			hitboxOffset[3][1] = getHeight() / 2f;
			
			init = true;
		}
		return hitboxOffset[corner][coordinate];
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
