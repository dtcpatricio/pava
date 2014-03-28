package ist.meic.pa;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class Inspector {

	/**
	 * 
	 */
	Graph graph;

	/**
	 * false inspector running, true inspector exit
	 */
	boolean exit;
	
	/**
	 * 
	 */
	TreeMap<String, Object> savedObjects;

	/**
	 * All types supported by inspector
	 */
	InspectorTypesFactory types;

	/**
	 * Contains all commands supported by inspector - key: represents the name
	 * of the command - value: name of the method that processes command key
	 */
	HashMap<String, String> inspectorCommands;
	
	/**
	 * 
	 */
	public Inspector() {
		//	current_object = null;
		graph = new Graph();
		exit = false;
		savedObjects = new TreeMap<String, Object>();
		types = new InspectorTypesFactory();
		inspectorCommands = new HashMap<String, String>();
		initializeCommands();
	}
	
	/**
	 * Insert all commands and respective methods names
	 */
	private void initializeCommands() {
		inspectorCommands.put("q", "commandQ");
		inspectorCommands.put("i", "commandI");
		inspectorCommands.put("m", "commandM");
		inspectorCommands.put("c", "commandC");
		inspectorCommands.put("h", "commandH");
		inspectorCommands.put("next", "commandNext");
		inspectorCommands.put("previous", "commandPrevious");
		inspectorCommands.put("listall", "commandListAll");
		inspectorCommands.put("current", "commandCurrent");
		inspectorCommands.put("save", "commandSave");
		inspectorCommands.put("showsaved", "commandShowSaved");
	}

	/**
	 * Presents all object's information
	 * @param object object being evaluated
	 */
	public void inspect(Object object) {
		//	current_object = object;
		graph.insertInspectedNode(object, object.getClass().isPrimitive());
		//graph = new Graph(object, object.getClass().isPrimitive());
		exit = false;
		getInformation(object);
		read_eval_loop();
	}

	/**
	 * Retrieve information about object's
	 * 	- Classes
	 *  - Fields
	 * 	- Methods
	 * 	- Superclass's
	 * 	- Interfaces
	 * @param object
	 */
	private void getInformation(Object object) {
		System.err.println("------------------------------");
		System.err.println(object.toString() + " is an instance of "
				+ object.getClass());
		getFields(object);
		getMethods(object);
		getSuperclass(object);
		getInterfaces(object);
		System.err.println("------------------------------");
	}
	
	/**
	 * Prints all object's fields from classes and superclass's
	 * @param object
	 */
	private void getFields(Object object) {
		System.err.println("----------");
		System.err.println("Fields:");
		Class<?> objectClass = object.getClass();
		List<Field> fields = getFieldsUpTo(objectClass, Object.class);

		for (Iterator<Field> iter = fields.iterator(); iter.hasNext();) {
			Field f = iter.next();
			f.setAccessible(true);
			try {
				System.err.println("\t" + Modifier.toString(f.getModifiers())
						+ " " + f.getType() + " " + f.getName() + " = "
						+ f.get(object));
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
				continue;
			} catch (IllegalAccessException e) {
				System.err.println(e.getMessage());
				continue;
			}
		}
	}
	
	/**
	 * @param startClass
	 * @param exclusiveParent
	 * @return List<Field>
	 */
	public static List<Field> getFieldsUpTo(Class<?> startClass,
	Class<?> exclusiveParent) {
		List<Field> currentClassFields = new LinkedList<Field>(
				Arrays.asList(startClass.getDeclaredFields()));
		Class<?> parentClass = startClass.getSuperclass();
		if (parentClass != null && !(parentClass.equals(exclusiveParent))) {
			List<Field> parentClassFields = (List<Field>) getFieldsUpTo(
					parentClass, exclusiveParent);
			currentClassFields.addAll(parentClassFields);
		}
		return currentClassFields;
	}

	/**
	 * Prints all object's methods from classes and superclass's
	 * @param object
	 */
	private void getMethods(Object object) {
		System.err.println("----------");
		System.err.println("Methods");
		Class<?> objectClass = object.getClass();
		Method[] ms;
		do {
			ms = objectClass.getDeclaredMethods();
			for (Method method : ms) {
				System.err.println("\t" + method);
			}
			objectClass = objectClass.getSuperclass();
		} while (!objectClass.equals(Object.class));
	}
	
	/**
	 * Prints all object's superclass's
	 * @param object
	 */
	private void getSuperclass(Object object) {
		System.err.println("----------");
		System.err.println("Superclasses:");
		if(object.getClass().getSuperclass().equals(Object.class)) { return; }
		Class<?> objectClass = object.getClass().getSuperclass();
		do {
			System.err.println("\t" + objectClass);
			objectClass = objectClass.getSuperclass();
		} while (!objectClass.equals(Object.class));
	}

	/**
	 * Prints all object's interfaces
	 * @param object
	 */
	private void getInterfaces(Object object) {
		System.err.println("----------");
		System.err.println("Interfaces:");
		Class<?> objectClass = object.getClass();
		Class<?>[] interfaces;
		do {
			interfaces = objectClass.getInterfaces();
			for (Class<?> i : interfaces) {
				System.err.println("\t" + i);
			}
			objectClass = objectClass.getSuperclass();
		} while (!objectClass.equals(Object.class));
	}
	
	/**
	 * Read and evaluate commands provided by the user
	 */
	private void read_eval_loop() {
		while (!exit) {
			System.err.print(" > ");
			String[] command = readLine().split(" ");
			evaluateCommand(command);
		}
	}

	/**
	 * Read line from standard input stream
	 * @return line read
	 */
	private String readLine() {
		Scanner in = new Scanner(System.in);
		return in.nextLine();
	}

	/**
	 * Evaluates command provided by the user
	 * 
	 * @param command
	 */
	private void evaluateCommand(String[] command) {
		String method = command[0].toLowerCase();
		try {
			if (inspectorCommands.containsKey(method)) {
				Method commandMethod = this.getClass().getDeclaredMethod(
						inspectorCommands.get(method), String[].class);
				commandMethod.invoke(this, new Object[] { command });
			} else {
				System.err.println("Command " + command[0] + " is invalid.");
				commandH(command);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ends inspection of current_object
	 * 
	 * @param command
	 */
	public void commandQ(String[] command) {
		System.err.println("Inpector exited.");
		exit = true;
	}
	
	/**
	 * Command I
	 * @param command
	 */
	public void commandI(String[] command) {
		Field[] fields;
		Class<?> objectClass = graph.getCurrentObject().getClass();
		try {
			while (!objectClass.equals(Object.class)) {
				fields = objectClass.getDeclaredFields();
				for (Field f : fields) {
					f.setAccessible(true);
					if (command[1].equals(f.getName())) {
						Class<?> fieldType = f.getType();
						boolean isPrimitive = false;
						Object graphObject = graph.getCurrentObject();
						Object newObj = f.get(graphObject).getClass();
						System.err.println(newObj);
						if (fieldType.isPrimitive()) {
							isPrimitive = true;
						}
						System.err.println("Inspected field '" + f.getName()
								+ "' = " + f.get(graphObject));
						graph.insertInspectedNode(newObj, isPrimitive);
			
						return;
					}
				}
				objectClass = objectClass.getSuperclass();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Command M - modify the fields of the inspected object
	 * 
	 * @param command
	 */
	public void commandM(String[] command) {

		try {
			String fieldName = command[1];
			String newValue = command[2];

			List<Field> classFields;
			Field f = null;
			Object obj = graph.getCurrentObject();
			Class<?> objClass = obj.getClass();

			classFields = getFieldsUpTo(objClass, Object.class);

			for (Iterator<Field> iter = classFields.iterator(); iter.hasNext();) {
				f = iter.next();
				if (f.getName().equals(fieldName))
					break;

				if (!iter.hasNext()) {
					System.err.println("No such field exists on the object");
					return;
				}
			}

			/**
			 * Save against private or protected fields, making the parameter
			 * accessible.
			 */
			f.setAccessible(true);

			/**
			 * Create an object of the same type as the parameter, converting from
			 * a string, and catching the right exception that can be thrown when the
			 * conversion reaches an invalid state.
			 */

			Object objValue = null;

			objValue = types.getTypeValue(f.getType(), newValue);
			f.set(obj, objValue);
			System.err.println(Modifier.toString(f.getModifiers()) + " "
					+ f.getType() + " " + f.getName() + " = " + f.get(obj));

		} catch (NumberFormatException nfe) {
			System.err.println("Invalid attribution.\n" + nfe.getMessage());
			return;
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage() + "\nThe argument is not of the correct type.\nUsage:\n\t"
					+ "For char type use \'<value>\'");
			return;
		} catch (IllegalAccessException e) {
			System.err.println(e.getMessage());
			return;
		}

	}

	/**
	 * Command C
	 * 
	 * @param command
	 */
	public void commandC(String[] command) {
		Class<?> objectClass = graph.getCurrentObject().getClass();
		Method[] declaredMethods = null;
		List<Method> allMethods = new ArrayList<Method>();
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
			result = evaluateMethodsParameters(methods, parameters.size(),
					parameters);
			allMethods.addAll(methods);

			objectClass = objectClass.getSuperclass();

		} while (!objectClass.equals(Object.class) && result == null);

		returnResult(result, command[1], allMethods);
	}
	
	/**
	 * Returns list (in string format) of all parameters provided by the user
	 * @param command
	 * @return list of parameters
	 */
	private List<String> getParameters(String[] command) {
		List<String> parameters = new ArrayList<String>();

		for (int cmd = 2; cmd < command.length; cmd++) {
			parameters.add(command[cmd]);
		}
		return parameters;
	}
	
	/**
	 * Filter methods by the name of provided methods
	 * @param methods list of all declared methods of current_object
	 * @param methodName
	 * @return list with all methods with same name as methodName
	 */
	private List<Method> evaluateMethodsName(List<Method> methods,
			String methodName) {
		List<Method> bestMethods = new ArrayList<Method>();

		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				bestMethods.add(method);
			}
		}
		return bestMethods;
	}
	
	/**
	 * Filter methods that have the same number of parameters as the parameters provided by the user
	 * @param methods methods evaluated and accepted in previous evaluations
	 * @param paramNumber number of parameters provided by the user
	 * @param parameters list of parameters provided by the user
	 * @return list with methods that have the same number of parameters
	 */
	private Object evaluateMethodsParameters(List<Method> methods, int paramNumber,
			List<String> parameters) {
		List<Method> bestMethods = new ArrayList<Method>();
		Class<?>[] param = null;
		Object result = null;
		for (Method method : methods) {
			param = method.getParameterTypes();
			if (param.length == paramNumber) {
				bestMethods.add(method);
				try {
					result = methodMatchParamType(method, param, parameters);
					if (result != null) {
						return result;
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * Filters methods by the type of parameters of method 
	 * @param method methods evaluated and accepted in previous evaluations
	 * @param params array of parameters of method
	 * @param parameters list of parameters provided by the user
	 * @return result of invoking the correct method if any, otherwise an exception is thrown
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Object methodMatchParamType(Method method, Class<?>[] params,
			List<String> parameters) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Object result = null;
		Object[] args = parameters.toArray();
		for (int param = 0; param < params.length; param++) {
			args[param] = evaluateType(params[param], parameters.get(param));
		}
		method.setAccessible(true);
		result = method.invoke(graph.getCurrentObject(), args);
		return result;
	}

	/**
	 * Evaluates:
	 * 	- if a parameter is a previous saved object by the user,
	 * 	  that is being used as a parameter;
	 * 	- if paramType is an int array (special case)
	 * 	  or any of the other supported types.
	 * @param paramType type of parameter
	 * @param parameter parameter provided by the user
	 * @return parameter converted to paramType type
	 */
	private Object evaluateType(Class<?> paramType, String parameter) {
		if(savedObjects.containsKey(parameter)) {
			return savedObjects.get(parameter);
		}
		if (paramType.isArray()) {
			return evaluateIntArray(paramType, parameter);
		}
		return types.getTypeValue(paramType, parameter);
	}

	/**
	 * @param paramType type of parameter
	 * @param parameter parameter provided by the user
	 * @return parameter converted to an int array
	 */
	private Object evaluateIntArray(Class<?> paramType, String parameter) {
		String[] array = null;
		String arrayElements = "";
		int[] result;

		if (isIntArray(paramType, parameter)) {
			arrayElements = parameter.substring(1, parameter.length() - 1);
			array = arrayElements.split(",");
			result = new int[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (Integer) types.getTypeValue(
						paramType.getComponentType(), array[i]);
			}
			return result;
		}
		return null;
	}

	/**
	 * @param paramType type of parameter
	 * @param parameter parameter provided by the user
	 * @return true if paramType if an int array, false otherwise
	 */
	private boolean isIntArray(Class<?> paramType, String parameter) {
		if (paramType.isArray() && parameter.startsWith("{")
				&& parameter.endsWith("}")
				&& paramType.getComponentType().equals(int.class)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Prints result of invoking method
	 * @param result
	 */
	private void returnResult(Object result, String method, List<Method> methods) {
		if (result == null) {
			System.err.println("No match found for method " + method);
			if (methods.size() > 0) {
				System.err.println("Maybe you mean: ");
				for (int methodIndex = 0; methodIndex < methods.size(); methodIndex++) {
					System.err.println("\t" + methods.get(methodIndex));
				}
			}
		} else {
			System.err.println(result);
		}
	}
	
	/**
	 * Prints information about available commands
	 */
	/**
	 * @param command
	 */
	public void commandH(String[] command) {
		System.err.println("Available Commands:");
		System.err.println("\t- q: Terminates inspection");
		System.err
				.println("\t- i name: Inspects the value of the field named name");
		System.err
				.println("\t- m name value: Modifies the value of the field named name");
		System.err
				.println("\t- c name value0 value1 ... valuen: Calls the method named name");
		System.err
				.println("\t- next: Allows to move forward in the graph of inspected objects");
		System.err
				.println("\t- previous: Allows to move backward in the graph of inspected objects");
		System.err
				.println("\t- listall: Shows all objects in the graph of inspected objects");
		System.err.println("\t- current: Shows the current inspected object");
		System.err.println("\t- save: Save the current inspected object");
		System.err.println("\t- showsaved: Shows all user saved objects");
		System.err.println("\t- h: Help information");
	}
	
	/**
	 * @param command
	 */
	public void commandNext(String[] command) {
		graph.listNext();
	}

	/**
	 * @param command
	 */
	public void commandPrevious(String[] command) {
		graph.previous();
	}

	/**
	 * @param command
	 */
	public void commandListAll(String[] command) {
		graph.printGraph();
	}

	/**
	 * @param command
	 */
	public void commandCurrent(String[] command) {
		graph.printCurrent();
	}

	/**
	 * @param command
	 */
	public void commandSave(String[] command) {
		try {
			String name = command[1];
			Object curr = graph.getCurrentObject();
			savedObjects.put(name, curr);
		} catch (IndexOutOfBoundsException e) {
			System.err
					.println("Please provide a name for the object you are trying to save");
			return;
		}
	}

	/**
	 * @param command
	 */
	public void commandShowSaved(String[] command) {
		System.err.println("-------- Saved Objects --------");
		for(String s : savedObjects.keySet())
			System.err.println(s + " " + savedObjects.get(s));	
	}
}