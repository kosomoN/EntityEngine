package com.tint.entityengine.server;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.tint.entityengine.network.packets.ChatPacket;
import com.tint.entityengine.network.packets.InputPacket;
import com.tint.entityengine.network.packets.Packet;

public class ServerPacketProcessor {

	private GameServer gameServer;
	private Queue<Packet> packets = new ConcurrentLinkedQueue<Packet>();
	
	public ServerPacketProcessor(GameServer gameServer) {
		this.gameServer = gameServer;
	}
	
	public void addPacket(Packet packet) {
		packets.add(packet);
	}
	
	public void update() {
		Packet p = null;
		while((p = packets.poll()) != null) {
			if(p instanceof InputPacket) {
				InputPacket inputPacket = (InputPacket) p;
				synchronized (gameServer.getClients()) {
					for(ServerClient client : gameServer.getClients()) {
						if(client.getID() == p.senderId) {
							client.getPlayerComponent().inputState = inputPacket.keyBits;
						}
					}
				}
			} else if(p instanceof ChatPacket) {
				((ChatPacket) p).messageSenderId = p.senderId;
				gameServer.getServer().sendToAllExceptTCP(p.senderId, p);
			} 
		}
	}

}
