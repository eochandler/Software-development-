import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public interface FileParser {

	/**
	 * for each line in the file, clean, stem, remove duplicates, calls either exact
	 * or partial search to build the ResultMap
	 * 
	 * @throws IOException if unable to read or write to file
	 * 
	 * @param queryPath  a Path object which leads to a query file.
	 * @param searchFlag true or false indicator of what search to perform
	 */
	public abstract void parseQueryFile(Path queryPath, boolean searchFlag) throws IOException;
	
	/**
	 * outPuts the results of a search to a file in JSON format
	 * 
	 * @param writer writer for a file location
	 */
	public abstract void outputResultMap(Writer writer) throws IOException;
	
}
