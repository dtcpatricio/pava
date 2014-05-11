package ist.meic.pa;

import java.util.IdentityHashMap;

public class ObjectTracer {

	// First object reference maps to trace constructor
	private static IdentityHashMap<Object, TraceObject> objectsMap = new IdentityHashMap<Object, TraceObject>();;

	public static void validateAndAddObject(Object object, Object[] arguments,
			String constructor_name, String constructor_line, String constructor_file) {
		// if object is null means is a FieldAccess Writer, so the object is in $args[0] instead of $_
		if(object == null) {
			if(arguments.length < 1)
				return; // throw exception
			addObject(arguments[0], constructor_name, constructor_line, constructor_file);
		}
		else {
			addObject(object, constructor_name, constructor_line, constructor_file);
			addArgumentsMethod(arguments, constructor_name, constructor_line, constructor_file);
			// if a argument already exists in objectsMap it must be added as used as argument
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
	
	public static boolean hasTraceObject(Object object) {
		if(!objectsMap.containsKey(object))
			return false;
		return true;
	}
	
	// TODO: lançamento de excepcao se object nao existir no objectsMap
	public static TraceObject getTraceObject(Object object) {
		if(hasTraceObject(object))
			return objectsMap.get(object);
		// throw exception
		return null;
	}

	public static boolean isObjectNull(Object object) {
		if(object == null)
			return true;
		return false;
	}
	
	// Iterates thought all arguments and add to traceObject
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
		if(!isObjectNull(object))
			getTraceObject(object).addMethod(false, name, line, file);
	}

	// Compare object to be evaluated with all stored objects
	public static void printObject(Object object) {
		for(Object o : objectsMap.keySet()) {
			if(object == o) {
				System.err.println(objectsMap.get(o));
			}
		}
	}
	
	public static void printObjectsMap() {
		for(Object o : objectsMap.keySet()) {
			System.err.println(objectsMap.get(o));
		}
	}
}
