package mlai.bayesian;

import java.util.LinkedList;

/**
 * Representation of a possible value for an attribute.
 * @author Steven Lamphear
 */
public class NodeValue {

	String valueName;
	LinkedList<Example> linkedExamples;
	double probability;
	LinkedList<Double> probabilityGivenClass;
	Node node;
	int index;

	/**
	 * Constructor for the node value.
	 * @param name  The name of the attribute.
	 * @param node  The node (attribute) to which this value belongs.
	 * @param index  The index for this value's position within the list of all
	 *               possible values for this value's node.
	 */
	public NodeValue(String name, Node node, int index) {
		valueName = name;
		linkedExamples = new LinkedList<Example>();
		probabilityGivenClass = new LinkedList<Double>();
		this.node = node;
		this.index = index;
	}

	/**
	 * Accessor method for the linked list of examples which contain this
	 * value.
	 * @return  A linked list of all examples which contain this value.
	 */
	public LinkedList<Example> getExamples() {
		return linkedExamples;
	}

	/**
	 * Accessor method for the index for this value's position within the list
	 * of all possible values for this value's node.
	 * @return  The index for this value's position within the list of all
	 *          possible values for this value's node.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Accessor method for the node (attribute) to which this value belongs.
	 * @return  The node (attribute) to which this value belongs.
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Accessor method for the number of examples which contain this value.
	 * @return  The number of examples which contain this value.
	 */
	public int getNumExamples() {
		return linkedExamples.size();
	}

	/**
	 * Accessor method for the (non-conditional) probability of this value.
	 * @return  The (non-conditional) probability of this value.
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * Accessor method for the conditional probability of this value given a
	 * class value.
	 * @param classIndex  The index of the class value (used for conditioning).
	 * @return  The conditional probability of this value given the class.
	 */
	public double getProbGivenClass(int classIndex) {
		return probabilityGivenClass.get(classIndex);
	}

	/**
	 * Calculates the probability of a given class for a given example.
	 * @param thisExample  The example for which probability is being
	 *                     calculated.
	 * @param classIndex  The index of the class value being considered.
	 * @return  The probability of the given class for the given example.
	 */
	public double getProbOfClassForExample(Example thisExample, int classIndex) {
		int numParents = getNode().getParents().size();

		if (numParents == 0) {
			return getProbability();
		}

		if (numParents == 1) {
			return getProbGivenClass(classIndex);
		}

		// Otherwise, this node has 2 parents.
		Node parentNode = getNode().getParents().getFirst();
		int parentIndexForExample = 0;

		// Find the example's value for the parent node.
		for (int parentIndex = 0; parentIndex < parentNode.getValues().size(); parentIndex++) {
			if (thisExample.getValues().contains(
					parentNode.getValueByIndex(parentIndex))) {
				parentIndexForExample = parentIndex;
			}
		}

		return getNode().getCPT().getProbability(getIndex(),
				parentIndexForExample, classIndex);
	}

	/**
	 * Accessor method for the name of this value.
	 * @return  The name of this value.
	 */
	public String getValueName() {
		return valueName;
	}

	/**
	 * Adds an example to the list of examples which contain this value.
	 * @param example  An example which contains this value.
	 */
	public void linkExample(Example example) {
		linkedExamples.add(example);
	}

	/**
	 * Mutator method for the (non-conditional) probability of this value.
	 * @param probability  The (non-conditional) probability of this value.
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}

	/**
	 * Mutator method for the conditional probability of this value given a
	 * class value.
	 * @param classIndex  The index of the class value (used for conditioning).
	 * @param probability  The conditional probability of this value given the
	 *                     class.
	 */
	public void setProbGivenClass(int classIndex, double probability) {
		probabilityGivenClass.add(classIndex, probability);
	}
}
