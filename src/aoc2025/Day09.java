package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static aoc2025.Day09.State.*;

public class Day09 {
    public static void main(String[] args) throws IOException {
        boolean example = false;
        BufferedReader br;
        if (example) {
            br = new BufferedReader(new FileReader("09-ex2"));
        } else {
            br = new BufferedReader(new FileReader("09-in"));
        }

        // Read and parse the input
        String s;
        List<Tile> allTiles = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            String[] coords = s.split(",");
            allTiles.add(new Tile(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
        }
        long startTime = System.currentTimeMillis();
        int nTiles = allTiles.size();

        // Calculate all pairwise areas
        long maxArea = 0;
        for (int i = 0; i < nTiles; i++) {
            Tile t1 = allTiles.get(i);
            for (int j = i + 1; j < nTiles; j++) {
                Tile t2 = allTiles.get(j);
                long area = (Math.abs(t1.x - t2.x) + 1) * (Math.abs(t1.y - t2.y) + 1);
                maxArea = Math.max(maxArea, area);
            }
        }
        long p1Time = System.currentTimeMillis();
        System.out.printf("result1: %10d in %10d milliseconds\n", maxArea, p1Time - startTime);

        // Calculate green verticals
        List<VerticalLine> greenVerticals = new ArrayList<>();
        for (int i = 0; i < nTiles; i++) {
            Tile t1 = allTiles.get(i);
            Tile t2 = allTiles.get((i + 1) % nTiles);
            if (t1.x == t2.x) {
                greenVerticals.add(new VerticalLine(t1.x, Math.min(t1.y, t2.y), Math.max(t1.y, t2.y)));
            }
        }
        greenVerticals.sort(Comparator.comparingLong(v -> v.x));
        System.out.println("greenVerticals: " + greenVerticals);

        if (example) {
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 17; x++) {
                    System.out.print(switch (green(x, y, greenVerticals)) {
                        case NONE -> ".";
                        case INSIDE -> "#";
                        case TOP_BORDER -> "T";
                        case BOTTOM_BORDER -> "B";
                    });
                }
                System.out.println();
            }
        }

        maxArea = 0;
        for (int i = 0; i < nTiles; i++) {
            System.out.printf("i=%d\n", i);
            Tile t1 = allTiles.get(i);
            for (int j = i + 1; j < nTiles; j++) {
                System.out.printf("j=%d\n", j);
                Tile t2 = allTiles.get(j);
                if (allGreen(t1, t2, greenVerticals)) {
                    long area = (Math.abs(t1.x - t2.x) + 1) * (Math.abs(t1.y - t2.y) + 1);
                    System.out.printf("red/green %10d %s (%d) %s (%d)\n", area, t1, i, t2, j);
                    maxArea = Math.max(maxArea, area);
                }
            }
        }
        long p2Time = System.currentTimeMillis();
        System.out.printf("result2: %10d in %10d milliseconds\n", maxArea, p2Time - p1Time);
    }

    private static boolean allGreen(Tile t1, Tile t2, List<VerticalLine> greenVerticals) {
        long xMin = Math.min(t1.x, t2.x);
        long yMin = Math.min(t1.y, t2.y);
        long xMax = Math.max(t1.x, t2.x);
        long yMax = Math.max(t1.y, t2.y);
        for (long x = xMin; x <= xMax; x++) {
            for (long y = yMin; y <= yMax; y++) {
                if (green(x, y, greenVerticals) == NONE) {
                    return false;
                }
            }
        }
        return true;
    }

    enum State {
        NONE,
        TOP_BORDER,
        BOTTOM_BORDER,
        INSIDE
    }

    private static State green(long x, long y, List<VerticalLine> greenVerticals) {
        State state = NONE;
        for (VerticalLine vl : greenVerticals) {
            if (vl.x > x) {
                return state;
            }
            State newState = state;
            if (y == vl.yMin) {
                newState = switch (state) {
                    case NONE -> TOP_BORDER;
                    case TOP_BORDER -> NONE;
                    case BOTTOM_BORDER -> INSIDE;
                    case INSIDE -> BOTTOM_BORDER;
                };
            }
            if (y == vl.yMax) {
                newState = switch (state) {
                    case NONE -> BOTTOM_BORDER;
                    case TOP_BORDER -> INSIDE;
                    case BOTTOM_BORDER -> NONE;
                    case INSIDE -> TOP_BORDER;
                };
            }
            if (vl.yMin < y && vl.yMax > y) {
                newState = switch (state) {
                    case NONE -> INSIDE;
                    case TOP_BORDER, BOTTOM_BORDER -> throw new RuntimeException("cannot happen");
                    case INSIDE -> NONE;
                };
            }
            if (vl.x == x && state != newState) {
                if (newState == NONE) {
                    return state;
                }
                return newState;
            }
            state = newState;
        }
        return state;
    }

    record Tile(long x, long y) {
    }

    record VerticalLine(long x, long yMin, long yMax) {
    }

    /*
    X
    01234567890123
    .............. 0 Y
    .......#XXX#.. 1
    .......X...X.. 2
    ..#XXXX#...X.. 3
    ..X........X.. 4
    ..#XXXXXX#.X.. 5
    .........X.X.. 6
    .........#X#.. 7
    .............. 8

    X
    01234567890123456
    ................. 0
    .......TTTTT..... 1
    .......####TTTTT. 2
    ..TTTTT#####BB##. 3
    ..##########..BB. 4
    ..BBBBBBB###..... 5
    .........###..... 6
    .........BBB..... 7
    ................. 8


     */
}