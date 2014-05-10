package ist.meic.pa;

public class Trace {
	// Responsible for printing the history of an object
	public static void print(Object object) {
		System.err.println("Tracing for " + object);
		ObjectTracer.printObject(object);
	}
}
