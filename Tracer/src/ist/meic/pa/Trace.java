package ist.meic.pa;

public class Trace {
	// Responsible for printing the history of an object
	public static void print(Object object) {
		if(ObjectTracer.hasTraceObject(object)) {
			System.err.println("Tracing for " + object.toString());
			ObjectTracer.printObject(object);
		} else {
			if(object != null) {
				System.err.println("Tracing for " + object.toString() + " is nonexistente");
			}
			else 
				//throw exception
				System.err.println("Tracing for null is nonexistent!");
		}	
	}
}
