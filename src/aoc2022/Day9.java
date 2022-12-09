package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Day9 {
    public static void main(String[] args) throws IOException {
        String filename = "day9.txt";

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String s;
        // or 2 instead of 10 for part 1
        Pos[] part2 = new Pos[10];
        for (int i = 0; i < part2.length; i++) {
            part2[i] = new Pos(0, 0);
        }
        Set<String> visited = new HashSet<>();
        visited.add(part2[part2.length - 1].toString());
        while ((s = br.readLine()) != null) {
            String[] comps = s.split(" ");
            int count = Integer.parseInt(comps[1]);
            for (int i = 0; i < count; i++) {
                switch (comps[0]) {
                    case "R" -> part2[0].x++;
                    case "L" -> part2[0].x--;
                    case "U" -> part2[0].y++;
                    case "D" -> part2[0].y--;
                    default -> throw new IllegalArgumentException("unknown " + comps[0]);
                }
                for (int j = 1; j < part2.length; j++) {
                    part2[j].follow(part2[j - 1]);
                }
                visited.add(part2[part2.length - 1].toString());
            }
        }
        System.out.println(visited.size());
    }

    private static class Pos {
        int x;
        int y;

        public Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void follow(Pos h) {
            int xDist = Math.abs(x - h.x);
            int yDist = Math.abs(y - h.y);
            if (xDist <= 1 && yDist <= 1) {
                return;
            }
            x += (h.x - x) / 2 + (h.x - x) % 2;
            y += (h.y - y) / 2 + (h.y - y) % 2;
        }

        @Override
        public String toString() {
            return x + "," + y;
        }
    }
}