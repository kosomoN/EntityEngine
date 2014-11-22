package com.tint.entityengine.entity.pathfinding;

public class DynamicField implements Field {

	private int x, y;
	private int[][] field;
	
	private DynamicField(int x, int y, int[][] field) {
		this.x = x;
		this.y = y;
		this.field = field;
	}
	
	/**
	 * Creates a dynamic field
	 * @param posX For position of the field in the world
	 * @param posY For position of the field in the world
	 * @param size The radius of the field
	 * @param attract Whether the field generates attraction or reflection
	 * @return An instance of DynamicField
	 */
	public static DynamicField generate(int posX, int posY, int size, boolean attract) {
		int[][] field = new int[2 * size + 1][2 * size + 1];
		
		for(int i = -size; i < size + 1; i++) {
			for(int j = -size; j < size + 1; j++) {
				if(!(i == 0 && j == 0)) {
					int dist = Math.abs(i) + Math.abs(j);
					// A parable function
					if(dist <= size)
						field[size + i][size + j] = (attract ? 2 : -1) * (size * size - dist * dist);
					else
						field[size + i][size + j] = 0;
				}
			}
		}
		
		// Set blocked to mininum value
		field[size][size] = Integer.MIN_VALUE;

		return new DynamicField(posX, posY, field);
	}
	
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	@Override
	public int getSize() {
		return field.length / 2;
	}
	
	@Override
	public int getValue(int x, int y) {
		return field[x][y];
	}
}
