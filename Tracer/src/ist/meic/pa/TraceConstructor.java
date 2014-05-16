package ist.meic.pa;

/**
 * TraceConstructor:
 * 	Class representing the constructor call used to create the object
 */
public class TraceConstructor extends TraceInformation {

	public TraceConstructor(String name, String line, String file) {
		setName(name);
		setLine(line);
		setFile(file);
	}

	@Override
	public String toString() {
		return "  <- " + super.toString();
	}
}
