import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	private final TreeMap<String, Integer> locationMap;

	/**
	 * Initializes the index
	 */
	public InvertedIndex() {
		index = new TreeMap<>();
		locationMap = new TreeMap<>();
	}

	/**
	 * calls JSON writing methods for outputting InvertedIndex
	 * 
	 * @param path the path to the output file location
	 */
	public void toJSON(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);) {
			JSONWriter.asDoubleNested(index, writer, 0);
		}
	}
	
	/**
	 * Outpus the map of locations to sizes in a pretty JSON format
	 * 
	 * @param path the path to the output file location
	 */
	public void outPutLocationMap(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);) {
			JSONWriter.asObject(locationMap, writer, 0);
		}
	}

	/**
	 * adds word, locations, and positions to inverted index inside a nested TreeMap
	 * data Structure
	 * 
	 * @param stemmedWord a stemmed and parsed word to be added to the index
	 * @param inputFile   the file path for where a word was found
	 * @param position    the position of the word when it was found in a file
	 */
	public void add(String stemmedWord, String inputFile, int position) {
		index.putIfAbsent(stemmedWord, new TreeMap<>());
		index.get(stemmedWord).putIfAbsent(inputFile, new TreeSet<>());
		index.get(stemmedWord).get(inputFile).add(position);
		int count = locationMap.getOrDefault(inputFile, 0);
		locationMap.put(inputFile, Math.max(count, position));
	}
	
	/**
	 * adds word, locations, and positions to inverted index inside a nested TreeMap
	 * data Structure from a smaller, local index.
	 * 
	 * @param localIndex a TreeMap<String, TreeMap<String, TreeSet<Integer>>> 
	 */
	public void addAll(InvertedIndex localIndex) throws IOException {

		for (String word : localIndex.index.keySet()) {
			if (!index.containsKey(word)) {
				index.put(word, localIndex.index.get(word));
			} else {
				for (String location : localIndex.index.get(word).keySet()) {
					if (!index.get(word).containsKey(location)) {
						index.get(word).put(location, localIndex.index.get(word).get(location));
					} else {
						index.get(word).get(location).addAll(localIndex.index.get(word).get(location));
					}
				}
			}
		}

		for (String location : localIndex.locationMap.keySet()) {
			if (!locationMap.containsKey(location)) {
				locationMap.put(location, localIndex.locationMap.get(location));
			} else {
				int count = locationMap.get(location) + localIndex.locationMap.get(location);
				locationMap.put(location, count);
			}
		}
	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word word to look for
	 * 
	 * @return true if the word is stored in the index
	 */
	public boolean contains(String word) {
		if (index == null) {
			return false;
		}
		return (index.containsKey(word));
	}

	/**
	 * updates search results if a new match is found, creates new search results
	 * for new search matches.
	 * 
	 * @param queryWord     a word to search for
	 * @param lookup        a map of previous searches to their words
	 * @param searchResults a map of searchResults
	 */
	private void searchHelper(String queryWord, HashMap<String, SearchResult> lookup,
			ArrayList<SearchResult> searchResults) {

		for (String path : index.get(queryWord).keySet()) {
			if (lookup.containsKey(path.toString())) {
				SearchResult result = lookup.get(path);
				result.addCount(index.get(queryWord).get(path).size());
			} else {
				Integer newCount = index.get(queryWord).get(path).size();
				SearchResult newMem = new SearchResult(path, newCount, locationMap.get(path));
				lookup.put(path, newMem);
				searchResults.add(newMem);
			}
		}
	}

	/**
	 * Searches through the InvertedIndex for exact matches to the Query of words
	 * passed in and generates a list of matches.
	 * 
	 * @param query a string set of query lines from all query files.
	 * 
	 * @return a sorted map of SearchResults
	 */

	public ArrayList<SearchResult> exactSearch(Collection<String> query) {
		ArrayList<SearchResult> searchResults = new ArrayList<>();
		HashMap<String, SearchResult> lookup = new HashMap<>();

		for (String queryWord : query) {
			if (index.containsKey(queryWord)) {
				searchHelper(queryWord, lookup, searchResults);
			}
		}
		Collections.sort(searchResults);
		return searchResults;
	}

	/**
	 * Searches through the InvertedIndex for partial matches to the Query of words
	 * passed in and generates a list of matches.
	 * 
	 * @param query a string set of query lines from all query files.
	 * 
	 * @return a sorted map of SearchResults
	 */
	public ArrayList<SearchResult> partialSearch(Collection<String> query) {
		ArrayList<SearchResult> searchResults = new ArrayList<>();
		HashMap<String, SearchResult> lookup = new HashMap<>();
		for (String queryWord : query) {
			for (String indexWord : index.tailMap(queryWord).keySet()) {
				if (indexWord.startsWith(queryWord)) {
					searchHelper(indexWord, lookup, searchResults);
				} else {
					break;
				}
			}
		}

		Collections.sort(searchResults);
		return searchResults;
	}

	/**
	 * Tests whether the index contains a specific word and if that word contains a
	 * specific path
	 *
	 * @param word word to look for
	 * @param path path to look for
	 * 
	 * @return true if the path is stored in the index
	 */
	public boolean contains(String word, String path) {
		if (index.containsKey(word)) {
			if (index.get(word).containsKey(path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether the index contains a specific position for a path for a word
	 *
	 * @param word     word to look for
	 * @param path     path to look for
	 * @param position int to look for
	 * 
	 * @return true if the position is stored in the path for a word in the index
	 */
	public boolean contains(String word, String path, int position) {
		if (index.containsKey(word)) {
			if (index.get(word).containsKey(path)) {
				if (index.get(word).get(path).contains(position)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tests whether the index contains the specified path.
	 *
	 * @param path word to look for
	 * 
	 * @return true if the path is stored in the index
	 */
	public boolean contains(Path path) {
		if (index == null) {
			return false;
		}
		String stringPath = path.toString();
		for (String key : index.keySet()) {
			if (index.get(key).keySet().contains(stringPath)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the total number of words in the inverted index.
	 * 
	 * @return int number of words in the index
	 */
	public int wordCount() {
		return index.size();
	}

	/**
	 * Returns the total number of paths for a word in the inverted index.
	 * 
	 * @param word word to look for
	 * 
	 * @return int number of words in the index
	 */
	public int pathCount(String word) {
		if (contains(word)) {
			return (index.get(word).size());
		} else {
			return 0;
		}
	}

	/**
	 * Returns the total number of numbers for a path for a word in the inverted
	 * index.
	 * 
	 * @param word word to look for
	 * @param path path to look for
	 * 
	 * @return int number of numbers found
	 */
	public int positionCount(String word, String path) {
		if (contains(word, path)) {
			return (index.get(word).get(path).size());
		}

		return 0;
	}

	/**
	 * Returns the number of times a word was found (i.e. the number of positions
	 * associated with a word in the index).
	 *
	 * @param word word to look for
	 * 
	 * @return number of times the word was found
	 */
	public int wordCount(String word) {
		int count = 0;
		for (String path : index.get(word).keySet()) {
			count = count + index.get(word).get(path).size();
		}
		return count;
	}

	/**
	 * Returns the number of times a path was found (i.e. the number of positions
	 * associated with a path in the index).
	 *
	 * @param path path to look for
	 * 
	 * @return number of times the path was found
	 */

	public int pathCount(Path path) {
		String stringPath = path.toString();
		int count = 0;
		for (String key : index.keySet()) {
			if (index.get(key).containsKey(stringPath)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns a string representation of the InvertedIndex
	 */
	@Override
	public String toString() {
		return index.toString();
	}
}
