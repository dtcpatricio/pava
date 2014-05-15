package ist.meic.pa;

import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.Translator;

public class TraceVMImplementation {

	private static final String package_name = "ist.meic.pa";

	private static final String object_tracer = package_name + ".ObjectTracer";

	private static final String trace_vm = package_name + ".TraceVMImplementation";

	private static final String trace = package_name + ".Trace";

	private static boolean extended = false;

	private static final String[] classes = new String[] {
		package_name + ".ObjectTracer",
		package_name + ".TraceConstructor",
		package_name + ".TraceFieldAccess",
		package_name + ".TraceInformation",
		package_name + ".TraceMethod",
		package_name + ".TraceObject",
		package_name + ".TraceTranslator",
		package_name + ".TraceVM",
		package_name + ".TraceVMExtended",
	};

	private static List<String> clientClasses = new ArrayList<String>();

	public static void loadTracerClasses(Loader classLoader) throws Throwable {
		classLoader.loadClass(object_tracer).newInstance();
		classLoader.loadClass(trace_vm).newInstance();
		addClientClass(trace);
	}
	
	public static void initializeTracer(String[] args, boolean extended) 
			throws Throwable {
		if(args.length != 1) {
			System.err.println("Tracer: Please insert one class.");
			return;
		}
		Translator translator = new TraceTranslator();
		ClassPool pool = ClassPool.getDefault();
		Loader classLoader = new Loader();
		classLoader.addTranslator(pool, translator);
		
		loadTracerClasses(classLoader);
		setExtended(extended);
		
		addClientClass(args[0]);
		classLoader.run(args[0], null);
	}

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

	public static boolean isExtended() {
		return extended;
	}

	public static void setExtended(boolean extended) {
		TraceVMImplementation.extended = extended;
	}
	
	public static void printClientClasses() {
		System.err.println("--------------------CLASSES---------------------");
		for(String c : clientClasses) {
			System.err.println("Client Classes: " + c);
		}
		System.err.println("--------------------CLASSES---------------------");
	}
}
