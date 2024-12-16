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

        PriorityQueue<State> queue = new PriorityQueue<>();
        HashMap<Pos, Set<Direction>> visited = new HashMap<>();
        queue.add(new State(start, Direction.EAST, 0));
        while (!queue.isEmpty()) {
            State state = queue.poll();
            if (end.equals(state.pos)) {
                System.out.println(state.score);
                break;
            }
            Pos ahead = new Pos(state.r() + state.dir().r, state.c() + state.dir().c);
            if (map.get(ahead.row()).get(ahead.col()) != '#') {
                State next = new State(ahead, state.dir, state.score + 1);
                if (!alreadyVisited(visited, next.pos, next.dir)) {
                    queue.add(next);
                }
            }
            State left = new State(state.pos(), state.dir().left(), state.score + 1000);
            if (!alreadyVisited(visited, left.pos, left.dir)) {
                queue.add(left);
            }
            State right = new State(state.pos(), state.dir().right(), state.score + 1000);
            if (!alreadyVisited(visited, right.pos, right.dir)) {
                queue.add(right);
            }
        }
    }

    private static boolean alreadyVisited(HashMap<Pos, Set<Direction>> visited, Pos pos, Direction dir) {
        if (!visited.containsKey(pos)) {
            visited.put(pos, EnumSet.noneOf(Direction.class));
        }
        boolean ret = visited.get(pos).contains(dir);
        visited.get(pos).add(dir);
        return ret;
    }

    record State(Pos pos, Direction dir, int score) implements Comparable<State> {
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
            return switch(this) {
                case NORTH -> WEST;
                case EAST -> NORTH;
                case SOUTH -> EAST;
                case WEST -> SOUTH;
            };
        }

        public Direction right() {
            return switch(this) {
                case NORTH -> EAST;
                case EAST -> SOUTH;
                case SOUTH -> WEST;
                case WEST -> NORTH;
            };
        }
    }
}