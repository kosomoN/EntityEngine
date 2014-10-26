package com.tint.entityengine.network;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.JsonSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.tint.entityengine.GameState;
import com.tint.entityengine.network.packets.Packet;

public class ClientHandler {
	private Client client;
	
	private PacketProcessor processor;

	public ClientHandler(GameState gs) {
		processor = new PacketProcessor(gs);
		
		client = new Client(8192, 2048, new JsonSerialization());
		client.start();

		try {
			client.connect(5000, "localhost", 54333, 54334);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		client.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				super.received(connection, object);
				if(object instanceof Packet) {
					processor.addUpdatePacket((Packet) object);
					return;
				}
				
				System.out.println("Unrecognized packet: " + object);
			}
		});
	}
	
	public void processPackets() {
		processor.update();
	}
}
