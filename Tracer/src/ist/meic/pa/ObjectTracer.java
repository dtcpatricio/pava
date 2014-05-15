package ist.meic.pa;

import java.util.IdentityHashMap;

public class ObjectTracer {

	private static IdentityHashMap<Object, TraceObject> objectsMap = new IdentityHashMap<Object, TraceObject>();

	public static void newObject(Object object, Object[] arguments,
			String name, String line, String file) {
		addObject(object, name, line, file);
		addArgumentsMethod(arguments, name, line, file);
	}

	public static void addObject(Object object, String constructor_name, 
			String constructor_line, String constructor_file) {
		if(!objectsMap.containsKey(object)) {
			TraceObject to = new TraceObject();
			to.setConstructor(constructor_name, constructor_line, constructor_file);
			objectsMap.put(object, to);
		}
	}



	public static void addArgumentsMethod(Object[] arguments, String name, String line, String file) {
		if(!isObjectNull(arguments)) {
			for(Object o : arguments) {
				if(objectsMap.containsKey(o)) {
					getTraceObject(o).addMethod(true, name, line, file);
				}
			}
		}
	}

	public static void addReturnMethod(Object object, String name, String line, String file) {
		if(!isObjectNull(object) && hasObject(object)) {
			getTraceObject(object).addMethod(false, name, line, file);
		}
	}

	public static void addField(Object object, Object field, Object[] arguments, boolean argument,
			String name, String line, String file) {
		String field_name = "";
		if(hasObject(object)) {
			if(argument) {
				field_name += name + " = " + arguments[0].toString();
				getTraceObject(object).addField(arguments[0], argument, 
					field_name, line, file);
			}
			else {
				field_name += name + " = " + field.toString();
				getTraceObject(object).addField(field, argument, 
						field_name, line, file);
			}
			
		}
		else
			return;
	}

	public static boolean hasObject(Object object) {
		if(objectsMap.containsKey(object))
			return true;
		return false;
	}

	public static TraceObject getTraceObject(Object object) {
		if(hasObject(object))
			return objectsMap.get(object);
		return null;
	}

	public static boolean isObjectNull(Object object) {
		if(object == null)
			return true;
		return false;
	}

	public static void printTraceObject(Object object) {
		for(Object o : objectsMap.keySet()) {
			if(object == o) {
				System.err.print(objectsMap.get(o).toString());
			}
		}
	}
}
