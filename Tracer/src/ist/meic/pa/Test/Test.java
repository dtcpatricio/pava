package ist.meic.pa.Test;
import ist.meic.pa.IdentityTracer;
import ist.meic.pa.Trace;
import java.lang.reflect.*;
class Test {
	static IdentityTracer iden = new IdentityTracer();
	
	public Object foo() {
		String s = new String("Foo");
		/*Integer i = new Integer(2);
		String t = new String("Foo");
		i++;
		*/
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
		
		System.out.println("Testing...Identity Hash Code of Foo : " + System.identityHashCode(o));
		Trace.print(o);
		foo2(o);
		foo3(5,6);
		
		Object b = bar();
		Trace.print(identity(b));
	}
}
