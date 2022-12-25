package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class DayTwentyFive {
	
	public static void main(String[] args) throws Exception {
		List<String> input = Files.readAllLines(Paths.get(DayTwentyFive.class.getResource("input-day25.txt").toURI()));

		long sum = input.stream().collect(Collectors.summingLong(DayTwentyFive::decode));
		
		System.out.println("Sum: " + sum);
		System.out.println("Encoded: " + encode(sum));
	}
	
	private static String encode(final long input) {
		StringBuilder sb = new StringBuilder();
		long value = input;
		while(value > 0) {
			long remainder = value % 5l;
			value = value / 5l;
			if(remainder == 4) {
				value += 1l;
				sb.insert(0, '-');
			} else if(remainder == 3) {
				value += 1l;
				sb.insert(0, '=');
			} else {
				sb.insert(0, Long.toString(remainder).toCharArray()[0]);
			}
		}
		return sb.toString();
	}
	
	private static long decode(String input) {
		long value = 0l;
		for(char c : input.toCharArray()) {
			value *= 5;
			if(c == '2') {
				value += 2;
			} else if(c == '1') {
				value += 1;
			} else if(c == '0') {
				value += 0;
			} else if(c == '-') {
				value -= 1;
			} else if(c == '=') {
				value -= 2;
			} else {
				throw new RuntimeException("Unexpected character: " + c); 
			}
		}
		return value;
	}
}
