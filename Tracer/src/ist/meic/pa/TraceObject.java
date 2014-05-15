package ist.meic.pa;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public class TraceObject {

	private TraceConstructor constructor;
	private List<TraceMethod> methodCalls;
	private IdentityHashMap<Object, TraceFieldAccess> fieldsMap;

	public TraceObject() {
		methodCalls = new ArrayList<TraceMethod>();
		fieldsMap = new IdentityHashMap<Object, TraceFieldAccess>();
	}

	public TraceConstructor getConstructor() { return constructor; }

	public void setConstructor(String name, String line, String file) { 
		constructor = new TraceConstructor(name, line, file); 
	}

	public void addMethod(boolean argument, String name, String line, String file) {
		methodCalls.add(new TraceMethod(argument, name, line, file)); 
	}

	public void addField(Object field, boolean argument, String name, String line, String file) {
		if(!hasField(field)) {
			newField(field, argument, name, line, file);
		}
		else {
			getFieldAccess(field).addFieldAccess(argument, name, line, file);
		}
	}

	public void newField(Object field, boolean argument, String name, String line, String file) {
		if(!fieldsMap.containsKey(field)) {
			TraceFieldAccess fa = new TraceFieldAccess();
			fa.addFieldAccess(argument, name, line, file);
			fieldsMap.put(field, fa);
		}
	}

	public boolean hasField(Object field) {
		if(fieldsMap.containsKey(field))
			return true;
		return false;
	}

	public TraceFieldAccess getFieldAccess(Object field) {
		if(fieldsMap.containsKey(field))
			return fieldsMap.get(field);
		return null;
	}

	@Override
	public String toString() {
		String result = constructor.toString() + "\n";
		for(TraceMethod mr : methodCalls) 
			result += mr.toString() + "\n";
		for(TraceFieldAccess fa : fieldsMap.values())
			result += fa.toString();
		return result;
	}
}
