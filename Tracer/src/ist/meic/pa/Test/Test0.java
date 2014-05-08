package ist.meic.pa.Test;

import ist.meic.pa.Trace;

public class Test0 {

	public static void main(String[] args) {
		(new Test()).test();
	}
}

class Test {

	public Object foo() {
		String s = new String("Foo");
		return s;
	}

	public Object foo2(Object o) {
		return o;
	}

	public void foo3(int dx, int dy) {
		return;
	}

	public Object bar() {
		return new String("Bar");
	}

	public Object identity(Object o) {
		return o;
	}

	public void test() {
		Object o = foo();

		Trace.print(o);

		Object b = bar();
		Trace.print(identity(b));
	}
}


