package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DayTwelve {

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayTwelve.class.getResource("input-day12.txt").toURI()));

		System.out.println("Part 1: " + part1(parseMap(lines)));

		System.out.println("Part 2: " + part2(parseMap(lines)));

	}

	private static Path part1(World world) {
		Map<Coord, Path> visited = new HashMap<>();

		Path path = new Path(null, world.start, world.map);
		visited.put(path.coord, path);

		Path bestPath = null;

		while (path != null && path.hasOptions()) {
			Path nextpath = path.nextOption(world.map);
			// System.out.println(" " + nextpath.coord + " -> " + world.map.get(nextpath.coord) + " (" + nextpath.length + ")");

			if (!visited.containsKey(nextpath.coord) || visited.get(nextpath.coord).length > nextpath.length) {
				visited.put(nextpath.coord, nextpath);
				if (nextpath.coord.equals(world.end)) {
					if (bestPath == null || bestPath.length > nextpath.length) {
						bestPath = nextpath;
					}
				} else {
					path = nextpath;
				}
			}

			while (!path.hasOptions() && path.previous != null) {
				// System.out.println("backtrack" + path.previous.coord);
				path = path.previous; // backtracks
			}
		}
		
		System.out.println("Visited count " + visited.size() + " / " + world.map.size());
		
		return bestPath;
	}
	
	private static int part2(World world) {
		
		Map<Coord, Integer> distance = new HashMap<>();
		
		Map<Coord, Path> visited = new HashMap<>();
		
		Path path = new Path(null, world.start, world.map);
		visited.put(path.coord, path);
		
		while (path != null && path.hasOptions()) {
			Path nextpath = path.nextOption(world.map);

			if (!visited.containsKey(nextpath.coord) || visited.get(nextpath.coord).length > nextpath.length) {
				visited.put(nextpath.coord, nextpath);
				if (nextpath.coord.equals(world.end)) {
					updateDistances(distance, nextpath);
				} else {
					path = nextpath;
				}
			}
			
			while (!path.hasOptions() && path.previous != null) {
				path = path.previous; // backtracks
			}
		}
		
		int best = Integer.MAX_VALUE;
		for(Entry<Coord, Character> entry : world.map.entrySet()) {
			if(entry.getValue() == 'a') {
				Integer dist = distance.get(entry.getKey());
				if(dist != null) {
					best = Math.min(best, dist);
				}
			}
		}
		
		return best;
	}
	
	

	private static void updateDistances(Map<Coord, Integer> distances, Path nextpath) {
		Path current = nextpath;
		int dist = 0;
		while (current.previous != null) {
			current = current.previous;
			dist++;
			distances.merge(current.coord, dist, (x, y) -> Math.min(x, y));
		}
	}

	private static World parseMap(List<String> lines) {
		Map<Coord, Character> map = new HashMap<>();
		Coord start = null;
		Coord end = null;
		for (int y = 0; y < lines.size(); y++) {
			char[] chars = lines.get(y).toCharArray();
			for (int x = 0; x < chars.length; x++) {
				char c = chars[x];
				if (c == 'S') {
					c = 'a';
					start = new Coord(x, y);
				}
				if (c == 'E') {
					c = 'z';
					end = new Coord(x, y);
				}

				map.put(new Coord(x, y), Character.valueOf(c));
			}
		}
		return new World(map, start, end);
	}

	private static class Path {
		private final Path previous;
		private final Coord coord;
		private final List<Coord> options = new ArrayList<>();
		private final int length;

		public Path(Path previous, Coord coord, Map<Coord, Character> map) {
			this.previous = previous;
			this.coord = coord;
			this.options.addAll(neigbours(coord, map));
			if (previous == null) {
				length = 0;
			} else {
				this.options.remove(previous.coord);
				length = previous.length + 1;
			}
		}

		public boolean hasOptions() {
			return !options.isEmpty();
		}

		public Path nextOption(Map<Coord, Character> map) {
			return new Path(this, options.remove(0), map);
		}

		private static Set<Coord> neigbours(Coord coord, Map<Coord, Character> map) {
			char height = map.get(coord);

			Set<Coord> result = new HashSet<>();
			Coord up = new Coord(coord.x, coord.y + 1);
			if (map.containsKey(up) && map.get(up) <= height + 1) {
				result.add(up);
			}
			Coord down = new Coord(coord.x, coord.y - 1);
			if (map.containsKey(down) && map.get(down) <= height + 1) {
				result.add(down);
			}
			Coord right = new Coord(coord.x + 1, coord.y);
			if (map.containsKey(right) && map.get(right) <= height + 1) {
				result.add(right);
			}
			Coord left = new Coord(coord.x - 1, coord.y);
			if (map.containsKey(left) && map.get(left) <= height + 1) {
				result.add(left);
			}
			return result;
		}
		
		@Override
		public String toString() {
			return "Path " + length;
		}
	}

	private static class World {
		private final Map<Coord, Character> map;
		private final Coord start;
		private final Coord end;

		public World(Map<Coord, Character> map, Coord start, Coord end) {
			this.map = map;
			this.start = start;
			this.end = end;
		}

	}

	private static class Coord {

		private final int x, y;

		public Coord(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			return 31 * (31 + x) + y;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass())
				return false;
			Coord other = (Coord) obj;
			return x == other.x && y == other.y;
		}

		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}
}
