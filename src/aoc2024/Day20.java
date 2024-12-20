package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day20 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("20-in"));

        String s;
        List<List<Character>> map = new ArrayList<>();
        int row = 0;
        Pos start = null;
        Pos end = null;
        while ((s = br.readLine()) != null) {
            List<Character> line = new ArrayList<>();
            char[] charArray = s.toCharArray();
            for (int col = 0, charArrayLength = charArray.length; col < charArrayLength; col++) {
                char c = charArray[col];
                line.add(c);
                if (c == 'S') {
                    start = new Pos(row, col);
                } else if (c == 'E') {
                    end = new Pos(row, col);
                }
            }
            map.add(line);
            row++;
        }

        // Run Dijkstra's single source shortest path algorithm, remembering the best path
        PriorityQueue<State> queue = new PriorityQueue<>();
        List<State> possiblePaths = new ArrayList<>();
        State noCheatPath = null;
        HashMap<Pos, List<Cheat>> visited = new HashMap<>();
        queue.add(new State(start, 0, NO_CHEAT_YET, null));
        while (!queue.isEmpty()) {
            State state = queue.poll();
            if (end.equals(state.pos)) {
                HashSet<Pos> path = new HashSet<>();
                for (State st = state; st != null; st = st.prev) {
                    path.add(st.pos);
                }
                print(map, path);
                if (state.cheat == NO_CHEAT_YET) {
                    noCheatPath = state;
                    System.out.println("no cheat len: " + state.dist);
                    break;
                }
                System.out.println("found: " + state.dist);
                possiblePaths.add(state);
                if (possiblePaths.size() % 1000 == 0) {
                    System.out.printf("%d %d%n", possiblePaths.size(), state.dist);
                }
            }
            for (Direction direction : Direction.values()) {
                int rr = state.r() + direction.r;
                int cc = state.c() + direction.c;
                if (rr < 0 || rr >= map.size() || cc < 0 || cc >= map.get(rr).size()) {
                    // out of bounds - can happen with cheat
                    continue;
                }

                char terrain = map.get(rr).get(cc);
                if (terrain != '#') {
                    Pos pos = new Pos(rr, cc);
                    List<Cheat> seenCheats = visited.computeIfAbsent(pos, k -> new ArrayList<>());
                    Cheat cheat;
                    if (state.cheat.begin != null && state.cheat.end == null) {
                        cheat = new Cheat(state.cheat.begin, pos);
                    } else {
                        cheat = state.cheat;
                    }
                    boolean vis = seenCheats.contains(cheat);
                    if (!vis) {
                        seenCheats.add(cheat);
                        queue.add(new State(pos, state.dist + 1, cheat, state));
                    }
                } else {
                    if (state.cheat == NO_CHEAT_YET && false) {
                        Pos pos = new Pos(rr, cc);
                        List<Cheat> seenCheats = visited.computeIfAbsent(pos, k -> new ArrayList<>());
                        Cheat cheat = new Cheat(pos, null);
                        boolean vis = seenCheats.contains(cheat);
                        if (!vis) {
                            seenCheats.add(cheat);
                            queue.add(new State(pos, state.dist + 1, cheat, state));
                        }
                    }
                }
            }
        }

        int count = 0;
        for (State st : possiblePaths) {
            if (noCheatPath.dist - st.dist >= 100) {
                count++;
            }
        }
        System.out.println("p1: " + count);
        // best for in: 9316
    }

    private static void print(List<List<Character>> map, Set<Pos> onBestPath) {
        for (int r = 0; r < map.size(); r++) {
            for (int c = 0; c < map.get(r).size(); c++) {
                Character terrain = map.get(r).get(c);
                if (onBestPath.contains(new Pos(r, c))) {
                    System.out.print(terrain == '#' ? 'X' : 'O');
                } else {
                    System.out.print(terrain);
                }
            }
            System.out.println();
        }
    }

    record State(Pos pos, int dist, Cheat cheat, State prev) implements Comparable<State> {
        int r() {
            return pos.row();
        }

        int c() {
            return pos.col();
        }

        @Override
        public int compareTo(State o) {
            return dist() - o.dist();
        }
    }

    record Pos(int row, int col) {
    }

    record Cheat(Pos begin, Pos end) {
    }

    private static Cheat NO_CHEAT_YET = new Cheat(null, null);

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