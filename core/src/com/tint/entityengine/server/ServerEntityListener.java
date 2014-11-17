package com.tint.entityengine.server;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.utils.ImmutableArray;
import com.tint.entityengine.network.packets.CreateEntityPacket;
import com.tint.entityengine.network.packets.RemoveEntityPacket;
import com.tint.entityengine.server.entity.components.Networked;

public class ServerEntityListener implements EntityListener {

	private GameServer gameServer;
	private CreateEntityPacket cep;
	private RemoveEntityPacket rep;

	public ServerEntityListener(GameServer gameServer) {
		this.gameServer = gameServer;
		
		cep = new CreateEntityPacket();
		rep = new RemoveEntityPacket();
	}
	
	@Override
	public void entityAdded(Entity entity) {
		cep.getComponents().clear();
		
		cep.serverId = entity.getId();
		
		ImmutableArray<Component> components = entity.getComponents();
		for(int j = 0; j < components.size(); j++) {
			Component comp =  components.get(j);
			if(comp instanceof Networked)
				cep.addComponent(components.get(j));
		}
		
		gameServer.getServer().sendToAllTCP(cep);
	}

	@Override
	public void entityRemoved(Entity entity) {
		rep.serverId = entity.getId();
		gameServer.getServer().sendToAllTCP(rep);
	}

}
