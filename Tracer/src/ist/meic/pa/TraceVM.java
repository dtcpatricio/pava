package ist.meic.pa;

import java.util.ArrayList;
import java.util.List;

import javassist.ClassPool;
import javassist.Loader;
import javassist.Translator;

// Responsible for bytecode transformation at class load time
// Transforms the bytecode of all classes loaded as a result of loading
// its first element
public class TraceVM {
	
	private static final String package_name = "ist.meic.pa";
	
	// TODO: Confirmar nomes das classes
	private static final String[] classes = new String[] {
		package_name + ".ObjectTracer",
		package_name + ".Trace",
		package_name + ".TraceConstructor",
		package_name + ".TraceInformation",
		package_name + ".TraceMethod",
		package_name + ".TraceObject",
		package_name + ".TraceTranslator",
		package_name + ".TraceVM",
	};
	
	private static List<String> clientClasses = new ArrayList<String>();
	
	public static void main(String[] args) throws Throwable {
		try {
			Translator translator = new TracerTranslator();
			ClassPool pool = ClassPool.getDefault();
			Loader classLoader = new Loader();
			classLoader.addTranslator(pool, translator);
			
			loadTracerClasses(classLoader);
			
			// TODO: arg0 must be given as argument representing class to be run
			addClientClass("ist.meic.pa.Test.Test2Main");
			classLoader.run("ist.meic.pa.Test.Test2Main", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Create new instance of static class Identity Tracer
	// All classes needed in load time must be loaded from here
	public static void loadTracerClasses(Loader classLoader) throws Throwable {
		Class<?> it = classLoader.loadClass("ist.meic.pa.ObjectTracer");
		it.newInstance();
	}
	
	// A class is considered valid if it's not a prioritary class (one of our classes)
	public static boolean isClassValid(String className) {
		boolean valid_class = true;
		for(String class_name : classes) {
			if(className.equals(class_name))
				valid_class = false;
		}
		return valid_class;
	}
	
	public static void addClientClass(String className) {
		if(!clientClasses.contains(className))
			clientClasses.add(className);
	}
	
	public static boolean isClientClass(String className) {
		for(String s : clientClasses) {
			if(className.equals(s))
				return true;
		}
		return false;
	}
	
	public static void printClientClass() {
		for(String s : clientClasses) {
			System.err.println("clientClasses: " + s);
		}
	}
}
