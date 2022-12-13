package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DayThirteen {

	private static final String MARKER_1 = "[[2]]";
	private static final String MARKER_2 = "[[6]]";

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayThirteen.class.getResource("input-day13.txt").toURI()));

		System.out.println("Part 1: " + part1(lines));

		System.out.println("Part 2: " + part2(lines));

	}

	private static int part1(List<String> lines) {
		int sum = 0;
		int index = 0;
		for (int i = 0; i < lines.size(); i += 3) {
			index++;
			ListData first = parsePacket(lines.get(i));
			ListData second = parsePacket(lines.get(i + 1));
			
			if (comparePackets(first, second) != 1) {
				sum += index;
			}
		}
		return sum;
	}

	private static int part2(List<String> input) {
		ArrayList<String> lines = new ArrayList<String>();
		lines.addAll(input);
		lines.add(MARKER_1);
		lines.add(MARKER_2);

		List<ListData> sorted = lines.stream()
				.filter(s -> !s.trim().isEmpty())
				.map(DayThirteen::parsePacket)
				.sorted(DayThirteen::comparePackets)
				.collect(Collectors.toList());

		int result = 1;
		for (int i = 0; i < sorted.size(); i++) {
			if (MARKER_1.equals(sorted.get(i).toString()) || MARKER_2.equals(sorted.get(i).toString())) {
				result *= (i + 1);
			}
		}
		return result;
	}

	private static int comparePackets(ListData firstList, ListData secondList) {
		for (int i = 0; i < Math.max(firstList.size(), secondList.size()); i++) {
			if (i >= firstList.size()) {
				return -1;
			}
			if (i >= secondList.size()) {
				return 1;
			}
			int compare = comparePackets(firstList.get(i), secondList.get(i));
			if (compare != 0) {
				return compare;
			}
		}
		return 0;
	}

	private static int comparePackets(PacketData first, PacketData second) {
		if (first instanceof ListData && second instanceof ListData) {
			return comparePackets((ListData) first, (ListData) second);
		}
		if (first instanceof ListData && second instanceof AtomicData) {
			ListData firstList = (ListData) first;
			ListData secondList = new ListData();
			secondList.add(second);
			return comparePackets(firstList, secondList);
		}
		if (first instanceof AtomicData && second instanceof ListData) {
			ListData firstList = new ListData();
			firstList.add(first);
			ListData secondList = (ListData) second;
			return comparePackets(firstList, secondList);
		}
		if (first instanceof AtomicData && second instanceof AtomicData) {
			return Integer.compare(((AtomicData) first).value, ((AtomicData) second).value);
		}
		throw new IllegalArgumentException();
	}

	private static ListData parsePacket(String line) {
		ArrayDeque<ListData> queue = new ArrayDeque<>();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (Character.isDigit(c)) {
				sb.append(c);
			} else {
				if (sb.length() > 0) {
					int value = Integer.parseInt(sb.toString());
					queue.peek().add(new AtomicData(value));
					sb = new StringBuilder();
				}
				if ('[' == c) {
					queue.push(new ListData());
				} else if (']' == c) {
					if (queue.size() > 1) {
						ListData data = queue.pop(); // done parsing this one
						queue.peek().add(data);
					}
				}
			}
		}
		return queue.pop();
	}

	public static interface PacketData {
	}

	public static class AtomicData implements PacketData {
		private final int value;

		public AtomicData(int value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return Integer.toString(value);
		}
	}

	public static class ListData implements PacketData {
		private final List<PacketData> values = new ArrayList<>();

		public void add(PacketData data) {
			values.add(data);
		}

		public int size() {
			return values.size();
		}

		public PacketData get(int i) {
			return values.get(i);
		}

		@Override
		public String toString() {
			return values.stream().map(Objects::toString).collect(Collectors.joining(",", "[", "]"));
		}
	}
}
