package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day09 {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("09-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("09-sample.txt"));

        String s;
        List<Node> firstNodes = new ArrayList<>();
        List<Node> lastNodes = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            Node prev = null;
            for (String n : s.split(" ")) {
                Node nn = new Node(Long.parseLong(n), prev);
                if (prev == null) {
                    firstNodes.add(nn);
                }
                prev = nn;
            }
            lastNodes.add(prev);
        }

        long p2 = 0;
        for (Node n : firstNodes) {
            n.build(null);
            p2 += n.predictLeft();
        }
        long p1 = 0;
        for (Node n : lastNodes) {
            p1 += n.predictRight();
        }

        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (endTime - startTime) / 1000f);
        System.out.println(p1);
        System.out.println(p2);
    }

    private static class Node {
        private long value;
        private Node left;
        private Node right;
        private Node bottom;

        public Node(long value, Node left) {
            this.value = value;
            this.left = left;
            if (left != null) {
                left.right = this;
            }
        }

        public void build(Node lowerLeft) {
            if (right != null) {
                bottom = new Node(right.value - value, lowerLeft);
                right.build(bottom);
                if (left == null) {
                    boolean allZeros = true;
                    Node n = bottom;
                    while (n != null) {
                        if (n.value != 0) {
                            allZeros = false;
                            break;
                        }
                        n = n.right;
                    }
                    if (!allZeros) {
                        bottom.build(null);
                    }
                }
            }
        }

        public long predictRight() {
            if (right != null) {
                throw new IllegalStateException();
            }
            if (left.bottom == null) {
                return 0;
            }
            return value + left.bottom.predictRight();
        }

        public long predictLeft() {
            if (left != null) {
                throw new IllegalStateException();
            }
            if (bottom == null) {
                return 0;
            }
            return value - bottom.predictLeft();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            Node n = right;
            while (n != null) {
                sb.append(" ");
                sb.append(n.value);
                n = n.right;
            }
            if (bottom != null) {
                sb.append("\n");
                sb.append(bottom);
            }
            return sb.toString();
        }
    }
}