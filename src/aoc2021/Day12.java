package aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day12 {
    private static final String EXAMPLE = "day12-1.txt";
    private static final String REAL = "day12-2.txt";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(REAL));
        String input;
        Map<String, Cave> caves = new HashMap<>();
        while ((input = br.readLine()) != null) {
            String[] names = input.split("-");
            Cave a = caves.computeIfAbsent(names[0], Cave::new);
            Cave b = caves.computeIfAbsent(names[1], Cave::new);
            a.addNeighbour(b);
            b.addNeighbour(a);
        }
        System.out.println(caves);

        long result1 = dfs(caves.get("start"), new HashSet<>(), caves.get("end"));
        System.out.printf("result1: %d\n", result1);
        long result2 = dfs(caves.get("start"), new HashSet<>(), null);
        System.out.printf("result1: %d\n", result2);
    }

    private static long dfs(Cave cur, Set<Cave> visited, Cave smallException) {
        if (cur.name.equals("end")) {
            return 1;
        }

        visited.add(cur);
        long ret = 0L;
        for (Cave cave : cur.neighbours) {
            if (!cave.small || !visited.contains(cave)) {
                ret += dfs(cave, visited, smallException);
            } else if (smallException == null && !cave.name.equals("start")) {
                ret += dfs(cave, visited, cave);
            }
        }
        if (!cur.equals(smallException)) {
            visited.remove(cur);
        }
        return ret;
    }

    record Cave(String name, boolean small, List<Cave> neighbours) {
        Cave(String name) {
            this(name, name.matches("[a-z]+"), new ArrayList<>());
        }

        void addNeighbour(Cave cave) {
            neighbours.add(cave);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Cave cave)) {
                return false;
            }

            return name.equals(cave.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return name + "/" + (small ? "s" : "") + "/" + neighbours.size();
        }
    }
}