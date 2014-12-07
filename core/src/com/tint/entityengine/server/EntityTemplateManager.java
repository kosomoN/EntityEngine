package com.tint.entityengine.server;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonReader;
import com.esotericsoftware.jsonbeans.JsonValue;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.minlog.Log;
import com.tint.entityengine.server.entity.components.Initializable;
import com.tint.entityengine.server.entity.systems.EntitySpawnSystem;

public class EntityTemplateManager {
	
	private static final String COMPONENT_PACKAGE = "com.tint.entityengine.entity.components";
	private static final String SERVER_COMPONENT_PACKAGE = "com.tint.entityengine.server.entity.components";
	
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
    			//Load the json file
    			JsonReader reader = new JsonReader();
    			JsonValue jsonValue = reader.parse(new File("server/entities/" + name));
    			
    			//Add full package declarations
				addFullPackage(jsonValue.get("components"), name.replace(".json", ""));
				EntitySpawnSystem.registerEntityType(name.replace(".json", ""));
    			
    			//De-serialize
    			EntityTemplate template = json.fromJson(EntityTemplate.class, jsonValue.toString());
    			
    			//Remove file extension and add to the map
				templates.put(name.substring(0, name.lastIndexOf('.')), template);
				
    		} catch (Exception e) {
    			System.err.println("Failed to load entity: " + name);
    			e.printStackTrace();
    		}
		}
    	
    	Log.info(templates.values().size() + " entities loaded");
	}
	
	private static void addFullPackage(JsonValue jsonValueArray, String filename) {
		for(JsonValue component : jsonValueArray) {
			
			//If a nested array was found, check it too
			if(component.isObject() || component.isArray()) {
				addFullPackage(component, filename);
				continue;
			}

			if(component.name.equals("class")) {
				String className = component.asString();
		
				//If the class name doesn't contain the package
				if(!className.contains(".")) {
					component.set(COMPONENT_PACKAGE + "." + className);
				} else if(className.startsWith(".")) {
					component.set(COMPONENT_PACKAGE + className);
				} else if(className.startsWith("$SERVER")) {
					component.set(SERVER_COMPONENT_PACKAGE + className.substring("$SERVER".length(), className.length()));
					
					if(className.contains("AiComponent"))
						EntitySpawnSystem.registerEntityType("Enemy " + filename);
				}
			}
		}
	}

	private static class EntityTemplate {
		public ArrayList<Component> components;
	}
}
