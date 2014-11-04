package com.tint.entityengine.entity.components;

import com.badlogic.ashley.core.Component;
import com.tint.entityengine.server.entity.components.Networked;

public class HealthComponent extends Component implements Networked {

	private int hp;
	private int maxHp;
	private boolean hasChanged;
	
	public HealthComponent(int maxHp) {
		this.hp = this.maxHp = maxHp;
	}
	
	public void addHp(int hp) {
		this.hp += hp;
		
		if(hp != 0)
			hasChanged = true;
	}
	
	public void setHp(int hp) {
		if(this.hp - Math.min(hp, maxHp) != 0) {
			if(hp <= maxHp)
				this.hp = hp;
			else
				this.hp = maxHp;
			
			hasChanged = true;
		}
	}
	
	public int getHp() {
		return hp;
	}
	
	public int getMaxHp() {
		return maxHp;
	}

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}

	@Override
	public void resetChanged() {
		this.hasChanged = false;
	}
}
