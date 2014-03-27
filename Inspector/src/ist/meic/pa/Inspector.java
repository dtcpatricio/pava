package ist.meic.pa;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Inspector {
	/* Contains object of previous command */
	List<Object> previous;

	Class<?> previous_class = null;
	/* Contains names of objects as key and objects as value */
	Map<String, Object> variables;

	boolean flagCommand = false;

	public Inspector() {
		previous = new ArrayList<Object>();
		variables = new HashMap<String, Object>();
	}
	
	public void inspect(Object object) {
		getInformation(object);
		read_eval_loop();
	}
	
	/* Retrieve information about Object object */
	public void getInformation(Object object) {
		System.err.println(object.toString() + " is an instance of " + object.getClass());
		System.err.println("----------");
		getFields(object);
		getMethods(object);
		
		previous.add(object);
		
	}
	
	/* reads ands evaluate commands provided by the user */
	public void read_eval_loop() {
		while(true) {
			System.err.print("> ");
			String[] command = readLine().split(" ");
			command(command);
		}
	}
	
	
	
	public void getMethods(Object object) {
		Class<?> objectClass = object.getClass();
		Method[] methods;
		do {
			methods = objectClass.getDeclaredMethods();
			for (Method method : methods) {
				System.err.println(method);
			}
			objectClass = objectClass.getSuperclass();
		}
		while(!objectClass.equals(Object.class));
	}
	
	public void getSuperClasses(Object object) {
		boolean superclass = true;
		while(superclass) {
			if(!object.getClass().getSuperclass().equals(Object.class)) {
				getMethods(object.getClass().getSuperclass());
			}
			else {
				superclass = false;
			}
		}
	}
	
	/* Duvida: os fields private e protected de superclasses devem ser impressos? 
	 * E os static imprimem-se?*/
	public void getFields(Object object) {
		Class<?> objectClass = object.getClass();
		Field[] fields;
		do {
			fields = objectClass.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				try {
					field.get(object);
					System.err.println(Modifier.toString(field.getModifiers()) + " " + field.getType()
							+ " " + field.getName() + " = " + field.get(object));
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
		if(command[0].toLowerCase().equals("m"))
			commandModify(command);
		
		/*
		commandClass(command);
		commandSet(command);
		commandGet(command);
		commandIndex(command);
		commandC(command);*/

		//flagCommand = false;
	}

	// Código de função do StackOverflow
	// @return type mudado de Iterable<Field> para List<Field>
	// @return List<Field> como LinkedList<Field> pois Arrays.asList retorna fixed-size
	public static List<Field> getFieldsUpTo(Class<?> startClass, Class<?> exclusiveParent) {

		List<Field> currentClassFields = new LinkedList<Field>(Arrays.asList(startClass
				.getDeclaredFields()));
		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null && !(parentClass.equals(exclusiveParent))) {
			List<Field> parentClassFields = (List<Field>)getFieldsUpTo(parentClass, exclusiveParent);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}
	
	private void commandModify(String[] command) {
		
		if(command.length != 3) {
			System.err.println("Invalid command.\nUsage:\n m <field> <value>");
			return;
		}
			
		String fieldName = command[1];
		String newValue = command[2];
		List<Field> classFields;
		Field f = null;
		Object obj = previous.get(previous.size() - 1);
		Class<?> objClass = obj.getClass();
		
		classFields = getFieldsUpTo(objClass, Object.class);
		for(Iterator<Field> iter = classFields.iterator(); iter.hasNext(); ) {
			f = iter.next();
			if(f.getName().equals(fieldName))
				break;
			
			if(!iter.hasNext()) {
				System.err.println("No such field exists on the object");
				return;
			}
		}
		
		/** 
		 * 	Salvaguardar de campos private ou protected
		 *  tornando o parâmetro accessível
		 */
		f.setAccessible(true);		
		Type fieldType = f.getGenericType();
		Object objValue = null;
		
		/** 
		 *  Criar objecto do mesmo tipo do parâmetro,
		 *  convertendo de uma string, e apanhar a devida
		 *  excepção que pode ser lançada
		 *  quando a conversão é inválida
		 */
		
		try {
		if(fieldType.toString().equals("int"))
			objValue = new Integer(newValue);
		
		if(fieldType.toString().equals("long"))
			objValue = new Long(newValue);
		
		if(fieldType.toString().equals("boolean") &&
				(newValue.equals("true") || newValue.equals("false") ))
			objValue = new Boolean(newValue);
			
		
		if(fieldType.toString().equals("short"))
			objValue = new Short(newValue);
		
		if(fieldType.toString().equals("byte"))
			objValue = new Byte(newValue);
		
		if(fieldType.toString().equals("float"))
			objValue = new Float(newValue);
		
		if(fieldType.toString().equals("double"))
			objValue = new Double(newValue);
		
		if(fieldType.toString().equals("char")) {
			objValue = new Character(sanitizeChar(newValue));
			if(objValue.equals(" ")) {
				System.err.println("Invalid modification of a char field.\nThe char should be between \'<char>\'");
				return;
			}
		}
		
		if(fieldType.equals(String.class))
			objValue = new String(newValue);
		
		} catch(NumberFormatException nfe) {
			System.err.println("Invalid attribution.\nThe field is of type " + fieldType);
			return;
		}
		
		
		try {
			f.set(obj, objValue);
		} catch (NumberFormatException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.err.println(Modifier.toString(f.getModifiers()) + " " + f.getType()
					+ " " + f.getName() + " = " + f.get(obj));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private char sanitizeChar(String newValue) {
		
		if(newValue.length() == 3 &&
				newValue.charAt(0) == '\'' &&
				newValue.charAt(2) == '\'')
			return newValue.charAt(1);
		
		return 0;
	}

	public void commandClass(String[] command) {
		try {
			if(command[0].toLowerCase().equals("class") && !flagCommand) {
				Class<?> result = getClass(command[1]);

				Object o = null;

				/* input as parameters? */
				if(command.length >= 3) {
					if(command[1].equals("java.lang.Integer")) {
						Constructor<Integer> c = Integer.class.getConstructor(int.class);
						o = c.newInstance(Integer.parseInt(command[2]));
					}
					if(command[1].equals("java.lang.String")) {
						Constructor<String> c = String.class.getConstructor(String.class);
						o = c.newInstance(command[2]);
					}
				}
				else {
					o = result;
				}

				System.out.println(result);
				previous.clear();
				previous.add(o);
				previous_class = result;
				flagCommand = true;
			}
		} catch(Exception e) {
			System.err.println("commandClass " + e.getMessage());
		}
	}

	public void commandSet(String[] command) {
		if(command[0].toLowerCase().equals("set") && !flagCommand) {
			System.out.println("Saved name for object of type: " + previous.get(previous.size() - 1).getClass());
			System.out.println(previous.get(previous.size() - 1));
			Object p =  previous.get(previous.size() - 1);
			variables.put(command[1], p);
			previous.clear();
			previous.add(p);
			flagCommand = true;
		}
	}

	public void commandGet(String[] command) {
		if(command[0].toLowerCase().equals("get") && !flagCommand) {
			System.out.println(variables.get(command[1]));
			previous.clear();
			previous.add(variables.get(command[1]));
			flagCommand = true;
		}
	}

	public void commandIndex(String[] command) {
		if(command[0].toLowerCase().equals("index") && !flagCommand) {
			if(previous.size() > Integer.parseInt(command[1])) {
				Object o = previous.get(Integer.parseInt(command[1]));
				System.out.println(o);
				previous.clear();
				previous.add(o);
			}
			else {
				System.err.println("Index out of bound");
			}
			flagCommand = true;
		}
	}

	/* Command c name value0 value1 ... valuen */
	public void commandC(String[] command) {
		try {
			Object o = null;

			Method m = previous.get(previous.size() - 1).getClass().getMethod(command[0]);
			Object result = m.invoke(previous.get(previous.size() - 1));

			previous.clear();

			if(result.getClass().isArray()) {
				Object[] resultArray = (Object[])result;
				for(int i = 0; i < resultArray.length; i++) {
					previous.add(resultArray[i]);
					System.out.println(resultArray[i]);
				}
			}
			else {
				previous.add(result);
				System.out.println(result);
			}


			if(command.length >= 2) {
				Method[] ms = previous.get(previous.size() - 1).getClass().getMethods();
				//Method m = previous.get(previous.size() - 1).getClass().getMethod(command[0], Object[].class);
				/*System.out.println(previous.get(previous.size() - 1));
				Object[] a = new Object[] {command[1]};
				Object o2 = m.invoke(previous.get(previous.size() - 1), (Object)a);			
				*/

				Method method;
				String[] methodName;
				String className = previous.get(previous.size() - 1).getClass().toString();
				System.out.println("Class: " + className);
				for(int i = 0; i < ms.length; i++) {
					method = ms[i];
					methodName = (String[])ms[i].toString().split(" ");
					Class<?>[] param = method.getParameterTypes();
					for(int j = 0; j < methodName.length; j++) {
						// Is method of name command[0]
						if(methodName[j].contains(command[0])) {
							System.out.println("	" + methodName[j]);
							// Are provided parameters equal to expected parameters of list?
							if(param.length == command.length - 1) {
								System.out.println("Method " + method + " correct");
									Object o2 = method.invoke(previous.get(previous.size() - 1), (Object)new Object[] {command[1]}); 
										//command[1].getClass().cast(param[0].getClass())); 
								System.out.println("RESULT: " + o2);
								break;
							}
						}
					}


				}

					//System.out.println("AKIII: " + result);
					//result = (Object[])invoke(previous.get(previous.size() - 1), command[0], command[1]);

					/*previous.clear();
					previous.add(o2);
					System.out.println(o2);
					*/
				}
				else {
					

				}
		} catch(Exception e) {
			System.err.println("commandUnknown: " + e.toString());
			e.printStackTrace();
		}

	}

	public void execute() {
		System.out.println("Insert Command: ");

		String[] command = readLine().split(" ");

		command(command);

	}
}