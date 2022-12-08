package tuur;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DayEight {

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(DayEight.class.getResource("input-day08.txt").toURI()));

		Tree[][] forest = parseForest(lines);

		long count = 0l;
		long bestScenicScore = 0l;

		for (int y = 0; y < forest.length; y++) {
			Tree[] row = forest[y];
			for (int x = 0; x < row.length; x++) {
				Tree tree = calculateTree(forest, y, x);
				
				if (tree.isVisible) {
					count++;
				}
				bestScenicScore = Math.max(bestScenicScore, tree.scenicScore);
			}
		}

		System.out.println("Part 1 : " + count);
		System.out.println("Part 2 : " + bestScenicScore);
	}

	private static Tree[][] parseForest(List<String> lines) {
		Tree[][] forest = new Tree[lines.size()][];
		for (int i = 0; i < lines.size(); i++) {
			char[] chars = lines.get(i).toCharArray();
			forest[i] = new Tree[chars.length];
			for (int j = 0; j < chars.length; j++) {
				forest[i][j] = new Tree(Integer.parseInt(Character.toString(chars[j])));
			}
		}
		return forest;
	}

	private static Tree calculateTree(Tree[][] forest, int y, int x) {
		final Tree[] row = forest[y];
		final Tree tree = row[x];

		boolean visibleLeft = true;
		boolean visibleRight = true;
		boolean visibleTop = true;
		boolean visibleBottom = true;

		int scenicLeft = 0;
		int scenicRight = 0;
		int scenicTop = 0;
		int scenicBottom = 0;

		for (int i = x - 1; i >= 0; i--) {
			Tree other = row[i];
			scenicLeft++;
			if (other.height >= tree.height) {
				visibleLeft = false;
				break;
			}
		}

		for (int i = x + 1; i < row.length; i++) {
			Tree other = row[i];
			scenicRight++;
			if (other.height >= tree.height) {
				visibleRight = false;
				break;
			}
		}

		for (int j = y - 1; j >= 0; j--) {
			Tree other = forest[j][x];
			scenicTop++;
			if (other.height >= tree.height) {
				visibleTop = false;
				break;
			}
		}

		for (int j = y + 1; j < forest.length; j++) {
			Tree other = forest[j][x];
			scenicBottom++;
			if (other.height >= tree.height) {
				visibleBottom = false;
				break;
			}
		}

		tree.setScenicScore(scenicLeft * scenicRight * scenicTop * scenicBottom);
		tree.setVisible(visibleLeft || visibleRight || visibleTop || visibleBottom);

		return tree;
	}

	public static class Tree {
		private final int height;

		public Tree(int height) {
			this.height = height;
		}

		public transient Boolean isVisible;

		public void setVisible(boolean visible) {
			isVisible = visible;
		}

		public Long scenicScore;

		public void setScenicScore(long score) {
			scenicScore = score;
		}
	}
}
