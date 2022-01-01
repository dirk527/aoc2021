import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day9B {
    public static final String EXAMPLE = "day9-1.txt";
    public static final String REAL = "day9-2.txt";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(REAL));
        String input;
        List<int[]> rows = new ArrayList<>();
        List<int[]> basins = new ArrayList<>();
        while ((input = br.readLine()) != null) {
            int[] line = new int[input.length()];
            for (int i = 0; i < input.length(); i++) {
                line[i] = Integer.parseInt(input.substring(i, i + 1));
            }
            rows.add(line);
            basins.add(new int[input.length()]);
        }

        int numRows = rows.size();
        int numCols = rows.get(0).length;
        int basinCount = 0;
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
                    basinCount++;
                    mark(rows, basins, r, c, basinCount);
                }
            }
        }
        int[] basinSize = new int[basinCount + 1];
        for (int r = 0; r < numRows; r++) {
            int[] row = rows.get(r);
            for (int c = 0; c < row.length; c++) {
                basinSize[basins.get(r)[c]]++;
            }
        }
        List<Integer> counts = new ArrayList<>();
        for (int i = 1; i < basinSize.length; i++) {
            counts.add(basinSize[i]);
        }
        counts.sort((o1, o2) -> o2 - o1);
        System.out.println(counts);
        System.out.println();
        long result = (long) counts.get(0) * counts.get(1) * counts.get(2);

        long end = System.currentTimeMillis();

        print(rows);
        System.out.println();
        print(basins);
        System.out.println();
        System.out.println("result: " + result);
        System.out.println("\nTime: " + (end - start) + "ms");
    }

    private static void mark(List<int[]> rows, List<int[]> basins, int row, int col, int basinId) {
        basins.get(row)[col] = basinId;
        int cur = rows.get(row)[col];
        if (row > 0) {
            if (rows.get(row - 1)[col] != 9 && rows.get(row - 1)[col] > cur) {
                mark(rows, basins, row - 1, col, basinId);
            }
        }
        if (row < rows.size() - 1) {
            if (rows.get(row + 1)[col] != 9 && rows.get(row + 1)[col] > cur) {
                mark(rows, basins, row + 1, col, basinId);
            }
        }
        if (col > 0) {
            if (rows.get(row)[col - 1] != 9 && rows.get(row)[col - 1] > cur) {
                mark(rows, basins, row, col - 1, basinId);
            }
        }
        if (col < rows.get(row).length - 1) {
            if (rows.get(row)[col + 1] != 9 && rows.get(row)[col + 1] > cur) {
                mark(rows, basins, row, col + 1, basinId);
            }
        }
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
