package tuur;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DayTwo {

	private static enum RPS {
		ROCK(1), PAPER(2), SCICCORS(3);

		final int value;

		RPS(int value) {
			this.value = value;
		}

		public RPS losesFrom() {
			switch (this) {
			case ROCK: return PAPER;
			case PAPER: return SCICCORS;
			case SCICCORS: return ROCK;
			default: throw new RuntimeException("Impossible value");
			}
		}

		public RPS winsFrom() {
			switch (this) {
			case ROCK: return SCICCORS;
			case PAPER: return ROCK;
			case SCICCORS: return PAPER;
			default: throw new RuntimeException("Impossible value");
			}
		}
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		List<String> lines = Files.readAllLines(Paths.get(DayTwo.class.getResource("input-day02.txt").toURI()));

		System.out.println("Part 1: " + play(DayTwo::parse, DayTwo::parsePart1, lines));
		System.out.println("Part 2: " + play(DayTwo::parse, DayTwo::parsePart2, lines));
	}

	public static int play(Function<String, RPS> parsePlayerOne, BiFunction<String, RPS, RPS> parsePlayerTwo, List<String> lines) {
		int score = 0;
		for (String line : lines) {
			if (line.trim().isEmpty()) {
				continue;
			}
			String[] split = line.split("\\s");
			if (split.length != 2) {
				throw new RuntimeException("Bad line:" + line + " -> " + split);
			}
			RPS otherPlayer = parsePlayerOne.apply(split[0]);
			RPS me = parsePlayerTwo.apply(split[1], otherPlayer);

			if (me.equals(otherPlayer)) { //draw
				score += me.value + 3;
			} else if (me.winsFrom().equals(otherPlayer)) { //win
				score += me.value + 6;
			} else { //lose
				score += me.value;
			}
		}

		return score;
	}

	private static RPS parse(String letter) {
		switch (letter) {
		case "A": return RPS.ROCK;
		case "B": return RPS.PAPER;
		case "C": return RPS.SCICCORS;
		default: throw new IllegalArgumentException("Unknown value " + letter);
		}
	}

	private static RPS parsePart1(String letter, RPS other) {
		switch (letter) {
		case "X": return RPS.ROCK;
		case "Y": return RPS.PAPER;
		case "Z": return RPS.SCICCORS;
		default: throw new IllegalArgumentException("Unknown value " + letter);
		}
	}

	private static RPS parsePart2(String letter, RPS otherPlayer) {
		switch (letter) {
		case "X": return otherPlayer.winsFrom();
		case "Y": return otherPlayer;
		case "Z": return otherPlayer.losesFrom();
		default: throw new IllegalArgumentException("Unknown value " + letter);
		}
	}
}