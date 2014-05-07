package ist.meic.pa;

public class TraceObject {

	private String _callingFunctionName;
	private String _constructorName;
	private int _lineNumber;
	private Object _obj;
	
	public TraceObject(String callingFun, String constructorName, int lineNumber, Object obj) {
		_callingFunctionName = callingFun;
		_constructorName = constructorName;
		_lineNumber = lineNumber;
		_obj = obj;
	}
	
	public TraceObject(Object obj) {
		_obj = obj;
	}
	
	public String get_constructorName() {
		return _constructorName;
	}
	public void set_constructorName(String _constructorName) {
		this._constructorName = _constructorName;
	}
	public int get_lineNumber() {
		return _lineNumber;
	}
	public void set_lineNumber(int _lineNumber) {
		this._lineNumber = _lineNumber;
	}
	public String getCallingFunctionName() {
		return _callingFunctionName;
	}
	public void setCallingFunctionName(String callingFunctionName) {
		this._callingFunctionName = callingFunctionName;
	}

	public Object get_obj() {
		return _obj;
	}

	public void set_obj(Object _obj) {
		this._obj = _obj;
	}
}
