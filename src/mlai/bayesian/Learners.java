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
			int numerator = classValue.getNumExamples();
			int denominator = numExamples;
			
			// See if Laplace estimates are in use and adjust counts
			// accordingly.
			if (net.getLearnerType().endsWith("l")) {
				numerator += 1;
				denominator += net.getClassNode().getNumValues();
			}
			
			// Set the probability for each value in the class.
			classValue.setProbability(numerator / (double) denominator);
		}

		// Create a CPT for the class node.
		net.getClassNode().setCPT(new CondProbTable(net, net.getClassNode()));

		// Now do the same for each attribute.
		for (Node thisAttribute : net.getAttributes()) {

			// Set the probability for each value.
			for (NodeValue thisAttVal : thisAttribute.getValues()) {
				int numerator = thisAttVal.getNumExamples();
				int denominator = net.getNumExamples();
				
				// See if Laplace estimates are in use and adjust counts
				// accordingly.
				if (net.getLearnerType().endsWith("l")) {
					numerator += 1;
					denominator += thisAttribute.getNumValues();
				}
				
				// Set probability for this value.
				thisAttVal.setProbability(numerator / (double) denominator);
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
		for (int classIndex = 0; classIndex < net.getClassNode()
				.getNumValues(); classIndex++) {
			NodeValue thisClassValue = net.getClassNode()
					.getValueByIndex(classIndex);
			int numerator = thisClassValue.getNumExamples();
			int denominator = numExamples;
			
			// See if Laplace estimates are in use and adjust counts
			// accordingly.
			if (net.getLearnerType().endsWith("l")) {
				numerator += 1;
				denominator += net.getClassNode().getNumValues();
			} else {
			}
			
			thisClassValue.setProbability(numerator / (double) denominator);

			// Estimate P(X_i = x | Y = y) for each X_i
			for (Node thisAttribute : net.getAttributes()) {
				for (NodeValue thisAttributeValue : thisAttribute.getValues()) {
					int count = 0;

					for (Example thisExample : thisAttributeValue.getExamples()) {
						// See if the class value of this example matches the
						// class value of this loop iteration.
						if (thisExample.getClassValue().equals(thisClassValue)) {
							count++;
						}
					}
					
					numerator = count;
					denominator = net.getNumExamples();
					int classDenominator = thisClassValue.getNumExamples();
					
					// See if Laplace estimates are in use and adjust counts
					// accordingly.
					if (net.getLearnerType().endsWith("l")) {
						numerator += 1;
						denominator += thisAttribute.getNumValues();
						classDenominator += thisAttribute.getNumValues();
					}

					// Calculate probabilities.
					double probability = (numerator / (double) denominator);
					double probabilityGivenClass = (numerator / (double)
							classDenominator);

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
