package com.tint.entityengine.network;

import java.io.IOException;

import com.badlogic.ashley.core.ComponentMapper;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.JsonSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.tint.entityengine.GameState;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.network.packets.UpdatePacket;

public class ClientHandler {
	private Client client;
	private GameState gameState;
	
	private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);


	public ClientHandler(final GameState gameState) {
		this.gameState = gameState;
		
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
				if(object instanceof UpdatePacket) {
					UpdatePacket up = (UpdatePacket) object;
					PositionComponent p = pm.get(gameState.e);
					p.set(((PositionComponent) up.getEntityUpdates().get(0).getComponents().get(0)));
				}
			}
		});
	}
}
