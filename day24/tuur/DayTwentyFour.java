package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DayTwentyFour {

	private static final char UP = '^';
	private static final char DOWN = 'v';
	private static final char RIGHT = '>';
	private static final char LEFT = '<';
	private static final char WALL = '#';

	public static void main(String[] args) throws Exception {
		List<String> test = Files
				.readAllLines(Paths.get(DayTwentyFour.class.getResource("test-input-day24.txt").toURI()));
		List<String> lines = Files.readAllLines(Paths.get(DayTwentyFour.class.getResource("input-day24.txt").toURI()));

		System.out.println("Part 1: ");
		World world = parse(lines);

		Coord start = null;
		for (long x = world.minX; x <= world.maxX; x++) {
			Coord c = new Coord(x, world.minY);
			if (world.get(c) == null || world.get(c).isEmpty()) {
				start = c;
				break;
			}
		}
		System.out.println("Start: " + start);

		Coord target = null;
		for (long x = world.minX; x <= world.maxX; x++) {
			Coord c = new Coord(x, world.maxY);
			if (world.get(c) == null || world.get(c).isEmpty()) {
				target = c;
				break;
			}
		}
		System.out.println("Target: " + target);

		World part1 = part1(world, start, target);

		/*---------------------------------------------------*/
		System.out.println();
		System.out.println("Part 2: ");
		
		World part2 = part1(part1, target, start);
		part1(part2, start, target);
		//result = part1 + (part2-1 - 1) + (part2-2 -1) //don't know where these minus ones are coming from.
	}

	private static World part1(World world, Coord initial, final Coord target) {

//		plot(world);

		Set<Path> expiditions = new HashSet<>();
		expiditions.add(new Path(null, initial));

		for (int i = 1; i <= 384; i++) {
//			System.out.println("======== Round " + i + " ===========");

			// 1.move the blizzards
			World nextWorld = moveBlizzards(world);

			// 2. move expidition
			Set<Path> nextExpiditions = moveExpiditions(expiditions, nextWorld);

			// 3. did we reach the target?
			for (Path path : nextExpiditions) {
				if (path.last().equals(target)) {
					System.out.println("GOAL: after " + i + " minutes");
					return world;
				}
			}

			world = nextWorld;
			expiditions = nextExpiditions;
//			plot(world);
//			System.out.println();
//			System.out.println("Expiditions: ");
//			expiditions.forEach(e -> System.out.println(e.positions.toString()));
//			System.out.println(" # Paths: " + expiditions.size());

			if (nextExpiditions.isEmpty()) {
				throw new RuntimeException("No more expiditions!");
			}
		}
		
		throw new RuntimeException("Did not finish!");
	}

	private static Set<Path> moveExpiditions(Set<Path> expiditions, World nextWorld) {
		Set<Path> next = new HashSet<>();
		for (Path path : expiditions) {
			final Coord position = path.last();

			final Coord up = new Coord(position.x, position.y - 1);
			final Coord down = new Coord(position.x, position.y + 1);
			final Coord left = new Coord(position.x - 1, position.y);
			final Coord right = new Coord(position.x + 1, position.y);

			nextStep(nextWorld, path, up).ifPresent(next::add);

			nextStep(nextWorld, path, down).ifPresent(next::add);

			nextStep(nextWorld, path, left).ifPresent(next::add);

			nextStep(nextWorld, path, right).ifPresent(next::add);

			nextStep(nextWorld, path, position).ifPresent(next::add); // stay in place?

		}
		return next;
	}

	private static Optional<Path> nextStep(World world, Path path, Coord next) {
		if (world.contains(next)) { // something is in our way!
			return Optional.empty();
		} else if (next.x < world.minX || next.x > world.maxX || next.y < world.minY || next.y > world.maxY) {
			return Optional.empty();
		}
		return Optional.of(path.diverge(next));
	}

	private static World moveBlizzards(World world) {
		World nextWorld = new World();
		for (Coord position : world.keys()) {
			List<Character> occupants = world.get(position);

			for (Character occupant : occupants) {
				Coord nextPosition = null;
				if (WALL == occupant) {
					nextPosition = position;
				} else if (LEFT == occupant) {
					if (position.x - 1 <= world.minX) {
						nextPosition = new Coord(world.maxX - 1, position.y);
					} else {
						nextPosition = new Coord(position.x - 1, position.y);
					}
				} else if (RIGHT == occupant) {
					if (position.x + 1 >= world.maxX) {
						nextPosition = new Coord(world.minX + 1, position.y);
					} else {
						nextPosition = new Coord(position.x + 1, position.y);
					}
				} else if (UP == occupant) {
					if (position.y - 1 <= world.minY) {
						nextPosition = new Coord(position.x, world.maxY - 1);
					} else {
						nextPosition = new Coord(position.x, position.y - 1);
					}
				} else { // if(DOWN == occupant) {
					if (position.y + 1 >= world.maxY) {
						nextPosition = new Coord(position.x, world.minY + 1);
					} else {
						nextPosition = new Coord(position.x, position.y + 1);
					}
				}
				nextWorld.put(nextPosition, occupant);
			}
		}
		return nextWorld;
	}

	private static World parse(List<String> lines) {
		World world = new World();
		for (int y = 0; y < lines.size(); y++) {
			char[] chars = lines.get(y).toCharArray();
			for (int x = 0; x < chars.length; x++) {
				if ('.' != chars[x]) {
					Coord c = new Coord(x, y);
					world.put(c, chars[x]);
				}
			}
		}
		return world;
	}

	private static void plot(World world) {
		for (long y = world.minY; y <= world.maxY; y++) {
			StringBuilder sb = new StringBuilder();
			for (long x = world.minX; x <= world.maxX; x++) {
				Coord c = new Coord(x, y);
				List<Character> list = world.get(c);
				if (list == null || list.isEmpty()) {
					sb.append(".");
				} else if (list.size() == 1) {
					sb.append(list.get(0));
				} else {
					sb.append(list.size());
				}
			}
			System.out.println(sb.toString());
		}

	}

	private static class Path {
		private final Path previous;
		private final Coord last;

		public Path(Path path, Coord c) {
			this.previous = path;
			this.last = c;
		}

		public Path diverge(Coord c) {
			return new Path(this, c);
		}

		public Coord last() {
			return last;
		}

		@Override
		public int hashCode() {
			return last.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Path other = (Path) obj;
			return last.equals(other.last);
		}

	}

	private static class World {
		private final Map<Coord, List<Character>> world = new HashMap<>();

		private long minX = Integer.MAX_VALUE;
		private long maxX = Integer.MIN_VALUE;
		private long minY = Integer.MAX_VALUE;
		private long maxY = Integer.MIN_VALUE;

		public void put(Coord position, Character c) {
			world.computeIfAbsent(position, coord -> new ArrayList<>()).add(c);
			minX = Math.min(minX, position.x);
			maxX = Math.max(maxX, position.x);
			minY = Math.min(minY, position.y);
			maxY = Math.max(maxY, position.y);
		}

		public Set<Coord> keys() {
			return world.keySet();
		}

		public List<Character> get(Coord position) {
			return world.get(position);
		}

		public boolean contains(Coord position) {
			return world.containsKey(position) && !world.get(position).isEmpty();
		}
	}

	private static class Coord implements Comparable<Coord> {

		private final long x, y;

		public Coord(long x, long y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			return (int) (31 * (31 + x) + y);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass())
				return false;
			Coord other = (Coord) obj;
			return x == other.x && y == other.y;
		}

		public String toString() {
			return "(" + x + ", " + y + ")";
		}

		@Override
		public int compareTo(Coord o) {
			if (y == o.y) {
				return Long.compare(x, o.x);
			} else {
				return Long.compare(y, o.y);
			}
		}
	}
}
