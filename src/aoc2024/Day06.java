package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day06 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("06-in"));
        String s;
        List<List<Character>> grid = new ArrayList<>();
        int startRow = -1;
        int startCol = -1;
        int row = 0;
        while ((s = br.readLine()) != null) {
            List<Character> line = new ArrayList<>();
            char[] charArray = s.toCharArray();
            for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
                char c = charArray[i];
                line.add(c);
                if (c == '^') {
                    startRow = row;
                    startCol = i;
                }
            }
            grid.add(line);
            row++;
        }

        simulateGuardWalk(grid, startRow, startCol);

        int p1 = 0;
        List<Pos> potentialSites = new ArrayList<>();
        for (int i = 0; i < grid.size(); i++) {
            List<Character> line = grid.get(i);
            for (int j = 0; j < line.size(); j++) {
                Character c = line.get(j);
                if (c == 'X') {
                    potentialSites.add(new Pos(i, j));
                    p1++;
                }
                System.out.print(c);
            }
            System.out.println();
        }

        System.out.println("\n" + p1);

        int p2 = 0;
        for (Pos pot : potentialSites) {
            resetGrid(grid);
            grid.get(pot.row).set(pot.col, '#');
            if (simulateGuardWalk(grid, startRow, startCol)) {
                p2++;
            }
            grid.get(pot.row).set(pot.col, '.');
        }
        System.out.println(p2);
    }

    private static void resetGrid(List<List<Character>> grid) {
        for (List<Character> line : grid) {
            for (int j = 0; j < line.size(); j++) {
                Character c = line.get(j);
                if (c == 'X') {
                    line.set(j, '.');
                }
            }
        }
    }

    private static boolean simulateGuardWalk(List<List<Character>> grid, int startRow, int startCol) {
        int rows = grid.size();
        int cols = grid.getFirst().size();

        Direction dir = Direction.NORTH;
        int row = startRow;
        int col = startCol;
        Set<Turn> turns = new HashSet<>();
        while (true) {
            grid.get(row).set(col, 'X');
            int nr = row + dir.r;
            int nc = col + dir.c;
            if (nr >= 0 && nc >= 0 && nr <= rows - 1 && nc <= cols - 1) {
                if (grid.get(nr).get(nc) == '#') {
                    Turn cur = new Turn(row, col, dir);
                    if (turns.contains(cur)) {
                        return true; // Loop detected
                    }
                    dir = dir.right();
                    turns.add(cur);
                } else {
                    row = nr;
                    col = nc;
                }
            } else {
                return false;
            }
        }
    }

    record Pos(int row, int col) {
    }

    record Turn(int row, int col, Direction direction) {
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

        Direction right() {
            return switch (this) {
                case NORTH -> EAST;
                case EAST -> SOUTH;
                case SOUTH -> WEST;
                case WEST -> NORTH;
            };
        }
    }
}