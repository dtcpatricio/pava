package ist.meic.pa;

import java.util.ArrayList;
import java.util.List;

/**
 * TraceField:
 * 	Class that represents the fields accesses of each object
 */
public class TraceField {

	private List<TraceMethod> fieldAccesses;

	public TraceField() {
		fieldAccesses = new ArrayList<TraceMethod>();
	}

	public void addFieldAccess(boolean argument, String name, String line, String file) {
		fieldAccesses.add(new TraceMethod(argument, name, line, file)); 
	}

	@Override
	public String toString() {
		String result = "";
		for(TraceMethod mr : fieldAccesses) 
			result += mr.toString() + "\n";
		return result;
	}
}
