package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day16 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("16-in"));

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
        int bestScore = Integer.MAX_VALUE;
        HashMap<Pos, Set<Direction>> visited = new HashMap<>();
        Set<Visit> onBestPath = new HashSet<>();
        queue.add(new State(start, Direction.EAST, 0, null));
        while (!queue.isEmpty()) {
            State state = queue.poll();
            if (end.equals(state.pos)) {
                for (State st = state; st != null; st = st.prev) {
                    onBestPath.add(new Visit(st.pos, st.dir, st.score));
                }
                if (bestScore == Integer.MAX_VALUE) {
                    for (Direction direction : Direction.values()) {
                        onBestPath.add(new Visit(state.pos, direction, state.score));
                    }
                    System.out.println("p1: " + state.score);
                    bestScore = state.score;
                }
                break;
            }
            Pos ahead = new Pos(state.r() + state.dir.r, state.c() + state.dir.c);
            if (map.get(ahead.row).get(ahead.col) != '#') {
                State next = new State(ahead, state.dir, state.score + 1, state);
                if (!alreadyVisited(visited, next.pos, next.dir)) {
                    queue.add(next);
                }
            }
            State left = new State(state.pos, state.dir.left(), state.score + 1000, state);
            if (!alreadyVisited(visited, left.pos, left.dir)) {
                queue.add(left);
            }
            State right = new State(state.pos, state.dir.right(), state.score + 1000, state);
            if (!alreadyVisited(visited, right.pos, right.dir)) {
                queue.add(right);
            }
        }

        int seats = countSeats(onBestPath);
        System.out.println(seats);
        print(map, onBestPath);

        int prev = 0;
        while (prev < seats) {
            // repeatedly try to find additional best paths and mark them as well.
            queue.clear();
            visited.clear();
            queue.add(new State(start, Direction.EAST, 0, null));
            while (!queue.isEmpty()) {
                State state = queue.poll();
                if (state.score > bestScore) {
                    continue;
                }
                if (end.equals(state.pos)) {
                    continue;
                }
                Visit curVisit = new Visit(state.pos, state.dir, state.score);
                Pos ahead = new Pos(state.r() + state.dir.r, state.c() + state.dir.c);
                if (map.get(ahead.row).get(ahead.col) != '#') {
                    State next = new State(ahead, state.dir, state.score + 1, state);
                    Visit v = new Visit(next.pos, next.dir, next.score);
                    if (!onBestPath.contains(curVisit) && onBestPath.contains(v)) {
                        for (State st = state; st != null; st = st.prev) {
                            onBestPath.add(new Visit(st.pos, st.dir, st.score));
                        }
                    } else if (!alreadyVisited(visited, next.pos, next.dir)) {
                        queue.add(next);
                    }
                }
                State left = new State(state.pos, state.dir.left(), state.score + 1000, state);
                Visit v = new Visit(left.pos, left.dir, left.score);
                if (!onBestPath.contains(curVisit) && onBestPath.contains(v)) {
                    for (State st = state; st != null; st = st.prev) {
                        onBestPath.add(new Visit(st.pos, st.dir, st.score));
                    }
                } else if (!alreadyVisited(visited, left.pos, left.dir)) {
                    queue.add(left);
                }
                State right = new State(state.pos, state.dir.right(), state.score + 1000, state);
                v = new Visit(right.pos, right.dir, right.score);
                if (!onBestPath.contains(curVisit) && onBestPath.contains(v)) {
                    for (State st = state; st != null; st = st.prev) {
                        onBestPath.add(new Visit(st.pos, st.dir, st.score));
                    }
                } else if (!alreadyVisited(visited, right.pos, right.dir)) {
                    queue.add(right);
                }
            }

            prev = seats;
            seats = countSeats(onBestPath);
            System.out.println("\n" + seats);
            print(map, onBestPath);
        }

        System.out.println("p2: " + seats);
    }

    private static void print(List<List<Character>> map, Set<Visit> onBestPath) {
        Set<Pos> seats = new HashSet<>();
        onBestPath.forEach(v -> seats.add(v.pos));
        for (int r = 0; r < map.size(); r++) {
            for (int c = 0; c < map.get(r).size(); c++) {
                if (seats.contains(new Pos(r, c))) {
                    System.out.print('O');
                } else {
                    System.out.print(map.get(r).get(c));
                }
            }
            System.out.println();
        }
    }

    private static int countSeats(Set<Visit> onBestPath) {
        Set<Pos> seats = new HashSet<>();
        onBestPath.forEach(v -> seats.add(v.pos));
        return seats.size();
    }


    private static boolean alreadyVisited(HashMap<Pos, Set<Direction>> visited, Pos pos, Direction dir) {
        if (!visited.containsKey(pos)) {
            visited.put(pos, EnumSet.noneOf(Direction.class));
        }
        boolean ret = visited.get(pos).contains(dir);
        visited.get(pos).add(dir);
        return ret;
    }

    private static boolean visited2(HashSet<Visit> visited, Pos pos, Direction dir, int score) {
        Visit v = new Visit(pos, dir, score);
        boolean ret = visited.contains(v);
        visited.add(v);
        return ret;
    }

    record Visit(Pos pos, Direction dir, int score) {
    }

    record State(Pos pos, Direction dir, int score, State prev) implements Comparable<State> {
        int r() {
            return pos.row();
        }

        int c() {
            return pos.col();
        }

        @Override
        public int compareTo(State o) {
            return score() - o.score();
        }
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

        static Direction fromChar(char c) {
            return switch (c) {
                case '^' -> NORTH;
                case 'v' -> SOUTH;
                case '<' -> WEST;
                case '>' -> EAST;
                default -> throw new IllegalArgumentException();
            };
        }

        Direction(int r, int c) {
            this.r = r;
            this.c = c;
        }

        public Direction left() {
            return switch (this) {
                case NORTH -> WEST;
                case EAST -> NORTH;
                case SOUTH -> EAST;
                case WEST -> SOUTH;
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
}