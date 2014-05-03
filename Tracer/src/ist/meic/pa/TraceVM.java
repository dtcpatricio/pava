package ist.meic.pa;

import javassist.ClassPool;
import javassist.Loader;
import javassist.Translator;

// Responsible for bytecode transformation at class load time
// Transforms the bytecode of all classes loaded as a result of loading
// its first element
public class TraceVM {
	public static void main(String[] args) throws Throwable {
		try {
			Translator translator = new MyTranslator();
			ClassPool pool = ClassPool.getDefault();
			Loader classLoader = new Loader();
			classLoader.addTranslator(pool, translator);
			
			// TODO Trace must print information before calling Test0
			
			// TODO: ist.meic.pa.Test.Test0 must be received as argument
			classLoader.run("ist.meic.pa.Test.Test0", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
