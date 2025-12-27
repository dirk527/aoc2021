package aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class Day11 {
    private static final String EXAMPLE = "day11-1.txt";
    private static final String REAL = "day11-2.txt";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(REAL));
        String input;
        Octopus[][] octopi = new Octopus[10][10];
        int row = 0;
        while ((input = br.readLine()) != null) {
            int col = 0;
            for (char c : input.toCharArray()) {
                octopi[row][col++] = new Octopus(c - '0');
            }
            row++;
        }

        System.out.println("start: ");
        debug(octopi);
        int total = 0;
        for (int step = 1; ; step++) {
            int flashes = step(octopi);
            total += flashes;
            if (step == 100) {
                System.out.println("part1: " + total);
            }
            if (flashes == 100) {
                System.out.println("part2: " + step);
                break;
            }
//            System.out.printf("step %d flashes %d total %d:\n", step, flashes, total);
//            debug(octopi);
        }
    }

    private static int step(Octopus[][] octopi) {
        HashSet<Octopus> flashed = new HashSet<>();
        HashSet<Octopus> flash = new HashSet<>();
        for (Octopus[] octopus : octopi) {
            for (Octopus value : octopus) {
                if (value.addOne()) {
                    flash.add(value);
                }
            }
        }

        int flashCount = 0;
        while (!flash.isEmpty()) {
            flashCount += flash.size();
            HashSet<Octopus> nextFlash = new HashSet<>();
            for (int row = 0; row < 10; row++) {
                for (int col = 0; col < 10; col++) {
                    Octopus octopus = octopi[row][col];
                    if (flash.contains(octopus)) {
                        for (Direction direction : Direction.values()) {
                            int rr = row + direction.r;
                            int cc = col + direction.c;
                            if (rr >= 0 && rr < 10 && cc >= 0 && cc < 10) {
                                Octopus neighbour = octopi[rr][cc];
                                if (!flashed.contains(neighbour) && !flash.contains(neighbour) && !nextFlash.contains(neighbour)) {
                                    if (neighbour.addOne()) {
                                        nextFlash.add(neighbour);
                                    }
                                }
                            }
                        }
                        flashed.add(octopus);
                    }
                }
            }
            flash = nextFlash;
        }

        flashed.forEach(Octopus::reset);

        return flashCount;
    }

    public static void debug(Octopus[][] octopi) {
        for (Octopus[] octoRow : octopi) {
            for (Octopus octopus : octoRow) {
                System.out.print(octopus.getLevel());
            }
            System.out.println();
        }
        System.out.println();
    }

    enum Direction {
        UL(-1, -1),
        U(0, -1),
        UR(1, -1),
        R(1, 0),
        DR(1, 1),
        D(0, 1),
        DL(-1, 1),
        L(-1, 0);

        final int c;
        final int r;

        Direction(int c, int r) {
            this.c = c;
            this.r = r;
        }
    }

    static class Octopus {
        private int level;

        public Octopus(int initial) {
            level = initial;
        }

        public int getLevel() {
            return level;
        }

        void reset() {
            level = 0;
        }

        boolean addOne() {
            level++;
            return level > 9;
        }
    }
}