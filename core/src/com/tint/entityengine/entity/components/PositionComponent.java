package com.tint.entityengine.entity.components;

import com.badlogic.ashley.core.Component;
import com.tint.entityengine.server.entity.components.Networked;

public class PositionComponent extends Component implements Networked {
    private float x, y;
    private transient float lastTickX, lastTickY;
	private transient boolean hasChanged = true;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getLerpX(float t) {
        return lastTickX;// + t * (x - lastTickX);
    }

    public float getLerpY(float t) {
        return lastTickY;// + t * (y - lastTickY);
    }

    public void updateOld() {
    	lastTickX = x;
    	lastTickY = y;
    }
    
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
		
		hasChanged = true;
	}

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}

	public void set(PositionComponent positionComponent) {
		set(positionComponent.x, positionComponent.y);
	}

	@Override
	public void resetChanged() {
		this.hasChanged = false;
	}
}
