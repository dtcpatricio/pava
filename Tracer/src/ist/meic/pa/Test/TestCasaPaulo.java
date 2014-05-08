package ist.meic.pa.Test;

import ist.meic.pa.IdentityTracer;
import ist.meic.pa.Trace;

import java.lang.reflect.*;
import java.util.ArrayList;

public class TestCasaPaulo {
	
	public Object foo() {
		String s = new String("Foo");
		
		return s;
		
	}

	public void test() {
		Trace.print(foo());
	}
}
