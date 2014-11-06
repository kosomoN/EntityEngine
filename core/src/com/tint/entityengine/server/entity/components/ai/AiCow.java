package com.tint.entityengine.server.entity.components.ai;

import com.tint.entityengine.entity.components.PositionComponent;

public class AiCow implements AiController {

	private PositionComponent position;
	private int dir;
	
	public AiCow(PositionComponent position) {
		this.position = position;
	}
	
	@Override
	public void update(int tick) {
		if(tick % 120 < 60) {
			if(tick % 60 == 0)
				dir = (int) (Math.random() * 4);
			
			
			if(dir == 0)
				position.add(0, 3, -1);
			else if(dir == 1)
				position.add(3, 0, -1);
			else if(dir == 2)
				position.add(0, -3, -1);
			else if(dir == 3)
				position.add(-3, 0, -1);
		}
	}
	
}
