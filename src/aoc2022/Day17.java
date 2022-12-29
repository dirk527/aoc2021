package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day17 {
    private static HashMap<Long,Integer> rows = new HashMap<>();
    private static int[] masks;

    static {
        masks = new int[8];
        for (int i = 0; i < 8; i++) {
            masks[i] = 1 << i;
        }
    }

    public static void main(String[] args) throws IOException {
        // Initialize
        long begin = System.currentTimeMillis();
        //        String jets = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>";
        BufferedReader br = new BufferedReader(new FileReader("day17.txt"));
        String jets = br.readLine();
        int jetLen = jets.length();
        List<Rock> rocks = new ArrayList<>();
        rocks.add(new Rock(new boolean[][]{{true, true, true, true}}));
        rocks.add(new Rock(new boolean[][]{{false, true, false}, {true, true, true}, {false, true, false}}));
        rocks.add(new Rock(new boolean[][]{{false, false, true}, {false, false, true}, {true, true, true}}));
        rocks.add(new Rock(new boolean[][]{{true}, {true}, {true}, {true}}));
        rocks.add(new Rock(new boolean[][]{{true, true}, {true, true}}));

        long maxheight = 0;
        int rockIdx = 0;
        int jetIdx = 0;
        // try to determine the length of the period: record where resting places are
        HashMap<IdxState, List<SimulationState>> detector = new HashMap<>();
        long goal = 1000000000000L;
        long additionalCycles = -1;
        long diffPerCycle = -1;
        for (long round = 0; round < goal; round++) {
            boolean debug = false;
            Rock cur = rocks.get(rockIdx);
            rockIdx = (rockIdx + 1) % 5;
            long y = maxheight + 3 + cur.height();
            int x = 2;
            while (true) {
                char dir = jets.charAt(jetIdx);
                jetIdx = (jetIdx + 1) % jetLen;
                if (dir == '<') {
                    x = cur.moveLeft(x, y);
                } else {
                    x = cur.moveRight(x, y);
                }
                if (cur.canMoveDown(x, y)) {
                    y--;
                } else {
                    cur.block(x, y);
                    maxheight = Math.max(maxheight, y);
                    if (additionalCycles == -1 && rockIdx == 0) {
                        IdxState is = new IdxState(jetIdx, rockIdx);
                        List<SimulationState> sim = detector.get(is);
                        if (sim == null) {
                            sim = new ArrayList<>();
                            detector.put(is, sim);
                        } else if (sim.size() > 1) {
                            if (debug) {
                                System.out.print(is + " " + y);
                                long ly = y;
                                long lr = round;
                                for (int i = sim.size() - 1; i >= 0; i--) {
                                    SimulationState s = sim.get(i);
                                    System.out.print(" (" + (ly - s.y) + "," + (lr - s.round) + ") " + s.y);
                                    ly = s.y;
                                    lr = s.round;
                                }
                                System.out.println();
                            }
                            SimulationState last = sim.get(sim.size() - 1);
                            for (int i = sim.size() - 2; i >= 0; i--) {
                                SimulationState cand = sim.get(i);
                                if (equal(cand, last)) {
                                    long period = last.round - cand.round;
                                    diffPerCycle = last.y - cand.y;
                                    System.out.println("Found period " + period + "/" + diffPerCycle + " " + last + " & " + cand + " | " + is);
                                    additionalCycles = (goal - round) / period;
                                    round += additionalCycles * period;
                                }
                            }
                        }
                        sim.add(new SimulationState(round, y));
                    }
                    break;
                }
            }
            if (round == 2021) {
                print("", maxheight);
                System.out.println("part 1: " + maxheight);
                long time = System.currentTimeMillis() - begin;
                System.out.println(time + "ms");
            }
        }
        System.out.println("cur height " + maxheight + " +" + additionalCycles + " * " + diffPerCycle);
        long pt2 = maxheight + additionalCycles * diffPerCycle;
        System.out.println("part 2: " + pt2);
        // check correctness for example
        // System.out.println(1514285714288L - pt2);
        long time = System.currentTimeMillis() - begin;
        System.out.println(time + "ms");
    }


    static boolean isBlocked(int x, long y) {
        Integer cur = rows.get(y);
        if (cur == null) {
            return false;
        }
        return (cur & masks[x]) != 0;
    }

    static void addBlocked(int x, long y) {
        Integer cur = rows.get(y);
        if (cur == null) {
            cur = masks[x];
        } else {
            cur = cur.intValue() | masks[x];
        }
        rows.put(y, cur);
    }

    private static boolean equal(SimulationState one, SimulationState two) {
        long diff = two.round - one.round;
        if (one.round < diff) {
            return false;
        }
        long r1 = one.y;
        long r2 = two.y;
        for (long r = 0; r < diff; r++) {
            for (int i = 0; i < 7; i++) {
                if (isBlocked(i, r1) != isBlocked(i, r2)) {
                    return false;
                }
            }
            r1--;
            r2--;
        }
        return true;
    }

    public static void print(String s, long height) {
        System.out.println(s);
        while (height > 0) {
            System.out.print("|");
            for (int i = 0; i < 7; i++) {
                if (isBlocked(i, height)) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println("|");
            height--;
        }
        System.out.println("+-------+\n");
    }

    record IdxState(int jetIdx, int rockIdx) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IdxState state)) return false;

            if (jetIdx != state.jetIdx) return false;
            return rockIdx == state.rockIdx;
        }

        @Override
        public int hashCode() {
            int result = jetIdx;
            result = 31 * result + rockIdx;
            return result;
        }
    }

    record SimulationState(long round, long y) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SimulationState that)) return false;

            if (round != that.round) return false;
            return y == that.y;
        }

        @Override
        public int hashCode() {
            int result = (int) (round ^ (round >>> 32));
            result = 31 * result + (int) (y ^ (y >>> 32));
            return result;
        }
    }

    static class Rock {
        boolean[][] shape;

        public Rock(boolean[][] shape) {
            this.shape = shape;
        }

        public int height() {
            return shape.length;
        }

        public int moveLeft(int x, long y) {
            if (x == 0) {
                return 0;
            }
            for (boolean[] row : shape) {
                for (int i = 0; i < row.length; i++) {
                    if (row[i] && isBlocked(x + i - 1, y)) {
                        return x;
                    }
                }
                y--;
            }
            return x - 1;
        }

        public int moveRight(int x, long y) {
            if (x + shape[0].length == 7) {
                return x;
            }
            for (boolean[] row : shape) {
                for (int i = 0; i < row.length; i++) {
                    if (row[i] && isBlocked(x + i + 1, y)) {
                        return x;
                    }
                }
                y--;
            }
            return x + 1;
        }

        public boolean canMoveDown(int x, long y) {
            if (y - height() == 0) {
                return false;
            }
            y--;
            for (boolean[] row : shape) {
                for (int i = 0; i < row.length; i++) {
                    if (row[i] && isBlocked(x + i, y)) {
                        return false;
                    }
                }
                y--;
            }
            return true;
        }

        public void block(int x, long y) {
            for (boolean[] row : shape) {
                for (int i = 0; i < row.length; i++) {
                    if (row[i]) {
                        addBlocked(x + i, y);
                    }
                }
                y--;
            }
        }
    }
}