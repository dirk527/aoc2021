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
        int nRows = maze.size();
        int nCols = maze.getFirst().length();
        Vertex finish = null;
        List<ConstructItem> todo = new LinkedList<>();
        todo.addFirst(new ConstructItem(Vertex.getUnique(new Pos(0, 1)), new Pos(1, 1), 1));
        HashSet<Pos> visited = new HashSet<>();
        visited.add(new Pos(0, 1));
        List<ConstructItem> poss = new ArrayList<>(4);
        while (!todo.isEmpty()) {
            ConstructItem ci = todo.removeLast();
            if (ci.pos.row == nRows - 1) {
                System.out.println("*** Finish found ***");
                finish = Vertex.getUnique(ci.pos);
                new Edge(ci.start, finish, ci.steps);
                continue;
            }
            poss.clear();
            for (Direction dir : Direction.values()) {
                int nr = ci.pos.row + dir.rowOffset;
                int nc = ci.pos.col + dir.colOffset;
                Pos npos = new Pos(nr, nc);
                if (Vertex.isVertex(npos)) {
                    Vertex reached = Vertex.getUnique(npos);
                    if (reached != ci.start) {
                        new Edge(ci.start, reached, ci.steps + 1);
                    }
                } else if (!visited.contains(npos) && maze.get(nr).charAt(nc) != '#') {
                    poss.add(new ConstructItem(ci.start, npos, ci.steps + 1));
                }
            }
            if (poss.size() == 1) {
                visited.add(ci.pos);
                todo.addLast(poss.getFirst());
            } else if (poss.size() > 1) {
                Vertex nnode = Vertex.getUnique(ci.pos);
                new Edge(ci.start, nnode, ci.steps);
                for (ConstructItem p : poss) {
                    todo.addLast(new ConstructItem(nnode, p.pos, 1));
                }
            }
        }

        LinkedList<SearchItem> todo2 = new LinkedList<>();
        todo2.add(new SearchItem(Vertex.getUnique(new Pos(0, 1)), 0, List.of()));
        longest = 0;
        int i = 0;
        while (!todo2.isEmpty()) {
            if (i++ % 1000 == 0) {
                System.out.printf("%8d todo %8d\n", i - 1, todo2.size());
            }
            SearchItem si = todo2.removeFirst();
            if (si.cur == finish) {
                if (si.steps > longest) {
                    System.out.println("finished at " + si.steps);
                }
                longest = Math.max(longest, si.steps);
            } else {
                for (Edge e : si.cur.edges) {
                    Vertex next = e.other(si.cur);
                    if (!si.visited.contains(next)) {
                        todo2.add(new SearchItem(next, si.steps + e.length, si.visited));
                    }
                }
            }
        }

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

    static class Edge {
        private Vertex one, two;
        private int length;

        public Edge(Vertex one, Vertex two, int length) {
            this.one = one;
            this.two = two;
            this.length = length;
            one.addEdge(this);
            two.addEdge(this);
            System.out.printf("Edge from %s (%d,%d) to %s (%d,%d) weight %d\n", one.label, one.pos.row, one.pos.col, two.label, two.pos.row, two.pos.col, length);
        }

        public Vertex other(Vertex in) {
            return in == one ? two : one;
        }
    }

    static class Vertex {
        static HashMap<Pos, Vertex> vertices = new HashMap<>();
        static char nextLabel = 'A';

        private char label;
        private Pos pos;
        private List<Edge> edges;

        public static Vertex getUnique(Pos pos) {
            return vertices.computeIfAbsent(pos, Vertex::new);
        }

        public static boolean isVertex(Pos pos) {
            return vertices.containsKey(pos);
        }

        private Vertex(Pos pos) {
            this.pos = pos;
            edges = new ArrayList<>();
            label = nextLabel++;
        }

        public void addEdge(Edge e) {
            edges.add(e);
        }

        @Override
        public String toString() {
            return "" + label;
        }
    }

    record ConstructItem(Vertex start, Pos pos, int steps) {
    }

    static class SearchItem {
        Vertex cur;
        int steps;
        HashSet<Vertex> visited;

        public SearchItem(Vertex cur, int steps, Collection<Vertex> prevVisits) {
            this.cur = cur;
            this.steps = steps;
            visited = new HashSet<>();
            visited.addAll(prevVisits);
            visited.add(cur);
        }
    }
}