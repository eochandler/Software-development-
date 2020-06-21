import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Driver {

	public static void main(String[] args) {

		ArgumentMap flagParser = new ArgumentMap();
		flagParser.parse(args);

		InvertedIndex index = null;
		ThreadSafeInvertedIndex threadSafe = null;

		FileParser resultMap = null;

		WorkQueue queue = null;

		if (flagParser.hasFlag("-threads")) {
			int numThreads = flagParser.getInteger("-threads", 5);
			queue = new WorkQueue(numThreads);

			threadSafe = new ThreadSafeInvertedIndex();
			index = threadSafe;
			resultMap = new ThreadSafeQueryParser(threadSafe, queue);
		} else {
			index = new InvertedIndex();
			resultMap = new QueryFileParser(index);
		}

		if (flagParser.hasFlag("-path")) {
			if (flagParser.getPath("-path") == null) {
				System.out.println("Cannot resolve path to file to build index");
				return;
			}

			Path path = flagParser.getPath("-path");

			try {
				if (threadSafe != null) {
					ThreadSafeInvertedIndexBuilder.traverse(path, threadSafe, queue);
					queue.finish();
				} else {
					InvertedIndexBuilder.traverse(path, index);
				}
			} catch (IOException e) {
				System.out.println("Cannot build inverted index from path: " + path);
			}
		}

		if ((flagParser.hasFlag("-index"))) {
			try {
				Path path = flagParser.getPath("-index", Paths.get("index.json"));
				index.toJSON(path);
			} catch (Exception e2) {
				System.out.println("Could not access file to write out index");
			}
		}

		if ((flagParser.hasFlag("-locations"))) {
			try {
				Path path = flagParser.getPath("-locations", Paths.get("locations.json"));
				index.outPutLocationMap(path);
			} catch (Exception e1) {
				System.out.println("couldn't access path to output location map");
			}
		}

		if (flagParser.hasFlag("-search")) {

			if (flagParser.getPath("-search") == null) {
				System.out.println("null search path");
				return;
			}

			Path queryFile = flagParser.getPath("-search");

			try {
				resultMap.parseQueryFile(queryFile, !flagParser.hasFlag("-exact"));
			} catch (Exception e) {
				System.out.println("Could not parse query file to execute search");
			}
		}

		if (flagParser.hasFlag("-results")) {
			Path path = flagParser.getPath("-results", Paths.get("results.json"));
			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);) {
				resultMap.outputResultMap(writer);
			} catch (IOException e1) {
				System.out.println("Could not output ResultMap to JSON format");
			}
		}
	}
}
