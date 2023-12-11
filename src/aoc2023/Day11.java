package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.binarySearch;

public class Day11 {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("11-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("11-sample.txt"));

        HashMap<Integer, List<Galaxy>> colToGalaxy = new HashMap<>();
        List<Galaxy> galaxies = new ArrayList<>();
        List<Integer> emptyRows = new ArrayList<>();
        List<Integer> emptyCols = new ArrayList<>();
        String s;
        int row = 0;
        int cols = -1;
        while ((s = br.readLine()) != null) {
            boolean oneInRow = false;
            cols = s.length();
            for (int col = 0; col < cols; col++) {
                char c = s.charAt(col);
                if (c == '#') {
                    oneInRow = true;
                    Galaxy g = new Galaxy(row, col);
                    galaxies.add(g);
                    colToGalaxy.computeIfAbsent(col, x -> new ArrayList<>()).add(g);
                }
            }
            if (!oneInRow) {
                emptyRows.add(row);
            }
            row++;
        }
        for (int col = 0; col < cols; col++) {
            if (colToGalaxy.get(col) == null) {
                emptyCols.add(col);
            }
        }
        long sum = calculate(galaxies, emptyRows, emptyCols, 2);
        System.out.println(sum);
        sum = calculate(galaxies, emptyRows, emptyCols, 1000000);
        System.out.println(sum);

        long endTime = System.currentTimeMillis();
        System.out.printf("\n%5.3f sec\n", (endTime - startTime) / 1000f);
    }

    private static long calculate(List<Galaxy> galaxies, List<Integer> emptyRows, List<Integer> emptyCols, long expandMultiplier) {
        expandMultiplier--;
        long sum = 0;
        for (int i = 0; i < galaxies.size(); i++) {
            Galaxy g1 = galaxies.get(i);
            for (int j = i + 1; j < galaxies.size(); j++) {
                Galaxy g2 = galaxies.get(j);
                long distance = Math.abs(g1.row - g2.row) + Math.abs(g1.col - g2.col);
                distance += Math.abs(binarySearch(emptyRows, g1.row) - binarySearch(emptyRows, g2.row)) * expandMultiplier;
                distance += Math.abs(binarySearch(emptyCols, g1.col) - binarySearch(emptyCols, g2.col)) * expandMultiplier;
//                System.out.printf("(%d,%d) = %d\n", i+1, j+1, distance);
                sum += distance;
            }
        }
        return sum;
    }

    record Galaxy(int row, int col) {
    }
}