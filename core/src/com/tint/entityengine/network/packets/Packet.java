package com.tint.entityengine.network.packets;

import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.server.entity.components.NetworkComponent;

public abstract class Packet {
	public transient int senderId = -1;
	
	public static void register(Kryo kryo) {
		kryo.register(UpdatePacket.class);
		kryo.register(UpdatePacket.EntityUpdate.class);
		kryo.register(CreateEntityPacket.class);
		kryo.register(InputPacket.class);
		
		kryo.register(PositionComponent.class);
		kryo.register(NetworkComponent.class);
		
		kryo.register(ArrayList.class);
	}
}
