package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DayTen {

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayTen.class.getResource("input-day10.txt").toURI()));

		long x = 1l;
		long cycle = 0l;
		
		List<Long> historyX = new ArrayList<Long>();
		historyX.add(x);
		
		StringBuilder sb = new StringBuilder();
		
		for (String command : lines) {
			draw(x, cycle % 40, sb);
			cycle++;
			historyX.add(x);
			
			if (command.startsWith("addx")) {			
				draw(x, cycle % 40, sb);
				cycle++;
				x += Long.parseLong(command.split("\\s")[1]);
				historyX.add(x);
			}
		}
		
		System.out.println("Part 1: " + (historyX.get(20 - 1) * 20l 
				+ historyX.get(60 - 1) * 60l
				+ historyX.get(100 - 1) * 100l 
				+ historyX.get(140 - 1) * 140l 
				+ historyX.get(180 - 1) * 180l
				+ historyX.get(220 - 1) * 220l));
		
		System.out.println("Part 2: " + sb.toString());
	}

	private static void draw(long x, long pixel, StringBuilder sb) {
		if (pixel == 0) {
			sb.append("\n");
		}
		if (pixel == x || pixel == x - 1l || pixel == x + 1l) {
			sb.append("#");
		} else {
			sb.append(".");
		}
	}
}