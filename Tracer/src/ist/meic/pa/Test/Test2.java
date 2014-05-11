package ist.meic.pa.Test;

import ist.meic.pa.Trace;

public class Test2 extends Test2Super {

	Float i;
	
	public Object foo() {
		String s = new String("Foo");
		return s;
	}
	
	public String bar(String p) {
		return p;
	}
	
	public void test() {
		String p = new String("Bars");
		
		/*i = 5.6f;
		i = 5.7f;
		i = 5.8f;*/
		
		Trace.print(p);
	}
}