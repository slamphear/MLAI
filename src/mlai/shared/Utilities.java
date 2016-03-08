package mlai.shared;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

import mlai.bayesian.Example;
import mlai.bayesian.Node;

/**
 * Various utility methods used by this package.
 * @author Steven Lamphear
 */
public class Utilities {

	/**
	 * Read in an ARFF data set from a file.
	 * @param filename  The name of the file to be read.
	 * @param relation  The name of the relation (specified by "@relation" in
	 *                  an ARFF data set).
	 * @param classNode  The node used to store the class (label) for each
	 *                   example in the data set.
	 * @param attributes  The linked list of attribute nodes for this data set.
	 *                    This will be empty when reading a training set, but
	 *                    populated when reading a test set.
	 * @param examples  The empty linked list to be populated with examples
	 *                  from this data set.
	 */
	public static void ReadData(String filename, String relation,
			Node classNode, LinkedList<Node> attributes,
			LinkedList<Example> examples) {

		String line;
		int index = 0;

		// Create a scanner and read the data set.
		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file '" + filename + "'.");
			System.exit(1);
		}

		while (fileScanner.hasNext()) {
			line = fileScanner.nextLine().trim();

			// Cut out comments.
			line = line.split("%", 2)[0];

			// Skip blank lines.
			if (line.length() == 0) {
				continue;
			}

			// See if this line is a relation.
			else if (line.split(" ", 0)[0].toLowerCase()
					.startsWith("@relation")) {
				relation = line.split(" ", 0)[1].trim();
			}
			// Then, see if this line is an attribute.
			else if (line.split(" ", 0)[0].toLowerCase().startsWith(
					"@attribute")) {
				// Get the attribute name
				String name = line.split(" ", 0)[1].trim();

				// Handle quoted attribute names
				if (name.startsWith("'")) {
					name = line.split("'", 0)[1].trim();
				} else if (name.startsWith("\"")) {
					name = line.split("\"", 0)[1].trim();
				}

				Node thisAttribute;

				if (name.toLowerCase().equals("class")) {
					thisAttribute = classNode;
				} else {
					thisAttribute = new Node(name, index++);
				}

				line = line.split("\\{", 0)[1];
				line = line.split("\\}", 0)[0];
				String[] values = line.split(",", 0);

				for (int i = 0; i < values.length; i++) {
					thisAttribute.addValue(values[i].trim());
				}

				if (!name.toLowerCase().equals("class")) {
					attributes.add(thisAttribute);
				}
			}

			// In both train and test sets, see if we're ready to move on to the
			// data.
			if (line.split(" ", 0)[0].toLowerCase().contains("data")) {
				break;
			}
		}

		// Now loop over data.
		while (fileScanner.hasNext()) {
			line = fileScanner.nextLine().trim();

			// Cut out comments.
			line = line.split("%", 2)[0];

			// Skip blank lines.
			if (line.length() == 0) {
				continue;
			}

			String[] exampleValues = line.split(",", 0);

			// Create example and add it to the list.
			Example thisExample = new Example(exampleValues, attributes,
					classNode);
			examples.add(thisExample);
		}
	}
	
	/**
	 * Calculates log base 2 of a given input.
	 * @param input  The input of the function.
	 * @return  Log base 2 of the given input.
	 */
	public static double logBaseTwo(double input) {
		if (input == 0.0) {
			return 0.0;
		} else {
			return Math.log(input) / Math.log(2.0);
		}
	}
}
