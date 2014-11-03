package com.tint.entityengine.network;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.minlog.Log;
import com.tint.entityengine.GameState;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.network.packets.CreateEntityPacket;
import com.tint.entityengine.network.packets.Packet;
import com.tint.entityengine.network.packets.RemoveEntityPacket;
import com.tint.entityengine.network.packets.UpdatePacket;
import com.tint.entityengine.network.packets.UpdatePacket.EntityUpdate;
import com.tint.entityengine.server.entity.components.Networked;

public class PacketProcessor {

	private static final float CORRECTION_THRESHOLD = 60 * 60;
	private GameState gs;
	private Queue<Packet> packets = new ConcurrentLinkedQueue<Packet>();
	private int lastUpdatePacketTick = -1;
	
	public PacketProcessor(GameState gs) {
		this.gs = gs;
	}
	
	public void addPacket(Packet packet) {
		packets.add(packet);
	}
	
	public void update() {
		Map<Long, Entity> entitiesMap = gs.getClientHandler().getServerIdMap();
		Entity ent = null;
		Packet p = null;
		while((p = packets.poll()) != null) {
			if(p instanceof UpdatePacket) {
				
				
				UpdatePacket up = (UpdatePacket) p;
				if(up.tick > lastUpdatePacketTick) {
					lastUpdatePacketTick = up.tick;
					
					for(EntityUpdate eu : up.getEntityUpdates()) {
						ent = entitiesMap.get(eu.getID());
						if(ent == null) {
							System.err.println("Entity does not exist! ID: " + eu.getID());
							continue;
						}
					
						//If it's the player
						if(eu.getID() == gs.getPlayer().serverEntityId) {
							for(Networked c : eu.getComponents()) {
								if(c instanceof PositionComponent) {
									PositionComponent newPos = (PositionComponent) c;
									PositionComponent pos = Mappers.position.get(ent);
									
									//Only apply if the local position is far off
									float dx = newPos.getX() - pos.getX();
									float dy = newPos.getY() - pos.getY();
									
									if(dx * dx + dy * dy > CORRECTION_THRESHOLD) {
										pos.set(newPos, gs.getTick());
									}
								}
							}
							
							continue;
						}
						
						for(Networked c : eu.getComponents()) {
							if(c instanceof PositionComponent) {
								PositionComponent pos = Mappers.position.get(ent);
								pos.set((PositionComponent) c, gs.getTick());
							}
						}
					}
				}
			} 
			
			else if(p instanceof CreateEntityPacket) {
				CreateEntityPacket cep = (CreateEntityPacket) p;
				
				Log.info("Creating entity with ID: " + cep.serverId);
				
				Entity e = new Entity();
				for(Component c : cep.getComponents()) {
					e.add(c);
				}
				gs.getEngine().addEntity(e);
				
				gs.getClientHandler().entityAdded(cep.serverId, e);
			}
			
			else if(p instanceof RemoveEntityPacket) {
				gs.getEngine().removeEntity(gs.getClientHandler().getServerIdMap().get(((RemoveEntityPacket) p).serverId));
			}
		}
	}

}
