package mlai.bayesian;

/**
 * Representation of an edge in a spanning tree.
 * @author Steven Lamphear
 */
public class TreeEdge {
	private Node parent;
	private Node child;
	private double weight;

	/**
	 * Constructor for the tree edge. Since this is a spanning tree, each edge
	 * has only one parent and one child.
	 * @param parent  The parent node for this edge.
	 * @param child  The child node for this edge.
	 * @param weight  The weight for this edge.
	 */
	public TreeEdge(Node parent, Node child, double weight) {
		this.parent = parent;
		this.child = child;
		this.weight = weight;
	}
	
	/**
	 * Accessor method for the parent node for this edge.
	 * @return  The parent node for this edge.
	 */
	public Node getParent() {
		return this.parent;
	}
	
	/**
	 * Accessor method for the child node for this edge.
	 * @return  The child node for this edge.
	 */
	public Node getChild() {
		return this.child;
	}
	
	/**
	 * Accessor method for the weight for this edge.
	 * @return  The weight for this edge.
	 */
	public double weight() {
		return this.weight;
	}
}
