package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day21 {
    static boolean trace = false;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("21-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("21-sample.txt"));

        String s;
        List<String> strings = new ArrayList<>();
        int startRow = -1;
        int startCol = -1;
        while ((s = br.readLine()) != null) {
            int idx = s.indexOf('S');
            if (idx != -1) {
                startRow = strings.size();
                startCol = idx;
            }
            strings.add(s);
        }
        int nRows = strings.size();
        int nCols = strings.getFirst().length();
        char[][] map = new char[nRows][nCols];
        for (int i = 0; i < nRows; i++) {
            map[i] = strings.get(i).toCharArray();
        }
        map[startRow][startCol] = '.';

        Pos start = new Pos(startRow, startCol);
        int steps = 0;
        Set<Pos> cur = new HashSet<>();
        cur.add(start);
        while (steps < 64) {
            Set<Pos> next = new HashSet<>();
            for (Pos p : cur) {
                p.possibleMoves(map, next, null);
            }
            steps++;
            cur = next;
        }

        System.out.println(cur.size());
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        // 26501365 steps is 202300 * 131 + 65. This is not random - you can reach every side of the starting square in
        // 65 steps, and 131 further out for another square.
        // figure out whole squares even/odd
        steps = 0;
        cur.clear();
        cur.add(start);
        HashSet<Pos> even = new HashSet<>();
        HashSet<Pos> odd = new HashSet<>();
        while (steps < 131) {
            int rem = steps % 2;
            HashSet<Pos> block = rem == 0 ? even : odd;
            Set<Pos> next = new HashSet<>();
            for (Pos p : cur) {
                p.possibleMoves(map, next, block);
            }
            block.addAll(next);
            steps++;
            cur = next;
            System.out.printf("%4d steps: %d even %d odd\n", steps, even.size(), odd.size());
        }
        long evenWhole = even.size();
        long oddWhole = odd.size();

        // figure out how many even / odd can be reached from the corners
        even.clear();
        odd.clear();
        cur.clear();
        cur.add(new Pos(0, 0));
        cur.add(new Pos(0, nCols - 1));
        cur.add(new Pos(nRows - 1, 0));
        cur.add(new Pos(nRows - 1, nCols - 1));
        steps = 0;
        while (steps < 65) {
            int rem = steps % 2;
            HashSet<Pos> block = rem == 0 ? even : odd;
            Set<Pos> next = new HashSet<>();
            for (Pos p : cur) {
                p.possibleMoves(map, next, block);
            }
            block.addAll(next);
            steps++;
            cur = next;
            System.out.printf("%4d corner steps: %d even %d odd\n", steps, even.size(), odd.size());
        }
        long evenCorners = even.size();
        long oddCorners = odd.size();

        long n = 202300;
        long result = (n + 1) * (n + 1) * oddWhole + n * n * evenWhole - (n + 1) * oddCorners + n * evenCorners;

        System.out.println(result);
        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (endTime - p1Time) / 1000f);
    }

    enum Direction {
        NORTH(-1, 0), EAST(0, 1), SOUTH(1, 0), WEST(0, -1);

        final long rowOffset;
        final long colOffset;

        Direction(int rowOffset, int colOffset) {
            this.rowOffset = rowOffset;
            this.colOffset = colOffset;
        }

        public Pos walkFrom(Pos pos) {
            return new Pos(pos.row + rowOffset, pos.col + colOffset);
        }
    }

    record Pos(long row, long col) {
        public void possibleMoves(char[][] map, Collection<Pos> out, HashSet<Pos> block) {
            int nRows = map.length;
            int nCols = map[0].length;
            for (Direction dir : Direction.values()) {
                Pos p = dir.walkFrom(this);
                if (block != null && block.contains(p)) {
                    continue;
                }
                if (p.col < nCols && p.row < nRows && p.col >= 0 && p.row >= 0 && map[(int) p.row][(int) p.col] == '.') {
                    out.add(p);
                }
            }
        }
    }
}