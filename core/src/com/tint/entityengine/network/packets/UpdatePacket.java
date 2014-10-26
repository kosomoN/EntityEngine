package com.tint.entityengine.network.packets;

import java.util.ArrayList;

import com.tint.entityengine.server.entity.components.Networked;

public class UpdatePacket extends Packet {
	private ArrayList<EntityUpdate> changedEntities;
	
	public void addComponent(EntityUpdate entityUpdate) {
		changedEntities.add(entityUpdate);
	}
	
	public void clear() {
		changedEntities.clear();
	}
	
	public class EntityUpdate {
		private ArrayList<Networked> changedComponents;
		private long id;
		
		public void addComponent(Networked component) {
			changedComponents.add(component);
		}
	}
}
