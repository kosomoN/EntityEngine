package com.tint.entityengine.server.entity.components;

import com.badlogic.ashley.core.Component;
import com.tint.entityengine.server.entity.components.ai.AiController;

public class AiComponent extends Component {
	public AiController aiController;
	
	public AiComponent(AiController controller) {
		this.aiController = controller;
	}
}
