package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DayTwenty {
	
	public static void main(String[] args) throws Exception {
		List<Integer> lines = Files.lines(Paths.get(DayTwenty.class.getResource("input-day20.txt").toURI()))
					.map(Integer::valueOf)
					.collect(Collectors.toList());
		
		System.out.println(lines.size());
		
		System.out.println("------------------------------------------------------------------");
		System.out.println("Attempt 1:");
		System.out.println();
		
		firstAttempt(lines);
		
		
		System.out.println("------------------------------------------------------------------");
		System.out.println("Attempt 2:");
		System.out.println();
		
		secondAttempt(lines);

		System.out.println("------------------------------------------------------------------");
		System.out.println("Attempt 3:");
		System.out.println();
		
		thirdAttempt(lines);
	}

	
	private static void secondAttempt(List<Integer> input) {
		Map<Integer, Node> mix = mix(input);
		
		int result = 0;
		
		Node zero = mix.get(0);
		Node current = zero;
		for(int i = 0; i <1000;i++) {
			current = current.next;
		}
		result += current.value;
		System.out.println("1000: " +current);
		
		for(int i = 0; i <1000;i++) {
			current = current.next;
		}
		result += current.value;
		System.out.println("2000: " +current);
		
		for(int i = 0; i <1000;i++) {
			current = current.next;
		}
		result += current.value;
		System.out.println("3000: " +current);
		
		System.out.println("Result: " +result);
		
	}
	private static void thirdAttempt(List<Integer> input) {
		List<Node> mix = mix2(input);
		
		int result = 0;
		
		final Node zero = mix.stream().filter(node -> node.value == 0).findAny().get();
		Node current = zero;
		for(int i = 0; i <1000;i++) {
			current = current.next;
		}
		result += current.value;
		System.out.println("1000: " +current);
		System.out.println(" - : " + current.previous);
		System.out.println(" - : " +current.next);
		
		for(int i = 0; i <1000;i++) {
			current = current.next;
		}
		result += current.value;
		System.out.println("2000: " +current);
		System.out.println(" - : " + current.previous);
		System.out.println(" - : " +current.next);
		
		for(int i = 0; i <1000;i++) {
			current = current.next;
		}
		result += current.value;
		System.out.println("3000: " +current);
		System.out.println(" - : " + current.previous);
		System.out.println(" - : " + current.next);
		
		System.out.println("Result: " +result);
		
	}
	private static void list(List<Integer> input) {
		
		Map<Integer, Node> mix = mix(input);
		
		listy(mix);
	}


	private static void listy(Map<Integer, Node> mix) {
		Node first = mix.get(0);
		Node current = first;
		int i = 0;
		do {
			System.out.print( current.value + ", ");
			current = current.next;
			i++;
		} while(current != first);
		System.out.println();
		System.out.println("Count: " + i);
	}
	
	private static List<Node> mix2(List<Integer> input) {
		List<Node> nodes = parse2(input);
		
		for(Node node : nodes) {
			
			if(node.value == 0) {
				continue;
			} else if(node.value < 0) {
				for(int i = 0; i > node.value; i--) {
					node.moveLeft();
				}
			} else {
				for(int i = 0; i < node.value; i++) {
					node.moveRight();
				}
			}
			System.out.println(node.value + " moves between " + node.previous.value + " and " + node.next.value );
		}
		System.out.println("# " + nodes.size());
//		listy(nodes);
		
		
		return nodes;
	}
	
	private static Map<Integer, Node> mix(List<Integer> input) {
		Map<Integer, Node> nodes = parse(input);
		
		for(int val : input) {
			
			Node current = nodes.get(val);
//			System.out.println(val + "->" + current);
			
			if(val == 0) {
				continue;
			} else if(val < 0) {
				Node moveTo = current.next;
				current.remove();
				for(int i = 0; i > val; i--) {
					moveTo = moveTo.previous;
				}
				moveTo.insertBefore(current);
			} else {
				Node moveTo = current.previous;
				current.remove();
				for(int i = 0; i < val; i++) {
					moveTo = moveTo.next;
				}
				moveTo.insertAfter(current);
				
			}
			
		}
		
		return nodes;
	}
	private static Map<Integer, Node> parse(List<Integer> input) {
		
		Map<Integer, Node> quickAccess = new HashMap<>();
		
		final Node first = new Node(input.get(0));
		quickAccess.put(first.value, first);
		Node previous = first;
		for(int i = 1; i < input.size(); i++) {
			Node current = new Node(input.get(i));
			
			previous.next = current;
			current.previous = previous;
			
			quickAccess.put(current.value, current);
			
			previous = current;
		}
		
		first.previous = previous;
		previous.next = first;
		
		System.out.println("Parsed :" + quickAccess.size() +  "/" + input.size());
		
		return quickAccess;
	}
	

	private static List<Node> parse2(List<Integer> input) {
		
		List<Node> nodes = new ArrayList<DayTwenty.Node>();
		
		final Node first = new Node(input.get(0));
		nodes.add(first);
		Node previous = first;
		for(int i = 1; i < input.size(); i++) {
			Node current = new Node(input.get(i));
			
			previous.next = current;
			current.previous = previous;
			
			nodes.add(current);
			
			previous = current;
		}
		
		first.previous = previous;
		previous.next = first;
		
		System.out.println("Parsed :" + nodes.size() +  "/" + input.size());
		return nodes;
	}
	
	
	private static class Node {
		private final int value;
		
		private Node previous = null;
		private Node next = null;
		
		private Node(int value) {
			this.value = value;
		}
		
		/**
		 * Inserting the given node before this node
		 */
		public void insertBefore(Node node)  {
			final Node first = this.previous;
			
			first.next = node;
			node.previous = first;
			
			this.previous = node;
			node.next = this;
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
		/**
		 * Inserting the given node after this node
		 */
		public void insertAfter(Node node)  {
			final Node second = this.next;
			
			this.next = node;
			node.previous = this;
			
			second.previous = node;
			node.next = second;
		}
		
		/**
		 * Removes this node from the ring
		 */
		public Node remove() {
			
			final Node first = this.previous;
			final Node second = this.next;
			
			this.previous = null;
			this.next = null;
			
			first.next = second;
			second.previous = first;
			
			return this;
		}
		
		public String toString() {
			return "Node ("+value + ")";
		}
	}
	
	private static void firstAttempt(List<Integer> lines) {
		LinkedList<Integer> sequence = new LinkedList<>();
		lines.forEach(sequence::add);
		
//		System.out.println(sequence);
		
		final int size = lines.size();
		for(Integer value: lines) {
			int originalIndex = sequence.indexOf(value);
			
			int newIndex = (originalIndex + value) % (size - 1);
			if (newIndex <= 0) {
				newIndex = (size - 1) + newIndex;
			} 
				
			

//			System.out.println();
//			System.out.println(value + " moves between " + originalIndex + " and " + newIndex);
			
			sequence.remove(originalIndex);
			sequence.add(newIndex, value);
			
//			System.out.println(sequence);
			
		}
		int zeroIndex = sequence.indexOf(Integer.valueOf(0)); 
				
		int index1000 = (zeroIndex + 1000) % size;
		int index2000 = (zeroIndex + 2000) % size;
		int index3000 = (zeroIndex + 3000) % size;
		
		System.out.println("size : "+ size);
		System.out.println("zero (idx=" + zeroIndex + ") : "+ sequence.get(zeroIndex));
		System.out.println("1000 (idx=" + index1000 + ") : "+ sequence.get(index1000));
		System.out.println("2000 (idx=" + index2000 + ") : "+ sequence.get(index2000));
		System.out.println("3000 (idx=" + index3000 + ") : "+ sequence.get(index3000));
		
		System.out.println("Part 1: " + (sequence.get(index1000) + sequence.get(index2000) + sequence.get(index3000)));
	}
}
