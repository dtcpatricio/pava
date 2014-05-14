package ist.meic.pa;

public class TraceVMExtended extends TraceVMImplementation {

	public static void main(String[] args) throws Throwable {
		try {
			initializeTracer(args, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
