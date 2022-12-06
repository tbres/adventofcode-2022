package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DaySix {
	public static final String test1 = "mjqjpqmgbljsphdztnvjfqwrcgsmlb";
	public static final String test2 = "bvwbjplbgvbhsrlpgdmjqwftvncz";
	public static final String test3 = "nppdvjthqldpwncqszvftbrmjlhg";
	public static final String test4 = "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg";
	public static final String test5 = "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw";
	
	public static void main(String[] args) throws Exception{
		String input = Files.readAllLines(Paths.get(DaySix.class.getResource("input-day06.txt").toURI())).get(0);
		
		System.out.println("Test 1: expected '7' was: " + solve(test1, 4));
		System.out.println("Test 2: expected '5' was: " + solve(test2, 4));
		System.out.println("Test 3: expected '6' was: " + solve(test3, 4));
		System.out.println("Test 4: expected '10' was: " + solve(test4, 4));
		System.out.println("Test 5: expected '11' was: " + solve(test5, 4));
		System.out.println("--------------------------------------");
		System.out.println("Part 1: " + solve(input, 4));
		System.out.println("--------------------------------------");
		System.out.println("Test 1: expected '19' was: " + solve(test1, 14));
		System.out.println("Test 2: expected '23' was: " + solve(test2, 14));
		System.out.println("Test 3: expected '23' was: " + solve(test3, 14));
		System.out.println("Test 4: expected '29' was: " + solve(test4, 14));
		System.out.println("Test 5: expected '26' was: " + solve(test5, 14));
		System.out.println("--------------------------------------");
		System.out.println("Part 2: " + solve(input, 14));
		System.out.println("--------------------------------------");
	}

	private static int solve(String datastream, final int markerSize) {
		List<Character> marker = new ArrayList<Character>();
		for(int i = 0; i < markerSize ; i++) {
			marker.add(datastream.charAt(i));
		}
		for(int i = markerSize; i < datastream.length(); i++) {
			Set<Character> uniqueChars = new HashSet<Character>(marker);
			if(uniqueChars.size() ==  markerSize) {
				System.out.println("Marker: " + marker);
				return i;
			}
			marker.add(datastream.charAt(i));
			marker.remove(0);
		}
		throw new IllegalArgumentException("No marker found!");
	}
}
