import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONWriter {

	/**
	 * Writes out a Map of String queries to SearchResults in proper JSON
	 * formatting.
	 * 
	 * @throws IOException if unable to read or write to file
	 * 
	 * @param ResultMap a Map of query strings to search Results
	 * 
	 * @param writer    a writer object that contains the outfile path to write to
	 * @param level     the base level of indentation for the outfile
	 * 
	 */
	public static void asSearchOutput(TreeMap<String, ArrayList<SearchResult>> ResultMap, Writer writer, int level)
			throws IOException {

		DecimalFormat FORMATTER = new DecimalFormat("0.000000");
		writer.flush();
		writer.write("[");
		writer.write(System.lineSeparator());

		for (String stringQuery : ResultMap.keySet()) {
			indent((int) (level + .5), writer);
			writer.write("{");
			writer.write(System.lineSeparator());
			indent(level + 1, writer);
			writer.write("\"");
			writer.write("queries");
			writer.write("\"");
			writer.write(": ");
			writer.write("\"");
			writer.write(stringQuery);
			writer.write("\"");
			writer.write(",");
			writer.write(System.lineSeparator());
			indent(level + 1, writer);
			writer.write("\"");
			writer.write("results");
			writer.write("\"");
			writer.write(": [");
			writer.write(System.lineSeparator());

			if (ResultMap.get(stringQuery) != null) {
				if (ResultMap.get(stringQuery).size() != 0) {
					for (SearchResult query : ResultMap.get(stringQuery)) {
						indent(level + 2, writer);
						writer.write("{");
						writer.write(System.lineSeparator());
						indent(level + 3, writer);
						writer.write("\"");
						writer.write("where");
						writer.write("\": ");
						writer.write("\"");
						writer.write(query.getLocation().toString());
						writer.write("\"");
						writer.write(",");
						writer.write(System.lineSeparator());
						indent(level + 3, writer);
						writer.write("\"");
						writer.write("count");
						writer.write("\": ");
						Integer c = query.getCount();
						writer.write(c.toString());
						writer.write(",");
						writer.write(System.lineSeparator());
						indent(level + 3, writer);
						writer.write("\"");
						writer.write("score");
						writer.write("\": ");
						String formattedScore = FORMATTER.format(query.getScore());
						writer.write(formattedScore.toString());
						writer.write(System.lineSeparator());
						indent(level + 2, writer);
						writer.write("}");
						SearchResult lastQuery = ResultMap.get(stringQuery).get(ResultMap.get(stringQuery).size() - 1);
						if (!query.equals(lastQuery)) {
							writer.write(",");
						}
						writer.write(System.lineSeparator());
					}
				}
				indent(level + 1, writer);
				writer.write("]");
				writer.write(System.lineSeparator());

				if (stringQuery != ResultMap.lastKey()) {
					indent((int) (level + .5), writer);
					writer.write("},");
					writer.write(System.lineSeparator());

				} else {
					indent((int) (level + .5), writer);
					writer.write("}");
					writer.write(System.lineSeparator());
				}
			}
		}
		writer.write("]");

	}

	/**
	 * The main writing method for writing to JSON. Iterates through the Key words
	 * in the InvertedIndex, writes out the word, then calls nestedObject.
	 * 
	 * @throws IOException if unable to read or write to file
	 * 
	 * @param elements the Inverted index with type TreeMap<String, WordIndex> that
	 *                 is to be written out
	 * @param writer   a writer object that contains the outfile path to write to
	 * @param level    the base level of indentation for the outfile
	 * 
	 */
	public static void asDoubleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer,
			int level) throws IOException {
		writer.write("{");
		if (elements != null) {
			if (elements.size() != 0) {
				String lastKey = elements.lastKey();
				for (String key : elements.headMap(lastKey, false).keySet()) {
					writer.write(System.lineSeparator());
					indent(level + 1, writer);
					writer.write("\"");
					writer.write(key.toString());
					writer.write("\"");
					writer.write(": {");
					nestedObject(elements.get(key), writer, 1);
					writer.write(",");
				}
				writer.write(System.lineSeparator());
				indent(level + 1, writer);
				writer.write("\"");
				writer.write(elements.lastKey().toString());
				writer.write("\"");
				writer.write(": {");
				nestedObject(elements.get(lastKey), writer, 1);
			}
		}
		writer.write(System.lineSeparator());
		writer.write("}");
	}

	/**
	 * This method accesses the path values of each word in inverted index. It
	 * writes out the path name and then calls as array to complete the JSON output
	 * to the specified file.
	 * 
	 * @param elements the Inverted index with type TreeMap<String, WordIndex> that
	 *                 is to be written out
	 * @param writer   a writer object that contains the outfile path to write to
	 * @param level    the base level of indentation for the outfile
	 */
	public static void nestedObject(TreeMap<String, TreeSet<Integer>> elements, Writer writer, int level)
			throws IOException {
		writer.write(System.lineSeparator());
		if (elements != null) {
			if (elements.size() != 0) {
				String lastKey = elements.lastKey();
				for (String key : elements.headMap(lastKey, false).keySet()) {
					indent(level + 1, writer);
					writer.write("\"");
					writer.write(key.toString());
					writer.write("\"");
					writer.write(": ".toString());
					asArray(elements.get(key), writer, 1);
					writer.write(",");
					writer.write(System.lineSeparator());
				}
				indent(level + 1, writer);
				writer.write("\"");
				writer.write(lastKey.toString());
				writer.write("\"");
				writer.write(": ".toString());
				asArray(elements.get(lastKey), writer, 1);
				writer.write(System.lineSeparator());
				indent(level, writer);
			}
		}
		writer.write("}");
	}

	/**
	 * This method writes out all of the integer values inside a TreeMap<Integer>
	 * data structure to JSON format.
	 * 
	 * @param elements the TreeSet<Integer> data structure which houses each path's
	 *                 integer values(Inside wordIndex)
	 * @param writer   a writer object that contains the outfile path to write to
	 * @param level    the base level of indentation for the outfile
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer, int level) throws IOException {
		writer.write("[");
		writer.write(System.lineSeparator());

		if (elements != null) {
			if (elements.size() != 0) {
				Integer last = elements.last();
				for (Integer q : elements.headSet(last, false)) {
					indent(level + 2, writer);
					writer.write(q.toString());
					writer.write(',');
					writer.write(System.lineSeparator());
				}
				indent(level + 2, writer);
				writer.write(last.toString());
			}
		}
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write("]");
	}

	/**
	 * this method indents the line on the JSON outputter file by one indent and
	 * provides ability to increase by more than one indent
	 * 
	 * @param times  the number of times to indent a line
	 * @param writer a writer object that contains the outfile path to write to
	 */
	public static void indent(int times, Writer writer) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * this method is a default implementation for the asNestedObject method which
	 * can be used on any object of type TreeMap<String, TreeSet<Integer>> to output
	 * to Json Format.
	 * 
	 * @param elements a TreeMap data structure to hold locations along with word
	 *                 locations
	 * @param path     the path to the output file location
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			nestedObject(elements, writer, 0);
		}
	}

	/**
	 * Outputs a TreeMap<String, Integer> in JSON format to the specified file.
	 * 
	 * @param elements a TreeMap data structure to hold locations along with word
	 *                 locations
	 * @param level    indent level to begin at for JSON outputting
	 * @param wrtier   writer location for file outputting
	 */
	public static void asObject(TreeMap<String, Integer> elements, Writer writer, int level) throws IOException {
		writer.flush();
		writer.write("{");
		writer.write(System.lineSeparator());
		if (elements != null) {
			if (elements.size() != 0) {
				String lastKey = elements.lastKey();
				for (String key : elements.headMap(lastKey, false).keySet()) {
					indent(level + 1, writer);
					writer.write("\"");
					writer.write(key.toString());
					writer.write("\"");
					writer.write(": ".toString());
					writer.write(elements.get(key).toString());
					writer.write(",");
					writer.write(System.lineSeparator());
				}
				indent(level + 1, writer);
				writer.write("\"");
				writer.write(lastKey.toString());
				writer.write("\"");
				writer.write(": ".toString());
				writer.write(elements.get(lastKey).toString());

			}
		}
		writer.write(System.lineSeparator());
		writer.write("}");
	}

	/**
	 * this method is a default implementation for the asObject method, creates a
	 * new writer then calls the asObject implemented method to ouput a TreeMap in
	 * JSON format.
	 * 
	 * @param elements a TreeMap of strings to integers
	 * @param path     the path to the output file location
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * this method is a default implementation for the asArray method which can be
	 * used on any object of type TreeSet<Integer>, creates a custom writer based on
	 * the path for writing out
	 * 
	 * @param elements treeSet of Integers
	 * @param path     the path to the output file location
	 */

	public static void asArray(TreeSet<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}
}
