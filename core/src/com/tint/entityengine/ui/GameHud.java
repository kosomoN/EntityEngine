package com.tint.entityengine.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tint.entityengine.GameState;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.HealthComponent;
import com.tint.entityengine.entity.systems.RenderingSystem;

public class GameHud {
	
	private Stage stage;
	private GameState gs;
	private HealthBar hpBar;
	private Label hpLabel;
	private HealthComponent playerHealth;
	private Frame debugWindow;
	public ChatWidget chat;
	private Skin skin;
	
	public GameHud(SpriteBatch batch, GameState gs) {
		this.gs = gs;
		stage = new Stage(new ScreenViewport(), batch) {
			@Override
			public boolean keyUp(int keyCode) {
				if(keyCode == Keys.F1) {
					debugWindow.setVisible(!debugWindow.isVisible());
					return true;
				}
				
				return super.keyUp(keyCode);
			}
		};
		
		skin = new Skin(Gdx.files.internal("graphics/ui/EntityEngineUI.json"));
		
		// Healthbar
		hpBar = new HealthBar(skin);
		hpBar.setSize(768, 64);
		hpBar.setPosition((Gdx.graphics.getWidth() - hpBar.getWidth()) / 2, 50);
		stage.addActor(hpBar);
		
		hpLabel = new Label("HP: 0/0", skin);
		hpLabel.setPosition(hpBar.getX() + hpBar.getWidth() - 2.5f * hpLabel.getWidth(), 50 + (hpBar.getHeight() - hpLabel.getHeight()) / 2);
		stage.addActor(hpLabel);
		
		
		chat = new ChatWidget(null, stage, gs);
		chat.setSize(400, 200);
		stage.addActor(chat);
 		
		debugWindow = new Frame("Debug tools", skin);
		debugWindow.setVisible(false);
		
		final CheckBox fieldsRendering = new CheckBox("Render fields or tiles", skin);
		fieldsRendering.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RenderingSystem.renderFields = fieldsRendering.isChecked();
			}
		});
		debugWindow.addContent(fieldsRendering).align(Align.left);
		
		final CheckBox hitboxRendering = new CheckBox("Render hitboxes", skin);
		hitboxRendering.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RenderingSystem.renderHitboxes = hitboxRendering.isChecked();
			}
		});
		debugWindow.addContent(hitboxRendering).align(Align.left);
		
		final CheckBox serverPositionRendering = new CheckBox("Render server position", skin);
		serverPositionRendering.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RenderingSystem.renderServerPos = serverPositionRendering.isChecked();
			}
		});
		debugWindow.rowContent();
		debugWindow.addContent(serverPositionRendering).align(Align.left);
		
		final CheckBox healthRendering = new CheckBox("Render health", skin);
		healthRendering.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RenderingSystem.renderHealth = healthRendering.isChecked();
			}
		});
		debugWindow.rowContent();
		debugWindow.addContent(healthRendering).align(Align.left);
		
		debugWindow.pack();
		
		stage.addActor(debugWindow);
	}
	
	public void updatePlayerHealth() {
		// Uninitialised
		if(hpBar.getMaxValue() == -1) {
			playerHealth = Mappers.health.get(gs.getPlayer().getEntity());
			hpBar.setMaxValue(playerHealth.getMaxHp());
		}

		hpBar.setValue(playerHealth.getHp());
		hpLabel.setText("HP: " + playerHealth.getHp() + "/" + playerHealth.getMaxHp());
	}

	public void render() {
		updatePlayerHealth();
		stage.act();
		stage.draw();
	}

	public Stage getStage() {
		return stage;
	}

	public Skin getSkin() {
		return skin;
	}
}
