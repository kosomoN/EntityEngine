package com.tint.entityengine.states;

import com.badlogic.gdx.Screen;
import com.tint.entityengine.Launcher;

public abstract class State implements Screen {

	Launcher launcher;
	
	public State(Launcher launcher) {
		this.launcher = launcher;
	}

	@Override
	public void render(float delta) {}

	@Override
	public void resize(int width, int height) {}

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
