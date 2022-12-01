package aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day9A {
    public static final String EXAMPLE = "day9-1.txt";
    public static final String REAL = "day9-2.txt";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(REAL));
        String input;
        List<int[]> rows = new ArrayList<>();
        while ((input = br.readLine()) != null) {
            int[] line = new int[input.length()];
            for (int i = 0; i < input.length(); i++) {
                line[i] = Integer.parseInt(input.substring(i, i + 1));
            }
            rows.add(line);
        }

        int numRows = rows.size();
        int numCols = rows.get(0).length;
        int totalRisk = 0;
        for (int r = 0; r < numRows; r++) {
            int[] row = rows.get(r);
            for (int c = 0; c < row.length; c++) {
                boolean isLow = true;
                int cur = row[c];
                if (r > 0) {
                    isLow = isLow && rows.get(r - 1)[c] > cur;
                }
                if (r < numRows - 1) {
                    isLow = isLow && rows.get(r + 1)[c] > cur;
                }
                if (c > 0) {
                    isLow = isLow && row[c - 1] > cur;
                }
                if (c < numCols - 1) {
                    isLow = isLow && row[c + 1] > cur;
                }
                if (isLow) {
                    totalRisk += cur + 1;
                }
            }
        }

        long end = System.currentTimeMillis();

        print(rows);
        System.out.println("Total risk: " + totalRisk);
        System.out.println("Time: " + (end - start) + "ms");
    }

    private static void print(List<int[]> rows) {
        int numCols = rows.get(0).length;
        for (int[] row : rows) {
            for (int j = 0; j < numCols; j++) {
                System.out.print(row[j]);
            }
            System.out.println();
        }
    }
}
