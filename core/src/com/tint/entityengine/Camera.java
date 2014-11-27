package com.tint.entityengine;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class Camera {
	private static final float CAMERA_SPEED = 0.2f;
	private static OrthographicCamera orthoCam;

	public static void smoothlyMoveTo(float x, float y) {
		orthoCam.position.x += (x - orthoCam.position.x) * CAMERA_SPEED;
		orthoCam.position.y += (y - orthoCam.position.y) * CAMERA_SPEED;
	}

	public static OrthographicCamera getCamera() {
		return orthoCam;
	}

	public static void setCamera(OrthographicCamera orthographicCamera) {
		orthoCam = orthographicCamera;
	}
}
