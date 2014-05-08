package ist.meic.pa;

import java.util.ArrayList;

public class Node {
	int id;
	int previous_id;
	Object obj;
	boolean writeAccess = true;
	ArrayList<Node> adjacencyList = new ArrayList<Node>();;
		
	public Node(int previous_id, int id, Object obj, boolean writeAccess) {
		this.previous_id = previous_id;
		this.obj = obj;
		this.id = id;
		this.writeAccess = writeAccess;
	}
	
	public ArrayList<Node> getAdjacencyList() {  return adjacencyList; }
	public Object getObject() { return obj; }
	public int getPreviousId() { return previous_id; }
	public int getId() { return id; }
	public boolean hasWriteAcess() { return writeAccess; }
	
	public void insertAdjacencyList(Node n) {
		adjacencyList.add(n);
	}

	public Node getAdjacentNode(int number_of_node) {
		int i = 0;
		for(Node n : adjacencyList) {
			if(i == number_of_node) {
				return n;
			}
			i++;
		}
		System.err.println("ERROR: User specified an invalid number");
		System.exit(-1);
		return null;
	}
}
