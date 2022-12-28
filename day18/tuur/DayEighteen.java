package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DayEighteen {
	
	private static final Character WATER = 'w';
	private static final Character LAVA = 'l';

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayEighteen.class.getResource("input-day18.txt").toURI()));

		
		
		System.out.println("Part 1: " + part1(parse(lines)));

		System.out.println("Part 2: " + part2(parse(lines)));

	}

	private static int part1(World world) {
		int surfaceArea = 0;
		for (Coord c: world.lava.keySet()) {
			for (Coord neighbour: neighbours(c)) {
				if (!world.lava.containsKey(neighbour)) {
					surfaceArea++;
				}
			}
		}
		return surfaceArea;
	}
	
	private static long part2(World world) {
		
		//start with all the corners (just outside the current box)
		Set<Coord> expansion = new HashSet<>();
		expansion.add(new Coord(world.minX - 1, world.minY - 1, world.minZ - 1));
		expansion.add(new Coord(world.minX - 1, world.minY - 1, world.maxZ + 1));
		expansion.add(new Coord(world.minX - 1, world.maxY + 1, world.minZ - 1));
		expansion.add(new Coord(world.minX - 1, world.maxY + 1, world.maxZ + 1));
		expansion.add(new Coord(world.maxX + 1, world.minY - 1, world.minZ - 1));
		expansion.add(new Coord(world.maxX + 1, world.minY - 1, world.maxZ + 1));
		expansion.add(new Coord(world.maxX + 1, world.maxY + 1, world.minZ - 1));
		expansion.add(new Coord(world.maxX + 1, world.maxY + 1, world.maxZ + 1));
		expansion.forEach(c -> world.add(c, WATER));

		while (!expansion.isEmpty()) {
			Set<Coord> last = new HashSet<>();
			for(Coord c : expansion) {
				for(Coord neighbour : neighbours(c)) {
					if(world.isInBounds(neighbour) && !world.contains(neighbour)) {
						world.add(neighbour, WATER);
						last.add(neighbour);
					}
				}
			}
			expansion = last;
		}
		
		return world.lava.entrySet().stream()
			.filter(entry -> LAVA.equals(entry.getValue()))
			.map(Entry::getKey)
			.flatMap(lava -> neighbours(lava).stream())
			.filter(neighbour -> WATER.equals(world.get(neighbour)))
			.count();
	}
	
	private static World parse(List<String> lines) {
		World world = new World();
		lines.stream()
				.map(line -> {
					String[] split = line.split(",");
					return new Coord(Long.parseLong(split[0]), Long.parseLong(split[1]), Long.parseLong(split[2]));
				})
				.forEach(c -> world.add(c, LAVA));
		return world;
	}
	
	private static List<Coord> neighbours(Coord c) {
		return Arrays.asList(
			new Coord(c.x + 1, c.y, c.z),
			new Coord(c.x - 1, c.y, c.z),
			new Coord(c.x, c.y + 1, c.z),
			new Coord(c.x, c.y - 1, c.z),
			new Coord(c.x, c.y, c.z + 1),
			new Coord(c.x, c.y, c.z - 1)
		);
	}
	
	private static class World {
		private final Map<Coord, Character> lava = new HashMap<>();
		
		private long minX = Long.MAX_VALUE , maxX = Long.MIN_VALUE;
		private long minY = Long.MAX_VALUE , maxY = Long.MIN_VALUE;
		private long minZ = Long.MAX_VALUE , maxZ = Long.MIN_VALUE;
		
		public void add(Coord c, Character value) {
			lava.put(c, value);
			
			minX = Math.min(minX, c.x);
			minY = Math.min(minY, c.y);
			minZ = Math.min(minZ, c.z);

			maxX = Math.max(maxX, c.x);
			maxY = Math.max(maxY, c.y);
			maxZ = Math.max(maxZ, c.z);
		}	
		
		public Character get(Coord neighbour) {
			return lava.get(neighbour);
		}

		public boolean isInBounds(Coord c) {
			return minX <= c.x && c.x <= maxX
					&& minY <= c.y && c.y <= maxY
					&& minZ <= c.z && c.z <= maxZ;
		}

		public boolean contains(Coord c) {
			return lava.containsKey(c);
		}
		
	}
	
	private static class Coord {

		private final long x, y, z;

		public Coord(long x, long y, long z) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public int hashCode() {
			return (int) (31 * (31 * (31 + x) + y) + z);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass())
				return false;
			Coord other = (Coord) obj;
			return x == other.x && y == other.y && z == other.z;
		}

		public String toString() {
			return "(" + x + ", " + y + ", " + z + ")";
		}
	}
}
