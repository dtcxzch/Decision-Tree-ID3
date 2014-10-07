
public class Node {

	private int i;
	private int j;
	
	public Node(int i, int j) {
		this.i = i;
		this.j = j;
	}
	
	public int getI() {
		return i;
	}
	
	public int getJ() {
		return j;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Node))
			return false;
		Node node = (Node)o;
		return i == node.getI() && j == node.getJ(); 
	}
	
	public int hashCode() {
		return i * 10 + j;
	}
	
	public String toString() {
		return"(" + i + ", " + j +")";
	}
}
