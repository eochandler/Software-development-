import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ThreadSafeInvertedIndexBuilder {
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
	public static void traverse(Path directory, ThreadSafeInvertedIndex index, WorkQueue queue)
			throws IOException {
		if (Files.isDirectory(directory)) {
			try (var listing = Files.newDirectoryStream(directory)) {
				for (Path path : listing) {
					traverse(path, index, queue);
				}
			}
		} else if (Files.exists(directory)) {
			String stringLower = directory.toString().toLowerCase();
			if ((stringLower.endsWith(".txt") == true) || (stringLower.endsWith(".text") == true)) {
				queue.execute(new AddTask(directory, index));
			}
		}
	}

	static class AddTask implements Runnable {
		private final Path file;
		private final ThreadSafeInvertedIndex index;

		public AddTask(Path file, ThreadSafeInvertedIndex index) {
			this.file = file;
			this.index = index;
		}

		@Override
		public void run() {
			try {
				InvertedIndex localIndex = new InvertedIndex();
				InvertedIndexBuilder.stemFile(file, localIndex);
				index.addAll(localIndex);
			} catch (IOException e) {
				System.out.println("Could not build local index from file");
			}
		}
	}
}
