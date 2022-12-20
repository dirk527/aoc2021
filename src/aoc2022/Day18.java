package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day18 {
    static int min = Integer.MAX_VALUE;
    static int max = Integer.MIN_VALUE;

    public static void main(String[] args) throws IOException {
        // Initialize
        long begin = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("day18.txt"));
        String s;
        List<Cube> cubes = new ArrayList<>();
        HashMap<Cube, Cube> set = new HashMap<>();
        while ((s = br.readLine()) != null) {
            Cube c = new Cube(s.split(","));
            cubes.add(c);
            set.put(c, c);
        }
        for (Cube c : cubes) {
            c.findNeighbors(set);
            min = Math.min(min, c.x);
            min = Math.min(min, c.y);
            min = Math.min(min, c.z);
            max = Math.max(max, c.x);
            max = Math.max(max, c.y);
            max = Math.max(max, c.z);
        }
        min--;
        max++;

        // Part 1: count unconnected
        int pt1 = 0;
        for (Cube c : cubes) {
            pt1 += c.unconnected();
        }
        System.out.println("part 1: " + pt1);
        long pt1t = System.currentTimeMillis() - begin;
        System.out.println(pt1t + "ms");

        // Part 2: dfs from min, min, min
        Cube start = new Cube();
        start.x = min;
        start.y = min;
        start.z = min;
        HashSet<Cube> visited = new HashSet<>();
        LinkedList<Cube> queue = new LinkedList<>();
        queue.add(start);
        int pt2 = 0;
        while (!queue.isEmpty()) {
            Cube cur = queue.removeLast();
            if (visited.contains(cur)) {
                continue;
            }
            visited.add(cur);
            for (Offset o : offsets) {
                Cube tst = new Cube();
                tst.x = cur.x + o.x;
                tst.y = cur.y + o.y;
                tst.z = cur.z + o.z;
                if (!tst.inBounds()) {
                    continue;
                }
                if (set.containsKey(tst)) {
                    pt2++;
                } else if (!visited.contains(tst)) {
                    queue.add(tst);
                }
            }
        }
        System.out.println("part 2: " + pt2);
        long pt2t = System.currentTimeMillis() - begin + pt1t;
        System.out.println(pt2t + "ms");
    }

    record Offset(int idx, int x, int y, int z) {
    }

    static Offset[] offsets = new Offset[]{
            new Offset(0, -1, 0, 0),
            new Offset(1, 1, 0, 0),
            new Offset(2, 0, -1, 0),
            new Offset(3, 0, 1, 0),
            new Offset(4, 0, 0, -1),
            new Offset(5, 0, 0, 1),
    };

    static class Cube {
        int x;
        int y;
        int z;
        Cube[] neighbors = new Cube[6];

        public Cube(String[] in) {
            this.x = Integer.parseInt(in[0]);
            this.y = Integer.parseInt(in[1]);
            this.z = Integer.parseInt(in[2]);
        }

        public Cube() {
        }

        public void findNeighbors(HashMap<Cube, Cube> set) {
            Cube tst = new Cube();
            for (Offset o : offsets) {
                tst.x = x + o.x;
                tst.y = y + o.y;
                tst.z = z + o.z;
                Cube c = set.get(tst);
                if (c != null) {
                    neighbors[o.idx] = c;
                }
            }
        }

        public int unconnected() {
            int sum = 0;
            for (Cube c : neighbors) {
                if (c == null) {
                    sum++;
                }
            }
            return sum;
        }

        public boolean inBounds() {
            return x >= min && x <= max && y >= min && y <= max && z >= min && z <= max;
        }

        @Override
        public String toString() {
            return x + "," + y + "," + z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Cube cube)) return false;

            if (x != cube.x) return false;
            if (y != cube.y) return false;
            return z == cube.z;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }
    }
}