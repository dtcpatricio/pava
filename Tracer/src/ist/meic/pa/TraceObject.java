package ist.meic.pa;

import java.util.ArrayList;
import java.util.List;

// All evaluated objects are represented as a TraceObject
public class TraceObject {
	
	private TraceConstructor constructor;
	private List<TraceMethod> methodReturns;
	
	public TraceObject() {
		methodReturns = new ArrayList<TraceMethod>();
	}

	public TraceConstructor getConstructor() { return constructor; }
	
	public void setConstructor(String name, String line, String file) { 
		constructor = new TraceConstructor(name, line, file); 
	}
	
	public void addMethod(boolean argument, String name, String line, String file) {
		methodReturns.add(new TraceMethod(argument, name, line, file)); 
	}
	
	@Override
	public String toString() {
		String result = constructor.toString() + "\n";
		for(TraceMethod mr : methodReturns) 
			result += mr.toString() + "\n";
		return result;
	}
}
