package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DayNineteen {
	static enum Robot {
		ore, clay, obsidian, geode;
	}
	
	private static final int LIMIT = 24;
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayNineteen.class.getResource("input-day19.txt").toURI()));
		
		// inventory:		
		final Inventory startingInventory = new Inventory(0, 0, 0, 0);
		
		final Robots startingRobots = new Robots(0, 0, 1, 0);
		
		Blueprint blueprint = new Blueprint(
				/*clay*/ new Cost(0, 0, 2), 
				/*ore*/ new Cost(0, 0, 4), 
				/*obsidian*/ new Cost(14, 0, 3), 
				/*geode*/ new Cost(0, 7, 2)
				);
		
		Inventory inventory = startingInventory;
		Robots robots = startingRobots;
		
		System.out.println("Part 1: " + recurse(blueprint, inventory, robots, 0));
	}
	
	public static int recurse(final Blueprint blueprint, final Inventory inventory, final Robots robots, int minute) {
		if (minute == LIMIT) {
			return inventory.geode;
		}
		
		int best = 0;
		
		for(Robot robot : Robot.values()) {
			Cost cost = blueprint.costOf(robot);
			if (inventory.canAfford(cost)) {
				Inventory currentInventory = inventory;
				Robots currentRobots = robots;
				// 1. Build something, reduce resources
				currentInventory = currentInventory.substract(cost);
			
				// 2. Increase resources based on active robots
				currentInventory = currentInventory.increase(currentRobots);
			
				// 3. Increase active robots
				currentRobots = currentRobots.add(robot);
			
				int result = recurse(blueprint, currentInventory, currentRobots, minute + 1);
				best = Math.max(best, result);
			}			
		}
		
		// extra option: do nothing, just harvest
		int result = recurse(blueprint, inventory.increase(robots), robots, minute + 1);
		best = Math.max(best, result);

		System.out.println("Best: " + best);
		return best;
	}
	
	private static final class Blueprint {
		
		final Cost clayRobot;
		final Cost oreRobot;
		final Cost obsidianRobot;
		final Cost geodeRobot;
		
		public Blueprint(Cost clayRobot, Cost oreRobot, Cost obsidianRobot, Cost geodeRobot) {
			this.clayRobot = clayRobot;
			this.oreRobot = oreRobot;
			this.obsidianRobot = obsidianRobot;
			this.geodeRobot = geodeRobot;
		}
		
		public Cost costOf(Robot robot) {
			switch (robot) {
				case ore: return oreRobot;
				case clay: return clayRobot;
				case obsidian: return obsidianRobot;
				case geode: return geodeRobot;
				default: throw new IllegalArgumentException();
			}
		}
	}
	
	private static final class Inventory {
		final int clay;
		final int obsidian;
		final int ore;
		final int geode; //the target!!
		
		public Inventory(int clay, int obsidian, int ore, int geode) {
			this.clay = clay;
			this.obsidian = obsidian;
			this.ore = ore;
			this.geode = geode;
		}
		
		public boolean canAfford(Cost cost) {
			return clay >= cost.clay && obsidian >= cost.obsidian && ore >= cost.ore;
		}
		
		public Inventory substract(Cost cost) {
			return  new Inventory(
						this.clay - cost.clay,
						this.obsidian - cost.obsidian,
						this.ore - cost.ore,
						geode
					);
		}

		public Inventory increase(Robots activeRobots) {
			return  new Inventory(
						this.clay + activeRobots.clay,
						this.obsidian + activeRobots.obsidian,
						this.ore + activeRobots.ore,
						this.geode + activeRobots.geode
					);
		}
		
		
	}

	private static final class Robots {
		final int clay;
		final int obsidian;
		final int ore;
		final int geode; //the target!!
		
		public Robots(int clay, int obsidian, int ore, int geode) {
			this.clay = clay;
			this.obsidian = obsidian;
			this.ore = ore;
			this.geode = geode;
		}
		
		public Robots add(Robot type) {
			if(type == Robot.clay) {
				return new Robots(clay + 1 , obsidian, ore, geode);
			} else if (type == Robot.obsidian) {
				return new Robots(clay, obsidian + 1, ore, geode);				
			} else if (type == Robot.ore) {
				return new Robots(clay, obsidian, ore + 1, geode);				
			} else {
				return new Robots(clay, obsidian, ore, geode + 1);				
			}
		}
	}

	private static final class Cost {
		final int clay;
		final int obsidian;
		final int ore;
		
		public Cost(int clay, int obsidian, int ore) {
			this.clay = clay;
			this.obsidian = obsidian;
			this.ore = ore;
		}
		
	}
}

