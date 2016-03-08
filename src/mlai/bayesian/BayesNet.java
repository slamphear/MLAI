package mlai.bayesian;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedList;

import mlai.shared.Utilities;

/**
 * Representation of a Bayesian Network.
 * @author Steven Lamphear
 */
public class BayesNet {
	// Declare class variables.
	String relation;
	Node classNode;
	LinkedList<Node> attributes;
	LinkedList<Example> trainExamples;
	LinkedList<Example> testExamples;
	String learnerType;
	MaxSpanningTree mst;

	/**
	 * Constructor method for the Bayes Net.
	 * @param trainFile  The name of the ARFF file containing the training examples.
	 * @param type  'n' for Naive Bayes, or 't' for TAN, with optional 'l' added
	 *              to indicate that Laplace estimates should be used.
	 */
	public BayesNet(String trainFile, String learnerType) {
		// Initialize variables.
		relation = null;
		// Temporarily set the class node's index to -1.
		classNode = new Node("class", -1);
		attributes = new LinkedList<Node>();
		trainExamples = new LinkedList<Example>();
		testExamples = new LinkedList<Example>();
		this.learnerType = learnerType;
		mst = null;

		// Read in the training set.
		Utilities.ReadData(trainFile, relation, classNode, attributes,
				trainExamples);

		// Update the class node's index so that it is immediately after the
		// last attribute.
		classNode.setIndex(attributes.size());

		// Train the network using the algorithm specified by the "type"
		// parameter.
		if (this.learnerType.startsWith("n")) {
			Learners.naive(this);
		} else if (this.learnerType.startsWith("t")) {
			Learners.tan(this);
		} else {
			System.err.println("Please pass in a type that begins with 'n' "
					+ "(for Naive) or a 't' (for TAN) as the third parameter "
					+ "to the program.");
			System.exit(1);
		}
	}

	/**
	 * Calculates the conditional mutual information (CMI) between two
	 * attributes. This assumes that <code>probGivenClass</code> values have
	 * already been set for both attributes.
	 * @param firstAttribute  The first attribute used in this CMI calculation.
	 * @param secondAttribute  The second attribute used in this CMI calculation.
	 * @return The conditional mutual information for the two given attributes.
	 */
	public double calcCMI(Node firstAttribute, Node secondAttribute) {
		// If values are the same, return -1.
		if (firstAttribute == secondAttribute) {
			return -1;
		}

		double cmi = 0.0;

		// Calculate the number of Laplace estimates needed for P(x_i, x_j, y)
		// calculations.
		int laplace = firstAttribute.getNumValues()
				* secondAttribute.getNumValues() * classNode.getNumValues();

		for (NodeValue firstAttVal : firstAttribute.getValues()) {
			for (NodeValue secondAttVal : secondAttribute.getValues()) {
				for (int classIndex = 0; classIndex < classNode.getNumValues(); classIndex++) {
					NodeValue classValue = classNode.getValueByIndex(classIndex);
					double probFirstGivenClass = firstAttVal.getProbGivenClass(classIndex);
					double probSecondGivenClass = secondAttVal.getProbGivenClass(classIndex);
					int matches = 1; // Start counting at 1 for Laplace estimate.

					// Loop over all of the examples with this class value;
					// count the number of examples which also have these values.
					for (Example thisExample : classValue.getExamples()) {
						if (thisExample.getValues().contains(firstAttVal)
								&& thisExample.getValues().contains(secondAttVal)) {
							matches++;
						}
					}

					double probAll = matches
							/ (double) (getNumExamples() + laplace);
					double probBothGivenClass = matches
							/ ((double) classValue.getNumExamples() + (firstAttribute.getNumValues()
									* secondAttribute.getNumValues()));
					cmi += (probAll * Utilities.logBaseTwo(probBothGivenClass
							/ (probFirstGivenClass * probSecondGivenClass)));
				}
			}
		}

		return cmi;
	}

	/**
	 * Calculate the probabilities of each class for the given test example.
	 * @param testExample  The example used for these probability calculations.
	 * @return  An array of the probabilities that this example belongs to each
	 *          class (such that the nth element of this array represents the
	 *          probability that this example is the nth class value).
	 */
	private double[] calcProbabilities(Example testExample) {
		double[] probabilities = new double[classNode.getValues().size()];

		// Calculate the probability of each class value for this example.
		for (int classIndex = 0; classIndex < classNode.getValues().size(); classIndex++) {

			// Start with the probability of this class value.
			probabilities[classIndex] = classNode.getValueByIndex(classIndex)
					.getProbability();

			// Then factor in the probability of each value given this class.
			for (NodeValue thisValue : testExample.getValues()) {
				if (learnerType.startsWith("n")) {
					probabilities[classIndex] *= thisValue.getProbGivenClass(classIndex);
				} else if (learnerType.startsWith("t")) {
					probabilities[classIndex] *= thisValue
							.getProbOfClassForExample(testExample, classIndex);
				}
			}
		}

		return probabilities;

	}

