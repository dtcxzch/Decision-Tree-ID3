
public class TreeNode extends Node{
	
	private int i;
	private int j;
	private int depth;
	private boolean isLeaf;
	private String label;
	
	protected TreeNode left;
	protected TreeNode right;
	
	public TreeNode(int i, int j) {
		super(i, j);
		isLeaf = false;
	}
	
	public TreeNode(int i, int j, int d) {
		super(i, j);
		depth = d;
		isLeaf = false;
	}
	
	public TreeNode(boolean isLeaf, String label, int d) {
		super(-1, -1);
		this.isLeaf = isLeaf;
		this.label = label;
		depth = d;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}
	
	public String getLabel() {
		return label;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof TreeNode))
			return false;
		TreeNode node = (TreeNode)o;
		return i == node.getI() && j == node.getJ();
	}
	
}
