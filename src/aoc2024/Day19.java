package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Day19 {

    private static Node head;
    private static HashMap<String, Long> cache = new HashMap<>();

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        head = new Node(null);
        BufferedReader br = new BufferedReader(new FileReader("19-in"));
        String[] towels = br.readLine().split(", ");
        for (String towel : towels) {
            head.addPossible(towel);
        }
//        head.print(0);

        List<String> patterns = new ArrayList<>();
        String s;
        while ((s = br.readLine()) != null) {
            patterns.add(s);
        }

        long p1 = 0;
        long p2 = 0;
        for (String pattern : patterns) {
            long numPoss = head.canMatch(pattern);
            if (numPoss > 0) {
                p1++;
            }
//            System.out.printf("%s: %d%n", pattern, numPoss);
            p2 += numPoss;
        }
        System.out.println(p1);
        System.out.println(p2);
    }

    //  0 white (w), 1 blue (u), 2 black (b), 3 red (r), or 4 green (g)
    static class Node {
        private Node[] out = new Node[5];
        private final Character c;
        private boolean possible;

        public Node(Character c) {
            this.c = c;
        }

        public void addPossible(String towel) {
            if (towel.isEmpty()) {
                possible = true;
            } else {
                char first = towel.charAt(0);
                int index = convert(first);
                if (out[index] == null) {
                    out[index] = new Node(first);
                }
                out[index].addPossible(towel.substring(1));
            }
        }

        private int convert(char c) {
            return switch (c) {
                case 'w' -> 0;
                case 'u' -> 1;
                case 'b' -> 2;
                case 'r' -> 3;
                case 'g' -> 4;
                default -> throw new IllegalArgumentException();
            };
        }

        public long canMatch(String pattern) {
            if (c == null) {
                if (cache.containsKey(pattern)) {
                    return cache.get(pattern);
                }
            }
            long result = 0;
            if (pattern.isEmpty()) {
                result = possible ? 1 : 0;
            } else {
                if (possible) {
                    result += head.canMatch(pattern);
                }
                int index = convert(pattern.charAt(0));
                if (out[index] != null) {
                    result += out[index].canMatch(pattern.substring(1));
                }
            }
            if (c == null) {
                cache.put(pattern, result);
            }
            return result;
        }

        public void print(int indent) {
            System.out.print(" ".repeat(indent));
            System.out.printf("%s %s%n", c, (possible) ? "+" : "-");
            for (Node node : out) {
                if (node != null) {
                    node.print(indent + 2);
                }
            }
        }
    }
}