package com.tint.entityengine.entity.components;

import com.badlogic.ashley.core.Component;
import com.tint.entityengine.server.entity.components.Networked;

public class AttackHitbox extends Component implements Networked {

	private int width, height;
	private int offsetX, offsetY;
	
	public AttackHitbox(int width, int height, int offsetX, int offsetY) {
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
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

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}
}
