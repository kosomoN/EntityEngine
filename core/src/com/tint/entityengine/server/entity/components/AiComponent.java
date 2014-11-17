package com.tint.entityengine.server.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.server.GameServer;
import com.tint.entityengine.server.entity.components.ai.AiController;

public class AiComponent extends Component implements Initializable {
	public AiController aiController;
	
	@Override
	public void init(GameServer gameServer, Entity entity) {
		aiController.init(gameServer, entity);
	}
}
