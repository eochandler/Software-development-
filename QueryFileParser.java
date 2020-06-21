import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class QueryFileParser implements FileParser {

	private final TreeMap<String, ArrayList<SearchResult>> resultMap;
	private final InvertedIndex index;

	/**
	 * initializes a TreeMap<String, ArrayList<SearchResult>> called a resultMap to
	 * hold search results, and assigns an InvertedIndex to that resultMap
	 * 
	 * @param InvertedIndex
	 */
	public QueryFileParser(InvertedIndex index) {
		resultMap = new TreeMap<>();
		this.index = index;
	}

	@Override
	public void parseQueryFile(Path queryPath, boolean searchFlag) throws IOException {

		Stemmer wordStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

		try (BufferedReader reader = Files.newBufferedReader(queryPath, StandardCharsets.UTF_8);) {

			String line = null;

			line = reader.readLine();

			while (line != null) {

				String[] parsed = ParserOfText.parse(line);

				TreeSet<String> words = new TreeSet<String>();

				for (String wordi : parsed) {
					words.add(wordStemmer.stem(wordi).toString());
				}

				String stringLine = String.join(" ", words);

				if (!resultMap.containsKey(stringLine) && !stringLine.isEmpty()) {
					if (searchFlag == false) {
						ArrayList<SearchResult> searchResult = index.exactSearch(words);
						resultMap.put(stringLine, searchResult);
					} else {
						ArrayList<SearchResult> searchResult = index.partialSearch(words);
						resultMap.put(stringLine, searchResult);
					}
				}
				line = reader.readLine();
			}
			reader.close();
		}
	}

	@Override
	public void outputResultMap(Writer writer) throws IOException {
		JSONWriter.asSearchOutput(resultMap, writer, 1);
	}
}