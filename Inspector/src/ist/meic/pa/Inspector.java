package ist.meic.pa;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Inspector {	
	/* Contains object beign evaluated */
	Object current_object = null;

	boolean flagCommand = false;
	
	public void inspect(Object object) {
		current_object = object;
		getInformation(object);
		read_eval_loop();
	}
	
	/* Retrieve information about Object object */
	public void getInformation(Object object) {
		System.out.println(object.toString() + " is an instance of " + object.getClass());
		getFields(object);
		getMethods(object);
		getSuperClasses(object);
		
	}
	
	/* reads ands evaluate commands provided by the user */
	public void read_eval_loop() {
		while(true) {
			System.out.print(" > ");
			String[] command = readLine().split(" ");
			command(command);
		}
	}
	
	public Inspector() {
	}
	
	public void getMethods(Object object) {
		System.out.println("----------");
		System.out.println("Methods");
		Class<?> objectClass = object.getClass();
		Method[] ms;
		do {
			ms = objectClass.getDeclaredMethods();
			for (Method method : ms) {
				System.out.println("\t" + method);
			}
			objectClass = objectClass.getSuperclass();
		}
		while(!objectClass.equals(Object.class));
	}
	
	public void getSuperClasses(Object object) {
		System.out.println("----------");
		System.out.println("Superclasses:");
		Class<?> objectClass = object.getClass().getSuperclass();
		do {
			System.out.println("\t" + objectClass);
			objectClass = objectClass.getSuperclass();
		}
		while(!objectClass.equals(Object.class));
	}
	
	/* Duvida: os fields private e protected de superclasses devem ser impressos? 
	 * E os static imprimem-se?*/
	public void getFields(Object object) {
		System.out.println("----------");
		System.out.println("Fields:");
		Class<?> objectClass = object.getClass();
		Field[] fields;
		do {
			fields = objectClass.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				try {
					field.get(object);
					// Falta imprimir os modifiers
					System.out.println("\t" + field.getType()+ " " + field.getName() + " = " + field.get(object));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			objectClass = objectClass.getSuperclass();
		}
		while(!objectClass.equals(Object.class));
	}
	
	public String readLine() {
		Scanner in = new Scanner(System.in);
		return in.nextLine();
	}

	public Class<?> getClass(String className) {
		try {
			return (Class.forName(className));
		} catch (ClassNotFoundException cnfe) {
			System.err.println("Class '" + className + "' not found");
			return null;
		}
	}

	public Method bestMethod(Class<?> type, String name, Class<?> argType) throws NoSuchMethodException {
		try {
			return type.getMethod(name, argType);
		} catch (NoSuchMethodException e) {
			if (argType == Object.class) {
				throw new NoSuchMethodException(name);
			} else {
				return bestMethod(type, name, argType.getSuperclass());
			}
		}
	}

	public Object invoke(Object receiver, String name, Object arg) {
		try {
			System.out.println("INVOKE");
			Method method = bestMethod(receiver.getClass(), name, arg.getClass());
			return method.invoke(receiver, arg);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public void command(String[] command) {
		//commandClass(command);
		//commandSet(command);
		//commandGet(command);
		//commandIndex(command);
		commandC(command);
		commandI(command);
		commandQ(command);
		if(flagCommand == false) {
			System.out.println("Error: Unknown command : the term '" + command[0] +
					"' is not recognized as the name of a command, please try again!\n" +
					"Type -help for more information");
		}
		flagCommand = false;
	}
	
	public void commandI(String[] command) {
		if(command[0].equals("i")) {
			Field[] fields;
			Class<?> objectClass = current_object.getClass();
			while(!objectClass.equals(Object.class)) {
				System.out.println("Class Name : " + objectClass.getName());
				fields = objectClass.getDeclaredFields();
				for(Field f : fields) {
					f.setAccessible(true);
					//System.out.println("FIELD: " + f.getName());
					if(command[1].equals(f.getName())) {
						try {
							Object newObj = f.get(current_object).getClass();
							System.out.println("Inspected field '" + f.getName() + "' = " + f.get(current_object));
							System.out.println("Current Object: " + newObj);
							flagCommand = true;
							current_object = newObj;
							return;
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
				objectClass = objectClass.getSuperclass();
			}
			flagCommand = true;
		}
	}

	public void commandQ(String[] command) {
		if(command[0].equals("q")) {
			System.out.println("Program exited");
			System.exit(0);
		}
	}
	/* Returns list (in string format) of all parameters of the method */
	public List<String> getParameters(String[] command) {
		List<String> parameters = new ArrayList<String>();
		
		/* parameters start in position number 2 */
		for(int cmd = 2; cmd < command.length; cmd++) {
			parameters.add(command[cmd]);
		}
		return parameters;
	}
	
	/* Command c name value0 value1 ... valuen */
	/* Obtain all methods of previous object looking first for the class's methods
	 * and next to the superclass methods and so on */
	public void commandC(String[] command) {
		if(!command[0].toLowerCase().equals("class"))
			return;
		
		Class<?> objectClass = current_object.getClass();
		Method[] declaredMethods = null;
		List<Method> methods = new ArrayList<Method>();
		List<String> parameters = getParameters(command);
		Object result = null;
		
		do {
			declaredMethods = objectClass.getDeclaredMethods();
			methods.clear();
			for (Method method : declaredMethods) {
				methods.add(method);
			}
			methods = evaluateMethodsName(methods, command[1]);	
			result = evaluateMethodsParam(methods, parameters.size(), parameters);
			
			objectClass = objectClass.getSuperclass();
		}
		while(!objectClass.equals(Object.class) && result == null);

		System.out.println("RESULT: " + result);
		
	}
	
	/* Receives list of methods */
	/* Obtain list with all methods with same name as methodName */
	public List<Method> evaluateMethodsName(List<Method> methods, String methodName) {
		List<Method> bestMethods = new ArrayList<Method>();
		
		for(Method method : methods) {
			if(method.getName().equals(methodName)) {
				bestMethods.add(method);
			}
		}
		return bestMethods;
	}

	/* Return list with methods that have the same number of parameters as the input */
	/* paramNumber number of parameters provided by the user */
	/* parameters: list of parameters provided by the user */
	public Object evaluateMethodsParam(List<Method> methods, int paramNumber, List<String> parameters) {
		List<Method> bestMethods = new ArrayList<Method>();
		Class<?>[] param = null;
		Object result = null;
		for(Method method : methods) {
			param = method.getParameterTypes();
			if(param.length == paramNumber) {
				bestMethods.add(method);
				try {
					result = methodMatchParamType(method, param, parameters);
					if(result != null) {
						return result;
					}
					
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					//System.out.println("IllegalArgumentException");
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//System.out.println("IllegalAccessException");
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					//System.out.println("InvocationTargetException");
				}
			}
		}
		
		return null;
	}
	
	
	
	
	/* returns list that contains all methods with paramNumber as number of parameters */
	/* Primitive types: boolean(0), byte(1), short(2), int(3), long(4), char(5), float(6) and double(7)
	 * Reference types: java.lang.String(8), java.io.Serializable, java.lang.Integer, all others
	 * Types are represented as ints CRIAR MACROS NO INICIO DO FICHEIRO, unknown (-1)
	 * LIMPAR EXCEPCOES
	 */	
	public Object methodMatchParamType(Method method, Class<?>[] params, List<String> parameters) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object result = null;
		Object[] args = parameters.toArray();
		for(int param = 0; param < params.length; param++) {
			if(params[param].equals(String.class) && 
					   parameters.get(param).startsWith("\"") &&
					   parameters.get(param).endsWith("\"")) {
				
						args[param] = (String)parameters.get(param).replace("\"", "");;
			}
			if(params[param].equals(int.class)) {
				args[param] = Integer.parseInt(parameters.get(param));
			}
		}
		method.setAccessible(true);
		result = method.invoke(current_object, args);
		
		return result;
	}

	/* Returns a list containing each type of each parameter
	 * Analyzes parameters provided by the user:
	 *  - if begins and ends with "" is considered a string, example: "5", "true"
	 *  - true or false are considered booleans
	 *  - ending in L or l, is considered a long, but could be accepted as int, or double, or float,
	 *  	example: 10L
	 *  - ending in F or f, is considered a float, but could be accepted as int, or double or float,
	 *  	example: 10f
	 *  - ending in D or d, is considered a double, but could be accepted as int, or float
	 *  	example: 10d
	 */	
	public int analyzeParameterType(String parameter) {
		if(isString(parameter)) {
			return 8;
		}
		return -1; // unknown
	}
	
	/** Checks if parameter starts and ends with quotation marks */
	public boolean isString(String parameter) {
		if(parameter.startsWith("\"") && parameter.endsWith("\"")) {
			return true;
		}
		return false;
	}
	
	/* returns true if list methods only contains one method, false otherwise */
	public boolean isMethodUnique(List<Method> methods) {
		if(methods.size() == 1)
			return true;
		return false;
	}	
}