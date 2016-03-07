package mlai.bayesian;

/**
 * Representation of a conditional probability table for a given attribute.
 * @author Steven Lamphear
 */
public class CondProbTable {
	int numValues;
	int numParents;
	double[][][] probabilities;
	BayesNet net;
	Node attribute;
	Node parentNode;
	Node classNode;

	/**
	 * Constructor for the conditional probability table (CPT).
	 * @param net  The Bayes Net in which this CPT is being used.
	 * @param attribute  The attribute for this CPT.
	 */
	public CondProbTable(BayesNet net, Node attribute) {
		numValues = attribute.getNumValues();
		numParents = attribute.getParents().size();
		this.net = net;
		this.attribute = attribute;

		// Parent and class nodes are initialized in calcProbabilities.
		parentNode = null;
		classNode = null;

		calcProbabilities();
	}

	/**
	 * Calculates the conditional probabilities for every combination of values
	 * for this node (attribute) and its parents. This populates the
	 * <code>probabilities[][][]</code> table using the following dimensions as
	 * indices:
	 * 1. the value of the given attribute,
	 * 2. the value of the non-class parent (where applicable), and
	 * 3. the value of the class (where applicable).
	 */
	private void calcProbabilities() {
		// Handle attributes with 0 parents (this will be the class node).
		if (numParents == 0) {
			probabilities = new double[numValues][1][1];
			for (int i = 0; i < numValues; i++) {
				probabilities[i][0][0] = attribute.getValueByIndex(i)
						.getProbability();
			}
			return;
		}

		// Handle attributes with 1 parent (this will be the root of the MST,
		// and the only parent will be the class).
		if (numParents == 1) {
			Node classNode = net.getClassNode();
			probabilities = new double[numValues][1][classNode.getNumValues()];
			for (int i = 0; i < numValues; i++) {
				for (int j = 0; j < classNode.getNumValues(); j++) {
					probabilities[i][0][j] = attribute.getValueByIndex(i)
							.getProbGivenClass(j);
				}
			}
			return;
		}

		// In all other cases, each node will have 2 parents (the parent node in
		// the MST and the class node).
		parentNode = attribute.getParents().getFirst();
		classNode = net.getClassNode();
		int numParentVals = attribute.getParents().getFirst().getNumValues();
		int numClassVals = net.getClassNode().getNumValues();
		probabilities = new double[numValues][numParentVals][numClassVals];

		// Estimate the probability of each possible combination.
		for (int classValIndex = 0; classValIndex < classNode.getNumValues(); classValIndex++) {
			NodeValue thisClassVal = classNode.getValueByIndex(classValIndex);
			
			int numerator = thisClassVal.getNumExamples();
			int denominator = net.getNumExamples();
			
			// See if Laplace estimates are in use and adjust counts
			// accordingly.
			if (net.getLearnerType().endsWith("l")) {
				numerator += 1;
				denominator += net.getClassNode().getNumValues();
			}
			
			thisClassVal.setProbability(numerator / (double) denominator);

			for (int parentValIndex = 0; parentValIndex < parentNode
					.getNumValues(); parentValIndex++) {
				NodeValue thisParentVal = parentNode
						.getValueByIndex(parentValIndex);

				for (int attValIndex = 0; attValIndex < attribute
						.getNumValues(); attValIndex++) {
					NodeValue thisAttVal = attribute
							.getValueByIndex(attValIndex);

					int count = 0;
					int givenCount = 0;

					for (Example thisExample : thisClassVal.getExamples()) {
						// See if the attribute values in this example match.
						if (thisExample.getValues().contains(thisParentVal)) {
							givenCount++;
							if (thisExample.getValues().contains(thisAttVal)) {
								count++;
							}
						}
					}
					
					numerator = count;
					denominator = givenCount;
					
					// See if Laplace estimates are in use and adjust counts
					// accordingly.
					if (net.getLearnerType().endsWith("l")) {
						numerator += 1;
						denominator += attribute.getNumValues();
					}

					// Calculate conditional probability given parent and class.
					double probability = (numerator / (double) denominator);
					probabilities[attValIndex][parentValIndex][classValIndex] = probability;
				}
			}
		}
	}

	/**
	 * Accessor method for the conditional probability of an attribute value
	 * given the values for its parent attribute and the class.
	 * @param attValIndex  The index of the value for the attribute.
	 * @param parentValIndex  The index of the value for the parent attribute.
	 * @param classValIndex  The index of the value for the class.
	 * @return  The conditional probability of the attribute value given the
	 *          values for its parent attribute and the class.
	 */
	public double getProbability(int attValIndex, int parentValIndex,
			int classValIndex) {
		return probabilities[attValIndex][parentValIndex][classValIndex];
	}
}
