package com.tint.entityengine.entity;

import java.util.ArrayList;
import java.util.List;

import com.tint.entityengine.GameMap;
import com.tint.entityengine.entity.pathfinding.Field;

public class FieldProcessor {

	List<Field> fields = new ArrayList<Field>();
	
	public FieldProcessor(GameMap map) {
		
	}
	
	public void addField(Field field) {
		fields.add(field);
	}
	
	public void removeField(Field field) {
		fields.remove(field);
	}
	
	public List<Field> getFields() {
		return fields;
	}
	
	public int getValue(int x, int y) {
		int sumValue = 0;
		for(Field field : fields) {
			int i = x - field.getX() + field.getSize();
			int j = y - field.getY() + field.getSize();

			// Return the biggest value in that square
			if(i >= 0 && i < field.getSize() * 2 && j >= 0 && j < field.getSize() * 2) {
				int tempValue = field.getValue(i, j);
				if(sumValue < tempValue)
					sumValue = tempValue;
			} else
				return 0;
		}
		return sumValue;
	}
}
