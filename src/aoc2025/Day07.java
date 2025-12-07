package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day07 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("07-in"));
        String s;
        List<String> grid = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            grid.add(s);
        }

        // Part 1: move down, count the splits; remember to unify beams at the same column
        int result1 = 0;
        int line = 1;
        Set<Integer> beams = new TreeSet<>();
        beams.add(grid.getFirst().indexOf('S'));
        while (line < grid.size()) {
            Set<Integer> nextLineBeams = new TreeSet<>();
            for (int beam : beams) {
                if (grid.get(line).charAt(beam) == '^') {
                    nextLineBeams.add(beam - 1);
                    nextLineBeams.add(beam + 1);
                    result1++;
                } else {
                    nextLineBeams.add(beam);
                }
            }
            beams = nextLineBeams;
            line++;
        }
        System.out.println(result1);

        // Part 2: dynamic programming, remember result for each splitter
        System.out.println(quantumCalculate(grid, new HashMap<Splitter, Long>(), 1, grid.getFirst().indexOf('S')));
    }

    private static long quantumCalculate(List<String> grid, Map<Splitter, Long> cache, int row, int col) {
        if (row == grid.size()) {
            return 1;
        }
        if (grid.get(row).charAt(col) == '^') {
            Splitter split = new Splitter(row, col);
            Long cached = cache.get(split);
            if (cached == null) {
                cached = quantumCalculate(grid, cache, row + 1, col - 1) +
                         quantumCalculate(grid, cache, row + 1, col + 1);
                cache.put(split, cached);
            }
            return cached;
        } else {
            return quantumCalculate(grid, cache, row + 1, col);
        }
    }

    record Splitter(int row, int col) {}
}