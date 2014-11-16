package com.tint.entityengine.states;

import java.io.IOException;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.tint.entityengine.ClientListener;
import com.tint.entityengine.ClientPlayer;
import com.tint.entityengine.GameState;
import com.tint.entityengine.Launcher;
import com.tint.entityengine.Launcher.States;
import com.tint.entityengine.entity.components.RenderComponent;
import com.tint.entityengine.network.packets.ConnectionApprovedPacket;
import com.tint.entityengine.network.packets.CreateEntityPacket;

public class ConnectionState extends State {
	
	private GameState gs;
	private boolean enterGame = false;
	private Listener listener;

	public ConnectionState(Launcher launcher) {
		super(launcher);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		
		if(enterGame) {
			launcher.enterState(States.GAMESTATE);
		}
	}

	@Override
	public void show() {
		super.show();
		
		gs = (GameState) launcher.states.get(States.GAMESTATE);
		
		try {
			connect();
		} catch (IOException e) {
			e.printStackTrace();
			launcher.enterState(States.MAINMENUSTATE);
		}
	}

	public void connect() throws IOException {
		Client client = gs.getClientHandler().getClient();
		client.start();

		client.connect(5000, "localhost", 54333, 54333);
		
		
		listener = new Listener() {

			@Override
			public void received(Connection connection, Object object) {
				super.received(connection, object);
				
				if(object instanceof ConnectionApprovedPacket) {
					ConnectionApprovedPacket cap = (ConnectionApprovedPacket) object;
					
					gs.player = new ClientPlayer(gs);
					gs.player.serverEntityId = cap.playerEntityId;
					gs.player.setPlayerEntity(gs.getClientHandler().getServerIdMap().get(cap.playerEntityId));
					
					gs.getClientHandler().getClient().removeListener(listener);
					gs.getClientHandler().getClient().addListener(new ClientListener(gs.getClientHandler().getPacketProcessor()));
					
					enterGame = true;
				} 
				
				else if(object instanceof CreateEntityPacket)  {
					CreateEntityPacket cep = (CreateEntityPacket) object;
					
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
			}
		};
		
		client.addListener(listener);
	}
}
