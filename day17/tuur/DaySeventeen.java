package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class DaySeventeen {
	
	public static void main(String[] args) throws Exception {
		String input = Files.readAllLines(Paths.get(DaySeventeen.class.getResource("input-day17.txt").toURI())).get(0);
		String test = Files.readAllLines(Paths.get(DaySeventeen.class.getResource("test-input-day17.txt").toURI())).get(0);

		System.out.println("Test 1: expected = 3068, actual = " + part1(test.toCharArray(),  2022));
		System.out.println("Part 1: expected = 3135, actual = " + part1(input.toCharArray(), 2022));

		System.out.println("Test 1: expected = 1514285714288, actual = " + part1(test.toCharArray(), 1_000_000_000_000l));
		System.out.println("Part 2: ");

	}
	
	/**
	 * The tall, vertical chamber is exactly seven units wide. 
	 * 
	 * Each rock appears so that its left edge is two units away from the left wall 
	 * and its bottom edge is three units above the highest rock in the room (or the floor, if there isn't one).
	 */
	public static long part1(char[] chars, final long rocks) {
		Jet jet = new Jet(chars);
		
		long height = 0;
		
		Set<Coord> occupied = new HashSet<>();
		occupied.add(new Coord(1, 0));
		occupied.add(new Coord(2, 0));
		occupied.add(new Coord(3, 0));
		occupied.add(new Coord(4, 0));
		occupied.add(new Coord(5, 0));
		occupied.add(new Coord(6, 0));
		occupied.add(new Coord(7, 0));
		
		for(long i = 0; i < rocks; i++) { 
			Shape shape = Shape.create(i, height + 4l);
//			System.out.println("Start: " + shape);
			while(true) {
				Shape next = null;
				if('>' == jet.next()) {
					next = shape.right();
//					System.out.println(" Move right: " + next );
				} else  {
					next = shape.left();
//					System.out.println(" Move left:  " + next );
				}

				boolean toSmall = next.minX < 1;
//				System.out.println(" - " + next.minX + " < 1 : " + toSmall);
				boolean toBig = next.maxX > 7;
//				System.out.println(" - " + next.maxX + " > 7 : " + toBig);
				boolean collides = next.collides(occupied);
//				System.out.println(" - collides " + collides);
				
				if (toSmall || toBig || collides) {
					next = shape; // can't move into that spot
//					System.out.println(" Can't move there! going back to: " + next );
				}		
				
				Shape down = next.down();
//				System.out.println(" Move down:  " + down);
				if (down.collides(occupied)) {
					integrate(occupied, next);
					
					height = Math.max(height, next.maxY);
					System.out.println(i + " height=" + height  + " occupied = " + occupied.size() + " -> " + next);
					break; //block has landed
				}		
				shape = down;
			}
		}
		
		return height;		
	}
	
	private static Set<Coord> integrate(Set<Coord> occupied, Shape s) {
		occupied.addAll(s.pieces);
		
		// Can we reduce this set?
//		for(long i = s.minY; i <= s.maxY; i++) {
//			
//			boolean fullLine = true;
//			for(long j = 1; j <= 7; j++) {
//				if(!occupied.contains(new Coord(j,i))) {
//					fullLine = false;
//				}
//			}
//			
//			final long line = i;
//			if(fullLine) {
//				System.out.println("Reducing to " + line);
//				occupied = occupied.stream()
//					.filter(c  -> c.y >= line)
//					.collect(Collectors.toCollection(HashSet::new));
//			}
//		}
		
		return occupied;
	}
	
	private static class Jet implements Iterator<Character> {
		
		private final char[] chars;

		public Jet(char[] chars) {
			this.chars = chars;
		}

		@Override
		public boolean hasNext() {
			return true;
		}
		
		private int position = 0;

		@Override
		public Character next() {
			Character result = chars[position];
			position++;
			if(position >= chars.length) {
				position = 0;
			}
			return result;
		}
		
	}	
	
	private static class Shape {
		private final SortedSet<Coord> pieces = new TreeSet<>();
		
		public final long minX;
		public final long maxX;
		public final long minY;
		public final long maxY;
		
		private Shape(Coord... coords) {
			long minX = 8;
			long maxX = 0;
			long minY = Integer.MAX_VALUE;
			long maxY = 0;
			for(Coord c : coords) {
				pieces.add(c);
				minX = Math.min(minX, c.x);
				maxX = Math.max(maxX, c.x);
				minY = Math.min(minY, c.y);
				maxY = Math.max(maxY, c.y);
			}
			this.minX = minX;
			this.maxX = maxX;
			this.minY = minY;
			this.maxY = maxY;
		}

		private Shape(Shape other, int dx, int dy) {
			other.pieces.stream().map(c -> new Coord(c.x + dx, c.y + dy)).forEach(pieces::add);
			this.minX = other.minX + dx;
			this.maxX = other.maxX + dx;
			this.minY = other.minY - dy;
			this.maxY = other.maxY + dy;
			
		}
		
		public boolean collides(Set<Coord> occupied) {
			for(Coord c : pieces) {
				if(occupied.contains(c)) {
					return true;
				}
			}
			return false;
		}
		
		public Shape down() {
			return new Shape(this, 0, -1);
		}
		public Shape left() {
			return new Shape(this, -1, 0);
		}
		public Shape right() {
			return new Shape(this, +1, 0);
		}
		
		public static  Shape create(long count, long height) {
			long l = count % 5;
			if (l == 0) {
				return createFirst(height);
			} else if (l == 1) {
				return createSecond(height);
			} else if (l == 2) {
				return createThird(height);
			} else if (l == 3) {
				return createFourth(height);
			} else if (l == 4) {
				return createFifth(height);
			} else {
				throw new RuntimeException();
			}
		}
		
		public static Shape createFirst(long height) {
			return new Shape(
						new Coord(3l, height),
						new Coord(4l, height),
						new Coord(5l, height),
						new Coord(6l, height)
					);
		}
		public static Shape createSecond(long height) {
			return new Shape(
						new Coord(4l, height + 2l),
						new Coord(3l, height + 1l),
						new Coord(4l, height + 1l),
						new Coord(5l, height + 1l),
						new Coord(4l, height)
					);
		}
		public static Shape createThird(long height) {
			return new Shape(
					new Coord(5l, height + 2l),
					new Coord(5l, height + 1l),
					new Coord(3l, height),
					new Coord(4l, height),
					new Coord(5l, height)
					);
		}
		public static Shape createFourth(long height) {
			return new Shape(
					new Coord(3l, height + 3l),
					new Coord(3l, height + 2l),
					new Coord(3l, height + 1l),
					new Coord(3l, height)
					);
		}
		public static Shape createFifth(long height) {
			return new Shape(
					new Coord(3l, height + 1l),
					new Coord(4l, height + 1l),
					new Coord(3l, height),
					new Coord(4l, height)
					);
		}
		
		@Override
		public String toString() {
			return pieces.stream().map(Coord::toString).collect(Collectors.joining(", ", "[", "]"));
		}
	}
	
	
	
	private static class Coord implements Comparable<Coord>{

		private final long x, y;

		public Coord(long x, long y) {
			super();
			this.x = x;
			this.y = y;
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
}
