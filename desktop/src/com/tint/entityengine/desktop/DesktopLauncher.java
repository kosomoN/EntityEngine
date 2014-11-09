package com.tint.entityengine.desktop;

import com.tint.entityengine.Launcher;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Settings s = new Settings();
		s.maxWidth = 2048;
		s.maxHeight = 2048;
		s.paddingX = 0;
		s.paddingY = 0;
		TexturePacker.process(s, "unpacked/entities", "graphics/entities", "Entities");
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.vSyncEnabled = false;
		//config.foregroundFPS = 0;
		new LwjglApplication(new Launcher(), config);
	}
}
