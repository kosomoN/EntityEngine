package com.tint.entityengine.network.packets;

import java.util.ArrayList;

import com.badlogic.ashley.core.Component;

public class CreateEntityPacket extends Packet {
	private ArrayList<Component> components = new ArrayList<Component>();
	private long serverId;

	public CreateEntityPacket(long serverId) {
		this.serverId = serverId;
	}
	
	//Required by de-serialization
	@SuppressWarnings("unused")
	private CreateEntityPacket() {}
	
	public void addComponent(Component component) {
		components.add(component);
	}
	
	public ArrayList<Component> getComponents() {
		return components;
	}
	
	public long getServerId() {
		return serverId;
	}

}
