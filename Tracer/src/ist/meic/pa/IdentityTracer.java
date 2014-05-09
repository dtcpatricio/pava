package ist.meic.pa;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.TreeMap;

public class IdentityTracer {

	// First object reference maps to trace constructor
	private static IdentityHashMap<Object, TraceObject> objectsMap = new IdentityHashMap<Object, TraceObject>();;
	
	public static void addObject(Object object) {
		objectsMap.put(object, new TraceObject());
	}
	
	// TODO: lançamento de excepcao se object nao existir no objectsMap
	public static TraceObject getTraceObject(Object object) {
		return objectsMap.get(object);
	}
	
	// Iterates thought all arguments and add to traceObject
	public static void addArguments(Object[] arguments, String name, String line, String file) {
		for(Object o : arguments) {
			if(objectsMap.containsKey(o)) {
				getTraceObject(o).addMethod(true, name, line, file);
			}
		}
	}
	
	public static void printObjectsMap() {
		for(Object object : objectsMap.keySet()) {
			System.err.println(objectsMap.get(object));
		}
	}
	
	// Compare object to be evaluated with all stored objects
	public static void printObject(Object object) {
		for(Object o : objectsMap.keySet()) {
			if(object == o)
				System.err.println(objectsMap.get(o));
		}
	}
}
