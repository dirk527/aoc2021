package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day20 {
    public static void main(String[] args) throws IOException {
        // Initialize
        long begin = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("day20.txt"));
        String s;
        List<Node> nodes = new ArrayList<>();
        Node zeroNode = null;
        while ((s = br.readLine()) != null) {
            Node node = new Node(Integer.parseInt(s));
            nodes.add(node);
            if (node.val == 0) {
                zeroNode = node;
            }
        }
        adjustLinks(nodes);

        // Mix
        mix(nodes);

        // find the 1000th / 2000th / 3000th numbers to add
        System.out.println("part 1: " + getSum(zeroNode));
        long pt1 = System.currentTimeMillis() - begin;
        System.out.println(pt1 + "ms");

        // Part 2: first reset, add encryption key
        adjustLinks(nodes);
        for (Node n : nodes) {
            n.val = Math.multiplyExact(n.val, 811589153L);
        }
        for (int i = 0; i < 10; i++) {
            print(zeroNode);
            mix(nodes);
        }
        print(zeroNode);
        System.out.println("part 2: " + getSum(zeroNode));
        long pt2 = System.currentTimeMillis() - begin + pt1;
        System.out.println(pt2 + "ms");
    }

    private static long getSum(Node zeroNode) {
        long sum = 0;
        Node node = zeroNode;
        for (int i = 0; i <= 3000; i++) {
            if (i % 1000 == 0) {
                sum = Math.addExact(sum, node.val);
            }
            node = node.next;
        }
        return sum;
    }

    private static void mix(List<Node> nodes) {
        for (Node n : nodes) {
            long effectiveVal = n.val % (nodes.size() - 1);
            if (effectiveVal == 0) {
                continue;
            }

            // remove
            n.prev.next = n.next;
            n.next.prev = n.prev;

            // find new prev
            Node newPrev = n.prev;
            if (n.val < 0) {
                for (long i = 0; i > effectiveVal; i--) {
                    newPrev = newPrev.prev;
                }
            } else {
                for (long i = 0; i < effectiveVal; i++) {
                    newPrev = newPrev.next;
                }
            }

            // change links
            n.next = newPrev.next;
            n.prev = newPrev;
            n.next.prev = n;
            n.prev.next = n;
        }
    }

    private static void adjustLinks(List<Node> nodes) {
        nodes.get(0).prev = nodes.get(nodes.size() - 1);
        for (int i = 1; i < nodes.size(); i++) {
            nodes.get(i).prev = nodes.get(i - 1);
        }
        for (int i = 0; i < nodes.size() - 1; i++) {
            nodes.get(i).next = nodes.get(i + 1);
        }
        nodes.get(nodes.size() - 1).next = nodes.get(0);
    }

    static void print(Node start) {
        System.out.print(start);
        Node cur = start.next;
        while (cur != start) {
            System.out.print(cur);
            cur = cur.next;
        }
        System.out.println();
    }

    static class Node {
        Node prev;
        long val;
        Node next;

        public Node(long val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return /*"(" + prev.val + ")" + */val + " ";
        }
    }
}