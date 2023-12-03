package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DayTwenty {

	public static void main(String[] args) throws Exception {
		List<Long> lines = Files.lines(Paths.get(DayTwenty.class.getResource("input-day20.txt").toURI()))
				.map(Long::valueOf).collect(Collectors.toList());

		System.out.println("Part 1: " + decrypt(lines, 1l, 1));
		System.out.println("Part 2: " + decrypt(lines, 811589153l, 10));

	}

	private static long decrypt(List<Long> input, long decryptionKey, int repeat) {
		List<Node> nodes = parse(input, decryptionKey);

		for (int loop = 0; loop < repeat; loop++) {
			mix(nodes);
		}

		long result = 0;
		Node current = nodes.stream().filter(node -> node.value == 0).findAny().get();
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 1000; i++) {
				current = current.next;
			}
			result += current.value;
		}
		return result;
	}

	private static void mix(List<Node> nodes) {
		for (Node node : nodes) {
			long moves = node.value % (nodes.size() - 1);

			if (moves < 0l) {
				for (long i = 0; i > moves; i--) {
					node.moveLeft();
				}
			} else {
				for (long i = 0; i < moves; i++) {
					node.moveRight();
				}
			}
		}
	}

	private static List<Node> parse(List<Long> input, long decryptionKey) {
		List<Node> result = new ArrayList<DayTwenty.Node>();

		final Node first = new Node(input.get(0) * decryptionKey);
		result.add(first);
		Node previous = first;
		for (int i = 1; i < input.size(); i++) {
			Node current = new Node(input.get(i) * decryptionKey);

			previous.next = current;
			current.previous = previous;

			result.add(current);
			previous = current;
		}

		first.previous = previous;
		previous.next = first;

		return result;
	}

	private static class Node {
		private final long value;

		private Node previous = null;
		private Node next = null;

		private Node(long value) {
			this.value = value;
		}

		public void moveLeft() {
			final Node second = this.previous;
			final Node first = second.previous;
			final Node third = this.next;

			first.next = this;
			this.previous = first;

			this.next = second;
			second.previous = this;

			second.next = third;
			third.previous = second;
		}

		public void moveRight() {
			final Node first = this.previous;
			final Node second = this.next;
			final Node third = second.next;

			first.next = second;
			second.previous = first;

			second.next = this;
			this.previous = second;

			this.next = third;
			third.previous = this;
		}

		public String toString() {
			return "Node (" + value + ")";
		}
	}
}
