package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day23 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("23-in"));

        String s;
        HashMap<String, Computer> all = new HashMap<>();
        while ((s = br.readLine()) != null) {
            String[] tokens = s.split("-");
            Computer c1 = all.computeIfAbsent(tokens[0], Computer::new);
            Computer c2 = all.computeIfAbsent(tokens[1], Computer::new);
            c1.addNeighbour(c2);
        }
        TreeSet<Computer> computers = new TreeSet<>(all.values());
        computers.forEach(c -> Collections.sort(c.neighbours));
        Set<ThreeClique> found = new HashSet<>();
        for (Computer c : computers) {
            if (c.neighbours.size() > 2) {
                for (int i = 0; i < c.neighbours.size(); i++) {
                    for (int j = i + 1; j < c.neighbours.size(); j++) {
                        Computer n1 = c.neighbours.get(i);
                        Computer n2 = c.neighbours.get(j);
                        if (n1.neighbours.contains(n2) && n2.neighbours.contains(n1)) {
                            found.add(new ThreeClique(c, n1, n2));
                        }
                    }
                }
            }
        }
        found.removeIf(ThreeClique::inCorrectOrder);
        found.removeIf(ThreeClique::noT);
        System.out.println(found);
        System.out.println(found.size());


    }

    record ThreeClique(Computer c1, Computer c2, Computer c3) {
        public boolean inCorrectOrder() {
            return c1.compareTo(c2) >= 0 || c2.compareTo(c3) >= 0;
        }

        public boolean noT() {
            return !c1.name.startsWith("t") && !c2.name.startsWith("t") && !c3.name.startsWith("t");
        }
    }

    static class Computer implements Comparable<Computer> {
        private String name;
        private List<Computer> neighbours;

        public Computer(String name) {
            this.name = name;
            neighbours = new ArrayList<>();
        }

        public void addNeighbour(Computer neighbour) {
            neighbours.add(neighbour);
            neighbour.neighbours.add(this);
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Computer computer)) return false;

            return Objects.equals(name, computer.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public int compareTo(Computer o) {
            return name.compareTo(o.name);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}