package com.tint.entityengine.network.packets;

import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.tint.entityengine.entity.components.HealthComponent;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.components.RenderComponent;
import com.tint.entityengine.entity.components.renderers.DirectionalRenderer;
import com.tint.entityengine.server.entity.components.NetworkComponent;

public abstract class Packet {
	public transient int senderId = -1;
	
	public static void register(Kryo kryo) {
		kryo.register(UpdatePacket.class);
		kryo.register(UpdatePacket.EntityUpdate.class);
		kryo.register(CreateEntityPacket.class);
		kryo.register(RemoveEntityPacket.class);
		kryo.register(InputPacket.class);
		kryo.register(ConnectionApprovedPacket.class);
		kryo.register(MapChunkPacket.class);
		
		kryo.register(RenderComponent.class);
		kryo.register(DirectionalRenderer.class);
		
		kryo.register(short[].class);
		kryo.register(short[][].class);
		
		kryo.register(PositionComponent.class);
		kryo.register(HealthComponent.class);
		kryo.register(NetworkComponent.class);
		
		kryo.register(ArrayList.class);
	}
}
