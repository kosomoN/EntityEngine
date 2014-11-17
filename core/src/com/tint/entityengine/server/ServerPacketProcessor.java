package com.tint.entityengine.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.jsonbeans.Json;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.network.packets.ChatPacket;
import com.tint.entityengine.network.packets.InputPacket;
import com.tint.entityengine.network.packets.Packet;
import com.tint.entityengine.server.entity.components.Initializable;
import com.tint.entityengine.server.entity.components.Networked;

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
			
			//TODO Add entity loading system
			Json json = new Json();
			try {
				SerializedComponents components = json.fromJson(SerializedComponents.class, new FileInputStream(new File("server/entities/" + split[1] + ".json")));
				
				Entity e = new Entity();
				
				for(Component comp : components.components) {
					if(comp instanceof Initializable)
						((Initializable) comp).init(gameServer, e);
					e.add(comp);
				}
				
				PositionComponent playerPos = Mappers.position.get(gameServer.getClientById(cp.messageSenderId).getEntity());
				e.getComponent(PositionComponent.class).set(playerPos.getX(), playerPos.getY() + 32, gameServer.getTicks());
				
				gameServer.getEngine().addEntity(e);
			} catch (Exception e) {
				e.printStackTrace();
				gameServer.getServer().sendToTCP(cp.senderId, new ChatPacket("Failed to spawn entity"));
				return;
			}
		}
	}

}
