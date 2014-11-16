package com.tint.entityengine.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tint.entityengine.GameState;
import com.tint.entityengine.Launcher;
import com.tint.entityengine.Launcher.States;
import com.tint.entityengine.ui.Frame;

public class MainmenuState extends State {

	private Stage stage;
	private Frame frame;
	
	public MainmenuState(final Launcher launcher) {
		super(launcher);
		stage = new Stage(new ScreenViewport(), launcher.batch);
		
		Skin skin = ((GameState) launcher.states.get(Launcher.States.GAMESTATE)).getHud().getSkin();
		
		frame = new Frame("Survival", skin);
		frame.setMovable(false);
		
		TextButton playButton = new TextButton("Play", skin);
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				launcher.enterState(States.CONNECTIONSTATE);
			}
		});
		frame.addContent(playButton).width(150).space(8);
		
		TextButton optionsButton = new TextButton("Options", skin);
		optionsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
			}
		});
		frame.rowContent();
		frame.addContent(optionsButton).width(150).space(8);
		
		TextButton quitButton = new TextButton("Quit", skin);
		quitButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		frame.rowContent();
		frame.addContent(quitButton).width(150).space(8);
		
		frame.pack();
		stage.addActor(frame);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.getViewport().update(width, height, true);
		
		frame.setPosition(width / 2, height / 2, Align.center);
	}

	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		super.hide();
		Gdx.input.setInputProcessor(null);
	}
}
