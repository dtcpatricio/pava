package ist.meic.pa.Test.finaltests;

import ist.meic.pa.Trace;

class Bozo extends Bozium {
	
	Bozo(Integer i) {
		super(i);
	}
	
	Object bozo() {
		return new String("bozium");
	}
	
	Integer radius() {
		return new Integer(100);
	}
	
	Object bozium(Object o) {
		return o;
	}
	
	void bigBozoTest() {
		Trace.print(bozo());
		Object r = radius();
		Trace.print(bozium(r));
	}
}

public class NewTest1 {
	public static void main(String[] args) {
		(new Bozo(100)).bigBozoTest();
	}
}
