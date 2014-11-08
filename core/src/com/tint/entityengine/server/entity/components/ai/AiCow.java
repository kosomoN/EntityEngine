package com.tint.entityengine.server.entity.components.ai;

import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.components.renderers.DirectionalRenderer;

public class AiCow implements AiController {

	private PositionComponent position;
	private int dir;
	private Entity entity;
	
	public AiCow(Entity entity) {
		this.position = Mappers.position.get(entity);
		this.entity = entity;
	}
	
	@Override
	public void update(int tick) {
		int tickMod = tick % 180;
		if(tickMod < 60) {
			if(tickMod == 0)
				dir = (int) (Math.random() * 4);
			
			
			if(dir == 0)
				position.add(0, 3, -1);
			else if(dir == 1)
				position.add(3, 0, -1);
			else if(dir == 2)
				position.add(0, -3, -1);
			else if(dir == 3)
				position.add(-3, 0, -1);
		} else if(tickMod == 60) {
			((DirectionalRenderer) Mappers.render.get(entity).renderer).setCustomAnimString("CowEat");
		}
	}
	
}
