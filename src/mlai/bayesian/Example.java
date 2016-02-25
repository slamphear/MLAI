package mlai.bayesian;

import java.util.LinkedList;

/**
 * Representation of an example in a data set.
 * @author Steven Lamphear
 */
public class Example {
	int numValues;
	NodeValue classValue;
	private LinkedList<NodeValue> values;
	double[][] probabilityGivenClass;
	int predictedClassIndex;
	double probOfPrediction;

	/**
	 * Constructor for the example.
	 * @param valueStrings  An array of the values for each attribute for this
	 *                      example.
	 * @param attributes  A linked list of the attribute nodes for which this
	 *                    example has data.
	 * @param classNode  The class (label) node for this example.
	 */
	public Example(String[] valueStrings, LinkedList<Node> attributes,
			Node classNode) {
		numValues = valueStrings.length;
		values = new LinkedList<NodeValue>();
		probabilityGivenClass = new double[attributes.size()][classNode
				.getValues().size()];

		// First, set the class. (This will be the last entry in the
		// valueStrings array.)
		classValue = classNode.getValueByName(valueStrings[numValues - 1]
				.trim());

		// Link the example to the class value.
		classValue.linkExample(this);

		// Loop over attributes.
		for (int i = 0; i < (numValues - 1); i++) {
			NodeValue thisValue = attributes.get(i).getValueByName(
					valueStrings[i].trim());

			// Link the value to the example.
			values.add(thisValue);

			// Link the example to the value.
			thisValue.linkExample(this);
		}
	}

	/**
	 * Accessor method for the class value for this example. This is only
	 * populated for training examples.
	 * @return  The class value for this example.
	 */
	public NodeValue getClassValue() {
		return classValue;
	}
	
	/**
	 * Accessor method for the index of the predicted class value for this
	 * example. This is only populated for test examples.
	 * @return  The index of the predicted class value for this example.
	 */
	public int getPredictedClassIndex() {
		return predictedClassIndex;
	}
	
	/**
	 * Accessor method for the probability (certainty) that the class index in
	 * <code>predictedClassIndex</code> is correct.
	 * @return  The probability (certainty) that the class index in
	 *          <code>predictedClassIndex</code> is correct.
	 */
	public double getProbOfPrediction() {
		return probOfPrediction;
	}

	/**
	 * Accessor method for the values for each attribute in this example.
	 * @return  A linked list of values for each attribute in this example.
	 */
	public LinkedList<NodeValue> getValues() {
		return values;
	}
	
	/**
	 * Mutator method for the index of the predicted class value for this
	 * example. This is only populated for test examples.
	 * @param classIndex
	 */
	public void setPredictedClassValue(int classIndex) {
		this.predictedClassIndex = classIndex;
	}
	
	/**
	 * Mutator method for the probability (certainty) that the class index in
	 * <code>predictedClassIndex</code> is correct.
	 * @param probability  The probability (certainty) that the class index in
	 *                     <code>predictedClassIndex</code> is correct.
	 */
	public void setProbOfPrediction(double probability) {
		this.probOfPrediction = probability;
	}
}
