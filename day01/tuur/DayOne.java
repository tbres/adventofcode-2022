package tuur;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DayOne {
	public static void main(String[] args) throws IOException, URISyntaxException {
		List<String> lines = Files.readAllLines(Paths.get(DayOne.class.getResource("input-part1.txt").toURI()));
		
		int current=0, max=0;
		for(String line : lines) {
			if (line.trim().isEmpty()) {
				if(max < current) {
					max = current;
				}
				current = 0;
			} else {
				current += Integer.parseInt(line);
			}
		}
		System.out.println("Part 1: Calories = " + max);
		
		List<Integer> counts = new ArrayList<>();
		current=0;
		for(String line : lines) {
			if (line.trim().isEmpty()) {
				counts.add(current);
				current = 0;
			} else {
				current += Integer.parseInt(line);
			}
		}
		
		Collections.sort(counts);
		int total = counts.get(counts.size()-1) + counts.get(counts.size()-2) + counts.get(counts.size()-3);
		System.out.println("Part 2: Calories = " + total);
		
	}
}
