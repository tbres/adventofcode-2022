package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayFourteen {

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayFourteen.class.getResource("input-day14.txt").toURI()));

		System.out.println("Part 1: " + part1(parse(lines)));
		System.out.println("Part 2: " + part2(parse(lines)));
	}

	private static int part1(World world) {
		final Coord start = new Coord(500, 0);

		int count = 0;
		while (true) { // drop 1 unit of sand
			Coord sand = start;

			while (true) { // move the sand
				Coord down = new Coord(sand.x, sand.y + 1);
				Coord downLeft = new Coord(sand.x - 1, sand.y + 1);
				Coord downRight = new Coord(sand.x + 1, sand.y + 1);
				if (world.isNotFilled(down)) {
					sand = down;
				} else if (world.isNotFilled(downLeft)) {
					sand = downLeft;
				} else if (world.isNotFilled(downRight)) {
					sand = downRight;
				} else {
					world.put(sand, 'o'); // sand comes to rest
					count++;
					break;
				}
				if (sand.y > world.maxY) {
					return count; // sand flows out the bottom, falling into the endless void
				}
			}
		}
	}

	private static int part2(World world) {
		final Coord start = new Coord(500, 0);
		final int bottom = world.maxY + 1;

		int count = 0;
		while (world.isNotFilled(start)) {
			Coord sand = start;

			while (true) {
				Coord down = new Coord(sand.x, sand.y + 1);
				Coord downLeft = new Coord(sand.x - 1, sand.y + 1);
				Coord downRight = new Coord(sand.x + 1, sand.y + 1);
				if (world.isNotFilled(down)) {
					sand = down;
				} else if (world.isNotFilled(downLeft)) {
					sand = downLeft;
				} else if (world.isNotFilled(downRight)) {
					sand = downRight;
				} else {
					world.put(sand, 'o');
					count++;
					break;
				}

				if (sand.y == bottom) {
					world.put(sand, 'o');
					count++;
					break;
				}
			}
		}
		return count;
	}

	private static World parse(List<String> lines) {
		World world = new World();

		for (String line : lines) {
			Coord previous = null;
			for (String split : line.split(" -> ")) {
				String[] xy = split.split(",");
				Coord current = new Coord(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
				world.put(current, '#');
				if (previous != null) { //draw line
					for (int i = Math.min(current.x, previous.x); i <= Math.max(current.x, previous.x); i++) {
						for (int j = Math.min(current.y, previous.y); j <= Math.max(current.y, previous.y); j++) {
							world.put(new Coord(i, j), '#');
						}
					}
				}
				previous = current;
			}
		}

		return world;
	}

	private static class World {
		private final Map<Coord, Character> map = new HashMap<>();

		private int minX = Integer.MAX_VALUE, maxX = 0, minY = Integer.MAX_VALUE, maxY = 0;

		public void put(Coord coord, Character c) {
			map.put(coord, c);
			minX = Math.min(minX, coord.x);
			maxX = Math.max(maxX, coord.x);
			minY = Math.min(minY, coord.y);
			maxY = Math.max(maxY, coord.y);
		}

		public boolean isNotFilled(Coord c) {
			return !map.containsKey(c);
		}
	}

	private static class Coord {
		private final int x, y;

		public Coord(int x, int y) {
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
