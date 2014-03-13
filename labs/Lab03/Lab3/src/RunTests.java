import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Scanner;
import java.util.TreeMap;

// setAccessible true, altera o visibilidade do método para public?
// Pq é que o m10 dá failed? (public static void)
// Não existe forma de descobrir uma subclass?, a forma implementada pode ser considerada correcta?
public class RunTests {
	static int passed = 0;
	static int failed = 0;
	
	public static void runTests(TreeMap<String, Method> setupMethods, Class<?> testClass) {
		for (Method m : testClass.getDeclaredMethods()) {
	    	  if(m.isAnnotationPresent(Setup.class) || m.isAnnotationPresent(Test.class)) {
	    		  Annotation[] annotations = m.getAnnotations();
	    		  for(Annotation ann: annotations) {
	    			  if(ann instanceof Setup) {
	    				  setupMethods.put(((Setup) ann).value(), m);
	    				  } else {
	    					  if(ann instanceof Test) {
	    						  try {
	    							  Test anTest = (Test) ann;
	    							  String[] anParameters = anTest.value();
	    							  for(String s: anParameters) {
	    								  // When dealing with '*', execute EVERY setup method??
	    								/*  if(s.equals("*")) {
	    									  System.out.println("Found a *");
	    								  }*/
	    								  setupMethods.get(s).setAccessible(true);
	    								  setupMethods.get(s).invoke(null);
	    							  }
	    							  m.setAccessible(true);
	    							  m.invoke(null);
	    							  System.out.printf("Test %s OK! %n", m);
	    							  passed++;
	    							  } catch (Throwable ex) {
	    								  System.out.printf("Test %s failed: %s %n", m, ex.getCause());
	    								  failed++;
	    								  }
	    						  }
	    					  }
	    			  }
	    		  }
	    	  }
	}

	
   public static void main(String[] args) throws Exception {
	  TreeMap<String, Method> setupMethods = new TreeMap<String, Method>();
	  Scanner scan = new Scanner(System.in);
	  String input = scan.nextLine();
	  Class<?> testClass = Class.forName(input);
	  String superClassName = testClass.getSuperclass().getName();
	  Class<?> testSuperClass = null;
	  
	  if(!superClassName.equals("java.lang.Object")) {
		  System.out.println("Class has superclass... " + testClass.getSuperclass().getName());
		  testSuperClass = testClass.getSuperclass();
	  }
	  if(testSuperClass != null) {
		  runTests(setupMethods, testSuperClass);
	  }
	  runTests(setupMethods, testClass);
	  
      System.out.printf("Passed: %d, Failed %d%n", passed, failed);
   }
}