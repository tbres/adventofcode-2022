package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
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
		
		
		LinkedList<Integer> sequence = new LinkedList<>();
		lines.forEach(sequence::add);
		
		System.out.println(sequence);
		
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
		
		
		System.out.println("------------------------------------------------------------------");
		System.out.println("Attempt 2:");
		System.out.println();
		
		part1(lines);
	}
	
	
	
	private static void part1(List<Integer> input) {
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
	private static void list(List<Integer> input) {
		
		Map<Integer, Node> mix = mix(input);
		
		Node first = mix.get(1);
		Node current = first;
		do {
			System.out.print( current.value + ", ");
			current = current.next;
		} while(current != first);
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
		
		return quickAccess;
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
}
