package com.tint.entityengine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tint.entityengine.network.packets.MapChunkPacket;

public class GameMap {
	
	public static final int TILE_SIZE = 32;
	public static final int CHUNK_SIZE = 16;
	public static final int LAYERS = 2;
	
	private int width, height;
	private short[][][] tiles;
	private TextureRegion[] tileset;
	
	public GameMap(int width, int height, String tilesetPath) {
		this.width = width;
		this.height = height;
		tiles = new short[LAYERS][width][height];
		
		if(tilesetPath != null) {
			Texture tilesetTex = new Texture(Gdx.files.internal(tilesetPath));
			
			int tilesetWidth = tilesetTex.getWidth() / TILE_SIZE;
			int tilesetHeight = tilesetTex.getHeight() / TILE_SIZE;
			
			tileset = new TextureRegion[tilesetWidth * tilesetHeight];
			
			for(int i = 0; i < tilesetWidth; i++) {
				for(int j = 0; j < tilesetHeight; j++) {
					tileset[j * tilesetWidth + i] = new TextureRegion(tilesetTex, i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
			}
		}
		
		for(int l = 0; l < LAYERS; l++) {
			for(int i = 0; i < width; i++) {
				for(int j = 0; j < height; j++) {
					tiles[l][i][j] = 162;
				}
			}
		}
	}
	
	public void render(SpriteBatch batch) {
		//Lower left
		int startX = (int) ((Camera.orthoCam.position.x - Camera.orthoCam.viewportWidth / 2) / TILE_SIZE);
		if(startX < 0) startX = 0;
		else if(startX >= width) startX = width - 1;
		
		int startY = (int) ((Camera.orthoCam.position.y - Camera.orthoCam.viewportHeight / 2) / TILE_SIZE);
		if(startY < 0) startY = 0;
		else if(startY >= height) startY = height - 1;
		
		//Upper right
		int endX = (int) (startX + Math.ceil(Camera.orthoCam.viewportWidth / TILE_SIZE) + 1);
		if(endX < 0) endX = 0;
		else if(endX >= width) endX = width - 1;
		
		int endY = (int) (startY + Math.ceil(Camera.orthoCam.viewportHeight / TILE_SIZE) + 1);
		if(endY < 0) endY = 0;
		else if(endY >= height) endY = height - 1;
		
		for(int l = 0; l < LAYERS; l++) {
			for(int i = startX; i < endX; i++) {
				for(int j = startY; j < endY; j++) {
					if(tiles[l][i][j] != -1)
						batch.draw(tileset[tiles[l][i][j]], i * TILE_SIZE, j * TILE_SIZE);
				}
			}
		}
		
	}

	public void chunkRecived(MapChunkPacket mcp) {
		for(int i = 0; i < mcp.tiles.length; i++) {
			for(int j = 0; j < mcp.tiles[0].length; j++) {
				tiles[mcp.layer][mcp.startX + i][mcp.startY + j] = mcp.tiles[i][j];
			}
		}
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return width;
	}
	
	public boolean isOnMap(float x, float y) {
		return (x >= 0 && y >= 0 && x < getWidth() && y < getHeight());
	}
	
	public short getTile(int x, int y, int layer) {
		return tiles[layer][x][y];
	}

	public void setTile(int x, int y, int layer, short tile) {
		tiles[layer][x][y] = tile;
	}
}
