package ist.meic.pa;

/**
 * TraceVM:
 * 	Class responsible for the bytecode transformation for the base implementation
 */
public class TraceVM extends TraceVMImplementation {

	public static void main(String[] args) throws Throwable {
		try {
			initializeTracer(args, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
