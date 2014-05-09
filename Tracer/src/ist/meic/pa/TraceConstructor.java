package ist.meic.pa;

// Responsible for storing information about the creation of an instance
public class TraceConstructor extends TraceInformation {

	public TraceConstructor(String name, String line, String file) {
		setName(name);
		setLine(line);
		setFile(file);
	}
	
	@Override
	public String toString() {
		return "<- " + super.toString();
	}
}
