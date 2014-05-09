package ist.meic.pa;

public class TraceMethod extends TraceInformation {
	
	private boolean argument;
	
	/**
	 * @param argument true if the object is passed as argument, false if it's returned
	 * @param name
	 * @param line
	 * @param file
	 */
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
			result += "-> ";
		else
			result += "<- ";
		
		return result + super.toString();
	}
}
