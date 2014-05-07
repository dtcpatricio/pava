package ist.meic.pa;

import java.util.IdentityHashMap;
import java.util.TreeMap;

public class IdentityTracer {

	// 1º Function Name, Object returned
	IdentityHashMap<Object, TraceFunction> hashFunction;
	
	// System hash code of object and the copy of the object inspected
	TreeMap<Integer, TraceObject> hashObjects;
	
	
	public IdentityTracer() {
		hashFunction = new IdentityHashMap<Object, TraceFunction>();
		hashObjects = new TreeMap<Integer, TraceObject>();
	}

	
	public void addIdentity(Object fun, TraceFunction newO) {
		hashFunction.put(fun, newO);
	}

	public void addInspectedObject(int hash, String callingFun, String constructName, int line, Object obj) {
		System.err.println("IdentityTracer.addInspectedObject called " + hash + " "+ obj);
		if(hash != 0) {
			TraceObject to = new TraceObject(callingFun, constructName, line, obj);
			hashObjects.put(hash, to);
		}
	}
	
	public void addInspectedObject(int hash, Object obj) {
		System.err.println("IdentityTracer.addInspectedObject called " + hash + " "+ obj);
		if(hash != 0) {
			TraceObject to = new TraceObject(obj);
			hashObjects.put(hash, to);
			System.err.println("Object hash adicionado com sucesso" + hash);
		}
	}
	
	public void functionCall(int hash) {
		System.err.println("Funcion call with hash " + hash);
		System.err.println(hashObjects.keySet());
		if(hashObjects.containsKey(hash)) {
			TraceObject to = hashObjects.get(hash);
			System.out.println("And the name is: " + to.get_obj());
		}
	}
	
	public void printIdentityHash() {
		System.err.println(hashFunction.toString());
	}
	
}
