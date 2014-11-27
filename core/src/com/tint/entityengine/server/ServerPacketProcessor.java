package com.tint.entityengine.server;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.HealthComponent;
import com.tint.entityengine.entity.components.PositionComponent;
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
				ChatPacket cp = (ChatPacket) p;
				cp.messageSenderId = p.senderId;
				
				if(cp.message.startsWith("/"))
					processCommand(cp);
				else	
					gameServer.getServer().sendToAllExceptTCP(p.senderId, p);
			} 
		}
	}

	private void processCommand(ChatPacket cp) {
		String[] split = cp.message.split(" ");
		
		if(split[0].startsWith("/spawn")) {
			if(split.length < 2) {
				gameServer.getServer().sendToTCP(cp.senderId, new ChatPacket("Usage: spawn NAME"));
				return;
			}
			
			Entity e = EntityTemplateManager.createEntity(split[1], gameServer);
			
			//If it the entity name is invalid
			if(e == null) {
				gameServer.getServer().sendToTCP(cp.senderId, new ChatPacket("Invalid entity name"));
				return;
			}
			
			//Position it above the player
			PositionComponent playerPos = Mappers.position.get(gameServer.getClientById(cp.messageSenderId).getEntity());
			e.getComponent(PositionComponent.class).set(playerPos.getX(), playerPos.getY() + 128, gameServer.getTicks());
			
			gameServer.getEngine().addEntity(e);
			
		} else if(split[0].equalsIgnoreCase("/heal")) {
			HealthComponent playerHp = Mappers.health.get(gameServer.getClientById(cp.messageSenderId).getEntity());
			playerHp.setHp(playerHp.getMaxHp());
		}
	}

}
