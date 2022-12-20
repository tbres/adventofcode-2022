package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DayFifteen {
	
//	private static final Pattern regex = Pattern.compile("x=(?<x1>[-]?\\d+),\\sy=(?<y1>[-]?//d+)");
	private static final Pattern regex = Pattern.compile("x=(?<x>[-]?\\d+), y=(?<y>[-]?\\d+)");
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayFifteen.class.getResource("input-day15.txt").toURI()));
		List<String> test = Files.readAllLines(Paths.get(DayFifteen.class.getResource("test-input-day15.txt").toURI()));

//		System.out.println("TEST Part 1: expected = 26, actual = " +  part1(parseSensorsAndBeacons(test), 10).entrySet().stream()
//				.filter(c -> c.getKey().y == 10)
//				.filter(c -> c.getValue() != 'B')
//				.count());
//		
//		Map<Coord, Coord> sensorsAndBeacons = parseSensorsAndBeacons(lines);
//		
//		System.out.println("Part 1: " +  part1(sensorsAndBeacons, 2_000_000).entrySet().stream()
//				.filter(c -> c.getKey().y == 2_000_000)
//				.filter(c -> c.getValue() != 'B')
//				.count());

		System.out.println("TEST Part 2: expected = (14, 11), actual = " + part2(parseSensorsAndBeacons(test), 0, 20));
		System.out.println("Part 2: " + part2(parseSensorsAndBeacons(lines), 0, 4_000_000));
	}

	/**
	 * Alternative approach: d
	 * 1. don't calculate the entire world
	 * 2. only look for things impacting the target line!
	 */
	private static Map<Coord, Character> part1(Map<Coord, Coord> sensorsAndBeacons, int lineNumber) {
		Map<Coord, Character> world = new HashMap<>();
		
		for(Entry<Coord,Coord> entry : sensorsAndBeacons.entrySet()) {
			final Coord sensor = entry.getKey();
			final Coord beacon = entry.getValue();
			world.put(sensor, 'S');
			world.put(beacon, 'B');
			
			final int distance = distance(sensor, beacon);
			
			if((sensor.y <= lineNumber && sensor.y + distance >= lineNumber)
				|| (sensor.y >= lineNumber && sensor.y - distance <= lineNumber)){
				int distanceY = Math.abs(sensor.y - lineNumber) ;
				
				for(int dx = 0; dx <= distance - distanceY; dx ++) {
					world.putIfAbsent(new Coord(sensor.x + dx, lineNumber), '#');
					world.putIfAbsent(new Coord(sensor.x - dx, lineNumber), '#');
				}
			}
		}
		
		return world;
	}
	
	private static Coord part2(Map<Coord, Coord> sensorsAndBeacons, final int min, final int max) {
		Map<Integer, SortedSet<Range>> linesToRanges = new HashMap<>();
		
		for(Entry<Coord,Coord> entry : sensorsAndBeacons.entrySet()) {
			final Coord sensor = entry.getKey();
			final Coord beacon = entry.getValue();
						
			final int distance = distance(sensor, beacon);
			System.out.println("Sensor: " + sensor + ", Beacon: " + beacon + " -> distance: " + distance);
			
			for (int dy = 0; dy <= distance; dy++) {
				int maxX = distance - dy;
				for (int dx = 0; dx <= maxX; dx++) {			
					Range range = new Range(sensor.x - dx, sensor.x + dx);
					mergeRange(linesToRanges, sensor.y + dy, range);
					mergeRange(linesToRanges, sensor.y - dy, range);
				}
			}
		}
		
		System.out.println("Finished mapping");
		
		for (int x = min; x <= max; x++) {
			for (int y = min; y <= max; y++) {
				Coord key = new Coord(x,y);
				if(!linesToRanges.containsKey(key)) {
					return key;
				}
			}
		}
		
		return null;
	}

	private static void mergeRange(Map<Integer, SortedSet<Range>> linesToRanges, int lineNumber, Range range) {
		System.out.println("Merge range line " + lineNumber + " " + range );
		SortedSet<Range> rangesOnLine = linesToRanges.get(lineNumber);
		if(rangesOnLine == null) {
			rangesOnLine  = new TreeSet<>();
			rangesOnLine.add(range);
			return;
		}
		
		SortedSet<Range> result = new TreeSet<>();
		for(Range other : rangesOnLine) {
			if(other.overlaps(range)) {
				range = Range.merge(range, other);
			} else {
				result.add(other);
			}
		}
		result.add(range);
	}

	private static void putInWorld(Map<Coord, Character> world, int x, int y, int min, int max) {
		if(min <= x && x<= max &&min <= y && y<= max) {
			world.putIfAbsent(new Coord(x,y), '#');
		}
		
	}
	private static Map<Coord, Character> part1original(Map<Coord, Coord> sensorsAndBeacons) {
		Map<Coord, Character> world = new HashMap<>();
		
		for(Entry<Coord,Coord> entry : sensorsAndBeacons.entrySet()) {
			final Coord sensor = entry.getKey();
			final Coord beacon = entry.getValue();
			world.put(sensor, 'S');
			world.put(beacon, 'B');
			
			
			final int distance = distance(sensor, beacon);
			
			
			for (int dx = 0; dx <= distance ; dx++) {			
				int maxY = distance - dx;
				for (int dy = 0; dy <= maxY; dy++) {
					world.putIfAbsent(new Coord(sensor.x + dx, sensor.y + dy), '#');
					world.putIfAbsent(new Coord(sensor.x + dx, sensor.y - dy), '#');
					world.putIfAbsent(new Coord(sensor.x - dx, sensor.y + dy), '#');
					world.putIfAbsent(new Coord(sensor.x - dx, sensor.y - dy), '#');
				}
			}
		}
		
		return world;
	}

	private static int distance(Coord sensor, Coord beacon) {
		return Math.abs(sensor.x - beacon.x) + Math.abs(sensor.y - beacon.y);
	}

	private static Map<Coord, Coord> parseSensorsAndBeacons(List<String> lines) {
		Map<Coord, Coord> sensorsAndBeacons = new HashMap<>();
		for(String line: lines) {
			Matcher matcher = regex.matcher(line);
			Coord sensor = null;
			Coord beacon = null;
			
			if (matcher.find()) {
				sensor = new Coord(
						Integer.parseInt(matcher.group("x"))
						,Integer.parseInt(matcher.group("y"))
						);
						
			}
			if (matcher.find()) {
				beacon = new Coord(
						Integer.parseInt(matcher.group("x"))
						,Integer.parseInt(matcher.group("y"))
						);
				
			}
			sensorsAndBeacons.put(sensor, beacon);
		}
		return sensorsAndBeacons;
	}
	
	private static class Range implements Comparable<Range>{
		private final int begin, end;

		public Range(int begin, int end) {
			if (begin > end) {
				throw new IllegalArgumentException(begin + " -> " + end);
			}
			this.begin = begin;
			this.end = end;
		}
		
		public boolean overlaps(Range other) {
			return (begin <= other.begin && other.begin < end) || (other.begin <= begin && begin < other.end)
					||  (begin <= other.end && other.end < end) || (other.begin <= end && end < other.end);

		}
		
		public static Range merge (Range first, Range second) {
			return new Range (Math.min(first.begin, second.begin) , Math.max(first.end, second.end));
		}

		@Override
		public int compareTo(Range other) {
			return Integer.compare(begin, other.begin);
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "[" + begin + ", " + end + "]";
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
