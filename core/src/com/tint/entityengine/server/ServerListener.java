package com.tint.entityengine.server;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.tint.entityengine.network.packets.CreateEntityPacket;
import com.tint.entityengine.server.entity.components.NetworkComponent;

public class ServerListener extends Listener {

	private GameServer gameServer;

	public ServerListener(GameServer gameServer) {
		this.gameServer = gameServer;
	}

	@Override
	public void connected(Connection connection) {
		super.connected(connection);
		
		ImmutableArray<Entity> enitites = gameServer.getEngine().getEntitiesFor(Family.getFor(NetworkComponent.class));
		
		for(int i = 0; i < enitites.size(); i++) {
			Entity e = enitites.get(i);
			
			CreateEntityPacket cep = new CreateEntityPacket(e.getId());
			
			ImmutableArray<Component> components = e.getComponents();
			for(int j = 0; j < components.size(); j++) {
				cep.addComponent(components.get(j));
			}
			
			connection.sendTCP(cep);
		}
	}
	
}
