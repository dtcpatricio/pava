package pt.ist.ap.labs;

public class Main {

	public static void main(String[] args) {
		InstrospectionShell is = new InstrospectionShell();
		
		try {
			while(true) {
				is.execute();
			}
		} catch(Exception e) {
			e.getMessage();
		}
		
		
	}

}
