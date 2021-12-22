import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14B {
    private static List<Rule> rules = new ArrayList<>();
    private static HashMap<DepthTuple, Counter> cache = new HashMap<>();

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String template = br.readLine();
        br.readLine();

        String s;
        Pattern pattern = Pattern.compile("([A-Z])([A-Z]) -> ([A-Z])");
        while ((s = br.readLine()) != null) {
            Matcher m = pattern.matcher(s);
            if (m.find()) {
                rules.add(new Rule(m.group(1), m.group(2), m.group(3)));
            } else {
                System.out.println("Äh, Mist");
                System.out.println(s);
                System.exit(1);
            }
        }

        Counter sum = new Counter();
        for (int i = 0; i < template.length() - 1; i++) {
            Node left = new Node(template.charAt(i), 0);
            Node right = new Node(template.charAt(i + 1), 0);
            Counter one = new Counter();
            left.descend(40, right, one);
            sum.countAll(one);
        }
        sum.count(template.charAt(template.length() - 1));
        System.out.println();

        long[] minmax = sum.findMinMax();
        long min = minmax[0];
        long max = minmax[1];

        long end = System.currentTimeMillis();
        System.out.println("\ntime: " + (end - start) + "ms\n");

        System.out.println(max - min);
    }

    private static class Node {
        char c;
        int level;

        public Node(char c, int level) {
            this.c = c;
            this.level = level;
        }

        public void descend(int maxLevel, Node right, Counter counter) {
            int opLevel = Math.max(level, right.level);
            if (opLevel %10==0) {
                System.out.println(new Date() + " " + opLevel + " " + Character.toString(c) + Character.toString(right.c));
            }

            if (opLevel == maxLevel) {
                counter.count(c);
                return;
            }
            for (Rule rule : rules) {
                if (rule.matches(this, right)) {
//                    System.out.println(level + "/" + right.level + " " + Character.toString(c) + Character.toString(right.c) + " matches " + rule);
                    DepthTuple key = new DepthTuple(c, right.c, opLevel + 1);
                    Counter sub = cache.get(key);
                    if (sub == null) {
                        sub = new Counter();
                        Node down = new Node(rule.getChar(), opLevel + 1);
                        descend(maxLevel, down, sub);
                        down.descend(maxLevel, right, sub);
                        cache.put(key, sub);
                    }
                    counter.countAll(sub);
                    return;
                }
            }
            counter.count(c);
        }
    }

    private static class Rule {
        char c1, c2;
        char insert;

        public Rule(String g1, String g2, String g3) {
            c1 = g1.charAt(0);
            c2 = g2.charAt(0);
            insert = g3.charAt(0);
        }

        public boolean matches(Node left, Node right) {
            return left.c == c1 && right.c == c2;
        }

        public char getChar() {
            return insert;
        }

        public String toString() {
            return Character.toString(c1) + Character.toString(c2) + " -> " + Character.toString(insert);
        }
    }

    private static class Counter {
        long[] count = new long[26];

        public void count(char c) {
            count[c - 'A']++;
        }

        public void countAll(Counter o) {
            for (int i = 0; i < count.length; i++) {
                count[i] += o.count[i];
            }
        }

        public long[] findMinMax() {
            long min = Long.MAX_VALUE;
            long max = Long.MIN_VALUE;
            for (int i = 0; i < 26; i++) {
                if (count[i] != 0) {
                    min = Math.min(min, count[i]);
                    max = Math.max(max, count[i]);
                    System.out.println(Character.toString('A' + i) + ": " + count[i]);
                }
            }
            return new long[]{min, max};
        }
    }

    private static class DepthTuple {
        char l, r;
        int level;

        public DepthTuple(char l, char r, int level) {
            this.l = l;
            this.r = r;
            this.level = level;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DepthTuple that = (DepthTuple) o;
            return l == that.l && r == that.r && level == that.level;
        }

        @Override
        public int hashCode() {
            return Objects.hash(l, r, level);
        }
    }
}