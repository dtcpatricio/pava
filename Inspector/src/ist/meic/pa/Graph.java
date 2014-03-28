package ist.meic.pa;

import java.util.HashMap;
import java.util.Scanner;

public class Graph {
	/* id of the object currently inspected */
	int current_id = 0;
	HashMap<Integer, Node> graph = new HashMap<Integer, Node>();
	
	public Graph(Object obj, boolean writeAccess) {
		/* The previous node is the node itself -> root */
		Node node = new Node(0, 0, obj, writeAccess);
		graph.put(current_id, node);
	}

	/* Returns the object currently being inspected */
	public Object getCurrentObject() {
		int id = current_id;
		Node n = graph.get(id);
		return n.getObject();
	}
	
	public boolean getCurrentWriteAccess() {
		int id = current_id;
		Node n = graph.get(id);
		return n.hasWriteAcess();
	}
	
	/* Inserts the new inspected node object in the graph */
	public void insertInspectedNode(Object obj, boolean writeAccess) {
		Node newNode = new Node(current_id, graph.size(),  obj, writeAccess); /*Node keeps the previous object id for quicker reference */
		Node n = graph.get(current_id);
		n.insertAdjacencyList(newNode);
		current_id = graph.size(); /* The current id is the new inspected node */
		graph.put(current_id, newNode);
	}
	
	public void printGraph() {
		System.out.println("List of all inspected objects");
		for(Integer i : graph.keySet()) {
			Node n = graph.get(i);
			System.out.println((i+1) + ": " + n.getObject() /*+ " previous_id=" + n.getPreviousId()*/);
			System.out.println("\tRoutes to : ");
			for(Node adj : n.getAdjacencyList()) {
				System.out.println("\t\t" + adj.getObject() /*+ " Previous_id=" + adj.previous_id*/);
			}
			System.out.println("");
		}
	}
	
	public boolean hasNext() {
		int id = current_id;
		Node n = graph.get(id);
		if(n.getAdjacencyList().size() > 0) {
			return true;
		}
		return false;
	}
	
	public Object previous() {
		int id = current_id;
		Node n = graph.get(id);
		int previous_id = n.getPreviousId();
		Node previous_node = graph.get(previous_id);
		System.out.println("Current inspected object is : " + previous_node.getObject());
		current_id = previous_id;
		return previous_node.getObject();
	}
	
	public void printCurrent() {
		int id = current_id;
		Node n = graph.get(id);
		System.out.println("Current inspected object is : " + n.getObject());
		System.out.println("IsPrimitive: " + n.hasWriteAcess());
	}
	
	/* Se calhar ter um metodo show? */
	public Object listNext() {
		Object current_object = null;
		if(hasNext()) {
			int id = current_id;
			Node n = graph.get(id);
			int i = 0;
			System.out.println("\nChoose one of the following objects");
			for(Node adj : n.getAdjacencyList()) {
				System.out.println("[" + (i+1) + "]" + ":" + adj.getObject()); 
				i++;
			}
			System.out.println("");
			current_object = next(n,i);
			if(current_object != null) {
				return current_object; /* Devolve o objecto resultado de fazer next */
			}
		}
		return graph.get(current_id); /* Vai buscar o mesmo objecto */
	}
	
	/* Adjacent node agora já n lança null e faz exit -> corrigir no Node.java */
	public Object next(Node n, int number_of_options) {
		int option = 0;
		Scanner in = new Scanner(System.in);
		option = Integer.parseInt(in.nextLine());
		if((option < 1) || (option > number_of_options)) {
			System.out.println("Error: Invalid option");
			return null;
		}
		Node chosenNode = n.getAdjacentNode(option-1);
		System.out.println("Chosen option: " + option + " Object: " + chosenNode.getObject());
		current_id = chosenNode.getId();
		return chosenNode.getObject();
	}
	
}
