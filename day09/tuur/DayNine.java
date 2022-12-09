package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DayNine {

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayNine.class.getResource("input-day09.txt").toURI()));

		System.out.println("Part 1: " + part1(lines));
		System.out.println("Part 2: "  + part2(lines));
	}

	private static int part1(List<String> lines) {
		Coord head = new Coord(0, 0);
		Coord tail = new Coord(0, 0);
		Set<Coord> allTailPositions = new HashSet<>();
		allTailPositions.add(tail);
		
		for (String line : lines) {
			String[] split = line.split("\\s");
			String direction = split[0];
			int amount = Integer.parseInt(split[1]);

			for (int i = 0; i < amount; i++) {
				Coord previousHead = head;
				head = move(head, direction);
				if (Math.abs(head.x - tail.x) > 1 || Math.abs(head.y - tail.y) > 1) {
					tail = previousHead;
					allTailPositions.add(tail);
				}
			}
		}
		return allTailPositions.size();
	}
	
	private static Coord move(Coord head, String direction) {
		switch (direction) {
		case "D":
			return new Coord(head.x, head.y - 1);
		case "U":
			return new Coord(head.x, head.y + 1);
		case "L":
			return new Coord(head.x - 1, head.y);
		case "R":
			return new Coord(head.x + 1, head.y);
		default:
			throw new IllegalStateException("Unknown direction " + direction);
		}
	}

	private static int part2(List<String> lines) {
		Coord head = new Coord(0, 0);
		List<Coord> tail = new ArrayList<DayNine.Coord>();
		for (int i = 1; i <= 9; i++) {
			tail.add(new Coord(0, 0));
		}

		Set<Coord> allTailPositions = new HashSet<>();
		allTailPositions.add(new Coord(0, 0));

		for (String line : lines) {
			String[] split = line.split("\\s");
			String direction = split[0];
			int amount = Integer.parseInt(split[1]);

			for (int i = 0; i < amount; i++) {
				// 1. move head
				head = move(head, direction);
				
				// 2. move tail
				Coord compare = head;
				for (int j = tail.size() - 1; j >= 0; j--) {
					Coord current = tail.get(j);
					
					if (Math.abs(compare.x - current.x) > 1 || Math.abs(compare.y - current.y) > 1) {
						current = new Coord(
								current.x + (int)Math.signum(compare.x - current.x), 
								current.y + (int)Math.signum(compare.y - current.y)
								);				
					}
					
					tail.set(j, current);
					compare = current;
				}
				allTailPositions.add(tail.get(0));
			}
		}
		return allTailPositions.size();
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
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Coord other = (Coord) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}
}
