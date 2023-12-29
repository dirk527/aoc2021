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

        // for debugging: print out the real simulated results up to n=6
        // simulateFor(map, 65 + 131 * 6, List.of(start), true);

        // 26501365 steps is 202300 * 131 + 65. This is not random - you can reach every side of the starting square in
        // 65 steps, and every 131 further moves out a whole more square.
        // figure out whole squares even/odd
        SimulationResult wholeSquare = simulateFor(map, 131, List.of(start), false);
        SimulationResult corners = simulateFor(map, 65, List.of(start), false);

        // for debugging: print out the calculated results up to n=6
        //for (int n = 0; n < 7; n += 2) {
        //    long result = (n + 1) * (n + 1) * wholeSquare.odd + n * n * wholeSquare.even - (n + 1) * (wholeSquare.odd - corners.odd) + n * (wholeSquare.even - corners.even);
        //    System.out.printf("n=%2d -> %d\n", n, result);
        //}

        long n = 202300;
        long result = (n + 1) * (n + 1) * wholeSquare.odd + n * n * wholeSquare.even - (n + 1) * (wholeSquare.odd - corners.odd) + n * (wholeSquare.even - corners.even);
        System.out.println(result);

        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (endTime - p1Time) / 1000f);
    }

    static SimulationResult simulateFor(char[][] map, long maxSteps, List<Pos> start, boolean infinite) {
        long steps = 0;
        Set<Pos> cur = new HashSet<>(start);
        HashSet<Pos> even = new HashSet<>();
        HashSet<Pos> odd = new HashSet<>();
        while (steps < maxSteps) {
            long rem = steps % 2;
            HashSet<Pos> block = rem == 0 ? odd : even;
            Set<Pos> next = new HashSet<>();
            for (Pos p : cur) {
                if (infinite) {
                    p.possibleMovesTwo(map, next, block);
                } else {
                    p.possibleMoves(map, next, block);
                }
            }
            block.addAll(next);
            steps++;
            cur = next;
            if ((steps - 65) % 131 == 0) {
                System.out.printf("n=%2d %7d steps: even %d odd %d\n", (steps - 65) / 131, steps, even.size(), odd.size());
            }
        }

        return new SimulationResult(even.size(), odd.size());
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

        public void possibleMovesTwo(char[][] map, Collection<Pos> out, Collection<Pos> block) {
            int nRows = map.length;
            int nCols = map[0].length;
            for (Direction dir : Direction.values()) {
                Pos p = dir.walkFrom(this);
                if (block.contains(p)) {
                    continue;
                }
                int row = (int) (p.row % nRows);
                if (row < 0) {
                    row += nRows;
                }
                int col = (int) (p.col % nCols);
                if (col < 0) {
                    col += nCols;
                }
                if (map[row][col] == '.') {
                    out.add(p);
                }
            }
        }
    }

    record SimulationResult(long even, long odd){}
}