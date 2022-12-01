package tuur;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DayOne {
	public static void main(String[] args) throws IOException, URISyntaxException {
		List<String> lines = Files.readAllLines(Paths.get(DayOne.class.getResource("input-part1.txt").toURI()));
		
		int current=0, max1=0, max2=0, max3=0;
		for(String line : lines) {
			if (line.trim().isEmpty()) {

				if(max3 < current) {
					max3 = current;
					if(max2 < max3) {
						int tmp = max2;
						max2 = max3;
						max3 = tmp;
						if(max1 < max2) {
							tmp = max1;
							max1 = max2;
							max2 = tmp;
						}
					}
				}
				
				current = 0;
			} else {
				current += Integer.parseInt(line);
			}
		}
		System.out.println("Part 1: Calories = " + max1);
		System.out.println("Part 2: Calories = " + (max1 + max2 + max3));		
	}
}
