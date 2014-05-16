package ist.meic.pa;

/**
 * TraceMethod:
 * 	Class representing the method calls where an object was used as argument
 *  or returned from a method
 */
public class TraceMethod extends TraceInformation {
	
	/**
	 * argument: true if the object is passed as argument, false if it's returned
	 * from a method
	 */
	private boolean argument;
	
	public TraceMethod(boolean argument, String name, String line, String file) {
		this.argument = argument;
		setName(name);
		setLine(line);
		setFile(file);
	}
	
	@Override
	public String toString() {
		String result = "";
		if(argument)
			result += "  -> ";
		else
			result += "  <- ";
		return result + super.toString();
	}
}
