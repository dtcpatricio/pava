package ist.meic.pa;

import java.util.IdentityHashMap;

public class ObjectTracer {

	private static IdentityHashMap<Object, TraceObject> objectsMap = new IdentityHashMap<Object, TraceObject>();

	public static void newObject(Object object, Object[] arguments,
			String name, String line, String file) {
		if(object == null) {
			addField(arguments[0], name, line, file);
		}
		else {
			addObject(object, name, line, file);
			addArgumentsMethod(arguments, name, line, file);
		}
	}

	public static void addObject(Object object, String constructor_name, 
			String constructor_line, String constructor_file) {
		if(!objectsMap.containsKey(object)) {
			TraceObject to = new TraceObject();
			to.setConstructor(constructor_name, constructor_line, constructor_file);
			objectsMap.put(object, to);
		}
	}

	public static void addField(Object argument, String name, String line, String file) {
		if(hasFieldName(name)) {
			Object o = getFieldName(name);
			TraceObject f = getTraceObject(o);
			objectsMap.remove(o);
			f.addMethod(true, name, line, file);
			objectsMap.put(argument, f);
		}
		else {
			if(!objectsMap.containsKey(argument)) {
				TraceObject f = new TraceObject();
				f.setField(true);
				f.setFieldName(name);
				f.addMethod(true, name, line, file);
				objectsMap.put(argument, f);
			}
			else {
				getTraceObject(argument).addMethod(true, name, line, file);
			}
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
		if(!isObjectNull(object) && hasTraceObject(object)) {
			getTraceObject(object).addMethod(false, name, line, file);
		}			
	}

	public static boolean hasFieldName(String name) {
		for(TraceObject to : objectsMap.values()) {
			if(to.getFieldName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static Object getFieldName(String name) {
		for(Object o : objectsMap.keySet()) {
			if(objectsMap.get(o).getFieldName().equals(name)) {
				return o;
			}
		}
		return null;
	}

	public static boolean hasTraceObject(Object object) {
		if(objectsMap.containsKey(object))
			return true;
		return false;
	}

	public static TraceObject getTraceObject(Object object) {
		if(hasTraceObject(object))
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
