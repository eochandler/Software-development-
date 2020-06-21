public class SearchResult implements Comparable<SearchResult> {

	private final String location;
	private int count;
	private double score;
	private final int total;

	/**
	 * Initializes a searchResult
	 * 
	 * @param location the file where a word is found
	 * @param count    how many times a word is seen
	 * @param total    the size of the file in which the word is found
	 */
	public SearchResult(String location, int count, int total) {
		this.location = location;
		this.count = count;
		this.score = (double) count / total;
		this.total = total;
	}

	/**
	 * Compares search results first by score, then by count, then by location.
	 * 
	 * @param other a SearchResult
	 */
	@Override
	public int compareTo(SearchResult other) {

		int result = Double.compare(other.score, this.score);

		if (result == 0) {

			result = Integer.compare(other.count, this.count);

			if (result == 0) {
				result = this.location.compareTo(other.location);
			}
		}

		return result;
	}

	/**
	 * returns the string representation of a path for a SearchResult
	 * 
	 * @return String of the path
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * returns an int of the count data member for a Search Result
	 * 
	 * @return int of count
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * returns the double value of score for a SearchResult
	 * 
	 * @return Double of the score
	 */
	public double getScore() {
		return this.score;
	}

	/**
	 * updates the count data member of a SearchResult
	 * 
	 * @param int count to be added
	 */
	public void setCount(int count) {
		this.count = count;
		this.score = (double) count / total;
	}

	/**
	 * adds the int count to the SearchResults count data member to update it
	 * 
	 * @param int count
	 */
	public void addCount(int count) {
		setCount(this.count + count);
	}

}
