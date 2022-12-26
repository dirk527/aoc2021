package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day24 {
    static int width;
    static int height;

    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 4;
    public static final int UP = 8;
    private static HashMap<Integer, Valley> minutes = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // Initialize
        BufferedReader br = new BufferedReader(new FileReader("day24.txt"));
        String s = br.readLine();
        width = s.length() - 2;
        List<String> lines = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            if (s.charAt(1) == '#') {
                break;
            }
            lines.add(s);
        }
        br.close();
        height = lines.size();
        int[][] initial = new int[width][height];
        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                initial[x][y] = switch (line.charAt(x + 1)) {
                    case '>' -> RIGHT;
                    case '<' -> LEFT;
                    case '^' -> UP;
                    case 'v' -> DOWN;
                    default -> 0;
                };
            }
        }

        /* Test simulation
        print(initial);
        int[][] cur = initial;
        for (int i = 0; i < 18; i++) {
            cur = nextRound(cur);
            System.out.println("\nMinute " + (i + 1));
            print(cur);
        }
        */

        // Search - cache states by minute - distance to goal as metric
        State start = new State(0, -1, 0, null);
        minutes.put(start.minute, new Valley(initial));
        State end = search(start, width - 1, height - 1);
        System.out.println("part 1: " + (end.minute + 1));
        State start2 = new State(width - 1, height, end.minute + 1, null);
        State end2 = search(start2, 0, 0);
        System.out.println("part 1.5: " + (end2.minute + 1));
        State end3 = search(new State(0, -1, end2.minute + 1, null), width - 1, height - 1);
        System.out.println("part 2: " + (end3.minute + 1));
    }

    private static State search(State start, int goalX, int goalY) {
        PriorityQueue<State> queue = new PriorityQueue<>((a, b) -> {
            int distA = Math.abs(goalX - a.x) + Math.abs(goalY - a.y) ;
            int distB = Math.abs(goalX - b.x) + Math.abs(goalY - b.y) ;
            return distA - distB + a.minute - b.minute;
        });
        HashSet<State> seen = new HashSet<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            State state = queue.poll();
            int x = state.x;
            int y = state.y;
            int nextMin = state.minute + 1;
            if (x == goalX && y == goalY) {
                return state;
            }
            Valley next = getValley(nextMin);
            if (y == -1 || y == height || next.valley[x][y] == 0) {
                State e = new State(x, y, nextMin, state);
                if (!seen.contains(e)) {
                    queue.add(e);
                    seen.add(e);
                }
            }
            if (y < height - 1 && next.valley[x][y + 1] == 0) {
                State e = new State(x, y + 1, nextMin, state);
                if (!seen.contains(e)) {
                    queue.add(e);
                    seen.add(e);
                }
            }
            if (y > 0 && next.valley[x][y - 1] == 0) {
                State e = new State(x, y - 1, nextMin, state);
                if (!seen.contains(e)) {
                    queue.add(e);
                    seen.add(e);
                }
            }
            if (y == -1 || y == height) {
                continue;
            }
            if (x > 0 && next.valley[x - 1][y] == 0) {
                State e = new State(x - 1, y, nextMin, state);
                queue.add(e);
                if (!seen.contains(e)) {
                    queue.add(e);
                    seen.add(e);
                }
            }
            if (x < width - 1 && next.valley[x + 1][y] == 0) {
                State e = new State(x + 1, y, nextMin, state);
                if (!seen.contains(e)) {
                    queue.add(e);
                    seen.add(e);
                }
            }
        }
        throw new IllegalArgumentException();
    }

    private static Valley getValley(int minute) {
        if (minutes.get(minute) == null) {
            minutes.put(minute, nextRound(getValley(minute - 1)));
        }
        return minutes.get(minute);
    }

    static void print(Valley in, int expX, int expY) {
        int[][] valley = in.valley;
        System.out.print("#");
        System.out.print(expY == -1 ? "E" : ".");
        for (int x = 0; x < width; x++) {
            System.out.print('#');
        }
        System.out.println();
        for (int y = 0; y < height; y++) {
            System.out.print("#");
            for (int x = 0; x < width; x++) {
                if (x == expX && y == expY) {
                    System.out.print('E');
                } else {
                    System.out.print(switch (valley[x][y]) {
                        case 0 -> ".";
                        case UP -> "^";
                        case DOWN -> "v";
                        case LEFT -> "<";
                        case RIGHT -> ">";
                        default -> "" + countBits(valley[x][y]);
                    });
                }
            }
            System.out.println("#");
        }
        for (int x = 0; x < width; x++) {
            System.out.print('#');
        }
        System.out.println(".#");
    }

    private static int countBits(int i) {
        return ((i & UP) > 0 ? 1 : 0) +
                ((i & DOWN) > 0 ? 1 : 0) +
                ((i & LEFT) > 0 ? 1 : 0) +
                ((i & RIGHT) > 0 ? 1 : 0);
    }

    static Valley nextRound(Valley valley) {
        int[][] in = valley.valley;
        int[][] ret = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int calc = 0;
                int yy = y == 0 ? height - 1 : y - 1;
                if ((in[x][yy] & DOWN) > 0) {
                    calc = calc | DOWN;
                }
                yy = y == height - 1 ? 0 : y + 1;
                if ((in[x][yy] & UP) > 0) {
                    calc = calc | UP;
                }
                int xx = x == 0 ? width - 1 : x - 1;
                if ((in[xx][y] & RIGHT) > 0) {
                    calc = calc | RIGHT;
                }
                xx = x == width - 1 ? 0 : x + 1;
                if ((in[xx][y] & LEFT) > 0) {
                    calc = calc | LEFT;
                }
                ret[x][y] = calc;
            }
        }
        return new Valley(ret);
    }

    record State (int x, int y, int minute, State prev){
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof State state)) return false;

            if (x != state.x) return false;
            if (y != state.y) return false;
            return minute == state.minute;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + minute;
            return result;
        }
    }

    // need this so I can insert it into a HashMap
    record Valley(int[][] valley) {
    }
}