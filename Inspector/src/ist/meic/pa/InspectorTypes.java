package ist.meic.pa;

import java.util.HashMap;

interface InspectorTypes {
	Object returnType(Class<?> paramType, String parameter);
}

class InspectorByte implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		if(isByte(paramType)) {
			return Byte.valueOf(parameter);
		}
		return null;
	}
	
	/* Returns true if paramType is of class byte */
	public boolean isByte(Class<?> paramType) {
		if(paramType.equals(byte.class))
			return true;
		return false;
	}
}
	
class InspectorShort implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		// Type short
		if(isShort(paramType)) {
			return Short.parseShort(parameter);
		}
		return null;
	}
	
	/* Returns true if paramType is of class int */
	public boolean isShort(Class<?> paramType) {
		if(paramType.equals(short.class))
			return true;
		return false;
	}
}

class InspectorInt implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		// Type int
		if(isInt(paramType)) {
			return Integer.parseInt(parameter);
		}
		return null;
	}
	
	/* Returns true if paramType is of class int */
	public boolean isInt(Class<?> paramType) {
		if(paramType.equals(int.class))
			return true;
		return false;
	}
}

class InspectorLong implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		// Type long
		if(isLong(paramType, parameter)) {
			return Long.valueOf(parameter.toLowerCase().replace("l", "")).longValue();
		}
		return null;
	}
	
	/* Returns true if paramType is of type long and if parameter ends with l or L */
	public boolean isLong(Class<?> paramType, String parameter) {
		if(paramType.equals(long.class) || parameter.toLowerCase().endsWith("l"))
			return true;
		return false;
	}
}

class InspectorFloat implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		// Type float
		if(isFloat(paramType, parameter)) {
			return Float.valueOf(parameter.toLowerCase().replace("f", "")).floatValue();
		}
		return null;
	}
	
	/* Returns true if paramType is of type long and if parameter ends with l or L */
	public boolean isFloat(Class<?> paramType, String parameter) {
		if(paramType.equals(float.class) || parameter.toLowerCase().endsWith("f"))
			return true;
		return false;
	}
	
}

class InspectorDouble implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		// Type double
		if(isDouble(paramType, parameter)) {
			return Double.valueOf(parameter.toLowerCase().replace("d", "")).doubleValue();
		}
		return null;
	}
	
	/* Returns true if paramType is of type long and if parameter ends with l or L */
	public boolean isDouble(Class<?> paramType, String parameter) {
		if(paramType.equals(double.class) || parameter.toLowerCase().endsWith("d"))
			return true;
		return false;
	}
}

class InspectorBoolean implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		// Type boolean
		if(isBoolean(paramType, parameter)) {
			return isBooleanTrue(parameter);
		}
		return null;
	}
	
	/* Returns true if paramType is of class boolean and parameter is true or false */
	public boolean isBoolean(Class<?> paramType, String parameter) {
		if(paramType.equals(boolean.class) && 
		  (parameter.toLowerCase().equals("true") 
		   || parameter.toLowerCase().equals("false")))
			return true;
		return false;
	}
	
	/* Returns true if has value true, false otherwise */
	public boolean isBooleanTrue(String parameter) {
		if(parameter.toLowerCase().equals("true"))
			return true;
		return false;
	}
}

class InspectorChar implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		if(isChar(paramType, parameter)) {
			return parameter.replace("\'", "").charAt(0);
		}
		return null;
	}
		
	/** Checks if paramType is of class char and if starts and ends with ' */
	public boolean isChar(Class<?> paramType, String parameter) {
		if(paramType.equals(char.class) && 
		   parameter.startsWith("\'") && 
		   parameter.endsWith("\'")) {
			return true;
		}
		return false;
	}
}

class InspectorString implements InspectorTypes {
	// Type String
	public Object returnType(Class<?> paramType, String parameter) {
		if(isString(paramType, parameter)) {
			return (String)parameter.replace("\"", "");
		}
		return null;
	}
	
	/** Checks if paramType is of class String and if starts and ends with quotation marks */
	public boolean isString(Class<?> paramType, String parameter) {
		if(paramType.equals(String.class) && 
		   parameter.startsWith("\"") && 
		   parameter.endsWith("\"")) {
			return true;
		}
		return false;
	}
}

class InspectorInteger implements InspectorTypes {
	public Object returnType(Class<?> paramType, String parameter) {
		if(isInteger(paramType)) {
			return Integer.parseInt(parameter);
		}
		return null;
	}
	
	/* Returns true if paramType is of class int */
	public boolean isInteger(Class<?> paramType) {
		if(paramType.equals(Integer.class))
			return true;
		return false;
	}
}

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
		inspectorTypes.put("string", new InspectorString());
	}
	
	public Object getTypeValue(Class<?> paramType, String parameter) {
		return inspectorTypes.get(paramType.toString()).returnType(paramType, parameter);
	}
}