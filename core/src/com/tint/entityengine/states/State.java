package com.tint.entityengine.states;

import com.badlogic.gdx.Screen;
import com.tint.entityengine.Camera;
import com.tint.entityengine.Launcher;

public abstract class State implements Screen {

	protected Launcher launcher;
	
	public State(Launcher launcher) {
		this.launcher = launcher;
	}

	@Override
	public void render(float delta) {}

	@Override
	public void resize(int width, int height) {
		Camera.getCamera().viewportWidth = width / 2;
		Camera.getCamera().viewportHeight = height / 2;
		Camera.getCamera().update();
	}

	@Override
	public void show() {}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}

}
