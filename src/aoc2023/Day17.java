package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;

import static aoc2023.Day17.Direction.*;

public class Day17 {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("17-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("17-sample.txt"));

        String s;
        List<String> lines = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            lines.add(s);
        }

        System.out.println(dijkstra(lines, Part.ONE));
        // 1262 too high
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        System.out.println(dijkstra(lines, Part.TWO));
        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (endTime - p1Time) / 1000f);
    }

    private static int dijkstra(List<String> lines, Part part) {
        int nRows = lines.size();
        int nCols = lines.getFirst().length();

        int maxSteps = part == Part.ONE ? 3 : 10;
        int[][] cave = new int[nRows][nCols];
        int[][][][] dist = new int[nRows][nCols][4][maxSteps];
        for (int r = 0; r < nRows; r++) {
            String line = lines.get(r);
            for (int c = 0; c < nCols; c++) {
                cave[r][c] = Integer.parseInt(line.substring(c, c + 1));
                for (int i = 0; i < 4; i++) {
                    for (int st = 0; st < maxSteps; st++) {
                        dist[r][c][i][st] = Integer.MAX_VALUE;
                    }
                }
            }
        }

        PriorityQueue<State> queue = new PriorityQueue<>();
        State initial = new State(0, 0, NORTH, 0, 0, null);
        queue.add(new State(0, 1, EAST, 1, cave[0][1], initial));
        queue.add(new State(1, 0, SOUTH, 1, cave[1][0], initial));
        int found = -1;
        while (found == -1) {
            State cur = queue.remove();
//            System.out.print(cur);
            if (dist[cur.row][cur.col][cur.dir.idx()][cur.steps] < Integer.MAX_VALUE) {
                if (cur.cost < dist[cur.row][cur.col][cur.dir.idx()][cur.steps]) {
                    throw new IllegalStateException();
                }
//                System.out.println(" skip");
                continue;
            }

            dist[cur.row][cur.col][cur.dir.idx()][cur.steps] = cur.cost;
            if (cur.row == nRows - 1 && cur.col == nCols - 1 &&
                    (part == Part.ONE || cur.steps > 2)) {
//                System.out.println("\n\nThe way: " + cur.cost);
//                State t = cur;
//                while (t != null) {
//                    System.out.println(t);
//                    t = t.prev;
//                }
                found = cur.cost;
            }
            if (part == Part.ONE) {
                cur.addNext(queue, cave);
            } else {
                cur.addNextUltra(queue, cave);
            }
        }
        return found;
    }

    enum Direction {
        NORTH(-1, 0), EAST(0, 1), SOUTH(1, 0), WEST(0, -1);

        final int rOffset;
        final int colOffset;

        Direction(int rOffset, int colOffset) {
            this.rOffset = rOffset;
            this.colOffset = colOffset;
        }

        public int idx() {
            return switch (this) {
                case NORTH -> 0;
                case WEST -> 1;
                case SOUTH -> 2;
                case EAST -> 3;
            };
        }

        public Direction left() {
            return switch (this) {
                case NORTH -> WEST;
                case WEST -> SOUTH;
                case SOUTH -> EAST;
                case EAST -> NORTH;
            };
        }

        public Direction right() {
            return switch (this) {
                case NORTH -> EAST;
                case EAST -> SOUTH;
                case SOUTH -> WEST;
                case WEST -> NORTH;
            };
        }
    }

    static class State implements Comparable<State> {
        private int row;
        private int col;
        private Direction dir;
        private int steps;
        private int cost;
        State prev;

        public State(int row, int col, Direction dir, int steps, int cost, State prev) {
            this.row = row;
            this.col = col;
            this.dir = dir;
            this.steps = steps;
            this.cost = cost;
            this.prev = prev;
        }

        @Override
        public int compareTo(State o) {
            int ret = cost - o.cost;
            if (ret == 0 && dir == o.dir) {
                ret = steps - o.steps;
            }
            if (ret == 0) {
                ret = o.row + o.col - row - col;
            }
            return ret;
        }

        private void addNextWhenInBounds(Collection<State> out, Direction d, int[][] costs) {
            int nr = row + d.rOffset;
            int nc = col + d.colOffset;
            if (nr >= 0 && nr < costs.length && nc >= 0 && nc < costs[0].length) {
                int nSteps = dir == d ? steps + 1 : 0;
                State nxt = new State(nr, nc, d, nSteps, cost + costs[nr][nc], this);
//                System.out.print(" add (" + nxt + ")");
                out.add(nxt);
            }
        }

        public void addNext(Collection<State> out, int[][] costs) {
            if (steps < 2) {
                addNextWhenInBounds(out, dir, costs);
            }
            addNextWhenInBounds(out, dir.left(), costs);
            addNextWhenInBounds(out, dir.right(), costs);
        }

        public void addNextUltra(Collection<State> out, int[][] costs) {
            if (steps < 9) {
                addNextWhenInBounds(out, dir, costs);
            }
            if (steps > 2) {
                addNextWhenInBounds(out, dir.left(), costs);
                addNextWhenInBounds(out, dir.right(), costs);
            }
        }

        @Override
        public String toString() {
            return row + "," + col + " " + steps + " " + dir + " | " + cost;
        }
    }

    enum Part { ONE, TWO }
}