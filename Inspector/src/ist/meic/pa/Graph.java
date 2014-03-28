package ist.meic.pa;

import java.util.HashMap;
import java.util.Scanner;

public class Graph {
	/* id of the object currently inspected */
	int current_id = 0;
	HashMap<Integer, Node> graph = new HashMap<Integer, Node>();
	
	
	public Graph() { }
	
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
		if(graph.isEmpty()) { 
			Node node = new Node(0, 0, obj, writeAccess);
			graph.put(current_id, node);
			return;
		}
		Node newNode = new Node(current_id, graph.size(),  obj, writeAccess); /*Node keeps the previous object id for quicker reference */
		Node n = graph.get(current_id);
		n.insertAdjacencyList(newNode);
		current_id = graph.size(); /* The current id is the new inspected node */
		graph.put(current_id, newNode);
	}
	
	public void printGraph() {
		System.err.println("List of all inspected objects");
		for(Integer i : graph.keySet()) {
			Node n = graph.get(i);
			System.err.println((i+1) + ": " + n.getObject() /*+ " previous_id=" + n.getPreviousId()*/);
			System.err.println("\tRoutes to : ");
			for(Node adj : n.getAdjacencyList()) {
				System.err.println("\t\t" + adj.getObject() /*+ " Previous_id=" + adj.previous_id*/);
			}
			System.err.println("");
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
		System.err.println("Current inspected object is : " + previous_node.getObject());
		current_id = previous_id;
		return previous_node.getObject();
	}
	
	public void printCurrent() {
		int id = current_id;
		Node n = graph.get(id);
		System.err.println("Current inspected object is : " + n.getObject());
		System.err.println("IsPrimitive: " + n.hasWriteAcess());
	}
	
	public void listNext() {
		if(hasNext()) {
			int id = current_id;
			Node n = graph.get(id);
			int i = 0;
			System.err.println("\nChoose one of the following objects");
			for(Node adj : n.getAdjacencyList()) {
				System.err.println("[" + (i+1) + "]" + ":" + adj.getObject()); 
				i++;
			}
			System.err.println("");
			next(n,i);
		}
		return;
	}
	

	public void next(Node n, int number_of_options) {
		int option = 0;
		Scanner in = new Scanner(System.in);
		option = Integer.parseInt(in.nextLine());
		if((option < 1) || (option > number_of_options)) {
			System.err.println("Error: Invalid option");
			return;
		}
		Node chosenNode = n.getAdjacentNode(option-1);
		System.err.println("Chosen option: " + option + " Object: " + chosenNode.getObject());
		current_id = chosenNode.getId();
		return;
	}
	
}
