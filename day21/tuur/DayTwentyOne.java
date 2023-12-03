package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DayTwentyOne {
	
	private static final String ROOT = "root";
	private static final String HUMAN = "humn";
	private static final Pattern operation = Pattern.compile("^(?<result>[a-z]{4}):\\s(?<left>[a-z]{4})\\s(?<op>[+\\-\\/*])\\s(?<right>[a-z]{4})$");
	private static final Pattern simple = Pattern.compile("^(?<result>[a-z]{4}):\\s(?<value>\\d+)$");
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayTwentyOne.class.getResource("input-day21.txt").toURI()));
		
		Map<String, Job> jobs = parse(lines);
		
		System.out.println("Part 1: " + evaluate(jobs.get(ROOT), jobs));
		System.out.println("-------------------------------------------");

		jobs = parse(lines);
		jobs.remove(HUMAN);
		
		Operation root = (Operation) jobs.get(ROOT);
		jobs.remove(ROOT);
		try {
			long evaluate = evaluate(jobs.get(root.left), jobs);
			System.out.println("left: " + evaluate);
			jobs.put(root.right, new Assignment(root.right, evaluate));
		} catch (Exception e) {
			System.out.println("Can't compute left");
		}
		try {
			long evaluate = evaluate(jobs.get(root.right), jobs);
			System.out.println("right: " + evaluate);
			jobs.put(root.left, new Assignment(root.left, evaluate));
		} catch (Exception e) {
			System.out.println("Can't compute right");
		}
		
		Operation human = find(jobs, HUMAN);
		
		System.out.println("Human = " + human);
		
		System.out.println(evaluatePart2(human, jobs));
		
		System.out.println("Part 2: ");

	}

	private static Operation find(Map<String, Job> jobs, String search) {
		Operation result = null;
		for(Entry<String, Job> e: jobs.entrySet()) {
			if(e.getValue() instanceof Operation) {
				Operation operation = (Operation) e.getValue();
				if(search.equals(operation.left)) {
					String name = e.getKey();
					if("+".equals(operation.op)) {
						result = new Operation(search, name, "-", operation.right);
					} else if("-".equals(operation.op)) {
						result = new Operation(search, name, "+", operation.right);
					} else if("*".equals(operation.op)) {
						result = new Operation(search, name, "/", operation.right);
					} else {
						result = new Operation(search, name, "*", operation.right);
					}
					System.out.println(" Found " + name + " = " + e.getValue());
					System.out.println(" -> Replace with " + search + " = " + result);
					jobs.remove(name);
//					jobs.put(search, result);S
					return result;
				} else if(search.equals(operation.right)) {
					String name = e.getKey();
					if("+".equals(operation.op)) {
						result = new Operation(search, name, "-", operation.left);
					} else if("-".equals(operation.op)) {
						result = new Operation(search, operation.left, "-", name);
					} else if("*".equals(operation.op)) {
						result = new Operation(search, name, "/", operation.left);
					} else {
						result = new Operation(search, operation.left, "/", name);
					}
					jobs.remove(name);
//					jobs.put(search, result);
					return result;
				}
			}
		}
		return null;
	}
	
	private static long evaluatePart2(Job job, Map<String, Job> jobs) {
		if(job instanceof Assignment) {
			return ((Assignment) job).value;
		}
		Operation operation = (Operation) job;
		System.out.println(operation);
		
		Job leftJob = jobs.get(operation.left);
		if(leftJob == null) {
			leftJob = find(jobs, operation.left);
		}
		if(leftJob == null) {
			throw new RuntimeException("Can't find something for " + operation.left);
		}
		long left = evaluatePart2(leftJob, jobs);
		jobs.put(operation.left, new Assignment(operation.left, left));
		
		Job rigthJob = jobs.get(operation.right);
		if(rigthJob == null) {
			rigthJob = find(jobs, operation.right);
		}
		if(rigthJob == null) {
			throw new RuntimeException("Can't find something for " + operation.right);
		}
		long right = evaluatePart2(rigthJob, jobs);
		jobs.put(operation.right, new Assignment(operation.right, right));
		
		switch (operation.op) {
		case "+": return left + right;
		case "-": return left - right;
		case "/": return left / right;
		case "*": return left * right;
		default:throw new RuntimeException();
		}	
	}
	private static long evaluate(Job job, Map<String, Job> jobs) {
		if(job instanceof Assignment) {
			return ((Assignment) job).value;
		}
		Operation operation = (Operation) job;
		System.out.println(operation);
		
		Job leftJob = jobs.get(operation.left);
		long left = evaluate(leftJob, jobs);
		jobs.put(operation.left, new Assignment(operation.left, left));

		Job rigthJob = jobs.get(operation.right);
		long right = evaluate(rigthJob, jobs);
		jobs.put(operation.right, new Assignment(operation.right, right));
		
		switch (operation.op) {
			case "+": return left + right;
			case "-": return left - right;
			case "/": return left / right;
			case "*": return left * right;
			default:throw new RuntimeException();
		}	
	}
	
	private static Map<String, Job> parse(List<String> lines) {
		Map<String, Job> result = new HashMap<>();
		for(String line : lines) {
			Matcher matcher = operation.matcher(line);
			if(matcher.matches()) {
				result.put(matcher.group("result"), new Operation(matcher.group("result"), matcher.group("left"), matcher.group("op"), matcher.group("right")));
			} else {
				matcher = simple.matcher(line);
				if(matcher.matches()) {
					result.put(matcher.group("result"), new Assignment(matcher.group("result"), Long.parseLong(matcher.group("value"))));
				} else {
					throw new IllegalArgumentException("Can't parse: " + line);
				}
			}
		}
		
		return result;
	}
	
	private static interface Job {
		
	}
	
	private static class Operation implements Job{
		private final String name;
		private final String left;
		private final String right;
		private final String op;
		
		private long result;
		
		public Operation(String name, String left, String op, String right) {
			this.name = name;
			this.left = left;
			this.op = op;
			this.right = right;
		}
		
		public void setResult(long result) {
			this.result = result;
		}
		
		@Override
		public String toString() {
			return name + " = " + left + " " + op + " " + right;
		}
	}
	
	private static class Assignment implements Job {
		private final String name;
		private final long value;

		public Assignment(String name, long value) {
			this.name = name;
			this.value = value;
		}
		
	}
}
