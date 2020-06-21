import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ThreadSafeInvertedIndex extends InvertedIndex {

	/**
	 * Initializes the index
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}

	private final ReadWriteLock lock;

	/**
	 * calls JSON writing methods for outputting InvertedIndex
	 * 
	 * @param path the path to the output file location
	 */
	public void toJSON(Path path) throws IOException {
		lock.lockReadOnly();
		try {
			super.toJSON(path);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * calls JSON writing methods for outputting LocationMap
	 * 
	 * @param path the path to the output file location
	 */
	public void outPutLocationMap(Path path) throws IOException {
		lock.lockReadOnly();
		try {
			super.outPutLocationMap(path);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * adds word, locations, and positions to inverted index inside a nested TreeMap
	 * data Structure.
	 * 
	 * @param localIndex an InvertedIndex of words, paths, and locations
	 */
	public void addAll(InvertedIndex localIndex) throws IOException {
		lock.lockReadWrite();
		try {
			super.addAll(localIndex);
		} finally {
			lock.unlockReadWrite();
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
		lock.lockReadWrite();
		try {
			super.add(stemmedWord, inputFile, position);
		} finally {
			lock.unlockReadWrite();
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
		lock.lockReadOnly();
		try {
			boolean result = super.contains(word);
			return result;
		} finally {
			lock.unlockReadOnly();
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
	public ArrayList<SearchResult> exactSearch(List<String> query) {
		lock.lockReadOnly();
		try {
			ArrayList<SearchResult> searchMap = super.exactSearch(query);
			return searchMap;

		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Searches through the InvertedIndex for partial matches to the Query of words
	 * passed in and generates a list of matches.
	 * 
	 * @param query a string set of query lines from all query files.
	 * 
	 * @return a sorted map of SearchResults
	 */
	public ArrayList<SearchResult> partialSearch(List<String> query) {
		lock.lockReadOnly();
		try {
			ArrayList<SearchResult> searchMap = super.partialSearch(query);
			return searchMap;
		} finally {
			lock.unlockReadOnly();
		}
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
		lock.lockReadOnly();
		try {
			boolean result = super.contains(word, path);
			return result;

		} finally {
			lock.unlockReadOnly();
		}
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
		lock.lockReadOnly();
		try {
			boolean result = super.contains(word, path, position);
			return result;
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Tests whether the index contains the specified path.
	 *
	 * @param path word to look for
	 * 
	 * @return true if the path is stored in the index
	 */
	public boolean contains(Path path) {
		lock.lockReadOnly();
		try {
			boolean result = super.contains(path);
			return result;
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Returns the total number of words in the inverted index.
	 * 
	 * @return int number of words in the index
	 */
	public int wordCount() {
		lock.lockReadOnly();
		try {
			int result = super.wordCount();
			return result;
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Returns the total number of paths for a word in the inverted index.
	 * 
	 * @param word word to look for
	 * 
	 * @return int number of words in the index
	 */
	public int pathCount(String word) {
		lock.lockReadOnly();
		try {
			int result = super.pathCount(word);
			return result;
		} finally {
			lock.unlockReadOnly();
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
		lock.lockReadOnly();
		try {
			int result = super.positionCount(word, path);
			return result;
		} finally {
			lock.unlockReadOnly();
		}
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
		lock.lockReadOnly();
		try {
			int result = super.wordCount(word);
			return result;
		} finally {
			lock.unlockReadOnly();
		}
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
		lock.lockReadOnly();
		try {
			int result = super.pathCount(path);
			return result;
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Returns a string representation of the InvertedIndex
	 */
	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			String index = super.toString();
			return index;
		} finally {
			lock.unlockReadOnly();
		}
	}
}