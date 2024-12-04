package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day04 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("04-in"));
        String s;
        List<String> grid = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            grid.add(s);
        }
        int cols = grid.size();
        int rows = grid.getFirst().length();

        String xmas = "XMAS";
        int p1 = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid.get(row).charAt(col) == xmas.charAt(0)) {
                    for (Direction direction : Direction.values()) {
                        if (match(grid, row, col, direction, xmas)) {
                            p1++;
                        }
                    }
                }
            }
        }

        int p2 = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid.get(row).charAt(col) == 'A') {
                    boolean one = twoDirMatch(grid, row, col, Direction.UL, Direction.DR, "AS", "AM");
                    boolean two = twoDirMatch(grid, row, col, Direction.UR, Direction.DL, "AS", "AM");
                    if (one && two) {
                        p2++;
                    }
                }
            }
        }

        System.out.println(p1);
        System.out.println(p2);
    }

    private static boolean twoDirMatch(List<String> grid, int row, int col, Direction d1, Direction d2, String s1, String s2) {
        return (match(grid, row, col, d1, s1) && match(grid, row, col, d2, s2)) ||
                (match(grid, row, col, d1, s2) && match(grid, row, col, d2, s1));
    }

    private static boolean match(List<String> grid, int row, int col, Direction direction, String xmas) {
        int pos = 1;
        while (pos < xmas.length()) {
            row += direction.r;
            col += direction.c;
            if (row < 0 || row >= grid.size()) {
                return false;
            }
            if (col < 0 || col >= grid.get(row).length()) {
                return false;
            }
            if (grid.get(row).charAt(col) != xmas.charAt(pos)) {
                return false;
            }
            pos++;
        }
        return true;
    }

    enum Direction {
        UL(-1, -1),
        U(0, -1),
        UR(1, -1),
        R(1, 0),
        DR(1, 1),
        D(0, 1),
        DL(-1, 1),
        L(-1, 0);

        int c, r;

        Direction(int c, int r) {
            this.c = c;
            this.r = r;
        }
    }
}