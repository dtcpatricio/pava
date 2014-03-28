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

/**
 * @author Susana Ferreira
 *
 */
public class Inspector {
	/**
	 * 
	 */
	Object current_object;
	
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
	 * Contains all commands supported by inspector
	 * 	- key: represents the name of the command
	 *  - value: name of the method that processes command key
	 */
	HashMap<String, String> inspectorCommands;
	
	/**
	 * 
	 */
	public Inspector() {
		current_object = null;
		exit = false;
		savedObjects = new TreeMap<String, Object>();
		types = new InspectorTypesFactory();
		inspectorCommands = new HashMap<String, String>();
		initializeCommands();
	}
	
	/**
	 * Insert all commands and respective methods names
	 */
	public void initializeCommands() {
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
		current_object = object;
		exit = false;
		graph = new Graph(object, object.getClass().isPrimitive());
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
	public void getInformation(Object object) {
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
	public void getFields(Object object) {
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
	public void getMethods(Object object) {
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
	public void getSuperclass(Object object) {
		System.err.println("----------");
		System.err.println("Superclass's:");
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
	public void getInterfaces(Object object) {
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
	public void read_eval_loop() {
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
	public String readLine() {
		Scanner in = new Scanner(System.in);
		return in.nextLine();
	}

	/**
	 * Evaluates command provided by the user
	 * @param command
	 */
	public void evaluateCommand(String[] command) {
		String method = command[0].toLowerCase();
		try {
			if(inspectorCommands.containsKey(method)) {
				Method commandMethod = this.getClass().getDeclaredMethod(
					inspectorCommands.get(method), String[].class);
				commandMethod.invoke(this, new Object[] {command});
			}
			else {
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
	 * @param command
	 */
	public void commandQ(String[] command) {
		System.err.println("Inpector exited.");
		exit = true;
	}
	
	/**
	 * Command I
	 * AKI: Imprimir IS PRIMITIVE TYPE NECESSARIO???
	 * @param command
	 */
	public void commandI(String[] command) {
		Field[] fields;
		Class<?> objectClass = current_object.getClass();
		try {
			while (!objectClass.equals(Object.class)) {
				fields = objectClass.getDeclaredFields();
				for (Field f : fields) {
					f.setAccessible(true);
					if (command[1].equals(f.getName())) {
						Class<?> fieldType = f.getType();
						boolean isPrimitive = false;
						Object newObj = f.get(current_object).getClass();
						System.err.println(newObj);
						if (fieldType.isPrimitive()) {
							System.err.println("IS PRIMITIVE TYPE");
							isPrimitive = true;
						}
						System.err.println("Inspected field '" + f.getName()
								+ "' = " + f.get(current_object));
						graph.insertInspectedNode(newObj, isPrimitive);
						current_object = newObj;
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
	 * Command M
	 * AKI: Duarte era fixe se este metodo pudesse ficar mais pequeno se conseguisses
	 * @param command
	 */
	public void commandM(String[] command) {
		int superclassNum = 0;
		boolean shadowField = false;
		String fieldName = command[1];
		String newValue = command[2];
		if (command[1].charAt(0) == '-' && command[1].charAt(1) == 's') {
			superclassNum = Integer.parseInt(command[1].substring(2,
					command[1].length()));
			shadowField = true;
			try {
				fieldName = command[2];
				newValue = command[3];
			} catch (ArrayIndexOutOfBoundsException ex) {
				System.err
						.println("The command has invalid parameters. Refer to help (command h) for more information");
				return;
			}

		}

		List<Field> classFields;
		Field f = null;
		Object obj = current_object;
		Class<?> objClass = obj.getClass();

		classFields = getFieldsUpTo(objClass, Object.class);

		int i = 0;
		for (Iterator<Field> iter = classFields.iterator(); iter.hasNext();) {
			f = iter.next();
			if (f.getName().equals(fieldName)) {
				if (shadowField && i == superclassNum)
					break;

				if (shadowField && i != superclassNum) {
					i++;
					continue;
				}

				break;
			}

			if (!iter.hasNext()) {
				System.err.println("No such field exists on the object");
				return;
			}
		}

		/**
		 * Salvaguardar de campos private ou protected tornando o parâmetro
		 * accessível
		 */
		f.setAccessible(true);
		Type fieldType = f.getGenericType();
		Object objValue = null;

		/**
		 * Criar objecto do mesmo tipo do parâmetro, convertendo de uma string,
		 * e apanhar a devida excepção que pode ser lançada quando a conversão é
		 * inválida
		 */

		try {
			// AKI: este � o methodo que retorna ja um objecto do tipo fieldType.getClass
			// e com nome newValue
			types.getTypeValue(fieldType.getClass(), newValue);
			/*if (fieldType.toString().equals("int"))
				objValue = new Integer(newValue);

			if (fieldType.toString().equals("long")) {
				try {
					objValue = new Long(sanitizeLong(newValue));
				} catch (IllegalArgumentException iae) {
					System.err.println(iae.getMessage());
					return;
				}
			}

			if (fieldType.toString().equals("boolean")
					&& (newValue.toLowerCase().equals("true") || newValue
							.equals("false")))
				objValue = new Boolean(newValue);

			if (fieldType.toString().equals("short"))
				objValue = new Short(newValue);

			if (fieldType.toString().equals("byte"))
				objValue = new Byte(newValue);

			if (fieldType.toString().equals("float")) {
				try {
					objValue = new Float(sanitizeFloat(newValue));
				} catch (IllegalArgumentException iae) {
					System.err.println(iae.getMessage());
					return;
				}
			}

			if (fieldType.toString().equals("double"))
				objValue = new Double(newValue);

			if (fieldType.toString().equals("char")) {
				try {
					objValue = new Character(sanitizeChar(newValue));
				} catch (IllegalArgumentException iae) {
					System.err.println(iae.getMessage());
					return;
				}

			}

			if (fieldType.equals(String.class))
				objValue = new String(newValue);
*/
		} catch (NumberFormatException nfe) {
			System.err.println("Invalid attribution.\nThe field is of type "
					+ fieldType);
			return;
		}

		try {
			f.set(obj, objValue);
		} catch (NumberFormatException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			return;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			System.err.println(Modifier.toString(f.getModifiers()) + " "
					+ f.getType() + " " + f.getName() + " = " + f.get(obj));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/* AKI: penso que isto nao � necessario, ou se for mesmo necessario deve ficar
	 * no InspectorTypes
		private String sanitizeFloat(String newValue) {

			if (newValue.charAt(newValue.length() - 1) == 'f')
				return newValue.substring(0, newValue.length() - 1);

			throw new IllegalArgumentException(
					"The given argument is not of type float.\n* Usage <number>f\n"
							+ "Example: 5f");
		}

		private String sanitizeLong(String newValue) {

			if (newValue.charAt(newValue.length() - 1) == 'L')
				return newValue.substring(0, newValue.length() - 1);

			throw new IllegalArgumentException(
					"The given argument is not of type long.\n* Usage <number>L\n"
							+ "Example: 5L");
		}

		private char sanitizeChar(String newValue) {

			if (newValue.length() == 3 && newValue.charAt(0) == '\''
					&& newValue.charAt(2) == '\'')
				return newValue.charAt(1);

			throw new IllegalArgumentException(
					"The given argument is not of type char.\n* Usage \'<char>\'\n"
							+ "Example: \'d\'");
		}*/

	/**
	 * Command C
	 * @param command
	 */
	public void commandC(String[] command) {
		Class<?> objectClass = current_object.getClass();
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
	public List<String> getParameters(String[] command) {
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
	public List<Method> evaluateMethodsName(List<Method> methods,
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
	public Object evaluateMethodsParameters(List<Method> methods, int paramNumber,
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
	public Object methodMatchParamType(Method method, Class<?>[] params,
			List<String> parameters) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Object result = null;
		Object[] args = parameters.toArray();
		for (int param = 0; param < params.length; param++) {
			args[param] = evaluateType(params[param], parameters.get(param));
		}
		method.setAccessible(true);
		result = method.invoke(current_object, args);
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
	public Object evaluateType(Class<?> paramType, String parameter) {
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
	public Object evaluateIntArray(Class<?> paramType, String parameter) {
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
	public boolean isIntArray(Class<?> paramType, String parameter) {
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
	public void returnResult(Object result, String method, List<Method> methods) {
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
	 * AKI: introuzir a informacao sobre os restantes comandos 
	 * (DEIXEM EM LETRAS MINUSCULAS SFF) 
	 * Prints information about available commands
	 */
	/**
	 * @param command
	 */
	public void commandH(String[] command) {
		System.err.println("Available Commands:");
		System.err.println("\t- q: Terminates inspection");
		System.err.println("\t- i name: Inspects the value of the field named name");
		System.err.println("\t- m name value: Modifies the value of the field named name");
		System.err.println("\t- c name value0 value1 ... valuen: Calls the method named name");
		System.err.println("\t- next: ");
		System.err.println("\t- previous: ");
		System.err.println("\t- lall: ");
		System.err.println("\t- current: ");
		System.err.println("\t- save: ");
		System.err.println("\t- showsaved: ");
	}
	
	/**
	 * @param command
	 */
	public void commandNext(String[] command) {
		current_object = graph.listNext();
	}

	/**
	 * @param command
	 */
	public void commandPrevious(String[] command) {
		current_object = graph.previous();
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