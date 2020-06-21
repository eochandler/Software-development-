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

public class ThreadSafeQueryParser implements FileParser {

	private final TreeMap<String, ArrayList<SearchResult>> resultMap;
	private final ThreadSafeInvertedIndex index;
	private final WorkQueue queue;

	// TODO
	/**
	 * @param index
	 * @param queue
	 */
	public ThreadSafeQueryParser(ThreadSafeInvertedIndex index, WorkQueue queue) {
		resultMap = new TreeMap<>();
		this.index = index;
		this.queue = queue;
	}

	@Override
	public void parseQueryFile(Path queryPath, boolean searchFlag) throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(queryPath, StandardCharsets.UTF_8);) {

			String line = null;

			line = reader.readLine();

			while (line != null) {
				queue.execute(new SearchTask(line, searchFlag));
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("invalid path");
		} finally {
			queue.finish();
		}
	}

	@Override
	public void outputResultMap(Writer writer) throws IOException {
		synchronized (resultMap) {
			JSONWriter.asSearchOutput(resultMap, writer, 1);
		}
	}

	class SearchTask implements Runnable {
		private final String line;
		private final boolean searchFlag;

		public SearchTask(String line, boolean searchFlag) {
			this.line = line;
			this.searchFlag = searchFlag;
		}

		@Override
		public void run() {
			try {
				Stemmer wordStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

				String[] parsed = ParserOfText.parse(line);

				TreeSet<String> words = new TreeSet<String>();

				for (String wordi : parsed) {
					words.add(wordStemmer.stem(wordi).toString());
				}

				String stringLine = String.join(" ", words);

				synchronized (resultMap) {
					if (resultMap.containsKey(stringLine)) {
						return;
					}
				}

				ArrayList<SearchResult> searchResult;
				
				if (stringLine.length() != 0) {
					if (searchFlag == false) {
						searchResult = index.exactSearch(words);
					} else {
						searchResult = index.partialSearch(words);
					}
					synchronized (resultMap) {
						resultMap.put(stringLine.toString(), searchResult);
					}
				}

//				if (!resultMap.keySet().contains(stringLine.toString())) {
//					synchronized (resultMap) {
//
//						if (searchFlag == false) {
//							ArrayList<SearchResult> searchResult = index.exactSearch(words);
//							if (stringLine.length() != 0) {
//								resultMap.put(stringLine.toString(), searchResult);
//
//							}
//						} else {
//							ArrayList<SearchResult> searchResult = index.partialSearch(words);
//							if (stringLine.length() != 0) {
//								resultMap.put(stringLine.toString(), searchResult);
//
//							}
//						}
//					}
//				}
			} finally {
			}
		}
	}
}
