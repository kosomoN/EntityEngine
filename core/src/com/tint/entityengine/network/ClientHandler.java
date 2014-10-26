package com.tint.entityengine.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.JsonSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.tint.entityengine.GameState;
import com.tint.entityengine.network.packets.Packet;

public class ClientHandler {
	private Client client;
	private Map<Long, Entity> serverIds = new HashMap<Long, Entity>();
	
	private PacketProcessor processor;

	public ClientHandler(GameState gs) {
		processor = new PacketProcessor(gs);
		
		client = new Client();
		Packet.register(client.getKryo());
		client.start();

		try {
			client.connect(5000, "localhost", 54333, 54334);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		client.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				super.received(connection, object);
				if(object instanceof Packet) {
					processor.addUpdatePacket((Packet) object);
					return;
				}
				
				System.out.println("Unrecognized packet: " + object);
			}
		});
	}
	
	public void entityAdded(long serverId, Entity e) {
		serverIds.put(serverId, e);
	}
	
	public void processPackets() {
		processor.update();
	}

	public Map<Long, Entity> getServerIdMap() {
		return serverIds;
	}
}
