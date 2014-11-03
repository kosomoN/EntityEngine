package com.tint.entityengine.network;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.kryonet.Client;
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
		
	}
	
	public void entityAdded(long serverId, Entity e) {
		serverIds.put(serverId, e);
	}
	
	public void processPackets() {
		processor.update();
	}
	
	public PacketProcessor getPacketProcessor() {
		return processor;
	}

	public Map<Long, Entity> getServerIdMap() {
		return serverIds;
	}
	
	public Client getClient() {
		return client;
	}
}
