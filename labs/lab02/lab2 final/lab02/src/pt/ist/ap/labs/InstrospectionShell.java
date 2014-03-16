package pt.ist.ap.labs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class InstrospectionShell {
	/* Contains object of previous command */
	List<Object> previous;
	
	Class<?> previous_class = null;
	/* Contains names of objects as key and objects as value */
	Map<String, Object> variables;
	
	boolean flagCommand = false;
	
	public InstrospectionShell() {
		previous = new ArrayList<Object>();
		variables = new HashMap<String, Object>();
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
		commandClass(command);
		commandSet(command);
		commandGet(command);
		commandIndex(command);
		commandUnknown(command);
		
		flagCommand = false;
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
	
	public void commandUnknown(String[] command) {
		try {
			if(!flagCommand) {
				System.out.println("Trying generic command: " + command[0]);
				
				Object o = null;

				
				
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
					
				}
				
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
