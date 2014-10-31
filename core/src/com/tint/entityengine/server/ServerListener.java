package com.tint.entityengine.server;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.tint.entityengine.network.packets.CreateEntityPacket;
import com.tint.entityengine.network.packets.Packet;
import com.tint.entityengine.server.entity.components.NetworkComponent;
import com.tint.entityengine.server.entity.components.ServerPlayerComponent;

public class ServerListener extends Listener {

	private GameServer gameServer;
	private ServerPacketProcessor packetProcessor;

	public ServerListener(GameServer gameServer) {
		this.gameServer = gameServer;
		
		packetProcessor = new ServerPacketProcessor(gameServer);
	}
	
	@Override
	public void received(Connection connection, Object object) {
		super.received(connection, object);
		
		if(object instanceof Packet) {
			Packet p = (Packet) object;
			p.senderId = connection.getID();
			packetProcessor.addPacket(p);
		}
	}

	@Override
	public void connected(Connection connection) {
		super.connected(connection);
		
		gameServer.addClient(new ServerClient(connection, gameServer));
	}
	
	@Override
	public void disconnected(Connection connection) {
		super.disconnected(connection);
		
		gameServer.removeClient(connection);
	}

	public void processPackets() {
		packetProcessor.update();
	}
	
}
