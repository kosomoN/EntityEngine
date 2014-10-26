package com.tint.entityengine.server;

import com.esotericsoftware.kryonet.Listener;

public class ServerListener extends Listener {

	private GameServer gameServer;

	public ServerListener(GameServer gameServer) {
		this.gameServer = gameServer;
	}
}
