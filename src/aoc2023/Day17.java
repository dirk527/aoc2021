package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static aoc2023.Day17.Direction.EAST;
import static aoc2023.Day17.Direction.SOUTH;

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
        int[][] cave = new int[lines.size()][lines.getFirst().length()];
        for (int r = 0; r < lines.size(); r++) {
            String line = lines.get(r);
            for (int c = 0; c < lines.getFirst().length(); c++) {
                cave[r][c] = Integer.parseInt(line.substring(c, c + 1));
            }
        }

        System.out.println(dijkstra(cave, Part.ONE));
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        System.out.println(dijkstra(cave, Part.TWO));
        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (endTime - p1Time) / 1000f);
    }

    private static int dijkstra(int[][] cave, Part part) {
        int nRows = cave.length;
        int nCols = cave[0].length;

        HashSet<Node> visited = new HashSet<>();
        PriorityQueue<State> queue = new PriorityQueue<>();
        queue.add(new State(new Node(0, 1, EAST, 1), cave[0][1], null));
        queue.add(new State(new Node(1, 0, SOUTH, 1), cave[1][0], null));
        while (!queue.isEmpty()) {
            State cur = queue.remove();
            if (visited.contains(cur.node)) {
                continue;
            }

            visited.add(cur.node);
            if (cur.node.row == nRows - 1 && cur.node.col == nCols - 1 &&
                    (part == Part.ONE || cur.node.steps > 2)) {
                return cur.cost;
            }
            if (part == Part.ONE) {
                cur.addNext(queue, cave);
            } else {
                cur.addNextUltra(queue, cave);
            }
        }
        return -1;
    }

    enum Direction {
        NORTH(-1, 0), EAST(0, 1), SOUTH(1, 0), WEST(0, -1);

        final int rowOffset;
        final int colOffset;

        Direction(int rowOffset, int colOffset) {
            this.rowOffset = rowOffset;
            this.colOffset = colOffset;
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

    record Node(int row, int col, Direction dir, int steps) {
    }

    static class State implements Comparable<State> {
        Node node;
        int cost;
        State prev;

        public State(Node node, int cost, State prev) {
            this.node = node;
            this.cost = cost;
            this.prev = prev;
        }

        @Override
        public int compareTo(State o) {
            int ret = cost - o.cost;
            if (ret == 0 && node.dir == o.node.dir) {
                ret = node.steps - o.node.steps;
            }
            if (ret == 0) {
                ret = o.node.row + o.node.col - node.row - node.col;
            }
            return ret;
        }

        private void addNextWhenInBounds(Collection<State> out, Direction d, int[][] costs) {
            int nr = node.row + d.rowOffset;
            int nc = node.col + d.colOffset;
            if (nr >= 0 && nr < costs.length && nc >= 0 && nc < costs[0].length) {
                int nSteps = node.dir == d ? node.steps + 1 : 0;
                State nxt = new State(new Node(nr, nc, d, nSteps), cost + costs[nr][nc], this);
                out.add(nxt);
            }
        }

        public void addNext(Collection<State> out, int[][] costs) {
            if (node.steps < 2) {
                addNextWhenInBounds(out, node.dir, costs);
            }
            addNextWhenInBounds(out, node.dir.left(), costs);
            addNextWhenInBounds(out, node.dir.right(), costs);
        }

        public void addNextUltra(Collection<State> out, int[][] costs) {
            if (node.steps < 9) {
                addNextWhenInBounds(out, node.dir, costs);
            }
            if (node.steps > 2) {
                addNextWhenInBounds(out, node.dir.left(), costs);
                addNextWhenInBounds(out, node.dir.right(), costs);
            }
        }

        @Override
        public String toString() {
            return node.row + "," + node.col + " " + node.steps + " " + node.dir + " | " + cost;
        }
    }

    enum Part {ONE, TWO}
}