package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DayFive {
	private static final Pattern pattern = Pattern
			.compile("^move\\s(?<amount>\\d+)\\sfrom\\s(?<from>\\d+)\\sto\\s(?<to>\\d+)$");

	private static interface MoveFunction {
		void apply(int amount, ArrayDeque<Character> fromHere, ArrayDeque<Character> toHere);
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayFive.class.getResource("input-day05.txt").toURI()));

		System.out.println("Part 1: " + execute(lines, DayFive::movePart1));
		System.out.println("Part 2: " + execute(lines, DayFive::movePart2));
	}

	public static String execute(List<String> lines, MoveFunction move) {
		Map<Integer, ArrayDeque<Character>> stack = parseStack(lines);

		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.matches()) {
				Integer from = Integer.valueOf(matcher.group("from"));
				Integer to = Integer.valueOf(matcher.group("to"));
				int amount = Integer.parseInt(matcher.group("amount"));

				move.apply(amount, stack.get(from), stack.get(to));
			}
		}

		String result = "";
		for (int i = 1; i <= stack.size(); i++) {
			result += stack.get(i).peek();
		}
		return result;
	}

	private static void movePart1(int amount, ArrayDeque<Character> fromHere, ArrayDeque<Character> toHere) {
		for (int i = 0; i < amount; i++) {
			toHere.push(fromHere.pop());
		}
	}

	private static void movePart2(int amount, ArrayDeque<Character> fromHere, ArrayDeque<Character> toHere) {
		if (amount == 0) {
			return;
		}
		Character item = fromHere.pop();
		movePart2(amount - 1, fromHere, toHere);
		toHere.push(item);
	}

	private static Map<Integer, ArrayDeque<Character>> parseStack(List<String> lines) {
		Map<Integer, ArrayDeque<Character>> stacks = new HashMap<>();

		for (String line : lines) {
			if (line.contains("[")) {
				for (int i = 1; i < line.length(); i += 4) {
					Character c = Character.valueOf(line.charAt(i));
					if (Character.isLetter(c)) {
						int stackNumber = (i / 4) + 1;
						stacks.computeIfAbsent(stackNumber, ArrayDeque::new).add(c);
					}
				}
			}
		}

		return stacks;
	}
}
