package com.tint.entityengine.network;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.minlog.Log;
import com.tint.entityengine.GameState;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.HealthComponent;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.components.RenderComponent;
import com.tint.entityengine.entity.systems.RenderingSystem;
import com.tint.entityengine.network.packets.CreateEntityPacket;
import com.tint.entityengine.network.packets.MapChunkPacket;
import com.tint.entityengine.network.packets.Packet;
import com.tint.entityengine.network.packets.RemoveEntityPacket;
import com.tint.entityengine.network.packets.UpdatePacket;
import com.tint.entityengine.network.packets.UpdatePacket.EntityUpdate;
import com.tint.entityengine.server.entity.components.Networked;

public class PacketProcessor {

	private static final float CORRECTION_THRESHOLD = 60 * 60, SMOOTH_CORRECTION_THRESHOLD = 10 * 10, SMOOTHING_SPEED = 0.04f;
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
					
						for(Networked c : eu.getComponents()) {
							if(c instanceof PositionComponent) {
								PositionComponent pos = Mappers.position.get(ent);
								
								if(eu.getID() == gs.getPlayer().serverEntityId) {
									PositionComponent newPos = (PositionComponent) c;
									
									RenderingSystem.serverPlayerPos.x = newPos.getX();
									RenderingSystem.serverPlayerPos.y = newPos.getY();
									
									//Only apply if the local position is far off
									float dx = newPos.getX() - pos.getX();
									float dy = newPos.getY() - pos.getY();
									
									float dist = dx * dx + dy * dy;
									
									if(dist > CORRECTION_THRESHOLD) {
										pos.set(newPos, gs.getTick());
										
										//Only apply smoothing if the player is moving
									} else if(dist > SMOOTH_CORRECTION_THRESHOLD && (pos.getLerpX(gs.getTick()) != pos.getX() || pos.getLerpY(gs.getTick()) != pos.getY())) {
										pos.add(dx * SMOOTHING_SPEED, dy * SMOOTHING_SPEED);
									}
								} else 
									pos.set((PositionComponent) c, gs.getTick());
							} else if(c instanceof HealthComponent) {
								HealthComponent health = Mappers.health.get(ent);
								health.setHp(((HealthComponent) c).getHp());
							} else if(c instanceof RenderComponent) {
								Mappers.render.get(ent).renderer.updatePacket(((RenderComponent) c).renderer);
							} else {
								Log.info("Unprocessed component: " + c);
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
					
					if(c instanceof RenderComponent)
						((RenderComponent) c).renderer.initialize(gs);
				}

				gs.getEngine().addEntity(e);
				
				gs.getClientHandler().entityAdded(cep.serverId, e);
			}
			
			else if(p instanceof RemoveEntityPacket) {
				RemoveEntityPacket rep = (RemoveEntityPacket) p;
				gs.getEngine().removeEntity(gs.getClientHandler().getServerIdMap().get(rep.serverId));
				gs.getClientHandler().getServerIdMap().remove(rep.serverId);
			}
			
			else if(p instanceof MapChunkPacket) {
				Log.info("Recived MapChunk");
				gs.getMap().chunkRecived((MapChunkPacket) p);
			} else {
				Log.info("Unprocessed packet: " + p);
			}
		}
		
		
	}

}
