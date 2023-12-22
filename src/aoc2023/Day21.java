package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day21 {
    static boolean trace = false;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
//        BufferedReader br = new BufferedReader(new FileReader("21-input.txt"));
        BufferedReader br = new BufferedReader(new FileReader("21-sample.txt"));

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
                p.possibleMovesOne(map, next);
            }
            steps++;
            cur = next;
        }

        System.out.println(cur.size());
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        steps = 0;
        cur.clear();
        cur.add(start);
        HashSet<Pos> even = new HashSet<>();
        HashSet<Pos> odd = new HashSet<>();
        long time = System.currentTimeMillis();
        while (steps < 26501365) {
            int rem = steps % 2;
            HashSet<Pos> block = rem == 0 ? even : odd;
            Set<Pos> next = new HashSet<>();
            for (Pos p : cur) {
                p.possibleMovesTwo(map, next, block);
            }
            block.addAll(next);
            steps++;
            cur = next;
            if (steps <= 10 || steps == 50 || steps == 100 || steps == 500 || steps % 1000 == 0) {
                long t = System.currentTimeMillis();
                System.out.printf("%10d steps: %12d possibilities %8d frontier size %5.3f sec\n", steps, block.size(), cur.size(), (t-time)/1000f);
                time = t;
            }
        }
        /*
        Wenn man in step s irgendwo ist, kann man immer 2 später wieder da sein. 1 später immer nicht. Das heißt, man
        muss nicht mehr dorthin gehen!

        10708469
        16733044
        16733044
         */

        System.out.println(cur.size());
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
        public void possibleMovesOne(char[][] map, Collection<Pos> out) {
            int nRows = map.length;
            int nCols = map[0].length;
            for (Direction dir : Direction.values()) {
                Pos p = dir.walkFrom(this);
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

    enum Part {ONE, TWO}
}