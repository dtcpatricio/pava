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
			Translator translator = new TracerTranslator();
			ClassPool pool = ClassPool.getDefault();
			Loader classLoader = new Loader();
			classLoader.addTranslator(pool, translator);
			
			loadTracerClasses(classLoader);
			
			// TODO: ist.meic.pa.Test.Test0 must be received as argument
			classLoader.run("ist.meic.pa.Test.Test0", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Create new instance of static class Identity Tracer
	// All classes needed in load time must be loaded from here
	public static void loadTracerClasses(Loader classLoader) throws Throwable {
		Class<?> it = classLoader.loadClass("ist.meic.pa.IdentityTracer");
		it.newInstance();
	}
}
