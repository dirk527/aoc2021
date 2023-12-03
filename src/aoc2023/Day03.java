package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day03 {

    private static final int INVALID = -1;
    private static int p1;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("03-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("03-sample.txt"));

        List<String> grid = new ArrayList<>();
        String s;
        while ((s = br.readLine()) != null) {
            grid.add(s);
        }

        p1 = 0;
        int rows = grid.size();
        int cols = grid.get(0).length();
        Map<Symbol, List<Integer>> gears = new HashMap<>();
        for (int row = 0; row < rows; row++) {
            String r = grid.get(row);
            int numStart = INVALID;
            for (int col = 0; col < cols; col++) {
                char c = r.charAt(col);
                if (Character.isDigit(c)) {
                    if (numStart == INVALID) {
                        numStart = col;
                    }
                } else {
                    if (numStart != INVALID) {
                        isItValid(grid, row, numStart, col - 1, r.substring(numStart, col), gears);
                    }
                    numStart = INVALID;
                }
            }
            if (numStart != INVALID) {
                isItValid(grid, row, numStart, r.length() - 1, r.substring(numStart), gears);
            }
        }
        System.out.println(p1);

        int p2 = 0;
        for (var x : gears.entrySet()) {
            if (x.getValue().size() == 2) {
                p2 += x.getValue().get(0) * x.getValue().get(1);
            }
        }
        System.out.println(p2);
    }

    private static void isItValid(List<String> grid, int row, int start, int end, String r, Map<Symbol, List<Integer>> gears) {
        List<Symbol> adj = adjacentSymbols(grid, row, start, end);
        if (!adj.isEmpty()) {
            int cur = Integer.parseInt(r);
            p1 += cur;
            adj.forEach(g -> {
                if (g.c() == '*') {
                    gears.computeIfAbsent(g, x -> new ArrayList<>()).add(cur);
                }
            });
        }
    }

    private static List<Symbol> adjacentSymbols(List<String> grid, int row, int start, int end) {
        List<Symbol> ret = new ArrayList<>();
        checkForSymbol(grid, row, start - 1, ret);
        checkForSymbol(grid, row, end + 1, ret);
        for (int col = start - 1; col <= end + 1; col++) {
            checkForSymbol(grid, row - 1, col, ret);
            checkForSymbol(grid, row + 1, col, ret);
        }
        return ret;
    }

    private static void checkForSymbol(List<String> grid, int row, int col, List<Symbol> result) {
        if (col >= 0 && col < grid.get(0).length() && row >= 0 && row < grid.size()) {
            char c = grid.get(row).charAt(col);
            if (!Character.isDigit(c) && c != '.') {
                result.add(new Symbol(row, col, c));
            }
        }
    }

    record Symbol(int row, int col, char c) {
    }
}