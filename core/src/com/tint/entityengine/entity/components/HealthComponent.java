package com.tint.entityengine.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.server.GameServer;
import com.tint.entityengine.server.ServerClient;
import com.tint.entityengine.server.entity.components.Networked;

public class HealthComponent extends Component implements Networked {

	private int hp;
	private int maxHp;
	private transient boolean hasChanged;
	private transient GameServer gs;
	private transient Entity entity;
	
	public HealthComponent(int maxHp, GameServer gs, Entity entity) {
		this.hp = this.maxHp = maxHp;
		this.entity = entity;
		this.gs = gs;
	}
	
	public void addHp(int hp) {
		setHp(this.hp + hp);
	}
	
	public void setHp(int hp) {
		if(this.hp != hp)
			hasChanged = true;
		
		this.hp = hp;
		
		if(this.hp > maxHp)
			this.hp = maxHp;
		else if(this.hp <= 0) {
			this.hp = 0;
			
			handleDeath(entity);
		}
	}
	
	public void handleDeath(Entity entity) {
		synchronized(gs.getClients()) {
			// Player
			for(ServerClient client : gs.getClients()) {
				if(client.getEntity() == entity) {
					client.getConnection().close();
					return;
				}
			}
			
			// Enemy
			System.out.println("Enemy died");
			gs.getEngine().removeEntity(entity);
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
