package ist.meic.pa;

/**
 * Trace:
 * 	Class that allows the "communication" between the client and the Tracer classes
 */
public class Trace {

	static public void print(Object object) {
		if(ObjectTracer.hasObject(object)) {
			System.err.println("Tracing for " + object.toString());
			ObjectTracer.printTraceObject(object);
		} 
		else {
			if(object != null)
				System.err.println("Tracing for " + object.toString() + " is nonexistent!");
			else
				System.err.println("Tracing for null is nonexistent!");
		}
	}
}
