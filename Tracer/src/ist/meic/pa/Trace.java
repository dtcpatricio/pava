package ist.meic.pa;

public class Trace {
	// Responsible for printing the history of an object
	public static void print(Object object) {
		System.out.println("Tracing for " + object);
		IdentityTracer.printConstructorMap();
	}
}
