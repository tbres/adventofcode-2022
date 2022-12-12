package tuur;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class DayEleven {

	private static final String STARTING_ITEMS = "  Starting items: ";
	private static final String OPERATION = "  Operation: new = old ";
	private static final String TEST_DIVISIBLE_BY = "  Test: divisible by ";
	private static final String IF_FALSE_THROW_TO_MONKEY = "    If false: throw to monkey ";
	private static final String IF_TRUE_THROW_TO_MONKEY = "    If true: throw to monkey ";

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayEleven.class.getResource("input-day11.txt").toURI()));

		List<Monkey> monkies = parse(lines);
		System.out.println("Part 1: " + shenanigans(monkies, 20, i -> i / 3));

		monkies = parse(lines);
		final long lcm = monkies.stream().map(m -> m.test).reduce(1l, (i, j) -> i * j);
		System.out.println("Part 2: " + shenanigans(monkies, 10_000, i -> i % lcm));
	}

	private static BigInteger shenanigans(List<Monkey> monkies, int rounds, Function<Long, Long> worryReduction) {
		for (int round = 1; round <= rounds; round++) {
			for (Monkey monkey : monkies) {
				for (long item : monkey.items) {
					long newItem = monkey.increaseWorryLevel(item);
					newItem = worryReduction.apply(newItem);
					if (newItem % monkey.test == 0l) {
						monkies.get(monkey.ifTrue).items.add(newItem);
					} else {
						monkies.get(monkey.ifFalse).items.add(newItem);
					}
					monkey.inspectionCount++;
				}
				monkey.items.clear();
			}
		}
		
		return monkies.stream()
				.map(m -> m.inspectionCount)
				.sorted(Comparator.reverseOrder())
				.limit(2)
				.map(l -> new BigInteger(Long.toString(l)))
				.reduce(BigInteger.ONE, (i, j) -> i.multiply(j));
	}

	private static List<Monkey> parse(List<String> lines) {
		List<Monkey> monkies = new ArrayList<DayEleven.Monkey>();
		Monkey currentMonkey = null;
		for (String line : lines) {
			if (line.startsWith("Monkey")) {
				currentMonkey = new Monkey();
				monkies.add(currentMonkey);
			} else if (line.startsWith(STARTING_ITEMS)) {
				String[] split = line.substring(STARTING_ITEMS.length()).split(",");
				for (String item : split) {
					currentMonkey.items.add(Long.parseLong(item.trim()));
				}
			} else if (line.startsWith(OPERATION)) {
				String[] split = line.substring(OPERATION.length()).split("\\s");
				currentMonkey.operator = split[0].trim();
				currentMonkey.operand = split[1].trim();
			} else if (line.startsWith(TEST_DIVISIBLE_BY)) {
				currentMonkey.test = Long.parseLong(line.substring(TEST_DIVISIBLE_BY.length()).trim());
			} else if (line.startsWith(IF_TRUE_THROW_TO_MONKEY)) {
				currentMonkey.ifTrue = Integer.parseInt(line.substring(IF_TRUE_THROW_TO_MONKEY.length()).trim());
			} else if (line.startsWith(IF_FALSE_THROW_TO_MONKEY)) {
				currentMonkey.ifFalse = Integer.parseInt(line.substring(IF_FALSE_THROW_TO_MONKEY.length()).trim());
			}
		}
		return monkies;		
	}

	private static class Monkey {
		private List<Long> items = new ArrayList<>();
		private Long test;
		private String operator, operand;
		private Integer ifTrue, ifFalse;
		private int inspectionCount = 0;

		private long increaseWorryLevel(long currentLevel) {
			long other = "old".equals(operand) ? currentLevel : Long.parseLong(operand);
			if ("*".equals(operator)) {
				return currentLevel * other;
			} else {
				return currentLevel + other;
			}
		}
	}
}
