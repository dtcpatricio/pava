package ist.meic.pa;

import java.util.ArrayList;
import java.util.List;

public class TraceObject {
	
	private boolean field;
	private String fieldName;
	private TraceConstructor constructor;
	private List<TraceMethod> methodCalls;
	
	public TraceObject() {
		fieldName = "";
		field = false;
		methodCalls = new ArrayList<TraceMethod>();
	}

	public TraceConstructor getConstructor() { return constructor; }
	
	public void setConstructor(String name, String line, String file) { 
		constructor = new TraceConstructor(name, line, file); 
	}
	
	public void addMethod(boolean argument, String name, String line, String file) {
		methodCalls.add(new TraceMethod(argument, name, line, file)); 
	}
	
	@Override
	public String toString() {
		String result = "";
		if(!field)
			result += constructor.toString() + "\n";
		for(TraceMethod mr : methodCalls) 
			result += mr.toString() + "\n";
		return result;
	}

	public boolean isField() {
		return field;
	}

	public void setField(boolean field) {
		this.field = field;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
