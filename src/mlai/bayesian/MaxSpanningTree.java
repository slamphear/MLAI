package mlai.bayesian;

import java.util.LinkedList;

/**
 * Representation of a maximum spanning tree.
 * @author Steven Lamphear
 */
public class MaxSpanningTree {
	private BayesNet net;
	private double[][] cmiTable;
	private LinkedList<Node> nodes;
	private LinkedList<TreeEdge> edges;
	private Node root;

	/**
	 * Constructor for the maximum spanning tree (MST).
	 * @param net  The Bayes Net for which this MST is being created.
	 * @param cmiTable  The table of conditional mutual information (CMI)
	 *                  calculations between attributes in the Bayes Net.
	 */
	public MaxSpanningTree(BayesNet net, double[][] cmiTable) {
		this.net = net;
		this.cmiTable = cmiTable;
		nodes = new LinkedList<Node>();
		edges = new LinkedList<TreeEdge>();

		// The first attribute in the list will be used as the root of the tree.
		root = net.getAttributes().getFirst();

		// Add the root.
		nodes.add(root);

		LinkedList<Node> nodesRemaining = new LinkedList<Node>();

		// Grow the tree. Start loop at 1, since the first attribute is already
		// in the tree (at the root).
		for (int i = 1; i < net.getNumAttributes(); i++) {
			nodesRemaining.add(net.getAttributes().get(i));
		}

		while (!nodesRemaining.isEmpty()) {
			findMaxCmi(nodesRemaining);
		}

	}

	/**
	 * Finds the attributes among the given nodes with the highest CMI value.
	 * @param nodesRemaining  The linked list of nodes still under
	 *                        consideration.
	 */
	private void findMaxCmi(LinkedList<Node> nodesRemaining) {
		int maxAttIndex = 0;
		int maxTreeIndex = 0;
		double maxCmi = Double.NEGATIVE_INFINITY;

		for (Node treeNode : nodes) {
			int treeIndex = treeNode.getIndex();

			for (Node thisAttribute : nodesRemaining) {
				// Skip attributes if they're already in the tree.
				if (nodes.contains(thisAttribute)) {
					continue;
				}

				int attIndex = thisAttribute.getIndex();

				double thisCmi = cmiTable[treeIndex][attIndex];

				if (thisCmi > maxCmi) {
					maxCmi = thisCmi;
					maxTreeIndex = treeIndex;
					maxAttIndex = attIndex;
				}
			}
		}

		// Create an edge from the tree node to the att node, then add the edge
		// to the tree.
		Node maxTreeNode = net.getAttributes().get(maxTreeIndex);
		Node maxAttNode = net.getAttributes().get(maxAttIndex);
		double weight = cmiTable[maxTreeIndex][maxAttIndex];
		TreeEdge newEdge = new TreeEdge(maxTreeNode, maxAttNode, weight);
		edges.add(newEdge);

		// Move the attribute node from "nodesRemaining" to "nodes".
		nodes.add(maxAttNode);
		nodesRemaining.remove(maxAttNode);

		// Finally, update parent/child relationships.
		maxTreeNode.addChild(maxAttNode);
		maxAttNode.addParent(maxTreeNode);
	}

	/**
	 * Accessor method for the list of edges in this MST.
	 * @return  A linked list of edges in this MST.
	 */
	public LinkedList<TreeEdge> getEdges() {
		return edges;
	}

	/**
	 * Accessor method for the list of nodes in this MST.
	 * @return  A linked list of nodes in this MST.
	 */
	public LinkedList<Node> getNodes() {
		return nodes;
	}

	/**
	 * Accessor method for the root node of this MST.
	 * @return  The root node of this MST.
	 */
	public Node getRoot() {
		return root;
	}
}
