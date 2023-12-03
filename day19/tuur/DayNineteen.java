package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DayNineteen {
	static enum Type {
		ore, clay, obsidian, geode;
		
		public static final Type[] NON_GEODE = new Type[] {ore, clay, obsidian};
	}
	
	private static final State START_24 = new State(0, 0, 0, 0, 1, 0, 0, 0, 24);
	private static final State START_32 = new State(0, 0, 0, 0, 1, 0, 0, 0, 32);
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayNineteen.class.getResource("input-day19.txt").toURI()));
				
		Blueprint blueprint = new Blueprint(1,
				new Recipe(Type.ore, 4, 0, 0), 
				new Recipe(Type.clay, 2, 0, 0), 
				new Recipe(Type.obsidian, 3, 14, 0), 
				new Recipe(Type.geode, 2, 0, 7)
				);
		Blueprint blueprint2 = new Blueprint(2,
				new Recipe(Type.ore, 2, 0, 0), 
				new Recipe(Type.clay, 3, 0, 0), 
				new Recipe(Type.obsidian, 3, 8, 0), 
				new Recipe(Type.geode, 3, 0, 12)
				);
		
		System.out.println("Test 1: " + solvePart1(Arrays.asList(blueprint, blueprint2)));
		System.out.println("Test 2: " + solvePart2(Arrays.asList(blueprint, blueprint2)));

		List<Blueprint> blueprints = parse(lines);
		System.out.println("Part 1: " + solvePart1(blueprints));
		System.out.println("Part 2: " + solvePart2(blueprints.subList(0, 3)));
	}
	
	private static final Pattern blueprint = Pattern.compile("Blueprint\\s(?<id>\\d+):");
	private static final Pattern oreRobot = Pattern.compile("ore\\srobot\\scosts\\s(?<ore>\\d+)");
	private static final Pattern clayRobot = Pattern.compile("clay\\srobot\\scosts\\s(?<ore>\\d+)");
	private static final Pattern obsidianRobot = Pattern.compile("obsidian\\srobot\\scosts\\s(?<ore>\\d+)\\sore\\sand\\s(?<clay>\\d+)");
	private static final Pattern geodeRobot = Pattern.compile("geode\\srobot\\scosts\\s(?<ore>\\d+)\\sore\\sand\\s(?<obsidian>\\d+)");
	
	public static List<Blueprint> parse(List<String> lines) {
		List<Blueprint> blueprints = new ArrayList<>();
		for(String line : lines) {
			Matcher blueprintIdMatcher = blueprint.matcher(line);
			if(!blueprintIdMatcher.find()) {
				throw new IllegalArgumentException("Can't match line: " + line + " for blueprint id");
			}
			int blueprintId = Integer.parseInt(blueprintIdMatcher.group("id"));

			Matcher oreRobotMatcher = oreRobot.matcher(line);
			if(!oreRobotMatcher.find()) {
				throw new IllegalArgumentException("Can't match line: " + line + " for ore robot costs");
			}
			Recipe oreRobotRecipe = new Recipe(Type.ore, Integer.parseInt(oreRobotMatcher.group("ore")), 0, 0);

			Matcher clayRobotMatcher = clayRobot.matcher(line);
			if(!clayRobotMatcher.find()) {
				throw new IllegalArgumentException("Can't match line: " + line + " for clay robot costs");
			}
			Recipe clayRobotRecipe = new Recipe(Type.clay, Integer.parseInt(clayRobotMatcher.group("ore")), 0, 0);

			Matcher obsidianRobotMatcher = obsidianRobot.matcher(line);
			if(!obsidianRobotMatcher.find()) {
				throw new IllegalArgumentException("Can't match line: " + line + " for obsidian robot costs");
			}
			Recipe obsidianRobotRecipe = new Recipe(Type.obsidian, Integer.parseInt(obsidianRobotMatcher.group("ore")), Integer.parseInt(obsidianRobotMatcher.group("clay")), 0);

			Matcher geodeRobotMatcher = geodeRobot.matcher(line);
			if(!geodeRobotMatcher.find()) {
				throw new IllegalArgumentException("Can't match line: " + line + " for geode robot costs");
			}
			Recipe geodeRobotRecipe = new Recipe(Type.geode, Integer.parseInt(geodeRobotMatcher.group("ore")), 0, Integer.parseInt(geodeRobotMatcher.group("obsidian")));
			
			blueprints.add(new Blueprint(blueprintId, oreRobotRecipe, clayRobotRecipe, obsidianRobotRecipe, geodeRobotRecipe));
		}
		return blueprints;
	}
	
	public static int solvePart1(List<Blueprint> blueprints) {
		int totalQualityLevel = 0;
		
		for(Blueprint blueprint: blueprints) {
			System.out.println("-----------------------------------------");
			System.out.println(blueprint);
			System.out.println("-----------------------------------------");
			
			AtomicInteger best = new AtomicInteger(0);
			recurse(blueprint, START_24, best, new HashSet<>());
			
			int qualityLevel = best.intValue() * blueprint.id();
			System.out.println("Quality level for Blueprint " + blueprint.id() + " * " + best.intValue() + " = " + qualityLevel);
			totalQualityLevel += qualityLevel;
		}
		
		return totalQualityLevel;
	}
	
	public static long solvePart2(List<Blueprint> blueprints) {
		long result = 1;
		
		for(Blueprint blueprint: blueprints) {
			System.out.println("-----------------------------------------");
			System.out.println(blueprint);
			System.out.println("-----------------------------------------");
			
			AtomicInteger best = new AtomicInteger(0);
			recurse(blueprint, START_32, best, new HashSet<>());
			
			System.out.println("Number of geodes for " + blueprint.id() + " = " + best.intValue());
			result *= best.longValue();
		}
		
		return result;
	}
	
	public static void recurse(final Blueprint blueprint, final State inventory, AtomicInteger best, HashSet<State> seen) {
		if(seen.contains(inventory)) {
			// already explored this situation
			return; 
		} else if (inventory.remainingTime == 1) {
			// no sense in building extra robots!
			State finalInventory = inventory.harvest();
			best.set(Math.max(finalInventory.geode, best.intValue()));
			
		} else if (inventory.canAfford(blueprint.costOf(Type.geode))) {
			// Always build a geode robot if possible
			Recipe recipe = blueprint.costOf(Type.geode);
			recurse(blueprint, inventory.harvestAndBuild(recipe), best, seen);
			
		} else {
			for (Type type : Type.NON_GEODE) {
				Recipe recipe = blueprint.costOf(type);
				
				// suppose we build the robot with the max cost of this type for the remaining time, do we have enough resources?
				boolean condition1 = ((inventory.remainingTime - 1) * blueprint.maxCost(type)) > inventory.numberOfResources(type) + inventory.numberOfRobots(type) * (inventory.remainingTime - 1);
				
				if (condition1 && inventory.numberOfRobots(type) < blueprint.maxCost(type) && inventory.canAfford(recipe)) {
					// 1. increase the amount of resources based on the existing robots
					// 2. Build the robot and reduce the resources
					recurse(blueprint, inventory.harvestAndBuild(recipe), best, seen);
				}			
			}
			
			// extra option: do nothing, just harvest
			recurse(blueprint, inventory.harvest(), best, seen);
		}
		
		seen.add(inventory);
	}
	
	private static final class State {
		// Inventory
		final int ore;
		final int clay;
		final int obsidian;
		final int geode; 
		// Robots
		final int oreRobots;
		final int clayRobots;
		final int obsidianRobots;
		final int geodeRobots;		
		// Keep track of the time
		final int remainingTime;
		
		final int hashCode;
		
		public State(int ore, int clay, int obsidian, int geode, 
				int oreRobots, int clayRobots, int obsidianRobots, int geodeRobots,
				int remainingTime) {
			this.ore = ore;
			this.clay = clay;
			this.obsidian = obsidian;
			this.geode = geode;
			// Robots
			this.oreRobots = oreRobots;
			this.clayRobots = clayRobots;
			this.obsidianRobots = obsidianRobots;
			this.geodeRobots = geodeRobots;
			// Time
			this.remainingTime = remainingTime;
			
			hashCode = calculateHashCode();
		}

		public int numberOfRobots(Type type) {
			switch (type) {
			case ore: return oreRobots;
			case clay: return clayRobots;
			case obsidian: return obsidianRobots;
			case geode: return geodeRobots;
			default:
				throw new IllegalArgumentException("Unknown type: " + type);
			}
		}
		public int numberOfResources(Type type) {
			switch (type) {
			case ore: return ore;
			case clay: return clay;
			case obsidian: return obsidian;
			case geode: return geode;
			default:
				throw new IllegalArgumentException("Unknown type: " + type);
			}
		}

		public boolean canAfford(Recipe recipe) {
			return this.ore >= recipe.oreCost() && this.clay >= recipe.clayCost() && this.obsidian >= recipe.obsidianCost();
		}
		
		public State harvestAndBuild(Recipe robot) {
			return new State(
					this.ore - robot.oreCost() + this.oreRobots,
					this.clay - robot.clayCost() + this.clayRobots,
					this.obsidian - robot.obsidianCost() + this.obsidianRobots,
					this.geode + this.geodeRobots,
					this.oreRobots + (Type.ore.equals(robot.type) ? 1 : 0),
					this.clayRobots + (Type.clay.equals(robot.type) ? 1 : 0),
					this.obsidianRobots + (Type.obsidian.equals(robot.type) ? 1 : 0),
					this.geodeRobots + (Type.geode.equals(robot.type) ? 1 : 0),
					this.remainingTime - 1);
		}
		
		public State harvest() {
			return new State(
					this.ore + this.oreRobots,
					this.clay + this.clayRobots,
					this.obsidian + this.obsidianRobots,
					this.geode + this.geodeRobots,
					this.oreRobots,
					this.clayRobots,
					this.obsidianRobots,
					this.geodeRobots,
					remainingTime - 1);
		}



		public int calculateHashCode() {
			int result = 31 + clay;
			result = 31 * result + clayRobots;
			result = 31 * result + geode;
			result = 31 * result + geodeRobots;
			result = 31 * result + obsidian;
			result = 31 * result + obsidianRobots;
			result = 31 * result + ore;
			result = 31 * result + oreRobots;
			result = 31 * result + remainingTime;
			return result;
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
			State other = (State) obj;
			if (remainingTime != other.remainingTime)
				return false;
			if (geode != other.geode)
				return false;
			if (clay != other.clay)
				return false;
			if (clayRobots != other.clayRobots)
				return false;
			if (geodeRobots != other.geodeRobots)
				return false;
			if (obsidian != other.obsidian)
				return false;
			if (obsidianRobots != other.obsidianRobots)
				return false;
			if (ore != other.ore)
				return false;
			if (oreRobots != other.oreRobots)
				return false;
			return true;
		}



		@Override
		public String toString() {
			return "[" + ore + ", " + clay + ", " + obsidian + ", " + geode
					+ "| " + oreRobots + ", " + clayRobots + ", " + obsidianRobots + ", " + geodeRobots + "]";
		}
		
	}
	
	private static final class Blueprint {
		
		final int id;
		
		final Recipe oreRobot;
		final Recipe clayRobot;
		final Recipe obsidianRobot;
		final Recipe geodeRobot;
		
		final int maxOreCost;
		final int maxClayCost;
		final int maxObsidianCost;
				
		public Blueprint(int id, Recipe oreRobot, Recipe clayRobot, Recipe obsidianRobot, Recipe geodeRobot) {
			this.id = id;
			this.oreRobot = oreRobot;
			this.clayRobot = clayRobot;
			this.obsidianRobot = obsidianRobot;
			this.geodeRobot = geodeRobot;
			
			maxOreCost = Math.max(Math.max(oreRobot.oreCost,clayRobot.oreCost), Math.max(obsidianRobot.oreCost, geodeRobot.oreCost));
			maxClayCost = Math.max(Math.max(oreRobot.clayCost,clayRobot.clayCost), Math.max(obsidianRobot.clayCost, geodeRobot.clayCost));
			maxObsidianCost = Math.max(Math.max(oreRobot.obsidianCost,clayRobot.obsidianCost), Math.max(obsidianRobot.obsidianCost, geodeRobot.obsidianCost));
		}
		
		public int id() {
			return id;
		}

		public int maxCost(Type type) {
			switch (type) {
			case ore: return maxOreCost;
			case clay: return maxClayCost;
			case obsidian: return maxObsidianCost;
			default:
				throw new IllegalArgumentException("No max cost for type: " + type);
			}
		}
		
		public Recipe costOf(Type type) {
			switch (type) {
			case ore: return oreRobot;
			case clay: return clayRobot;
			case obsidian: return obsidianRobot;
			case geode: return geodeRobot;
			default:
				throw new IllegalArgumentException("Unknown type: " + type);
			}
		}
		
		public String toString() {
			return "Blueprint " + id + ": "
					+ "Each ore robot costs " + oreRobot.oreCost() + " ore. "
					+ "Each clay robot costs " + clayRobot.oreCost() + "  ore. "
					+ "Each obsidian robot costs " + obsidianRobot.oreCost() + " ore and " + obsidianRobot.clayCost() + " clay. "
					+ "Each geode robot costs " + geodeRobot.oreCost() + " ore and " + geodeRobot.obsidianCost() + " obsidian.";
		}
	}

	private static final class Recipe {
		final Type type;
		
		final int oreCost;
		final int clayCost;
		final int obsidianCost;
 		
		public Recipe(Type type, int ore, int clay, int obsidian) {
			this.type = type;
			this.oreCost = ore;
			this.clayCost = clay;
			this.obsidianCost = obsidian;
		}
		
		public int oreCost() {
			return oreCost;
		}
		public int clayCost() {
			return clayCost;
		}
		public int obsidianCost() {
			return obsidianCost;
		}

		@Override
		public String toString() {
			return "Recipe [type=" + type + ", oreCost=" + oreCost() + ", clayCost=" + clayCost() + ", obsidianCost="
					+ obsidianCost() + "]";
		}
		
	}
}

