package com.tint.entityengine;

import com.badlogic.gdx.Game;

public class Launcher extends Game {

	@Override
	public void create () {
        setScreen(new GameState());
    }
}
