package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;

import static aoc2022.Day14.Content.*;

public class Day14 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("day14.txt"));
        String s;
        AbstractList<Line> lines = new ArrayList<>();
        int xmin = Integer.MAX_VALUE;
        int xmax = Integer.MIN_VALUE;
        int ymax = Integer.MIN_VALUE;
        while ((s = br.readLine()) != null) {
            String[] coords = s.split(" -> ");
            for (int i = 1; i < coords.length; i++) {
                String[] s1 = coords[i - 1].split(",");
                String[] s2 = coords[i].split(",");
                int x1 = Integer.parseInt(s1[0]);
                int y1 = Integer.parseInt(s1[1]);
                int x2 = Integer.parseInt(s2[0]);
                int y2 = Integer.parseInt(s2[1]);
                lines.add(new Line(x1, y1, x2, y2));
                xmin = Math.min(xmin, x1);
                xmin = Math.min(xmin, x2);
                xmax = Math.max(xmax, x1);
                xmax = Math.max(xmax, x2);
                ymax = Math.max(ymax, y1);
                ymax = Math.max(ymax, y2);
            }
        }
        // inelegant hack for part 2
        xmin -= 130;
        xmax += 160;
        int width = xmax - xmin + 1;
        int height = ymax + 1;
        System.out.println(xmin + " " + xmax + " " + ymax);
        Content[][] cave = new Content[height + 2][width];
        for (int y = 0; y < height + 1; y++) {
            for (int x = 0; x < width; x++) {
                cave[y][x] = AIR;
            }
        }
        for (int x = 0; x < width; x++) {
            cave[height + 1][x] = ROCK;
        }

        for (Line l : lines) {
            l.mark(cave, xmin);
        }

        int part1 = 0;
        int part2 = 0;
        int print = 5000;
        out:
        for (int round = 0;;round++) {
            int x = 500 - xmin;
            int y = 0;
            System.out.println("\nround " + round);
            if (round % print == 0) {
                printCave(cave);
            }
            boolean rest = false;
            while (!rest) {
                if (cave[y][x] != AIR) {
                    printCave(cave);
                    part2 = round;
                    break out;
                }
                if (y + 1 > ymax && part1 == 0) {
                    printCave(cave);
                    part1 = round;
                }
                if (y + 1 > ymax + 3) {
                    if (round % print != 0) {
                        printCave(cave);
                    }
                    System.out.println("part 1: " + part1);
                    System.out.println("part 2: " + round);
                    break out;
                }

                if (cave[y + 1][x] == AIR) {
                    y ++;
                } else if (cave[y+1][x-1] == AIR) {
                    y++;
                    x--;
                } else if (cave[y+1][x+1] == AIR) {
                    y++;
                    x++;
                } else {
                    cave[y][x] = SAND;
                    rest = true;
                }
            }
        }
        System.out.println(part1);
        System.out.println(part2);
    }

    private static void printCave(Content[][] cave) {
        for (Content[] contents : cave) {
            for (int x = 0; x < cave[0].length; x++) {
                System.out.print(contents[x]);
            }
            System.out.println();
        }
    }

    enum Content {
        AIR("."), ROCK("#"), SAND("o");
        private final String s;

        Content(String s) {
            this.s = s;
        }

        public String toString() {
            return s;
        }
    }

    record Line(int x1, int y1, int x2, int y2) {
        public void mark(Content[][] cave, int xmin) {
            if (x1 == x2) {
                int start = Math.min(y1, y2);
                int end = Math.max(y1, y2);
                for (int y = start; y <= end; y++) {
                    cave[y][x1 - xmin] = ROCK;
                }
            } else if (y1 == y2) {
                int start = Math.min(x1, x2) - xmin;
                int end = Math.max(x1, x2) - xmin;
                for (int x = start; x <= end; x++) {
                    cave[y1][x] = ROCK;
                }
            } else {
                assert (false);
            }
        }
    }
}