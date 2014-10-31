package com.tint.entityengine.network.packets;

import java.util.ArrayList;

import com.tint.entityengine.server.entity.components.Networked;

public class UpdatePacket extends Packet {
	private ArrayList<EntityUpdate> changedEntities = new ArrayList<UpdatePacket.EntityUpdate>();
	public int tick;
	
	public void addEntityUpdate(EntityUpdate entityUpdate) {
		changedEntities.add(entityUpdate);
	}
	
	public ArrayList<EntityUpdate> getEntityUpdates() {
		return changedEntities;
	}
	
	public void clear() {
		changedEntities.clear();
	}

	public static class EntityUpdate {
		private ArrayList<Networked> changedComponents = new ArrayList<Networked>();
		private long id;
		
		public EntityUpdate(long id) {
			this.id = id;
		}
		
		//Required by de-serialization
		@SuppressWarnings("unused")
		private EntityUpdate() {}
		
		public void addComponent(Networked component) {
			changedComponents.add(component);
		}
		
		public ArrayList<Networked> getComponents() {
			return changedComponents;
		}
		
		public long getID() {
			return id;
		}
	}
}
