package com.tint.entityengine.entity.components;

import com.badlogic.ashley.core.Component;

public class PositionComponent extends Component {
    private float x, y;
    private float lastTickX, lastTickY;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getLerpX(float t) {
        return lastTickX + t * (x - lastTickX);
    }

    public float getLerpY(float t) {
        return lastTickY + t * (y - lastTickY);
    }

    public void updateOld() {
    	lastTickX = x;
    	lastTickY = y;
    }
    
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
