package ist.meic.pa.Test.finaltests;

import ist.meic.pa.Trace;

class Bozium {
	int k = 0;
	Bozium(Integer i) {
		Integer radius = new Integer(i.intValue());
		Trace.print(createAllMightyBozo(radius));		
	}
	
	Object createAllMightyBozo(Integer r) {
		System.out.println(r);
		int i = r.intValue() * 10;
		System.out.println(i);
		if(k == 10) {
			return null;
		}
		k++;
		return createAllMightyBozo(new Integer(i));
	}
	
}
