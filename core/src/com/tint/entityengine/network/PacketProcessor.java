package com.tint.entityengine.network;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.GameState;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.network.packets.UpdatePacket;
import com.tint.entityengine.network.packets.UpdatePacket.EntityUpdate;
import com.tint.entityengine.server.entity.components.Networked;

public class PacketProcessor {

	private GameState gs;
	private ArrayList<UpdatePacket> updatePackets = new ArrayList<UpdatePacket>();
	
	public PacketProcessor(GameState gs) {
		this.gs = gs;
	}
	
	public void addUpdatePacket(UpdatePacket packet) {
		updatePackets.add(packet);
	}
	
	public void update() {
		Map<Long, Entity> entities = gs.getEntities();
		Entity ent = null;
		for(UpdatePacket up : updatePackets) {
			for(EntityUpdate eu : up.getEntities()) {
				ent = entities.get(eu.getID());
				PositionComponent pos = Mappers.position.get(ent);
			}
		}
	}

}
