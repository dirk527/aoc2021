package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static aoc2023.Day16.Direction.*;

public class Day16 {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("16-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("16-sample.txt"));

        String s;
        List<String> cave = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            cave.add(s);
        }

        int nRows = cave.size();
        int nCols = cave.get(0).length();
        long most = 0;
        boolean part1 = true;
        long p1Time = 0;
        for (int row = 0; row < nRows; row++) {
            Beam initial = new Beam(row, -1, EAST);
            most = Math.max(most, countEnergised(cave, initial));
            if (part1) {
                part1 = false;
                System.out.println(most);
                p1Time = System.currentTimeMillis();
                System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);
            }
            initial = new Beam(row, nCols, WEST);
            most = Math.max(most, countEnergised(cave, initial));
        }
        for (int col = 0; col < nCols; col++) {
            Beam initial = new Beam(-1, col, SOUTH);
            most = Math.max(most, countEnergised(cave, initial));
            initial = new Beam(nRows, col, NORTH);
            most = Math.max(most, countEnergised(cave, initial));
        }

        System.out.println(most);
        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (endTime - p1Time) / 1000f);
    }

    private static long countEnergised(List<String> cave, Beam initial) {
        int nRows = cave.size();
        int nCols = cave.get(0).length();
        long[][] count = new long[nRows][nCols];
        LinkedList<Beam> todo = new LinkedList<>();
        HashSet<Beam> done = new HashSet<>();
        todo.add(initial);
        while (!todo.isEmpty()) {
            Beam beam = todo.removeFirst();
            if (done.contains(beam)) {
                continue;
            }
            if (inBounds(beam, nRows, nCols)) {
                count[beam.row][beam.col]++;
            }

            int nr = beam.row + beam.dir.rOffset;
            int nc = beam.col + beam.dir.colOffset;

            if (inBounds(new Beam(nr, nc, EAST), nRows, nCols)) {
                var element = cave.get(nr).charAt(nc);
                switch (element) {
                    case '.' -> todo.addLast(new Beam(nr, nc, beam.dir));
                    case '-' -> {
                        if (beam.dir == EAST || beam.dir == WEST) {
                            todo.addLast(new Beam(nr, nc, beam.dir));
                        } else {
                            todo.addLast(new Beam(nr, nc, EAST));
                            todo.addLast(new Beam(nr, nc, WEST));
                        }
                    }
                    case '|' -> {
                        if (beam.dir == NORTH || beam.dir == SOUTH) {
                            todo.addLast(new Beam(nr, nc, beam.dir));
                        } else {
                            todo.addLast(new Beam(nr, nc, NORTH));
                            todo.addLast(new Beam(nr, nc, SOUTH));
                        }
                    }
                    case '/' -> {
                        Direction nDir = switch (beam.dir) {
                            case NORTH -> EAST;
                            case EAST -> NORTH;
                            case WEST -> SOUTH;
                            case SOUTH -> WEST;
                        };
                        todo.addLast(new Beam(nr, nc, nDir));
                    }
                    case '\\' -> {
                        Direction nDir = switch (beam.dir) {
                            case NORTH -> WEST;
                            case WEST -> NORTH;
                            case EAST -> SOUTH;
                            case SOUTH -> EAST;
                        };
                        todo.addLast(new Beam(nr, nc, nDir));
                    }
                    default -> throw new IllegalStateException("unknown op " + element);
                }
            }
            done.add(beam);
        }

        long sum = 0;
        for (int r = 0; r < nRows; r++) {
            for (int c = 0; c < nCols; c++) {
                if (count[r][c] > 0) {
                    sum++;
                }
            }
        }
        return sum;
    }

    private static boolean inBounds(Beam beam, int nRows, int nCols) {
        return beam.col >= 0 && beam.col < nCols && beam.row >= 0 && beam.row < nRows;
    }

    enum Direction {
        NORTH(-1, 0), EAST(0, 1), SOUTH(1, 0), WEST(0, -1);

        final int rOffset;
        final int colOffset;

        Direction(int rOffset, int colOffset) {
            this.rOffset = rOffset;
            this.colOffset = colOffset;
        }
    }

    record Beam(int row, int col, Direction dir) {
    }
}