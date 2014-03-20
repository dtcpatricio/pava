
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    static String readLine() {
	try {
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    PrintStream ps = System.out;
	    return br.readLine();
	}
	catch(IOException e) {
	    return null;
	}
    }
	
    static Class getClass(String className) {
	try {
	    return (Class.forName(className));
	} catch (ClassNotFoundException cnfe) {
	    System.err.println("Class '" + className + "' not found");
	    System.exit(1);
	    return null;
	}
    }
	
    static void command(List<Object> list, Map<String, Object> variables) throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		
	System.out.print("Command:> ");
	String[] command = readLine().split(" ");
		
	if(command[0].equals("Class")) {
	    Class result = getClass(command[1]);
	    System.out.println(result);
	    list.clear();
	    list.add(result.newInstance());
	    return;
	}

	if(command[0].equals("Set")) {
	    Object obj = list.get(list.size() - 1);
	    System.out.println("Saved name for object of type: " + obj.getClass());
	    System.out.println(obj.toString());
	    variables.put(command[1], obj);
	    return;
	}

	if(command[0].equals("Get")) {
	    System.out.println(command[1] + " = " + variables.get(command[1]).getClass());
	    return;
	}

	if(command[0].equals("Index")) {
	    if(list.size() > Integer.parseInt(command[1])) {
		Object obj = list.get(Integer.parseInt(command[1]));
		System.out.println(obj.toString());
		list.add(obj);
	    } else {
		System.err.println("Index out of bound");
		System.exit(1);
	    }
	    return;
	}


	System.out.println("Trying generic command: " + command[0]);

	Class concreteClass;
	Object[] result;
	Method m;
	if(command.length > 1)
	    {
		System.out.println(list.get(list.size() - 1).getClass());
		concreteClass = list.get(list.size() - 1).getClass().getClass();
		m = concreteClass.getMethod(command[0]);
		System.out.println(m.toString());
		Object[] args = new Object[] { command[1] };
		result = (Object[])m.invoke(list.get(list.size() - 1), args);

	    } else {

	    concreteClass = list.get(list.size() - 1).getClass().getClass();
	    m = concreteClass.getMethod(command[0]);
	    result = (Object[])m.invoke(list.get(list.size() - 1).getClass());
	}

	list.clear();
	for(int i = 0; i < result.length; i++) {
	    list.add(result[i]);
	    System.out.println(result[i]);
	}

		
    }
    /**
     * @param args
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws NoSuchMethodException 
     * @throws SecurityException 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     */
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
	/* Contains previous command */
	List<Object> list = new ArrayList<Object>();
		
	/* Contains names of objects as key and objects as value */
	Map<String, Object> variables = new HashMap<String, Object>();

	while(true) {
	    command(list, variables);
	}
		
		
    }

}
