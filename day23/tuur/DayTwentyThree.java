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

public class DayTwentyThree {

	private static final char ELF = '#';
	private static final char EMPTY = '.';

	private static enum Direction {
		north, east, south, west;
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayTwentyThree.class.getResource("input-day23.txt").toURI()));

		System.out.println("Part 1: ");
		Set<Coord> elves = parse(lines);
		part1(elves, 10);
		System.out.println("---------------------------------------");
		System.out.println("Part 2: ");
		elves = parse(lines);
		part1(elves, 10_000_0000);


	}

	private static void part1(Set<Coord> elves, int iterations) {
		List<Direction> directions = new ArrayList<DayTwentyThree.Direction>();
		directions.add(Direction.north);
		directions.add(Direction.south);
		directions.add(Direction.west);
		directions.add(Direction.east);
		
//		plot(elves);

		for(int i = 1; i <= iterations; i++) {
//			System.out.println("Round " + i +" =====================================");
//			System.out.println("Directions: " + directions);
			// 1. propose directions
			Map<Coord, List<Coord>> proposals = proposeDirections(elves, directions);
//			System.out.println("Proposals: " + proposals);
			if(proposals.isEmpty()) {
				System.out.println("No elves need to move, round = " + i);
				break;
			}
			
			// 2. move
			for (Entry<Coord, List<Coord>> entry : proposals.entrySet()) {
				if (entry.getValue().size() == 1) {
					Coord proposedPosition = entry.getKey();
					Coord originalPosition = entry.getValue().get(0);
					elves.remove(originalPosition);
					elves.add(proposedPosition);
				}
			}
			// 3. rotate starting direction
			directions.add(directions.remove(0));
			
//			System.out.println(elves);
//			System.out.println();
		}
		plot(elves);
	}

	private static Map<Coord, List<Coord>> proposeDirections(Set<Coord> elves, List<Direction> directions) {
		Map<Coord, List<Coord>> result = new HashMap<>();

		for (Coord elf : elves) {
			Coord north = new Coord(elf.x, elf.y - 1);
			Coord northEast = new Coord(elf.x + 1, elf.y - 1);
			Coord east = new Coord(elf.x + 1, elf.y);
			Coord southEast = new Coord(elf.x + 1, elf.y + 1);
			Coord south = new Coord(elf.x, elf.y + 1);
			Coord southWest = new Coord(elf.x - 1, elf.y + 1);
			Coord west = new Coord(elf.x - 1, elf.y);
			Coord northWest = new Coord(elf.x - 1, elf.y - 1);

			if (elves.contains(north) || elves.contains(northEast) || elves.contains(east) || elves.contains(southEast)
					|| elves.contains(south) || elves.contains(southWest) || elves.contains(west)
					|| elves.contains(northWest)) {

				for (Direction direction : directions) {
					if (Direction.north.equals(direction) && !elves.contains(north) && !elves.contains(northWest)
							&& !elves.contains(northEast)) {
						result.computeIfAbsent(north, pos -> new ArrayList<>()).add(elf);
						break;
					} else if (Direction.east.equals(direction) && !elves.contains(east) && !elves.contains(northEast)
							&& !elves.contains(southEast)) {
						result.computeIfAbsent(east, dir -> new ArrayList<>()).add(elf);
						break;
					} else if (Direction.south.equals(direction) && !elves.contains(south) && !elves.contains(southEast)
							&& !elves.contains(southWest)) {
						result.computeIfAbsent(south, dir -> new ArrayList<>()).add(elf);
						break;
					} else if (Direction.west.equals(direction) && !elves.contains(west) && !elves.contains(northWest)
							&& !elves.contains(southWest)) {
						result.computeIfAbsent(west, dir -> new ArrayList<>()).add(elf);
						break;
					}
				}
			}
		}
		return result;
	}
	
	private static void plot(Set<Coord> elves) {
		long minX = Integer.MAX_VALUE;
		long maxX = Integer.MIN_VALUE;
		long minY = Integer.MAX_VALUE;
		long maxY = Integer.MIN_VALUE;
		for(Coord elf : elves) {
			minX = Math.min(minX, elf.x);
			maxX = Math.max(maxX, elf.x);
			minY = Math.min(minY, elf.y);
			maxY = Math.max(maxY, elf.y);
		}
		
		int empty = 0;
		for(long y = minY; y <=maxY; y++) {
			StringBuilder sb = new StringBuilder();
			for(long x = minX; x <=maxX; x++) {
				Coord c = new Coord(x, y);
				if(elves.contains(c)) {
					sb.append("#");
				} else {
					sb.append(".");
					empty++;
				}
			}
			System.out.println(sb.toString());
		}
		System.out.println("Empty : " + empty);
		
	}

	private static Set<Coord> parse(List<String> lines) {
		Set<Coord> elves = new HashSet<>();
		for (int y = 0; y < lines.size(); y++) {
			char[] chars = lines.get(y).toCharArray();
			for (int x = 0; x < chars.length; x++) {
				if (chars[x] == ELF) {
					elves.add(new Coord(x, y));
				}
			}
		}
		return elves;
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
