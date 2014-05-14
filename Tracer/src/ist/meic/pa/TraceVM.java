package ist.meic.pa;

public class TraceVM extends TraceVMImplementation {

	public static void main(String[] args) throws Throwable {
		try {
			initializeTracer(args, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
