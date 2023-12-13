package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day13 {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("13-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("13-sample.txt"));

        String s;
        List<String> cur = new ArrayList<>();
        List<Part> parts = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            if (s.isEmpty()) {
                parts.add(new Part(cur));
                cur.clear();
            } else {
                cur.add(s);
            }
        }
        parts.add(new Part(cur));

        long sum = 0;
        for (Part p : parts) {
            sum += p.value();
        }
        System.out.println(sum);
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        sum = 0;
        for (Part p : parts) {
            int smudge = p.smudge();
            sum += smudge;
            System.out.println(smudge);
        }
        System.out.println(sum);
        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (endTime - p1Time) / 1000f);
    }

    static class Part {
        boolean[][] rocks;
        long[] rows;
        long[] cols;


        public Part(List<String> lines) {
            rocks = new boolean[lines.size()][lines.get(0).length()];
            for (int row = 0; row < lines.size(); row++) {
                String s = lines.get(row);
                for (int col = 0; col < s.length(); col++) {
                    rocks[row][col] = s.charAt(col) == '#';
                }
            }
            rows = new long[rocks.length];
            for (int i = 0; i < rocks.length; i++) {
                for (int j = rocks[0].length - 1; j >= 0; j--) {
                    rows[i] = rows[i] << 1;
                    if (rocks[i][j]) {
                        rows[i] += 1;
                    }
                }
            }
            cols = new long[rocks[0].length];
            for (int j = 0; j < rocks[0].length; j++) {
                for (int i = rocks.length - 1; i >= 0; i--) {
                    cols[j] = cols[j] << 1;
                    if (rocks[i][j]) {
                        cols[j] += 1;
                    }
                }
            }
//            System.out.println(Arrays.toString(rows));
//            System.out.println(Arrays.toString(cols));
        }

        public int value() {
            int v = value(cols, -1);
            if (v == -1) {
                v = value(rows, -1) * 100;
            }
            if (v < 0) {
                throw new IllegalStateException();
            }
            return v;
        }

        public int smudge() {
//            System.out.println("starting cols");
            int v = smudge(cols, rows.length);
            if (v == -1) {
//                System.out.println("starting rows");
                v = smudge(rows, cols.length) * 100;
            }
            if (v < 0) {
                throw new IllegalStateException();
            }
            return v;
        }

        private static int smudge(long[] test, int nBits) {
            int smudge = 1;
            int ignore = value(test, -1);
            System.out.println(Arrays.toString(test) + " " + nBits + " - " + ignore);
            for (int i = 0; i < nBits; i++) {
                for (int c = 0; c < test.length; c++) {
                    test[c] ^= smudge;
                    System.out.println(smudge + " " + i + " " + c + Arrays.toString(test));
                    int v = value(test, ignore);
                    if (v != -1) {
                        return v;
                    }
                    test[c] ^= smudge;
                }
                smudge <<= 1;
            }

            return -1;
        }

        private static int value(long[] test, int ignore) {
            out:
            for (int i = 1; i < test.length; i++) {
                int max = Math.min(i, test.length - i);
                for (int t = 0; t < max; t++) {
                    if (test[i - t - 1] != test[i + t]) {
                        continue out;
                    }
                }
                if (i != ignore) {
                    return i;
                }
            }

            return -1;
        }
    }
}