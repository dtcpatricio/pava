package ist.meic.pa;

import java.util.ArrayList;
import java.util.List;

public class TraceFieldAccess {
	private String fieldName;
	private List<TraceMethod> fieldAccesses;
	
	public TraceFieldAccess() {
		setFieldName("");
		fieldAccesses = new ArrayList<TraceMethod>();
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
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
