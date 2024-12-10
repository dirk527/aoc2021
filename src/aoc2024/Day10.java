package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day10 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("10-in"));
        String s;
        List<List<Character>> grid = new ArrayList<>();
        List<Pos> trailheads = new ArrayList<>();
        int row = 0;
        while ((s = br.readLine()) != null) {
            List<Character> line = new ArrayList<>();
            char[] charArray = s.toCharArray();
            for (int col = 0, charArrayLength = charArray.length; col < charArrayLength; col++) {
                char c = charArray[col];
                line.add(c);
                if (c == '0') {
                    trailheads.add(new Pos(row, col));
                }
            }
            grid.add(line);
            row++;
        }

        int p1 = 0;
        int p2 = 0;
        for (Pos trailhead : trailheads) {
            p1 += findSummits(grid, trailhead).size();
            p2 += calculatePathNumber(grid, trailhead);
        }
        System.out.println(p1);
        System.out.println(p2);
    }

    private static Set<Pos> findSummits(List<List<Character>> grid, Pos curPos) {
        Character cur = grid.get(curPos.row).get(curPos.col);
        HashSet<Pos> ret = new HashSet<>();
        if (cur == '9') {
            ret.add(curPos);
            return ret;
        }
        for (Direction direction : Direction.values()) {
            int r = curPos.row + direction.r;
            int c = curPos.col + direction.c;
            if (r >= 0 && r < grid.size() && c >= 0 && c < grid.get(r).size() && grid.get(r).get(c) == cur + 1) {
                ret.addAll(findSummits(grid, new Pos(r, c)));
            }
        }
        return ret;
    }

    private static int calculatePathNumber(List<List<Character>> grid, Pos curPos) {
        Character cur = grid.get(curPos.row).get(curPos.col);
        if (cur == '9') {
            return 1;
        }
        int score = 0;
        for (Direction direction : Direction.values()) {
            int r = curPos.row + direction.r;
            int c = curPos.col + direction.c;
            if (r >= 0 && r < grid.size() && c >= 0 && c < grid.get(r).size() && grid.get(r).get(c) == cur + 1) {
                score += calculatePathNumber(grid, new Pos(r, c));
            }
        }
        return score;
    }

    record Pos(int row, int col) {
    }

    enum Direction {
        NORTH(-1, 0),
        EAST(0, 1),
        SOUTH(1, 0),
        WEST(0, -1);

        final int r;
        final int c;

        Direction(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
}