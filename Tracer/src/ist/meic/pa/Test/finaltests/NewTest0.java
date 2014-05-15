package ist.meic.pa.Test.finaltests;

import ist.meic.pa.Trace;

class Test0 {
	public Object queFazeis() {
		return new String("Que estais a fazer?");
	}

	public Object anormal() {
		return queFazeis();
	}
	
	public Object crystalBoy() {
		return anormal();
	}
	
	public void test() {
		Trace.print(crystalBoy());
		Trace.print(anormal());
		Trace.print(queFazeis());
	}
}

public class NewTest0 {
	public static void main(String[] args) {
		(new Test0()).test();
	}
}
