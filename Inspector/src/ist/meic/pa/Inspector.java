package ist.meic.pa;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class Inspector {
	/**
	 * Contains object beign evaluated
	 */
	Object current_object = null;

	InspectorTypesFactory types;
	boolean flagCommand = false;

	public Inspector() {
	}

	public void inspect(Object object) {
		current_object = object;
		getInformation(object);
		read_eval_loop();
	}

	/**
	 * Retrieve information about Object object
	 */
	public void getInformation(Object object) {
		System.err.println(object.toString() + " is an instance of "
				+ object.getClass());

		System.err.println("----------");
		System.err.println("Fields:");
		getFields(object);

		System.err.println("----------");
		System.err.println("Methods");
		getMethods(object);
		getSuperClasses(object);
		getInterfaces(object);

	}

	/* reads ands evaluate commands provided by the user */
	public void read_eval_loop() {
		while (true) {
			System.err.print(" > ");
			String[] command = readLine().split(" ");
			command(command);
		}
	}

	public void getMethods(Object object) {

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

	public void getSuperClasses(Object object) {
		System.err.println("----------");
		System.err.println("Superclasses:");
		Class<?> objectClass = object.getClass().getSuperclass();
		do {
			System.out.println("\t" + objectClass);
			objectClass = objectClass.getSuperclass();
		} while (!objectClass.equals(Object.class));
	}

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
	 * Obtem e imprime os atributos do objecto e das suas superclasses. Utiliza
	 * a função getFieldsUpTo para obter os atributos pela hierarquia acima até
	 * à superclasse Object.
	 */
	public void getFields(Object object) {
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

	public String readLine() {
		Scanner in = new Scanner(System.in);
		return in.nextLine();
	}

	public void command(String[] command) {
		// commandC(command);
		if (command[0].toLowerCase().equals("m"))
			commandModify(command);

		if (command[0].toLowerCase().equals("c"))
			commandC(command);
		/*
		 * commandI(command); commandQ(command); if(flagCommand == false) {
		 * System.out.println("Error: Unknown command : the term '" + command[0]
		 * + "' is not recognized as the name of a command, please try again!\n"
		 * + "Type -help for more information"); } flagCommand = false;
		 */
	}

	/* Returns list (in string format) of all parameters provided by the user */
	public List<String> getParameters(String[] command) {
		List<String> parameters = new ArrayList<String>();

		for (int cmd = 2; cmd < command.length; cmd++) {
			parameters.add(command[cmd]);
		}
		return parameters;
	}

	/**
	 * @param command
	 *            user input
	 */
	public void commandC(String[] command) {
		Class<?> objectClass = current_object.getClass();
		Method[] declaredMethods = null;
		List<Method> allMethods = new ArrayList<Method>();
		List<Method> methods = new ArrayList<Method>();
		List<String> parameters = getParameters(command);
		Object result = null;

		if (!command[0].toLowerCase().equals("c"))
			return;
		do {
			declaredMethods = objectClass.getDeclaredMethods();
			methods.clear();
			for (Method method : declaredMethods) {
				methods.add(method);
			}
			methods = evaluateMethodsName(methods, command[1]);
			allMethods.addAll(methods);
			result = evaluateMethodsParam(methods, parameters.size(),
					parameters);

			objectClass = objectClass.getSuperclass();
		} while (!objectClass.equals(Object.class) && result == null);

		returnResult(result, command[1], allMethods);
	}

	/**
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

	/* Receives list of methods */
	/* Obtain list with all methods with same name as methodName */
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

	/*
	 * Return list with methods that have the same number of parameters as the
	 * input
	 */
	/* paramNumber number of parameters provided by the user */
	/* parameters: list of parameters provided by the user */
	public Object evaluateMethodsParam(List<Method> methods, int paramNumber,
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
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("IllegalArgumentException");
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					System.out.println("IllegalAccessException");
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					System.out.println("InvocationTargetException");
				}
			}
		}

		return null;
	}

	/*
	 * returns list that contains all methods with paramNumber as number of
	 * parameters
	 */
	/*
	 * Primitive types: boolean(0), byte(1), short(2), int(3), long(4), char(5),
	 * float(6) and double(7) Reference types: java.lang.String(8),
	 * java.io.Serializable, java.lang.Integer, ARRAYS, all others Types are
	 * represented as ints CRIAR MACROS NO INICIO DO FICHEIRO, unknown (-1)
	 * LIMPAR EXCEPCOES
	 */
	/*
	 * Returns a list containing each type of each parameter Analyzes parameters
	 * provided by the user: - if begins and ends with "" is considered a
	 * string, example: "5", "true" - true or false are considered booleans -
	 * ending in L or l, is considered a long, but could be accepted as int, or
	 * double, or float, example: 10L - ending in F or f, is considered a float,
	 * but could be accepted as int, or double or float, example: 10f - ending
	 * in D or d, is considered a double, but could be accepted as int, or float
	 * example: 10d
	 */
	// ORGANIZAR ESTE CODIGO
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

	// Return an object array the first element of the array is either 0 or 1
	// 0 means the type of the return is not an array, 1 otherwise
	public Object evaluateType(Class<?> paramType, String parameter) {		
		if (paramType.isArray()) {
			return evaluateIntArray(paramType, parameter);
		}
		return types.getTypeValue(paramType, parameter);
	}

	public Object evaluateIntArray(Class<?> paramType, String parameter) {
		String[] array = null;
		String arrayElements = "";
		int[] result;

		if (isIntArray(paramType, parameter)) {
			arrayElements = parameter.substring(1, parameter.length() - 1);
			array = arrayElements.split(",");
			result = new int[array.length];

			// All elements of array are of the same type
			for (int i = 0; i < array.length; i++) {
				result[i] = (Integer) types.getTypeValue(
						paramType.getComponentType(), array[i]);
			}
			return result;
		}
		return null;
	}

	// checks if is an array
	public boolean isIntArray(Class<?> paramType, String parameter) {
		if (paramType.isArray() && parameter.startsWith("{")
				&& parameter.endsWith("}")
				&& paramType.getComponentType().equals(int.class)) {
			return true;
		}
		return false;
	}


	/**
	 * Função principal do comando 'm'
	 * 
	 * @param command
	 */
	private void commandModify(String[] command) {

		/**
		 * if(command.length != 3 || command.length != 4) { System.err.println(
		 * "The command is invalid.\n* Usage: m [OPTION] <field> <value>\n" +
		 * "The [OPTION] can be -s<number>\t " +
		 * "Indicates which superclass on the hierarchy is to have it's field modified"
		 * ); return; } /** Extensão
		 */
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
			if (fieldType.toString().equals("int"))
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

	/**
	 * Código de função do StackOverflow
	 * 
	 * @param startClass
	 * @param exclusiveParent
	 * @return type mudado de Iterable<Field> para List<Field>
	 * @return List<Field> como LinkedList<Field> pois Arrays.asList retorna
	 *         fixed-size
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
	}
}