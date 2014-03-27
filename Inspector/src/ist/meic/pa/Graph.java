package ist.meic.pa;

import java.util.HashMap;
import java.util.Scanner;

public class Graph {
	/* id of the object currently inspected */
	int current_id = 0;
	HashMap<Integer, Node> graph = new HashMap<Integer, Node>();
	
	public Graph(Object obj) {
		/* The previous node is the node itself -> root */
		Node node = new Node(0, 0, obj);
		graph.put(current_id, node);
	}

	/* Inserts the new inspected node object in the graph */
	public void insertInspectedNode(Object obj) {
		Node newNode = new Node(current_id, graph.size(),  obj); /*Node keeps the previous object id for quicker reference */
		Node n = graph.get(current_id);
		n.insertAdjacencyList(newNode);
		current_id = graph.size(); /* The current id is the new inspected node */
		graph.put(current_id, newNode);
	}
	
	public void printGraph() {
		System.out.println("Graph of inspected objects");
		for(Integer i : graph.keySet()) {
			Node n = graph.get(i);
			System.out.println("i=" + i + " " + n.getObject() + " previous_id=" + n.getPreviousId());
			System.out.println("\tAdjacent Nodes: ");
			for(Node adj : n.getAdjacencyList()) {
				System.out.println("\t\t" + adj.getObject() + " Previous_id=" + adj.previous_id);
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
	
	public void previous() {
		System.out.print("Invoked Previous commands:  ");
		int id = current_id;
		Node n = graph.get(id);
		int previous_id = n.getPreviousId();
		Node previous_node = graph.get(previous_id);
		System.out.println("The Node is : " + previous_node.getObject());
		current_id = previous_id;
	}
	
	/* Se calhar ter um metodo show? */
	public void listNext() {
		if(hasNext()) {
			int id = current_id;
			Node n = graph.get(id);
			int i = 0;
			System.out.println("\nChoose one of the following objects");
			for(Node adj : n.getAdjacencyList()) {
				System.out.println("[" + i + "]" + ":" + adj.getObject()); 
				i++;
			}
			System.out.println("");
			next(n);
		}
	}
	
	public void next(Node n) {
		int option = 0;
		Scanner in = new Scanner(System.in);
		option = Integer.parseInt(in.nextLine());
		Node chosenNode = n.getAdjacentNode(option);
		System.out.println("Chosen option: " + option + " Object: " + chosenNode.getObject());
		current_id = chosenNode.getId();
	}
	
}
