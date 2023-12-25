package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day23 {

    static boolean part2 = false;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("23-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("23-sample.txt"));

        String s;
        List<String> maze = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            maze.add(s);
        }

        int longest = bfs(maze);
        System.out.println(longest);
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        part2 = true;
        longest = bfs(maze);
        System.out.println(longest);
        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (endTime - p1Time) / 1000f);
    }

    private static int bfs(List<String> maze) {
        int nRows = maze.size();
        int nCols = maze.getFirst().length();
        Pos finish = new Pos(nRows - 1, nCols - 2);
        LinkedList<State> todo = new LinkedList<>();
        HashMap<Pos, Integer> mostSteps = new HashMap<>();
        todo.add(new State(new Pos(0, 1), 0, null));
        int longest = 0;
        int i = 0;
        while (!todo.isEmpty()) {
            if (i++ % 10000 == 0) {
                System.out.printf("%8d todo %8d\n", i, todo.size());
            }
            State state = todo.removeLast();
            if (state.pos.equals(finish)) {
                System.out.println("finished at " + state.steps);
                longest = Math.max(longest, state.steps);
            } else {
                state.addPossibleMoves(maze, mostSteps, todo);
            }
        }
        return longest;
    }

    static class State {
        Pos pos;
        int steps;
        private HashSet<Pos> visited = new HashSet<>();

        public State(Pos pos, int steps, HashSet<Pos> prevVisited) {
            this.pos = pos;
            this.steps = steps;
            if (prevVisited != null) {
                visited.addAll(prevVisited);
            }
            visited.add(pos);
        }

        public void addPossibleMoves(List<String> maze, HashMap<Pos, Integer> mostSteps, LinkedList<State> todo) {
            char c = maze.get(pos.row).charAt(pos.col);
            if (!part2) {
                if (c == '<') {
                    todo.add(new State(new Pos(pos.row, pos.col - 1), steps + 1, visited));
                    return;
                } else if (c == '>') {
                    todo.add(new State(new Pos(pos.row, pos.col + 1), steps + 1, visited));
                    return;
                } else if (c == 'v') {
                    todo.add(new State(new Pos(pos.row + 1, pos.col), steps + 1, visited));
                    return;
                } else if (c == '^') {
                    todo.add(new State(new Pos(pos.row - 1, pos.col), steps + 1, visited));
                    return;
                }
            }
            for (Direction dir : Direction.values()) {
                int nr = pos.row + dir.rowOffset;
                int nc = pos.col + dir.colOffset;
                int nst = steps + 1;
                Pos np = new Pos(nr, nc);
                Integer longestSt = mostSteps.get(np);
                boolean optimize = longestSt == null || nst > longestSt;
                if (optimize && !visited.contains(np) &&
                        nr >= 0 && nr < maze.size() && nc >= 0 && nc <= maze.getFirst().length() &&
                        dir.allows(maze.get(nr).charAt(nc))) {
                    todo.add(new State(np, nst, visited));
                }
            }
        }
    }

    record Pos(int row, int col) {
    }

    enum Direction {
        NORTH(-1, 0, '^'),
        EAST(0, 1, '>'),
        SOUTH(1, 0, 'v'),
        WEST(0, -1, '<');

        final int rowOffset;
        final int colOffset;
        final char myChar;

        Direction(int rowOffset, int colOffset, char myChar) {
            this.rowOffset = rowOffset;
            this.colOffset = colOffset;
            this.myChar = myChar;
        }

        public boolean allows(char c) {
            if (part2) {
                return c != '#';
            } else {
                return c == '.' || c == myChar;
            }
        }
    }
}