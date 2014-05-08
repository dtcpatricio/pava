package ist.meic.pa;

import java.util.IdentityHashMap;
import java.util.TreeMap;

public class IdentityTracer {

	// First object reference maps to trace construtor
	private static IdentityHashMap<Object, TraceConstructor> constructorMap = new IdentityHashMap<Object, TraceConstructor>();;
	

	public static void addConstructor(Object o, String name, String line, String file) {
		TraceConstructor cons = new TraceConstructor(name, line, file);
		constructorMap.put(o, cons);
	}
	
	public static void printConstructorMap() {
		for(Object o : constructorMap.keySet()) {
			System.err.println(constructorMap.get(o));
		}
	}
	
}
