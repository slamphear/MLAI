package mlai.shared;

import java.util.ArrayList;

import mlai.bayesian.BayesNet;

/**
 * Driver program for various machine learning algorithm implementations.
 *
 * This program assumes that the following conditions are true:
 * 1. All provided data sets are ARFF files.
 * 2. The last attribute is named "class" and serves as the label for each 
 *    example in each data set.
 * 3. The names and available values for the attributes and class are identical
 *    in the train and test sets.
 * 4. The attributes appear in the same order in the train and test sets.
 * 
 * @author Steven Lamphear
 */
public class MLAI {

	/**
	 * Driver method for the program.
	 * @param args  An array of the following three parameters:
	 *              1. the name of the training set,
	 *              2. the name of the test set, and
	 *              3. the type of learner to use. Here are the options:
	 *                 * Enter 'n' for Naive Bayes (with no estimates)
	 *                 * Enter 't' for Tree-Augmented Naive Bayes (with no
	 *                   estimates)
	 *                 * Enter 'nl' for Naive Bayes (using Laplace estimates)
	 *                 * Enter 'tl' for Tree-Augmented Naive Bayes (using
	 *                   Laplace estimates)
	 */
	public static void main(String[] args) {

		// Confirm that all necessary input parameters have been included.
		if (args.length != 3) {
			System.err.println("This program should be called as follows: "
					+ "MLAI <train-set-file> <test-set-file> <type>");
			System.exit(1);
		}
		
		String learnerType = args[2].toLowerCase();
		
		ArrayList<String> bayesianTypes = new ArrayList<String>(4);
		bayesianTypes.add("n");
		bayesianTypes.add("nl");
		bayesianTypes.add("t");
		bayesianTypes.add("tl");
		
		if (bayesianTypes.contains(learnerType)) {
			// Create the BayesNet (this handles the reading of the training set).
			BayesNet net = new BayesNet(args[0], learnerType);

			// Print out the network structure.
			net.printStructure();

			// Print a blank line to separate the structure from the predictions.
			System.out.println();

			// Print the network's performance on the test set.
			net.printTest(args[1]);
		}
	}
}