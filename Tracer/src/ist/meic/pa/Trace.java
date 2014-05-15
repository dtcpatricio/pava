package ist.meic.pa;

public class Trace {

	public static void print(Object object) {
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
