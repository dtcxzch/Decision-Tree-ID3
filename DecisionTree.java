import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DecisionTree {

	private TreeNode root;
	private int depth;
	private int numOfNodes;
	private int numOfLeaves;
	
	public DecisionTree() {
		root = null;
		depth = 0;
	}
	
	public TreeNode getRoot() {
		return root;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public int getNumOfNodes() {
		return numOfNodes;
	}
	
	public int getNumOfLeaves() {
		return numOfLeaves;
	}
	
	public static double computeEntropy(double a, double b) {
		return -(a * Math.log(a) / Math.log(2) + b * Math.log(b) / Math.log(2));
	}
	
	public static double computeEntropy(List<List<String>> data) {
		int n = data.size();
		if (n == 0)
			return 0;
		int benign = 0;
		int malignant = 0;
		for (List<String> list : data) {
			if (list.get(0).equals("B"))
				benign++;
			else 
				malignant++;
		}
		if (benign == n || malignant == n)
			return 0;
		return computeEntropy(benign * 1.0 / n, malignant * 1.0 / n);
	}
	
	public static Node findMinEntropy(List<List<String>> data, List<Node> comb, double setEntropy) {
		Node res = new Node(-1, -1);
		double min = setEntropy;
		int n = data.size();
		for (Node node : comb) {
			int i = node.getI();
			int j = node.getJ();
			List<List<String>> subset1 = new ArrayList<List<String>>();
			List<List<String>> subset2 = new ArrayList<List<String>>();
			for (List<String> list : data) {
				if (Integer.parseInt(list.get(i)) <= j)
					subset1.add(list);
				else
					subset2.add(list);
			}
			double entropy = subset1.size() * 1.0 / n * computeEntropy(subset1) + subset2.size() * 1.0 / n * computeEntropy(subset2);
			if (entropy < min) {
				min = entropy;
				res = node;
			}
		}
		return res;
	}
	
	public static String classify(List<List<String>> data) {
		int benign = 0;
		int malignant = 0;
		for (List<String> list : data) {
			if (list.get(0).equals("B"))
				benign++;
			else
				malignant++;
		}
		return benign > malignant ? "B" : "M";
	}
	
	public TreeNode buildTree(List<List<String>> data, List<Node> comb, double setEntropy, int d, int restrict) {
		numOfNodes++;
		TreeNode res = null;
		if (d == restrict - 1 ||comb.size() == 0) {
			res = new TreeNode(true, classify(data), d + 1);
			numOfLeaves++;
			if (root == null)
				root = res;
			depth = Math.max(depth, res.getDepth());
			return res;
		}
		boolean allB = true;
		boolean allM = true;
		for (List<String> list: data) {
			if (list.get(0).equals("B") && allM) {
				allM = false;
			} 
			else if (list.get(0).equals("M") && allB) {
				allB = false;
			}
			if (!allB && !allM)
				break;
		}
		if (allB) {
			res = new TreeNode(true, "B", d + 1);
			numOfLeaves++;
			if (root == null)
				root = res;
		}
		else if (allM) {
			res = new TreeNode(true, "M", d + 1);
			numOfLeaves++;
			if (root == null)
				root = res;
		}
		else {
			Node splitNode = findMinEntropy(data, comb, setEntropy);
			if (splitNode.getI() == -1) {
				String type = classify(data);
				res = new TreeNode(true, type, d + 1);
				numOfLeaves++;
				if (root == null)
					root = res;
			}
			else {
				int i = splitNode.getI();
				int j = splitNode.getJ();
				res = new TreeNode(i, j, d + 1);
				if (root == null) 
					root = res;		
				List<List<String>> subset1 = new ArrayList<List<String>>();
				List<List<String>> subset2 = new ArrayList<List<String>>();
				for (List<String> list : data) {
					if (Integer.parseInt(list.get(i)) <= j)
						subset1.add(list);
					else
						subset2.add(list);
				}
				double entropy1 = computeEntropy(subset1);
				double entropy2 = computeEntropy(subset2);
				List<Node> newComb = new ArrayList<Node>(comb);
				newComb.remove(splitNode);
				res.left = buildTree(subset1, newComb, entropy1, d + 1, restrict);
				res.right = buildTree(subset2, newComb, entropy2, d + 1, restrict);
			}		
		}
		depth = Math.max(depth, res.getDepth());
		return res;
	}
	
	public double getAccuracy(List<List<String>> data) {
		int n = data.size();
		int count = 0;
		for (List<String> list : data) {
			TreeNode cur = root;
			while (!cur.isLeaf()) {
				if (Integer.parseInt(list.get(cur.getI())) <= cur.getJ()) 
					cur = cur.left;
				else
					cur = cur.right;
			}
			if (cur.getLabel().equals(list.get(0)))
				count++;
		}
		return count * 1.0 / n;
	}
	
	
	public static void printTree(TreeNode root) {
		Queue<TreeNode> queue = new LinkedList<TreeNode>(); 
		queue.offer(root);
		while (!queue.isEmpty()) {
			int n = queue.size();
			for (int i = 0; i < n; i++) {
				TreeNode node = queue.poll();
				System.out.print(node);
				if (node.left != null)
					queue.offer(node.left);
				if (node.right != null)
					queue.offer(node.right);
			}
			System.out.println("");
		}
	}

	public static void main(String[] args) {
		
		// get data set from files
		List<List<String>> trainingData = DataProcessor.importData("bcan.train");
		List<List<String>> validationData = DataProcessor.importData("bcan.validate");
		List<List<String>> testData = DataProcessor.importData("bcan.test");
		
		// all possible combination of (i, j)
		List<Node> comb = new ArrayList<Node>();
		for (int i = 1; i <= 9; i++) {
			for (int j = 1; j <= 9; j++) {
				Node n = new Node(i, j);
				comb.add(n);
			}
		}
		
		// (a) build decision tree
		DecisionTree dt = new DecisionTree();
		double trainingSetEntropy = computeEntropy(trainingData);
		dt.buildTree(trainingData, comb, trainingSetEntropy, 0, Integer.MAX_VALUE);
		
		// output
		System.out.println("training set accuracy: " + dt.getAccuracy(trainingData));
		System.out.println("test set accuracy: " + dt.getAccuracy(testData));
		System.out.println("number of nodes: " + dt.getNumOfNodes());
		System.out.println("number of leaves: " + dt.getNumOfLeaves());
		System.out.println("depth: " + dt.getDepth());
		System.out.println("root: " + dt.root);
		System.out.println("children of root: " + dt.root.left + ", " + dt.root.right);
		
		// (b)
		System.out.println("\n-------------------------------------------------------------------\n");
		System.out.println("   d\t" + "# of nodes\t" + "# of leaf nodes\t\t" + "accuracy");
		double max = 0;
		int d = 0;
		for (int i = 2; i <= 20; i++) {
			dt = new DecisionTree();
			dt.buildTree(trainingData, comb, trainingSetEntropy, 0, i);
			double accuracyTest = dt.getAccuracy(testData);
			double accuracyValidate = dt.getAccuracy(validationData);
			if (accuracyValidate > max) {
				max = accuracyValidate;
				d = i;
			}
			System.out.println("   " + i + "\t    " + dt.getNumOfNodes() + "\t\t\t" + dt.getNumOfLeaves() + "\t   " + accuracyTest);
		}
		System.out.println("\n d = " + d);
	}

}
