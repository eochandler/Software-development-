import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class InvertedIndexBuilder {
	/**
	 * Traverses a directory until a path is found a recursive function which
	 * examines paths: if the path is a directory then it enters the directory and
	 * calls itself again. When the function finds a file with a txt variation
	 * ending it will call a stem file function.
	 * 
	 * @throws IOException if unable to read or write to file
	 * 
	 * @param directory     a path object. Can be a directory or path to a specific
	 *                      file
	 * @param InvertedIndex a data structure that will be populated with words,
	 *                      paths, integers that lives in main
	 */
	public static void traverse(Path directory, InvertedIndex index) throws IOException {
		if (Files.isDirectory(directory)) {
			try (var listing = Files.newDirectoryStream(directory)) {
				for (Path path : listing) {
					traverse(path, index);
				}
			}
		} else if (Files.exists(directory)) {
			String stringLower = directory.toString().toLowerCase();
			if ((stringLower.endsWith(".txt") == true) || (stringLower.endsWith(".text") == true)) {
				stemFile(directory, index);
			}
		}
	}

	/**
	 * Parses a text file into stemmed words, and adds those words to an inverted
	 * index. creates a new snowballerstemmer object and reader. calls line stemmer
	 * to stem all words in a line and then adds those words to a stemmedWords
	 * Array. Calls add function to put new values in the InvertedIndex
	 *
	 * @param inputFile     the input file to parse
	 * @param InvertedINdex index InvertedIndex data structure.
	 * @throws IOException if unable to read or write to file
	 *
	 * @see #stemLine(String)
	 * @see TextParser#parse(String)
	 */
	public static void stemFile(Path inputFile, InvertedIndex index) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			String line = null;

			line = reader.readLine();

			Stemmer wordStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			Integer count = 1;

			String location = inputFile.toString();
			while (line != null) {
				String[] parsed = ParserOfText.parse(line);
				for (String word : parsed) {
					String stemmedWord = wordStemmer.stem(word).toString();
					index.add(stemmedWord, location, count);
					count++;
				}
				line = reader.readLine();
			}
		}
	}
}
