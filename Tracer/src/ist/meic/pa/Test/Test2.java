package ist.meic.pa.Test;

import ist.meic.pa.Trace;

public class Test2 {
	
	Integer i;
	
	public Object foo() {
		String s = new String("Foo");
		return s;
	}
	
	public String bar(String p) {
		return p;
	}
	
	public void test() {
		String p = new String("Bars");
		i = 2;
		Trace.print(i);
	}
}