package com.tint.entityengine;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.tint.entityengine.network.PacketProcessor;
import com.tint.entityengine.network.packets.Packet;

public class ClientListener extends Listener {
	
	private PacketProcessor processor;
	
	public ClientListener(PacketProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void received(Connection connection, Object object) {
		super.received(connection, object);
		if(object instanceof Packet) {
			processor.addPacket((Packet) object);
			return;
		}
		
		System.out.println("Unrecognized packet: " + object);
	}
}
