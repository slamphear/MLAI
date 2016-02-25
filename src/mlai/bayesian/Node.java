package mlai.bayesian;

import java.util.LinkedList;

/**
 * Representation of a node in a maximum spanning tree, or an attribute in a
 * Bayesian Network.
 * @author Steven Lamphear
 */
public class Node {
	private String name;
	private LinkedList<NodeValue> values;
	private LinkedList<Node> parents;
	private LinkedList<Node> children;
	private int index;
	private CondProbTable cpt;

	/**
	 * Constructor for the node.
	 * @param name  The name of the attribute.
	 * @param index  The index for this attribute in the list of attributes.
	 */
	public Node(String name, int index) {
		this.name = name;
		this.index = index;
		values = new LinkedList<NodeValue>();
		parents = new LinkedList<Node>();
		children = new LinkedList<Node>();
		cpt = null; // Use setCPT to initialize.
	}

	/**
	 * Adds a node to the list of children of the current node.
	 * @param child  The node to be added as a child node.
	 */
	public void addChild(Node child) {
		children.add(child);
	}

	/**
	 * Adds a node to the list of parents of the current node.
	 * @param parent  The node to be added as a parent node.
	 */
	public void addParent(Node parent) {
		parents.add(parent);
	}

	/**
	 * Adds a value to the list of node values for the current node.
	 * @param valueName The name of the value to be added.
	 */
	public void addValue(String valueName) {
		NodeValue thisValue = new NodeValue(valueName, this, values.size());
		values.add(thisValue);
	}

	/**
	 * Accessor method for the name of the current attribute.
	 * @return  The name of the current attribute.
	 */
	public String getAttributeName() {
		return name;
	}

	/**
	 * Accessor method for the list of children of the current node.
	 * @return  The list of children of the current node.
	 */
	public LinkedList<Node> getChildren() {
		return children;
	}

	/**
	 * Accessor method for the conditional probability table for the current
	 * node.
	 * @return  The conditional probability table for the current node.
	 */
	public CondProbTable getCPT() {
		return cpt;
	}

	/**
	 * Accessor method for the index of the current node in the list of
	 * attributes for the Bayes Net.
	 * @return  The index of the current node.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Accessor method for the number of different values for this attribute.
	 * @return  The number of different values for this attribute.
	 */
	public int getNumValues() {
		return values.size();
	}

	/**
	 * Accessor method for the list of parents of the current node.
	 * @return  The list of parents of the current node.
	 */
	public LinkedList<Node> getParents() {
		return parents;
	}

	/**
	 * Used to retrieve a specific value for the current node when given the
	 * value's index.
	 * @param index  The index for the desired value.
	 * @return  The value with the given index.
	 */
	public NodeValue getValueByIndex(int index) {
		return values.get(index);
	}

	/**
	 * Used to retrieve a specific value for the current node when given the
	 * name of the requested value.
	 * @param valueName  The name of the requested value.
	 * @return  The value for the current node whose name matches the provided
	 *          name.
	 */
	public NodeValue getValueByName(String valueName) {
		for (NodeValue thisValue : values) {
			if (thisValue.getValueName().equals(valueName)) {
				return thisValue;
			}
		}

		// Handle cases where a value cannot be found.
		System.err.println("Value " + valueName + " not found for attribute "
				+ name + ".");
		System.exit(1);
		return null;
	}

	/**
	 * Accessor method for the list of values for this attribute.
	 * @return  The Linked List of values for this attribute.
	 */
	public LinkedList<NodeValue> getValues() {
		return values;
	}

	/**
	 * Mutator method for the conditional probability table for the current
	 * node.
	 * @param cpt  The conditional probability table for the current node.
	 */
	public void setCPT(CondProbTable cpt) {
		this.cpt = cpt;
	}

	/**
	 * Mutator method for the index of the current node.
	 * @param index  The index of the current node.
	 */
	public void setIndex(int index) {
		this.index = index;
	}

}
