package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day8 {
    public static void main(String[] args) throws IOException {
        String filename = "day8.txt";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String s;
        int[][] trees = null;
        int row = 0;
        int n = 0;
        while ((s = br.readLine()) != null) {
            if (trees == null) {
                trees = new int[s.length()][s.length()];
                n = trees.length;
            }
            for (int i = 0; i < n; i++) {
                trees[row][i] = Integer.parseInt(s, i, i + 1, 10);
            }
            row++;
        }

        boolean[][] visible = new boolean[n][n];
        for (int x = 0; x < n; x++) {
            visible[x][0] = true;
            int col = 1;
            int max = trees[x][0];
            while (max < 9 && col < n) {
                if (max < trees[x][col]) {
                    visible[x][col] = true;
                }
                max = Math.max(max, trees[x][col]);
                col++;
            }

            visible[x][n - 1] = true;
            col = n - 2;
            max = trees[x][n - 1];
            while (max < 9 && col > 0) {
                if (max < trees[x][col]) {
                    visible[x][col] = true;
                }
                max = Math.max(max, trees[x][col]);
                col--;
            }

            visible[0][x] = true;
            int r = 1;
            max = trees[0][x];
            while (max < 9 && r < n) {
                if (max < trees[r][x]) {
                    visible[r][x] = true;
                }
                max = Math.max(max, trees[r][x]);
                r++;
            }

            visible[n - 1][x] = true;
            r = n - 2;
            max = trees[n - 1][x];
            while (max < 9 && r > 0) {
                if (max < trees[r][x]) {
                    visible[r][x] = true;
                }
                max = Math.max(max, trees[r][x]);
                r--;
            }

        }

        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
//                System.out.print(visible[i][j] ? "(" + trees[i][j] + ")" : " " + trees[i][j] + " ");
                if (visible[i][j]) {
                    count++;
                }
            }
            System.out.println();
        }
        System.out.println(count);

        int max = 0;
        for (row = 1; row < n - 1; row++) {
            for (int col = 1; col < n - 1; col++) {
                max = Math.max(max, calcScenicScore(trees, row, col));
            }
        }
        System.out.println(max);
    }

    private static int calcScenicScore(int[][] trees, int row, int col) {
        int up = calcDist(trees, row, col, -1, 0);
        int down = calcDist(trees, row, col, 1, 0);
        int left = calcDist(trees, row, col, 0, -1);
        int right = calcDist(trees, row, col, 0, 1);
//        System.out.println("left = " + left + "; right = " + right + "; up = " + up + "; down = " + down);
        return up * down * left * right;
    }

    private static int calcDist(int[][] trees, int row, int col, int rDiff, int cDiff) {
        int n = trees.length;
        int max = trees[row][col];
        int steps = 0;
        while (0 < row && row < n - 1 && 0 < col && col < n - 1) {
            steps++;
            row += rDiff;
            col += cDiff;
            if (trees[row][col] >= max) {
                break;
            }
        }
        return steps;
    }
}