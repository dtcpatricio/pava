package ist.meic.pa;

/**
 * TraceVMExtended:
 * 	Class responsible for the bytecode transformation for the extended implementation
 */
public class TraceVMExtended extends TraceVMImplementation {

	public static void main(String[] args) throws Throwable {
		try {
			initializeTracer(args, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
