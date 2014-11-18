package com.tint.entityengine.server;


public class ServerLauncher {

    public static void main(String[] args) {

	    System.out.println("Server starting up");
	    GameServer gameServer = new GameServer();
	    gameServer.start();
	    
	    /*
    	try {
	    	String[] files = new File("server/entities/").list();
	    	
	    	Json json = new Json();
	    	Kryo kryo = new Kryo();
	    	
	    	SerializedComponents[] entityTemplates = new SerializedComponents[files.length];
	    	for(int i = 0; i < files.length; i++) {
	    		entityTemplates[i] = json.fromJson(SerializedComponents.class, new FileInputStream(new File("server/entities/" + files[i])));
	    	}
			
			long time = System.currentTimeMillis();
			for(int i = 0; i < 100000; i++) {
				
				SerializedComponents components = kryo.copy(entityTemplates[i % 3]);
				
				Entity e = new Entity();
				
				for(Component comp : components.components) {
					e.add(comp);
				}
			}
			
			System.out.println((System.currentTimeMillis() - time) + "ms");
			
			//4640ms
			//420ms
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}*/
    }
}
