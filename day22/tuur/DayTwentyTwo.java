package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DayTwentyTwo {
	
	public static final Character OPEN = '.';
	public static final Character WALL = '#';
	public static final Character VOID = ' ';
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayTwentyTwo.class.getResource("input-day22.txt").toURI()));

		
		Map<Coord, Character> map = parse(lines);
		
		Coord start = map.entrySet().stream()
				.filter(e -> e.getKey().y == 0)
				.filter(e -> e.getValue() == OPEN)
				.map(Entry::getKey)
				.sorted()
				.findFirst()
				.get();
		
		String commands = lines.get(lines.size() - 1);
		
//		System.out.println("Part 1: " + letswalk(map, commands, start));

		System.out.println(discoverEdges(map, start, 50));
		System.out.println("Part 2: ");

	}
	
	public static Map<Edge, Edge> mapEdges(List<Edge> edges) {
		
		return null;
	}
	
	public static List<Edge> discoverEdges(Map<Coord, Character> map, final Coord start, final int edgeSize) {
		List<Edge> edges = new ArrayList<>();
		Direction direction = Direction.right;
		Coord current = start;
		do {
			
			Coord other = current.move(direction, edgeSize - 1);
			Edge edge = Edge.of(current, other);
			System.out.println(edge);
			if (map.containsKey(other) ) {
				edges.add(edge);
			} else {
				throw new RuntimeException("This shouldn't have happened, coord is of the map! " + other);
			}
			
			//next?
			long count = probes(other).stream().filter(c -> map.containsKey(c)).count();
			if(count == 1) { //outer corner, turn right
				direction = direction.rotateClockwise();
				current = other;
			} else if (count == 2) { //continue straight
				current = other.move(direction);
			} else if (count == 3) {
				current = other.move(direction);
				direction = direction.rotateCounterClockwise();
				current = current.move(direction);
			} else {
				throw new IllegalArgumentException("what happened here? " + other);
			}
		} while (!current.equals(start));
		return edges;
	}
	
	private static List<Coord> probes(Coord coord) {
		return Arrays.asList(
				new Coord(coord.x - 1, coord.y - 1),
				new Coord(coord.x - 1, coord.y + 1),
				new Coord(coord.x + 1, coord.y - 1),
				new Coord(coord.x + 1, coord.y + 1)
				);
	}
	
	public static long letswalk(Map<Coord, Character> map, String command, Coord start) {
		Matcher distance = Pattern.compile("\\d+").matcher(command);
		Matcher rotate = Pattern.compile("[LR]").matcher(command);
		
		Coord location = start;
		Direction direction = Direction.right;
		while (distance.find()) {
			int dist = Integer.parseInt(distance.group());
			System.out.println(location + " walking " + dist + " " + direction.name());
			
			for(int i = 0; i < dist; i++) {
				Coord c = location.move(direction);
				Character value = map.get(c);
				if (value == OPEN) {
					location = c;
				} else if (value == WALL) {
					break;
				} else if (value == null) {
					Direction searchDir = direction.flip();
					Coord searchCoord = location.move(searchDir);
					while (map.get(searchCoord) != null) {
						searchCoord = searchCoord.move(searchDir);
					}
					searchCoord = searchCoord.move(direction);// get back on the map
					
					if(map.get(searchCoord) == WALL) {
						break;
					} else {
						location = searchCoord;
					}
				}
			}
			
			if(rotate.find()) {
				String rotation = rotate.group();
				if("L".equals(rotation)) {
					direction = direction.rotateCounterClockwise();
				} else if("R".equals(rotation)) {
					direction = direction.rotateClockwise();
				} else {
					throw new RuntimeException("Unexpected rotation: " + rotation);
				}
			}
		}
		
		System.out.println("Destination: " + location + ", last direction: " + direction);
		
		
		return 1000 * (location.y + 1) + 4 * (location.x + 1) + direction.numericValue();
		
	}
	
	
	
	public static Map<Coord, Character> parse(List<String> lines) {
		Map<Coord, Character> result = new HashMap<>();
		for(int y = 0; y < lines.size(); y++) {
			char[] chars = lines.get(y).toCharArray();
			for(int x = 0; x < chars.length; x++) {
				char c = chars[x];
				if(c != ' ') {
					result.put(new Coord(x, y), c);
				}
			}
		}
		return result;
	}
	
	public static enum Direction {
		up, down, left, right;
		
		public Direction rotateClockwise() {
			switch (this) {
				case up: return right;
				case right: return down;
				case down: return left;
				case left: return up;
				default: throw new RuntimeException();
			}
		}
		public Direction rotateCounterClockwise() {
			switch (this) {
			case up: return left;
			case left: return down;
			case down: return right;
			case right: return up;
			default: throw new RuntimeException();
			}
		}
		public Direction flip() {
			switch (this) {
			case up: return down;
			case left: return right;
			case down: return up;
			case right: return left;
			default: throw new RuntimeException();
			}
		}
		public int numericValue() {
			switch (this) {
			case right: return 0;
			case down: return 1;
			case left: return 2;
			case up: return 3;
			default: throw new RuntimeException();
			}
		}
	}
	
	private static class Coord implements Comparable<Coord>{

		private final long x, y;

		public Coord(long x, long y) {
			super();
			this.x = x;
			this.y = y;
		}
		
		public Coord move(Direction direction) {
			return move(direction, 1);
		}
		
		public Coord move(Direction direction, int amount) {
			switch (direction) {
			case down: return new Coord(x, y + amount);
			case up: return new Coord(x, y - amount);
			case left: return new Coord(x - amount, y);
			case right: return new Coord(x + amount, y);
			default: throw new RuntimeException();
			}
		}

		@Override
		public int hashCode() {
			return (int) (31 *(31 + x) + y);
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
			if(y == o.y) {
				return Long.compare(x, o.x);				
			} else {
				return Long.compare(y, o.y);
			}
		}
	}
	
	private static class Edge {

		private final Coord start, end;
		
		private Edge(Coord start, Coord end) {
			this.start = start;
			this.end = end;
		}
		
		public static Edge of(Coord a, Coord b) {
			if(a.y == b.y) {
				if(a.x < b.x) {
					return new Edge(a, b);
				} else {
					return new Edge(b, a);
				}
			} 
			if(a.x == b.x) {
				if(a.y < b.y) {
					return new Edge(a, b);
				} else {
					return new Edge(b, a);
				}
			}
			throw new IllegalArgumentException("Not a straight edge: " + a + " " + b);
		}
		
		@Override
		public String toString() {
			return "<" + start + " - " + end + ">";
		}

		@Override
		public int hashCode() {
			return 31 * (31 + end.hashCode()) + start.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Edge)) {
				return false;
			}
			Edge other = (Edge) obj;
			return other.start.equals(start) && other.end.equals(end);
		}
	}
	
}
