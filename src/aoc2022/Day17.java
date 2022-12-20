package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day17 {
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

        // Simulate part 1
        long maxheight = 0;
        int rockIdx = 0;
        int jetIdx = 0;
        Set<Point> blocked = new HashSet<>();
        HashSet<Point> curBlock = new HashSet<>();
        for (long round = 0; round < 1000000000000L; round++) {
            if (round % 1000000000 == 0) {
                System.out.println("round " + round);
            }
            boolean debug = false;
            Rock cur = rocks.get(rockIdx);
            rockIdx = (rockIdx + 1) % 5;
            long startY = maxheight + 3 + cur.height();
            long y = startY;
            int x = 2;
            if (debug) {
                curBlock.clear();
                cur.block(x, y, curBlock);
                print("Start round " + round, startY, blocked, curBlock);
            }
            while (true) {
                char dir = jets.charAt(jetIdx);
                jetIdx = (jetIdx + 1) % jetLen;
                if (dir == '<') {
                    x = cur.moveLeft(x, y, blocked);
                } else {
                    x = cur.moveRight(x, y, blocked);
                }
                if (debug) {
                    curBlock.clear();
                    cur.block(x, y, curBlock);
                    print("After jet " + dir, startY, blocked, curBlock);
                }
                if (cur.canMoveDown(x, y, blocked)) {
                    y--;
                    if (debug) {
                        curBlock.clear();
                        cur.block(x, y, curBlock);
                        print("Moved down", startY, blocked, curBlock);
                    }
                } else {
                    if (false) {
                        curBlock.clear();
                        print("Resting place", startY, blocked, curBlock);
                        cur.block(x, y, curBlock);
                    }
                    cur.block(x, y, blocked);
                    maxheight = Math.max(maxheight, y);
                    break;
                }
            }
            if (round == 2021) {
                System.out.println(maxheight);
                print("", maxheight, blocked, new HashSet<>());
            }
            if (maxheight > 1000) {
                for (int i=0;i<7;i++) {
                    blocked.remove(new Point(i, maxheight - 1000));
                    blocked.remove(new Point(i, maxheight - 1001));
                    blocked.remove(new Point(i, maxheight - 1002));
                    blocked.remove(new Point(i, maxheight - 1003));
                }
            }
        }
        System.out.println(maxheight);
    }

    record Point(int x, long y) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Point point)) return false;

            if (x != point.x) return false;
            return y == point.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + (int) (y ^ (y >>> 32));
            return result;
        }
    }

    public static void print(String s, long height, Set<Point> blocked, Set<Point> shape) {
        System.out.println(s);
        while (height > 0) {
            System.out.print("|");
            for (int i = 0; i < 7; i++) {
                Point p = new Point(i, height);
                if (blocked.contains(p)) {
                    System.out.print("#");
                } else if (shape.contains(p)) {
                    System.out.print("@");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println("|");
            height--;
        }
        System.out.println("+-------+\n");
    }

    static class Rock {
        boolean[][] shape;

        public Rock(boolean[][] shape) {
            this.shape = shape;
        }

        public int height() {
            return shape.length;
        }

        public int moveLeft(int x, long y, Set<Point> blocked) {
            if (x == 0) {
                return 0;
            }
            for (boolean[] row : shape) {
                for (int i = 0; i < row.length; i++) {
                    if (row[i] && blocked.contains(new Point(x + i - 1, y))) {
                        return x;
                    }
                }
                y--;
            }
            return x - 1;
        }

        public int moveRight(int x, long y, Set<Point> blocked) {
            if (x + shape[0].length == 7) {
                return x;
            }
            for (boolean[] row : shape) {
                for (int i = 0; i < row.length; i++) {
                    if (row[i] && blocked.contains(new Point(x + i + 1, y))) {
                        return x;
                    }
                }
                y--;
            }
            return x + 1;
        }

        public boolean canMoveDown(int x, long y, Set<Point> blocked) {
            if (y - height() == 0) {
                return false;
            }
            y--;
            for (boolean[] row : shape) {
                for (int i = 0; i < row.length; i++) {
                    if (row[i] && blocked.contains(new Point(x + i, y))) {
                        return false;
                    }
                }
                y--;
            }
            return true;
        }

        public void block(int x, long y, Set<Point> blocked) {
            for (boolean[] row : shape) {
                for (int i = 0; i < row.length; i++) {
                    if (row[i]) {
                        blocked.add(new Point(x + i, y));
                    }
                }
                y--;
            }
        }
    }
}