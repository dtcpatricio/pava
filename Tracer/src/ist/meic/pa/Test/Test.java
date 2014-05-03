package ist.meic.pa.Test;
import ist.meic.pa.Trace;
class Test {
	
	public Object foo() {
		return new String("Foo");
	}
	
	public Object bar() {
		return new String("Bar");
	}
	
	public Object identity(Object o) {
		return o;
	}
	
	public void test() {
		Trace.print(foo());
		Object b = bar();
		Trace.print(identity(b));
	}
}