	/**
	 * Accessor method for the list of attributes.
	 * @return  A linked list of nodes, each of which represents an attribute.
	 */
	public LinkedList<Node> getAttributes() {
		return attributes;
	}

	/**
	 * Accessor method for the class node.
	 * @return  A node representing the class for the given data set.
	 */
	public Node getClassNode() {
		return classNode;
	}

	/**
	 * Accessor method for the list of training examples.
	 * @return  A linked list of examples found in the training set.
	 */
	public LinkedList<Example> getTrainExamples() {
		return trainExamples;
	}

	/**
	 * Accessor method for the type of network being used.
	 * @return The type of learner passed into the driver program.
	 */
	public String getLearnerType() {
		return learnerType;
	}

	/**
	 * Accessor method for this Bayes Net's maximum spanning tree.
	 * @return  The maximum spanning tree used by this Bayes Net. This is used
	 *          in the TAN algorithm.
	 */
	public MaxSpanningTree getMST() {
		return mst;
	}

	/**
	 * Accessor method for the number of attributes in this Bayes Net.
	 * @return  The number of attributes in this Bayes Net.
	 */
	public int getNumAttributes() {
		return attributes.size();
	}

	/**
	 * Accessor method for the number of examples in the training set.
	 * @return  The number of examples in the training set.
	 */
	public int getNumExamples() {
		return trainExamples.size();
	}
	
	/**
	 * Accessor method for the list of test examples.
	 * @return  A linked list of examples found in the test set.
	 */
	public LinkedList<Example> getTestExamples() {
		return testExamples;
	}

	/**
	 * Prints the structure of the Bayes Net.
	 */
	public void printStructure() {
		for (Node thisAttribute : attributes) {
			String line = thisAttribute.getAttributeName();
			for (Node thisParent : thisAttribute.getParents()) {
				line += " " + thisParent.getAttributeName();
			}
			System.out.println(line);
		}
	}

	/**
	 * Prints the results of running a test file through the Bayes Net.
	 * @param testFile  The file containing the examples to be tested.
	 */
	public void printTest(String testFile) {
		Utilities.ReadData(testFile, relation, classNode, attributes,
				testExamples);
		DecimalFormat decimalFormat = new DecimalFormat("#.############");
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
		int numCorrect = 0;

		for (Example thisExample : testExamples) {
			classify(thisExample);
			
			int prediction = thisExample.getPredictedClassIndex();
			double probability = thisExample.getProbOfPrediction();

			// Print the results (predicted class, actual class, and posterior
			// probability).
			String line = classNode.getValueByIndex(prediction).getValueName();
			line += " " + thisExample.getClassValue().getValueName();
			line += " " + decimalFormat.format(probability);
			System.out.println(line);

			if (classNode.getValueByIndex(prediction).getValueName()
					.equals(thisExample.getClassValue().getValueName())) {
				numCorrect++;
			}
		}

		System.out.println("\n" + numCorrect);
	}

	/**
	 * Use this Bayes Net to predict the class of the given example.
	 * @param testExample  The example to be classified.
	 */
	private void classify(Example testExample) {
		double maxProbability = Double.NEGATIVE_INFINITY;
		int prediction = 0;

		double[] probabilities = calcProbabilities(testExample);
		double totalProbability = 0.0;

		// Calculate probabilities for each class value and find the most
		// probable one.
		for (int i = 0; i < probabilities.length; i++) {

			totalProbability += probabilities[i];

			if (probabilities[i] > maxProbability) {
				maxProbability = probabilities[i];
				prediction = i;
			}
		}
		
		testExample.setPredictedClassValue(prediction);
		testExample.setProbOfPrediction(maxProbability / totalProbability);
	}

	/**
	 * Mutator method to assign a Maximum Spanning Tree to this Bayes Net.
	 * @param mst  The Maximum Spanning Tree (used in the TAN algorithm).
	 */
	public void setMST(MaxSpanningTree mst) {
		this.mst = mst;
	}
}
