package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day04 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("04-in"));

        String s;
        List<List<Character>> rows = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            List<Character> row = new ArrayList<>(s.chars().mapToObj(i -> (char) i).toList());
            rows.add(row);
        }

        List<Pos> removable = removable(rows);
        int result = removable.size();
        System.out.println(result);   // part 1
        while (!removable.isEmpty()) {
            removable.forEach(pos -> rows.get(pos.row).set(pos.col, 'x'));
            removable = removable(rows);
            result += removable.size();
        }
        System.out.println(result);
    }

    private static List<Pos> removable(List<List<Character>> grid) {
        List<Pos> ret = new ArrayList<>();
        for (int r = 0; r < grid.size(); r++) {
            for (int c = 0; c < grid.getFirst().size(); c++) {
                int n = calculateNeighbours(grid, r, c);
                if (n < 4) {
                    ret.add(new Pos(r, c));
                }
            }
        }
        return ret;
    }

    private static int calculateNeighbours(List<List<Character>> grid, int row, int col) {
        int ret = 0;
        if (grid.get(row).get(col) != '@') {
            return 9;
        }
        for (Direction direction : Direction.values()) {
            int rr = row + direction.r;
            int cc = col + direction.c;
            if (rr >= 0 && rr < grid.size() && cc >= 0 && cc < grid.getFirst().size()) {
                if (grid.get(rr).get(cc) == '@') {
                    ret++;
                }
            }
        }
        return ret;
    }

    private enum Direction {
        NW(-1, -1),
        N(-1, 0),
        NE(-1, 1),
        E(0, 1),
        SE(1, 1),
        S(1, 0),
        SW(1, -1),
        W(0, -1);

        public final int r;
        public final int c;

        Direction(int row, int col) {
            this.r = row;
            this.c = col;
        }
    }

    private record Pos(int row, int col){}
}