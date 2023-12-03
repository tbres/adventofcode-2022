package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DaySixteen {
	private static final boolean OPENED = true;
	private static final Pattern name = Pattern.compile("Valve\\s(?<name>\\w+)");
	private static final Pattern rate = Pattern.compile("rate=(?<rate>\\d+)");
	private static final Pattern valves = Pattern.compile("valve[s]?\\s(?<valves>.+)$");
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DaySixteen.class.getResource("input-day16.txt").toURI()));

	
		Map<String, Valve> valves = parse(lines);
		
		
		
		
		System.out.println("Part 1: " + part1(valves));

		System.out.println("Part 2: ");

	}
	
	private static long part1(Map<String, Valve> valves) {
		
		Valve valve = valves.get("AA");
		
		// keep track of 
		// 1. total relieved pressure
		// 2. pressure relief per minute
		// 3. open valves
		// 4. path taken??
		Action start = new GotoValve("AA", null);
		long best = recurse(valves, start, 30);
		return best;
	}
	
	public static long recurse(Map<String, Valve> valves, Action lastAction, int timeLeft) {
		if(timeLeft < 0) { // oeps
			return 0l;
		}
		if(timeLeft == 0) {
			return evaluate(valves, lastAction);
		}
		
		long best = 0l;
		
		Valve valve = valves.get(lastAction.name);
		
		for(String name : valve.neigbours) {
			Valve next = valves.get(name);
			
			GotoValve gotoValve = new GotoValve(name, lastAction);
			best = Math.max(best, recurse(valves,gotoValve, timeLeft-1));
			
			if (next.flowRate > 0 && hasNotBeenOpened(lastAction, name)) {
				OpenValve openValve = new OpenValve(name, gotoValve);
				best = Math.max(best, recurse(valves, openValve, timeLeft- 2));
			}
		}
		
		System.out.println(" -> best: " + best);
		return best;
		
	}
	
	private static boolean hasNotBeenOpened(Action last, String name) {
		Action current = last;
		do {
			if(current instanceof OpenValve && current.name.equals(name)) {
				return false;
			}
			current = current.previous;
		} while (current != null);
		return true;
	}

	public static long evaluate(Map<String, Valve> valves, Action path) {
		long total = 0l;
		long increment = 0l;
		for(Action a : toList(path)) {
			total += increment;
			if(a instanceof OpenValve) {
				increment += valves.get(a.name).flowRate;
			}
		}		
		return total;
		
	}
	
	public static List<Action> toList(Action last)  {
		Action current = last;
		ArrayList<Action> result = new ArrayList<>();
		do {
			result.add(0, current);
			current = current.previous;
		} while (current != null);
		
		return result;
	}
	
	public static abstract class Action {
		
		final Action previous;

		final String name;
		public Action(String name, Action previous) {
			this.name = name;
			this.previous = previous;
		}
		
	}
	
	public static class OpenValve extends Action{

		public OpenValve(String name, Action previous) {
			super(name, previous);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	public static class GotoValve extends Action{

		public GotoValve(String name, Action previous) {
			super(name, previous);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	public static class Path {
		final int pressureReliefPerMinute;
		final int totalPressureReliefed;
		final Path previous;
		final Valve valve;
		final boolean opened;
		
		public Path(int pressureReliefPerMinute, int totalPressureReliefed, Path previous, Valve valve,
				boolean opened) {
			this.pressureReliefPerMinute = pressureReliefPerMinute;
			this.totalPressureReliefed = totalPressureReliefed;
			this.previous = previous;
			this.valve = valve;
			this.opened = opened;
		}
		
		
	}
	
	public static Map<String, Valve> parse(List<String> lines) {
		Map<String, Valve> result = new HashMap<String, DaySixteen.Valve>();
		
		for(String line:lines) {
			System.out.println(line);
			Matcher matcher = name.matcher(line);
			if(!matcher.find()) {
				throw new IllegalArgumentException("Can't find valve name on line: " + line);
			}
			String name = matcher.group("name");

			matcher = rate.matcher(line);
			if(!matcher.find()) {
				throw new IllegalArgumentException("Can't find valve flow-rate on line: " + line);
			}
			int flowrate = Integer.parseInt(matcher.group("rate"));

			matcher = valves.matcher(line);
			if(!matcher.find()) {
				throw new IllegalArgumentException("Can't find valve neighours on line: " + line);
			}
			String[] neighours = matcher.group("valves").split(",");
			
			Valve v = new Valve(name, flowrate);
			for(String neighbour : neighours) {
				v.addNeighbour(neighbour.trim());
			}
			
			result.put(name, v);
			System.out.println(" -> " + v);
		}
		
		return result;
	}
	
	public static class Valve {
		private final String name;
		private final int flowRate;
		
		private boolean open = false;
		
		private List<String> neigbours = new ArrayList<>();
		
		public Valve(String name, int flowRate) {
			this.name=name;
			this.flowRate = flowRate;
		}
		
		public void addNeighbour(String other) {
			neigbours.add(other);
		}
		
		@Override
		public String toString() {
			return "Valve " + name + " has flow rate=" + flowRate + "; tunnels lead to valves " + neigbours.stream().collect(Collectors.joining(", "));
		}
	}
}
