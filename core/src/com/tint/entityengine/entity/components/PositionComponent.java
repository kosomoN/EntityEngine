package com.tint.entityengine.entity.components;

import com.badlogic.ashley.core.Component;
import com.tint.entityengine.server.entity.components.Networked;

public class PositionComponent extends Component implements Networked {
    private float x1, y1;
    private transient int tick1;
    
    private transient float x2, y2;
    private transient int tick2;
    
    private transient float x3, y3;
    private transient int tick3;
    
	private transient boolean hasChanged = true;
	
	public transient boolean isPlayer = false;
	
	public PositionComponent(float x, float y) {
		x1 = x;
		y1 = y;
	}
	
	public PositionComponent() {}
	
    public float getX() {
        return x1;
    }

    public float getY() {
        return y1;
    }

    public float getLerpX(float tick) {
    	if(isPlayer) return x2 + (tick % 1) * (x1 - x2);
    	
    	tick -= 3;
    	
    	//If the current tick is less than the oldest snapshot
    	if(tick < tick3)
    		return x3;
    	
    	//If it's more than the latest
    	if(tick >= tick1)
    		return x1;
    	
    	//If it's between the latest and second latest
    	if(tick > tick2) {
    		float t = (tick - tick2) / (tick1 - tick2);
        	
            return x2 + t * (x1 - x2);
    	}
    	
    	//Interpolate between the two last ones
    	
    	//Optimize?
    	float t = (tick - tick3) / (tick2 - tick3);
    	
        return x3 + t * (x2 - x3);
    }

    public float getLerpY(float tick) {
    	if(isPlayer) return y2 + (tick % 1) * (y1 - y2);
    	
    	tick -= 3;
    	
    	//If the current tick is less than the oldest snapshot
    	if(tick < tick3)
    		return y3;
    	
    	//If it's more than the latest
    	if(tick >= tick1)
    		return y1;
    	
    	//If it's between the latest and neyt latest
    	if(tick > tick2) {
    		float t = (tick - tick2) / (tick1 - tick2);
        	
            return y2 + t * (y1 - y2);
    	}
    	
    	//Interpolate between the two last ones
    	
    	//Optimize?
    	float t = (tick - tick3) / (tick2 - tick3);
    	
        return y3 + t * (y2 - y3);
    }
    
	public void set(float x, float y, int tick) {
		x3 = x2;
		y3 = y2;
		
		tick3 = tick2;
		
		x2 = x1;
		y2 = y1;
		
		tick2 = tick1;
		
		
		this.x1 = x;
		this.y1 = y;
		
		tick1 = tick;
		
		hasChanged = true;
	}
	
	public void add(float x, float y, int tick) {
		set(this.x1 + x, this.y1 + y, tick);
	}
	
	public void add(float x, float y) {
		x1 += x;
		y1 += y;
	}

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}

	public void set(PositionComponent positionComponent, int tick) {
		set(positionComponent.x1, positionComponent.y1, tick);
	}

	@Override
	public void resetChanged() {
		this.hasChanged = false;
	}
}
