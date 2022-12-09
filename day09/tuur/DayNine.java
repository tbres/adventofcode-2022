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

		System.out.println("Part 1: " + part2(lines, 1));
		System.out.println("Part 2: " + part2(lines, 9));
	}

	private static int part2(List<String> lines, final int tailLength) {
		List<Coord> rope = new ArrayList<DayNine.Coord>();
		for (int i = 0; i <= tailLength; i++) {
			rope.add(new Coord(0, 0));
		}
		Set<Coord> allTailPositions = new HashSet<>();
		allTailPositions.add(rope.get(0));

		for (String line : lines) {
			String[] split = line.split("\\s");
			String direction = split[0];
			int amount = Integer.parseInt(split[1]);

			for (int i = 0; i < amount; i++) {
				// 1. move head
				rope.set(tailLength, move(rope.get(tailLength), direction));

				// 2. move the rest of the rope
				for (int j = rope.size() - 2; j >= 0; j--) {
					Coord compare = rope.get(j + 1);
					Coord current = rope.get(j);

					if (Math.abs(compare.x - current.x) > 1 || Math.abs(compare.y - current.y) > 1) {
						current = new Coord(current.x + (int) Math.signum(compare.x - current.x),
								current.y + (int) Math.signum(compare.y - current.y));
					}

					rope.set(j, current);
				}
				allTailPositions.add(rope.get(0));
			}
		}
		return allTailPositions.size();
	}

	private static Coord move(Coord head, String direction) {
		switch (direction) {
		case "D": return new Coord(head.x, head.y - 1);
		case "U": return new Coord(head.x, head.y + 1);
		case "L": return new Coord(head.x - 1, head.y);
		case "R": return new Coord(head.x + 1, head.y);
		default: throw new IllegalStateException("Unknown direction " + direction);
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
			return 31 *(31 + x) + y;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass())
				return false;
			Coord other = (Coord) obj;
			return x == other.x && y == other.y;
		}
	}
}
