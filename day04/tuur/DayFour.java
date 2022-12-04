package tuur;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DayFour {
	private static final Pattern pattern = Pattern.compile("^(?<a>\\d+)-(?<b>\\d+),(?<c>\\d+)-(?<d>\\d+)$");

	public static void main(String[] args) throws IOException, URISyntaxException {
		List<String> lines = Files.readAllLines(Paths.get(DayFour.class.getResource("input-day04.txt").toURI()));

		int part1 = 0;
		int part2 = 0;
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.find()) {
				throw new RuntimeException("Cannot parse line: " + line);
			}
			int a = Integer.parseInt(matcher.group("a"));
			int b = Integer.parseInt(matcher.group("b"));
			int c = Integer.parseInt(matcher.group("c"));
			int d = Integer.parseInt(matcher.group("d"));
			if (oneRangeIncludesTheOther(a, b, c, d)) {
				part1++;
			}
			if(overlap(a, b, c, d)) {
				part2++;
			}
		}
		
		System.out.println("Part 1: " + part1);
		System.out.println("Part 2: " + part2);
	}

	/**
	 *Given 2 ranges: [a, b] [c, d] 
	 * exclusive: 
	 * ---a---b---c---d--- 
	 * ---c---d---a---b--- 
	 * overlap:
	 * ---a---c---b---d--- 
	 * ---c---a---d---b--- 
	 * inclusive: 
	 * ---a---c---d---b---
	 * ---c---a---b---d---
	 */
	private static boolean oneRangeIncludesTheOther(int a, int b, int c, int d) {
		return (a <= c && b >= d) || (c <= a && d >= b);
	}

	private static boolean overlap(int a, int b, int c, int d) {
		return !(b < c || d < a);
	}
}
