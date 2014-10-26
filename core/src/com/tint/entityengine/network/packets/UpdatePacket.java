package com.tint.entityengine.network.packets;

import java.util.ArrayList;

import com.tint.entityengine.server.entity.components.Networked;

public class UpdatePacket extends Packet {
	private ArrayList<EntityUpdate> changedEntities = new ArrayList<UpdatePacket.EntityUpdate>();
	
	public void addEntityUpdate(EntityUpdate entityUpdate) {
		changedEntities.add(entityUpdate);
	}
	
	public ArrayList<EntityUpdate> getEntityUpdates() {
		return changedEntities;
	}
	
	public void clear() {
		changedEntities.clear();
	}

	public ArrayList<EntityUpdate> getEntities() {
		return changedEntities;
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
		
		public void setID(long id) {
			this.id = id;
		}
		
		public ArrayList<Networked> getComponents() {
			return changedComponents;
		}
		
		public long getID() {
			return id;
		}
	}
}
