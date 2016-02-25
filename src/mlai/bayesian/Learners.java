package mlai.bayesian;

import java.util.LinkedList;

/**
 * Methods for Bayesian learners.
 * @author Steven Lamphear
 */
public class Learners {

	/**
	 * Creates conditional probability tables for attributes.
	 * @param net  The Bayes Net whose attributes need CPTs.
	 */
	public static void createCPTs(BayesNet net) {
		int numExamples = net.getTrainExamples().size();

		for (NodeValue classValue : net.getClassNode().getValues()) {
			// Set the probability for each value in the class.
			classValue
					.setProbability((classValue.getNumExamples() + 1)
							/ (double) (numExamples + net.getClassNode()
									.getNumValues()));
		}

		// Create a CPT for the class ndoe.
		net.getClassNode().setCPT(new CondProbTable(net, net.getClassNode()));

		// Now do the same for each attribute.
		for (Node thisAttribute : net.getAttributes()) {

			// Set the probability for each value.
			for (NodeValue thisAttVal : thisAttribute.getValues()) {
				// Set probability for this value.
				thisAttVal.setProbability(thisAttVal.getNumExamples()
						/ (double) (net.getNumExamples() + thisAttribute
								.getNumValues()));
			}

			// Create a new CPT for this node.
			thisAttribute.setCPT(new CondProbTable(net, thisAttribute));
		}
	}

	/**
	 * Learner which calculates the probability of each attribute given the class.
	 * @param net  The Bayes Net for which the learning is being performed.
	 */
	public static void naive(BayesNet net) {
		// Start by setting the probabilities for each value given the example's
		// class value.
		setProbabilitiesGivenClass(net);

		// Then update the structure by setting the parent for each attribute.
		for (Node thisAttribute : net.getAttributes()) {
			// For Naive Bayes, the only parent is the class.
			thisAttribute.addParent(net.getClassNode());
		}
	}

	/**
	 * Sets the probability, and the probability given the class, for each
	 * attribute in the Bayes Net. 
	 * @param net  The Bayes Net for which the learning is being performed.
	 */
	public static void setProbabilitiesGivenClass(BayesNet net) {
		int numExamples = net.getTrainExamples().size();

		// Estimate the probability of each possible value for the class.
		for (int classIndex = 0; classIndex < net.getClassNode().getNumValues(); classIndex++) {
			NodeValue thisClassValue = net.getClassNode().getValueByIndex(
					classIndex);
			thisClassValue
					.setProbability((thisClassValue.getNumExamples() + 1)
							/ (double) (numExamples + net.getClassNode()
									.getNumValues()));

			// Estimate P(X_i = x | Y = y) for each X_i
			for (Node thisAttribute : net.getAttributes()) {
				for (NodeValue thisAttributeValue : thisAttribute.getValues()) {
					// Start count at 1 for Laplace estimates.
					int count = 1;

					for (Example thisExample : thisAttributeValue.getExamples()) {
						// See if the class value of this example matches the
						// class value of this loop iteration.
						if (thisExample.getClassValue().equals(thisClassValue)) {
							count++;
						}
					}

					// Calculate probabilities.
					double probability = (count / (double) (net
							.getNumExamples() + thisAttribute.getNumValues()));
					double probabilityGivenClass = (count / (double) (thisClassValue
							.getNumExamples() + thisAttribute.getNumValues()));

					// Set probabilities.
					thisAttributeValue.setProbability(probability);
					thisAttributeValue.setProbGivenClass(classIndex,
							probabilityGivenClass);
				}
			}
		}
	}

	/**
	 * Learner which constructs a maximum spanning tree to augment the Naive 
	 * Bayes structure, then calculates the probability of each attribute given
	 * its parents.
	 * @param net  The Bayes Net for which the learning is being performed.
	 */
	public static void tan(BayesNet net) {
		double[][] cmiTable = new double[net.getNumAttributes()][net
				.getNumAttributes()];
		LinkedList<Node> attributes = net.getAttributes();

		// Start by setting the probabilities for each value given the example's
		// class value.
		setProbabilitiesGivenClass(net);

		// Then loop through each combination of attributes to complete the CMI
		// table.
		for (int i = 0; i < net.getNumAttributes(); i++) {
			for (int j = 0; j < net.getNumAttributes(); j++) {
				cmiTable[i][j] = net.calcCMI(attributes.get(i),
						attributes.get(j));
			}
		}

		// Next, find a maximum-weight spanning tree (MST) for the graph over
		// X_1,...,X_n.
		MaxSpanningTree mst = new MaxSpanningTree(net, cmiTable);

		// Make the class a parent to each node.
		for (Node thisNode : mst.getNodes()) {
			thisNode.addParent(net.getClassNode());
		}

		// Link the Maximum Spanning Tree to the Bayesian Network.
		net.setMST(mst);

		// Finally, calculate the probabilities (given parents) for each value.
		createCPTs(net);
	}
}
