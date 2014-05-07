package ist.meic.pa;

public class TraceFunction {

	private String name;
	// Input parameters
	private String returnType;
	
	public TraceFunction() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	@Override
	public String toString() {
		return "Function name: " + name + " return type is: " + returnType;
	}
	
}
