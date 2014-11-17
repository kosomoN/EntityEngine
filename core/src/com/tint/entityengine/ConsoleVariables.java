package com.tint.entityengine;

import java.util.HashMap;
import java.util.Map;

public class ConsoleVariables {
	private static final String WRONG_FORMAT = "Wrong format: set NAME VALUE";
	private static final String VARIABLE_NOT_FOUND = "Variable not found: %s";
	private static final String WRONG_TYPE = "New value is of the wrong type, expected %s";
	private static final String VALUE_SET = "Value %s set to %s";
	
	private static Map<String, VariableListener<?>> registeredVariables = new HashMap<String, VariableListener<?>>();
	
	public static <T> void registerValue(String name, VariableListener<T> listener) {
		registeredVariables.put(name, listener);
	}
	
	public static void setCommand(String[] command, LogOutput output) {
		if(command.length < 3) {
			output.print(WRONG_FORMAT);
			return;
		}
		
		VariableListener<?> listener = registeredVariables.get(command[1]);
		
		if(listener == null) {
			output.print(String.format(VARIABLE_NOT_FOUND, command[1]));
			return;
		}
		
		Object newValue = null;
		try {
			newValue = toObject(listener.type, command[2]);
		} catch(NumberFormatException e) {
			output.print(String.format(WRONG_TYPE, listener.type.getSimpleName()));
			return;
		}
		
		if(newValue == null) {
			output.print(String.format(WRONG_TYPE, listener.type.getSimpleName()));
			return;
		}
		
		output.print(String.format(VALUE_SET, command[1], command[2]));
		listener.castValue(newValue);
	}
	
	public static Object toObject(Class<?> clazz, String value) {
		if(Byte.class 	 == clazz) return Byte.parseByte(value);
		if(Short.class   == clazz) return Short.parseShort(value);
		if(Integer.class == clazz) return Integer.parseInt(value);
		if(Long.class    == clazz) return Long.parseLong(value);
		if(Float.class   == clazz) return Float.parseFloat(value);
		if(Double.class  == clazz) return Double.parseDouble(value);
		if(Boolean.class == clazz) return Boolean.parseBoolean(value);
	    return null;
	}

	public static abstract class VariableListener<T> {
		
		private Class<T> type;
		
		public VariableListener(Class<T> type) {
			this.type = type;
		}
		
		//The value is checked before calling this
		@SuppressWarnings("unchecked")
		private void castValue(Object object) {
			valueChanged((T) object);
		}
		
		public abstract void valueChanged(T value);
	}
}
