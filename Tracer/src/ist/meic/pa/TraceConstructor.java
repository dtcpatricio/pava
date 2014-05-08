package ist.meic.pa;

public class TraceConstructor {

	private String name;
	private String line;
	private String file;
	
	public TraceConstructor(String name, String line, String file) {
		this.name = name;
		this.line = line;
		this.file = file;
	}

	public String getName() { return name; }
	public String getLine() { return line; }
	public String getFile() { return file; }
	
	@Override
	public String toString() {
		return "<- " + name + " on " + file + ":" + line;
	}
}
