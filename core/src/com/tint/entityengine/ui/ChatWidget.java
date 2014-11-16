package com.tint.entityengine.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.tint.entityengine.ConsoleVariables;
import com.tint.entityengine.GameState;
import com.tint.entityengine.LogOutput;
import com.tint.entityengine.network.packets.ChatPacket;

public class ChatWidget extends Table implements LogOutput {
	private TextField chatField;
	private Label chatLabel;
	private ScrollPane chatScroll;
	private GameState gameState;
	
	public ChatWidget(Skin skin, final Stage stage, final GameState gameState) {
		this.gameState = gameState;
		
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		chatLabel = new Label("", skin);
		chatLabel.setWrap(true);
		chatLabel.setAlignment(Align.bottomLeft);
		
		chatScroll = new ScrollPane(chatLabel, skin);
		chatScroll.setSmoothScrolling(false);
		chatScroll.setFadeScrollBars(false);
		chatScroll.setFlickScroll(false);
		chatScroll.setColor(1, 1, 1, 0.7f);
		add(chatScroll).expand().fill();
		
		row();
		chatField = new TextField("", skin);
		chatField.setColor(1, 1, 1, 0.7f);
		add(chatField).expandX().fillX();
		
		chatField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped(TextField textField, char c) {
				if(c == '\n' || c == '\r') {
					stage.setKeyboardFocus(null);
					
					if(chatField.getText().startsWith("/")) {
						//If the command is not recognized, send to server
						if(processCommand(chatField.getText())) {
							chatField.setText("");
							return;
						}
					} else {
						chatLabel.getText().append("\n").append("MY USERNAME").append(": ").append(chatField.getText());
					}
					
					if(!chatField.getText().trim().isEmpty()) {
						
						gameState.getClientHandler().getClient().sendTCP(new ChatPacket(chatField.getText()));
						
						chatLabel.invalidateHierarchy();
						chatLabel.layout();
						chatScroll.layout();
						chatScroll.setScrollPercentY(1);
						chatField.setText("");
					}
				}
			}
		});
		
		chatField.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				super.keyboardFocusChanged(event, actor, focused);
				if(focused)
					chatField.setColor(1, 1, 1, 1);
				else 
					chatField.setColor(1, 1, 1, 0.7f);
			}
		});
	}

	private boolean processCommand(String text) {
		String[] split = text.split(" ");
		
		if(split[0].equals("/ping")) {
			//Should be moved somewhere else, cause it won't have time to update it until the next command
			gameState.getClientHandler().getClient().updateReturnTripTime();
			
			chatLabel.getText().append("\n").append("Ping: ");
			chatLabel.getText().append(gameState.getClientHandler().getClient().getReturnTripTime()).append("ms");
			
			updateLabel();
			
			return true;
		}
		
		else if(split[0].equals("/set")) {
			ConsoleVariables.setCommand(split, this);
			return true;
		}
		
		
		return false;
	}
	
	private void updateLabel() {
		boolean scrollDown = false;
		if(chatScroll.getScrollPercentY() == 1)
			scrollDown = true;
		
		chatLabel.invalidateHierarchy();
		chatLabel.layout();
		chatScroll.layout();
		
		if(scrollDown)
			chatScroll.setScrollPercentY(1);
	}

	public void messageReceived(ChatPacket p) {
		chatLabel.getText().append("\n").append(p.messageSenderId).append(": ").append(p.message);
		updateLabel();
	}

	public TextField getTextField() {
		return chatField;
	}

	@Override
	public void print(String string) {
		chatLabel.getText().append("\n").append(string);
		updateLabel();
	}
}
