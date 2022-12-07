package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DaySeven {
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DaySeven.class.getResource("input-day07.txt").toURI()));

		Directory rootDir = parseFileSystem(lines);

		PartOne partOne = new PartOne();
		rootDir.acceptVisit(partOne);
		System.out.println("Part 1: " + partOne.getResult());

		long totalSpace = 70000000l;
		long requiredSpace = 30000000l;
		long totalUsedSpace = rootDir.size();
		long freeSpace = totalSpace - totalUsedSpace;
		long toDelete = requiredSpace - freeSpace;

		PartTwo partTwo = new PartTwo(toDelete);
		rootDir.acceptVisit(partTwo);
		System.out.println("Part 2: " + partTwo.getResult());

	}

	private static Directory parseFileSystem(List<String> lines) {
		Directory rootDir = new Directory(null);
		Directory currentDir = rootDir;
		for (String line : lines) {
			if (line.startsWith("$ cd ")) {
				String target = line.substring(5);
				if ("/".equals(target)) {
					currentDir = rootDir;
				} else if ("..".equals(target)) {
					currentDir = currentDir.parent;
				} else {
					currentDir = currentDir.getSubdir(target);
				}
			} else if (line.startsWith("dir")) {
				currentDir.newSubdir(line.substring(4));
			} else if (line.startsWith("$ ls")) {
				// ignore
			} else {
				String[] split = line.split("\\s");
				currentDir.addFile(split[1], Integer.parseInt(split[0]));
			}
		}
		return rootDir;
	}

	private static class PartOne implements Consumer<Directory> {

		private long result = 0l;

		@Override
		public void accept(Directory t) {
			long dirSize = t.size();
			if (dirSize <= 100000l) {
				result += dirSize;
			}
		}

		public long getResult() {
			return result;
		}
	}

	private static class PartTwo implements Consumer<Directory> {

		private Directory toDelete;

		private final long targetSize;

		public PartTwo(long targetSize) {
			this.targetSize = targetSize;
		}

		@Override
		public void accept(Directory t) {
			long dirSize = t.size();
			if (dirSize >= targetSize) {
				if (toDelete == null || toDelete.size() > dirSize) {
					toDelete = t;
				}
			}
		}

		public long getResult() {
			return toDelete.size();
		}
	}

	private static class Directory {
		private final Directory parent;

		private final Map<String, Integer> files = new HashMap<>();

		private final Map<String, Directory> subdirs = new HashMap<>();

		private transient Long size = null;

		public Directory(Directory parent) {
			this.parent = parent;
		}

		public void addFile(String name, Integer size) {
			this.files.put(name, size);
		}

		public void newSubdir(String name) {
			this.subdirs.put(name, new Directory(this));
		}

		public Directory getSubdir(String name) {
			return subdirs.get(name);
		}

		public long size() {
			if (size == null) {
				long result = 0;
				for (Integer size: files.values()) {
					result += size;
				}
				for (Directory subdir : subdirs.values()) {
					result += subdir.size();
				}
				size = result;
			}
			return size;
		}

		public void acceptVisit(Consumer<Directory> function) {
			for (Directory subdir : subdirs.values()) {
				subdir.acceptVisit(function);
			}
			function.accept(this);
		}
	}
}