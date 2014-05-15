package ist.meic.pa.Test;

import ist.meic.pa.Trace;
class Test {
	Integer i;
	public Object foo() {
		return new String("Foo");
	}

	public Object bar() {
		return foo();
	}
	
	public Object baz() {
		return bar();
	}
	
	public void test() {
		Test0Test t = new Test0Test();
		t.methodtest();
		
		Trace.print(t);
	}
}

public class Test0 {

	public static void main(String[] args) {
		(new Test()).test();
	}
}