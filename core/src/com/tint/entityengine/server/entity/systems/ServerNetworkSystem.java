package com.tint.entityengine.server.entity.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.tint.entityengine.network.packets.UpdatePacket;
import com.tint.entityengine.network.packets.UpdatePacket.EntityUpdate;
import com.tint.entityengine.server.GameServer;
import com.tint.entityengine.server.entity.components.NetworkComponent;
import com.tint.entityengine.server.entity.components.Networked;

public class ServerNetworkSystem extends IteratingSystem {

	private int updateRate = 1;
	
	private int ticksSinceUpdate = 0;
	private UpdatePacket updatePacket;
	private GameServer server;
	
	public ServerNetworkSystem(GameServer server) {
		super(Family.getFor(NetworkComponent.class), Integer.MAX_VALUE);
		this.server = server;
		updatePacket = new UpdatePacket();
		
	}
	
	@Override
	public void update(float deltaTime) {
		if(ticksSinceUpdate >= updateRate) {
			ticksSinceUpdate = 0;
			
			updatePacket.clear();
			super.update(deltaTime);
			
			if(!updatePacket.getEntityUpdates().isEmpty()) {
				//Don't log updatePackets
				server.jsonSerialization.setLogging(false, true);
				updatePacket.tick = server.getTicks();
				server.sendToAllConnectedUDP(updatePacket);
				//server.jsonSerialization.setLogging(true, true);
			}
		}
		
		ticksSinceUpdate++;
	}

	@Override
	protected void processEntity(Entity entity, float delta) {
		ImmutableArray<Component> components = entity.getComponents();
		EntityUpdate eu = null;
		for(int i = 0; i < components.size(); i++) {
			Component c = components.get(i);
			
			if(c instanceof Networked) {
				Networked n = (Networked) c;
				
				if(n.hasChanged()) {
					n.resetChanged();
					if(eu == null) {
						eu = new EntityUpdate(entity.getId());
						updatePacket.addEntityUpdate(eu);
					}
					
					eu.addComponent(n);
				}
			}
		}
	}

}
