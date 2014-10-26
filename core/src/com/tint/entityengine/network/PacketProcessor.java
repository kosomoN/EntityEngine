package com.tint.entityengine.network;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.GameState;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.network.packets.CreateEntityPacket;
import com.tint.entityengine.network.packets.Packet;
import com.tint.entityengine.network.packets.UpdatePacket;
import com.tint.entityengine.network.packets.UpdatePacket.EntityUpdate;
import com.tint.entityengine.server.entity.components.Networked;

public class PacketProcessor {

	private GameState gs;
	private Queue<Packet> packets = new ConcurrentLinkedQueue<Packet>();
	
	public PacketProcessor(GameState gs) {
		this.gs = gs;
	}
	
	public void addUpdatePacket(Packet packet) {
		packets.add(packet);
	}
	
	public void update() {
		Map<Long, Entity> entities = gs.getClientHandler().getServerIdMap();
		Entity ent = null;
		Packet p = null;
		while((p = packets.poll()) != null) {
			if(p instanceof UpdatePacket) {
				UpdatePacket up = (UpdatePacket) p;
				for(EntityUpdate eu : up.getEntityUpdates()) {
					ent = entities.get(eu.getID());
					if(ent == null) {
						System.err.println("Entity does not exist! ID: " + eu.getID());
						continue;
					}
				
					for(Networked c : eu.getComponents()) {
						if(c instanceof PositionComponent) {
							PositionComponent pos = Mappers.position.get(ent);
							pos.updateOld();
							pos.set((PositionComponent) c);
						}
					}
				}
			} 
			
			else if(p instanceof CreateEntityPacket) {
				CreateEntityPacket cep = (CreateEntityPacket) p;
				Entity e = new Entity();
				for(Component c : cep.getComponents()) {
					e.add(c);
				}
				gs.getEngine().addEntity(e);
				
				gs.getClientHandler().entityAdded(cep.getServerId(), e);
			}
		}
	}

}
