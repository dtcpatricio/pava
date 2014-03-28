package ist.meic.pa;

import java.util.HashMap;

interface InspectorTypes {
	Object returnType(Class<?> paramType, String parameter);
}

/**
 * Type byte
 */
class InspectorByte implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		if(isByte(paramType)) {
			return Byte.valueOf(parameter);
		}
		return null;
	}
	
	/* Returns true if paramType is of class byte */
	private boolean isByte(Class<?> paramType) {
		if(paramType.equals(byte.class))
			return true;
		return false;
	}
}
	
/**
 * Type short
 */
class InspectorShort implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		if(isShort(paramType)) {
			return Short.parseShort(parameter);
		}
		return null;
	}
	
	/* Returns true if paramType is of class short */
	private boolean isShort(Class<?> paramType) {
		if(paramType.equals(short.class))
			return true;
		return false;
	}
}

/**
 * Type int
 */
class InspectorInt implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		if(isInt(paramType)) {
			return Integer.parseInt(parameter);
		}
		return null;
	}
	
	/* Returns true if paramType is of class int */
	private boolean isInt(Class<?> paramType) {
		if(paramType.equals(int.class))
			return true;
		return false;
	}
}

/**
 * Type long
 */
class InspectorLong implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		if(isLong(paramType, parameter)) {
			return Long.valueOf(parameter.toLowerCase().replace("l", "")).longValue();
		}
		return null;
	}
	
	/* Returns true if paramType is of type long and if parameter ends with l or L */
	private boolean isLong(Class<?> paramType, String parameter) {
		if(paramType.equals(long.class) || parameter.toLowerCase().endsWith("l"))
			return true;
		return false;
	}
}

/**
 * Type float
 */
class InspectorFloat implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		if(isFloat(paramType, parameter)) {
			return Float.valueOf(parameter.toLowerCase().replace("f", "")).floatValue();
		}
		return null;
	}
	
	/* Returns true if paramType is of type long and if parameter ends with f or F */
	private boolean isFloat(Class<?> paramType, String parameter) {
		if(paramType.equals(float.class) || parameter.toLowerCase().endsWith("f"))
			return true;
		return false;
	}
	
}

/**
 * Type double
 */
class InspectorDouble implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		// Type double
		if(isDouble(paramType, parameter)) {
			return Double.valueOf(parameter.toLowerCase().replace("d", "")).doubleValue();
		}
		return null;
	}
	
	/* Returns true if paramType is of type long and if parameter ends with d or D */
	private boolean isDouble(Class<?> paramType, String parameter) {
		if(paramType.equals(double.class) || parameter.toLowerCase().endsWith("d"))
			return true;
		return false;
	}
}

/**
 * Type boolean
 */
class InspectorBoolean implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		// Type boolean
		if(isBoolean(paramType, parameter)) {
			return isBooleanTrue(parameter);
		}
		return null;
	}
	
	/* Returns true if paramType is of class boolean and parameter is true or false */
	private boolean isBoolean(Class<?> paramType, String parameter) {
		if(paramType.equals(boolean.class) && 
		  (parameter.toLowerCase().equals("true") 
		   || parameter.toLowerCase().equals("false")))
			return true;
		return false;
	}
	
	/* Returns true if has value true, false otherwise */
	private boolean isBooleanTrue(String parameter) {
		if(parameter.toLowerCase().equals("true"))
			return true;
		return false;
	}
}

/**
 * Type char
 */
class InspectorChar implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		if(isChar(paramType, parameter)) {
			return parameter.replace("\'", "").charAt(0);
		}
		return null;
	}
		
	/* Returns true if paramType is of class char and if starts and ends with ' */
	private boolean isChar(Class<?> paramType, String parameter) {
		if(paramType.equals(char.class) && 
		   parameter.startsWith("\'") && 
		   parameter.endsWith("\'")) {
			return true;
		}
		return false;
	}
}

/**
 * Type String
 */
class InspectorString implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		if(isString(paramType, parameter)) {
			return (String)parameter.replace("\"", "");
		}
		return null;
	}
	
	/* Returns true if paramType is of class String and if starts and ends with quotation marks */
	private boolean isString(Class<?> paramType, String parameter) {
		if(paramType.equals(String.class) && 
		   parameter.startsWith("\"") && 
		   parameter.endsWith("\"")) {
			return true;
		}
		return false;
	}
}

/**
 * Type Integer
 */
class InspectorInteger implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		if(isInteger(paramType)) {
			return Integer.parseInt(parameter);
		}
		return null;
	}
	
	/* Returns true if paramType is of class int */
	private boolean isInteger(Class<?> paramType) {
		if(paramType.equals(Integer.class))
			return true;
		return false;
	}
}

/**
 * Creates a hash map that stores all available types
 * and the respective class for the type conversion
 */
class InspectorTypesFactory {
	HashMap<String, InspectorTypes> inspectorTypes;
	
	public InspectorTypesFactory() {
		inspectorTypes = new HashMap<String, InspectorTypes>();
		inspectorTypes.put("byte", new InspectorByte());
		inspectorTypes.put("short", new InspectorShort());
		inspectorTypes.put("int", new InspectorInt());
		inspectorTypes.put("long", new InspectorLong());
		inspectorTypes.put("float", new InspectorFloat());
		inspectorTypes.put("double", new InspectorDouble());
		inspectorTypes.put("boolean", new InspectorBoolean());
		inspectorTypes.put("char", new InspectorChar());
		inspectorTypes.put("class java.lang.String", new InspectorString());
		inspectorTypes.put("class java.lang.Integer", new InspectorInteger());
	}
	
	public Object getTypeValue(Class<?> paramType, String parameter) {
		return inspectorTypes.get(paramType.toString()).returnType(paramType, parameter);
	}
}
