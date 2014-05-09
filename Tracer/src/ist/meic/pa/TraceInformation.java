package ist.meic.pa;

// All information necessary to store from a object implements this class
public abstract class TraceInformation {
	private String name;
	private String line;
	private String file;

	public String getName() { return name; }
	public String getLine() { return line; }
	public String getFile() { return file; }
	public void setName(String name) { this.name = name; }
	public void setLine(String line) { this.line = line; }
	public void setFile(String file) { this.file = file; }
	
	@Override
	public String toString() {
		return name + " on " + file + ":" + line;
	}
}
