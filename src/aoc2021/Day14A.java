package aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14A {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String template = br.readLine();
        br.readLine();

        String s;
        Pattern pattern = Pattern.compile("([A-Z])([A-Z]) -> ([A-Z])");
        List<Rule> rules = new ArrayList<>();
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

        Node start = new Node(template.charAt(0));
        Node prev = start;
        for (int i = 1; i < template.length(); i++) {
            Node cur = new Node(template.charAt(i));
            prev.setNext(cur);
            prev = cur;
        }

        System.out.println("template = " + template);
        for (int step = 0; step < 40; step++) {
            System.out.println("step "+step);
            rules.forEach(rule -> rule.apply(start));
            Node.insertAll(start);
//            System.out.println("Step " + (step + 1) + ": " + start.getString());
        }

        long[] count = new long[26];
        Node cur = start;
        while (cur != null) {
            count[cur.c - 'A']++;
            cur = cur.next;
        }
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (int i = 0; i < 26; i++) {
            if (count[i] != 0) {
                min = Math.min(min, count[i]);
                max = Math.max(max, count[i]);
                System.out.println(Character.toString('A' + i) + ": " + count[i]);
            }
        }

        System.out.println(max - min);
    }

    private static class Node {
        char c;
        Node next;
        char inserted = '0';

        public Node(char c) {
            this.c = c;
        }

        public void setNext(Node n) {
            next = n;
        }

        public static void insertAll(Node start) {
            Node cur = start;
            while (cur != null) {
                if (cur.inserted != '0') {
                    Node newNode = new Node(cur.inserted);
                    newNode.next = cur.next;
                    cur.next = newNode;
                    cur.inserted = '0';
                }
                cur = cur.next;
            }
        }

        public String getString() {
            StringBuffer sb = new StringBuffer();
            append(sb);
            return sb.toString();
        }

        private void append(StringBuffer sb) {
            sb.append(c);
            if (next != null) {
                next.append(sb);
            }
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

        public void apply(Node start) {
            Node cur = start;
            while (cur.next != null) {
                if (cur.c == c1 && cur.next.c == c2) {
                    cur.inserted = insert;
                }
                cur = cur.next;
            }
        }
    }
}