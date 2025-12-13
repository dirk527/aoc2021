package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static aoc2025.Day09.State.*;

public class Day09 {
    private static List<VerticalLine> greenVerticals;
    private static HashMap<Long, List<Range>> ranges = new HashMap<>();
    private static long maxX;

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
        maxX = 0L;
        greenVerticals = new ArrayList<>();
        for (int i = 0; i < nTiles; i++) {
            Tile t1 = allTiles.get(i);
            maxX = Math.max(t1.x, maxX);
            Tile t2 = allTiles.get((i + 1) % nTiles);
            if (t1.x == t2.x) {
                greenVerticals.add(new VerticalLine(t1.x, Math.min(t1.y, t2.y), Math.max(t1.y, t2.y)));
            }
        }
        greenVerticals.sort(Comparator.comparingLong(v -> v.x));

        if (example) {
            debugGrid(0, 9);
        }

        // same as part1, but with the allGreen() condition
        maxArea = 0;
        for (int i = 0; i < nTiles; i++) {
            Tile t1 = allTiles.get(i);
            for (int j = i + 1; j < nTiles; j++) {
                Tile t2 = allTiles.get(j);
                if (allGreen(t1, t2)) {
                    long area = (Math.abs(t1.x - t2.x) + 1) * (Math.abs(t1.y - t2.y) + 1);
//                    System.out.printf("red/green %10d %s (%d) %s (%d)\n", area, t1, i, t2, j);
                    maxArea = Math.max(maxArea, area);
                }
            }
        }
        long p2Time = System.currentTimeMillis();
        System.out.printf("result2: %10d in %10d milliseconds\n", maxArea, p2Time - p1Time);
        System.out.printf("ranges %d %d\n", ranges.size(), ranges.values().stream().mapToLong(List::size).sum());
    }

    private static void debugGrid(long yMin, long yMax) {
        for (long y = yMin; y < yMax; y++) {
            for (Range r : ranges.computeIfAbsent(y, Day09::calculateRanges)) {
                for (long i = r.xMin; i <= r.xMax; i++) {
                    System.out.print(switch (r.state) {
                        case NONE -> ".";
                        case INSIDE -> "#";
                        case TOP_BORDER -> "T";
                        case BOTTOM_BORDER -> "B";
                    });
                }
            }
            System.out.println();
        }
    }

    private static boolean allGreen(Tile t1, Tile t2) {
        long xMin = Math.min(t1.x, t2.x);
        long yMin = Math.min(t1.y, t2.y);
        long xMax = Math.max(t1.x, t2.x);
        long yMax = Math.max(t1.y, t2.y);
        for (long y = yMin; y <= yMax; y++) {
            List<Range> rs = ranges.computeIfAbsent(y, Day09::calculateRanges);
            for (Range r : rs) {
                if (r.state == NONE && ((xMin >= r.xMin && xMin <= r.xMax) || (xMax >= r.xMin && xMax <= r.xMax))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static List<Range> calculateRanges(long y) {
        List<Range> result = new ArrayList<>();
        State state = NONE;
        long xStart = 0;
        for (VerticalLine vl : greenVerticals) {
            State newState = null;
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
            if (newState != null) {
                long xEnd;
                if (newState == NONE) {
                    xEnd = vl.x;
                } else {
                    xEnd = vl.x - 1;
                }
                result.add(new Range(xStart, xEnd, state));
                state = newState;
                xStart = xEnd + 1;
            }
        }
        result.add(new Range(xStart, maxX + 5, state));
//        System.out.printf("%d %s\n", y, result);
        return result;
    }

    enum State {NONE, TOP_BORDER, BOTTOM_BORDER, INSIDE}

    record Tile(long x, long y) {}

    record VerticalLine(long x, long yMin, long yMax) {}

    record Range(long xMin, long xMax, State state) {}
}