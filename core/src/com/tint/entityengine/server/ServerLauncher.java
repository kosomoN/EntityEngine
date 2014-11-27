package com.tint.entityengine.server;

public class ServerLauncher {

    public static void main(String[] args) {

	    System.out.println("Server starting up");
	    GameServer gameServer = new GameServer();
	    gameServer.start();
    }
}
