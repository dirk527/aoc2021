package aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.PriorityQueue;

public class Day15 {

    private static int size;
    private static Vertex[][] vals;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String input;
        Vertex[][] tile = null;
        int row = 0;
        while ((input = br.readLine()) != null) {
            if (row == 0) {
                tile = new Vertex[input.length()][input.length()];
            }
            for (int i = 0; i < input.length(); i++) {
                tile[row][i] = new Vertex(row, i, Integer.parseInt(input, i, i + 1, 10));
            }
            row++;
        }

        int tileSize = tile.length;
        size = tileSize * 5;
        vals = new Vertex[size][size];
        copy(tile, vals, 0, 0, 0);
        for (int i = 1; i < 5; i++) {
            copy(tile, vals, 0, i, i);
        }
        for (int i = 1; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                copy(tile, vals, i, j, i + j);
            }
        }

        PriorityQueue<Vertex> queue = new PriorityQueue<>();
        vals[0][0].distance = 0;
        queue.add(vals[0][0]);

        while (true) {
            Vertex cur = queue.remove();
            cur.handled = true;
//            System.out.println(cur.row + "/" + cur.col + " " + cur.distance + " " + queue.size());

            if (cur == vals[size - 1][size - 1]) {
                break;
            }

            visit(cur, cur.row, cur.col - 1, queue);
            visit(cur, cur.row, cur.col + 1, queue);
            visit(cur, cur.row - 1, cur.col, queue);
            visit(cur, cur.row + 1, cur.col, queue);
        }

        long end = System.currentTimeMillis();
        System.out.println("\ntime: " + (end - start) + "ms\n");

        System.out.println(vals[size - 1][size - 1].distance);
    }

    private static void visit(Vertex cur, int row, int col, PriorityQueue<Vertex> queue) {
        if (row == -1 || col == -1 || row == size || col == size) {
            return;
        }
        Vertex neighbor = vals[row][col];
        if (neighbor.handled) {
            return;
        }
        int pathLen = cur.distance + neighbor.weight;
        if (pathLen < neighbor.distance) {
            neighbor.distance = pathLen;
            queue.remove(neighbor);
            queue.add(neighbor);
        }
    }

    private static void copy(Vertex[][] tile, Vertex[][] vals, int row, int col, int add) {
        int ts = tile.length;
        for (int i = 0; i < ts; i++) {
            for (int j = 0; j < ts; j++) {
                Vertex src = tile[i][j];
                int newWeight = src.weight + add;
                if (newWeight > 9) {
                    newWeight -= 9;
                }
                int rr = row * ts + i;
                int cc = col * ts + j;
                vals[rr][cc] = new Vertex(rr, cc, newWeight);
            }
        }
    }

    private static class Vertex implements Comparable<Vertex> {
        int weight;
        int distance;
        int row;
        int col;
        boolean handled;

        public Vertex(int row, int col, int weight) {
            this.row = row;
            this.col = col;
            this.weight = weight;
            distance = Integer.MAX_VALUE;
        }

        @Override
        public int compareTo(Vertex o) {
            return Integer.compare(distance, o.distance);
        }
    }
}
