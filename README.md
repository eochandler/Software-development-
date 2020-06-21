# Software-development-
A multithreaded document processor that builds an index of all the words found in a document

Project 1 Inverted Index
Java program that processes all text files in a directory and its subdirectories, cleans and parses the text into word stems, and builds an in-memory inverted index to store the mapping from word stems to the documents and position within those documents where those word stems were found.

Functionality

* Process command-line arguments to determine the input to process and output to produce. See the Input and Output sections below for specifics.
* Create a custom inverted index data structure that stores a mapping from a word stem to the file(s) the word was found, and the position(s) in that file the word is located. The positions should start at 1. This will require nesting multiple built-in data structures.
* If provided a directory as input, find all files within that directory and all subdirectories and parse each text file found. If provided a single text file as input, only parse that individual file. Any files that end in the .text or .txt extension (case insensitive) should be considered a text file.
    * Use the UTF-8 character encoding for all file processing, including reading and writing.
* Efficiently process text files into word stems by removing any non-letter symbols (including digits, punctuation, accents, special characters), convert the remaining alphabetic characters to lowercase, splitting the text into words by whitespace, and then stemming the word using the Apache OpenNLP toolkit.
    * Use the regular expression (?U)[^\\p{Alpha}\\p{Space}]+ to remove special characters from text.
    * Use the regular expression (?U)\\p{Space}+ to split text into words by whitespace.
    * Use the SnowballStemmer English stemming algorithm in OpenNLP to stem words.
* If the appropriate command-line arguments are provided, output the inverted index in pretty JSON format. See the Output section below for specifics.
* Output user-friendly error messages in the case of exceptions or invalid input. Under no circumstance should your main() method output a stack trace to the user!
The functionality of your project will be evaluated with the Project1Test.java group of JUnit tests.

Project 2 Partial Search
code is able to track the total number of words found in each text file, parse and stem a query file, generate a sorted list of search results from the inverted index, and support writing those results to a JSON file.

Functionality

* Process additional command-line parameters to determine whether to search the inverted index and whether to produce additional JSON ouput. See the Input section for specifics.
* Parse a query file line-by-line into a normalized and optimized multiple word query matching the processing used to build the inverted index. See the Query Parsing section for specifics.
* Efficiently return exact search results from your inverted index, such that any word stem in your inverted index that exactly matches a query word is returned.
* Efficiently return partial search results from your inverted index, such that any word stem in your inverted index that starts with a query word is returned.
* Sort the search results using a simple term frequency metric to determine the most “useful” search results to output first. See the Result Sorting section for specifics.
* Produce additional pretty JSON output for the word count of each location and the sorted search results. See the Output section for specifics.

Project 3 Multithreading
Code makes a thread-safe inverted index, and use a work queue to build and search an inverted index using multiple threads.

Functionality

* Process additional command-line parameters to whether to use multithreading and if so, how many threads to use in the work queue. See the Input section for specifics.
* Support the same output capability as before. See the Output section for specifics.
* Create a custom read/write lock class that allows multiple concurrent read operations, but non-concurrent write and read/write operations.
* Create a thread-safe inverted index using the custom read/write lock class above.
* Use a work queue to build your inverted index from a directory of files using multiple worker threads. Each worker thread should parse a single file.
* Use a work queue to search your inverted index from a file of multiple word queries (for both exact and partial search). Each worker thread should handle an individual query (which may contain multiple words).
* Exit gracefully without calling System.exit() when all of the building and searching operations are complete.

