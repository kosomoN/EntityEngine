package com.tint.entityengine.entity.pathfinding;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.tint.entityengine.GameMap;

public class MapField implements Field {

	public int[][] field;
	
	private MapField(int[][] field) {
		this.field = field;
	}

	/**
	 * Generates a full map field with all the static blocked tiles
	 * @param map The GameMap
	 * @return An instance of MapField
	 */
	public static MapField generate(GameMap map) {
		// Create default field
		int[][] field = new int[map.getWidth()][map.getHeight()];
		for(int i = 0; i < field.length; i++)
			for(int j = 0; j < field[i].length; j++)
				field[i][j] = 0;
		
		// Will change
		List<Vector2> checked = new ArrayList<Vector2>();
		for(int x = 0; x < field.length; x++) {
			for(int y = 0; y < field[x].length; y++) {
				if(!checked.contains(new Vector2(x, y))) {
					if(map.isBlocked(x, y, 1)) {
						for(int i = 0; i < 3; i++)
							for(int j = 0; j < 3; j++) {
								
								//If one tile offset is not blocked, set field (-10 + offset tiles * 2)
								if(!map.isBlocked(x + Math.min(i, 1), y + Math.min(j, 1), 1)) {
									field[x + i][y + j] += -10 + (i + j) * 2;
									checked.add(new Vector2(x + i, y + j));
								}
								
								if(!map.isBlocked(x - Math.min(i, 1), y - Math.min(j, 1), 1)) {
									field[x - i][y - j] += -10 + (i + j) * 2;
									checked.add(new Vector2(x - i, y - j));
								}
							}
						
						// Set blocked to a value of -100
						field[x][y] = Integer.MIN_VALUE;
					}
				}
			}
		}
		System.out.println("[MapField]: generated");
		return new MapField(field);
	}
	
	@Override
	public int getValue(int x, int y) {
		return field[x][y];
	}

	@Override
	public int getX() {
		return field.length / 2;
	}

	@Override
	public int getY() {
		return field[0].length / 2;
	}

	@Override
	public int getSize() {
		return field.length / 2;
	}

}
