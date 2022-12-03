package tuur;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DayThree {
	public static void main(String[] args) throws IOException, URISyntaxException {
		List<String> lines = Files.readAllLines(Paths.get(DayThree.class.getResource("input-day03.txt").toURI()));

		System.out.println("Part 1: " + part1(lines));
		System.out.println("Part 2: " + part2(lines));
	}

	private static int part1(List<String> lines) {
		int total = 0;
		for (String line : lines) {
			Set<Character> firstCompartment = asCharacterset(line.substring(0, line.length() / 2));
			Set<Character> secondCompartment = asCharacterset(line.substring(line.length() / 2, line.length()));

			firstCompartment.retainAll(secondCompartment);
			Character commonItem = firstCompartment.stream().findFirst().get();
			total += priorityOf(commonItem);
		}
		return total;
	}

	private static int part2(List<String> lines) {
		int total = 0;
		for (int i = 0; i < lines.size(); i += 3) {
			Set<Character> commonChars = asCharacterset(lines.get(i));
			commonChars.retainAll(asCharacterset(lines.get(i + 1)));
			commonChars.retainAll(asCharacterset(lines.get(i + 2)));

			Character commonItem = commonChars.stream().findFirst().get();
			total += priorityOf(commonItem);
		}
		return total;
	}

	private static Set<Character> asCharacterset(String secondCompartment) {
		Set<Character> itemsSecond = new HashSet<>();
		for (char c : secondCompartment.toCharArray()) {
			itemsSecond.add(Character.valueOf(c));
		}
		return itemsSecond;
	}

	private static int priorityOf(Character c) {
		if (Character.isUpperCase(c)) {
			return (int) c.charValue() - 64 + 26;
		}
		return (int) c.charValue() - 96;
	}
}