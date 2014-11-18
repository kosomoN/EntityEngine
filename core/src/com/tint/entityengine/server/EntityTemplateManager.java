package com.tint.entityengine.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.minlog.Log;
import com.tint.entityengine.server.entity.components.Initializable;

public class EntityTemplateManager {
	
	private static Map<String, EntityTemplate> templates = new HashMap<String, EntityTemplate>();
	
	public static Entity createEntity(String entityName, GameServer gameServer) {
		Entity e = new Entity();
		
		EntityTemplate template = templates.get(entityName);
		
		if(template == null)
			return null;
		
		//Copy it to create new objects
		EntityTemplate components = gameServer.getServer().getKryo().copy(template);
		
		//Initialize and add them
		for(Component comp : components.components) {
			if(comp instanceof Initializable)
				((Initializable) comp).init(gameServer, e);
			e.add(comp);
		}
		
		return e;
	}
	
	public static void loadEntityTemplates(Kryo kryo) {
	
		kryo.register(EntityTemplate.class);
		
		//Only accept json
		String[] files = new File("server/entities/").list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
		});
    	
    	Json json = new Json();
    	
    	for(int i = 0; i < files.length; i++) {
    		String name = files[i];
    		try {
    			
    			EntityTemplate template = json.fromJson(EntityTemplate.class, new FileInputStream(new File("server/entities/" + name)));
    			
    			//Remove file extension and add to the map
				templates.put(name.substring(0, name.lastIndexOf('.')), template);
				
    		} catch (Exception e) {
    			System.err.println("Failed to load entity: " + name);
    			e.printStackTrace();
    		}
		}
    	
    	Log.info(templates.values().size() + " entities loaded");
	}

	private static class EntityTemplate {
		public ArrayList<Component> components;
	}
}
