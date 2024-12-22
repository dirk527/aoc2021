package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day22 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("22-in"));

        String s;
        List<Integer> seeds = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            seeds.add(Integer.parseInt(s));
        }

        long p1 = 0;
        for (int seed : seeds) {
            long cur = seed;
            for (int i = 0; i < 2000; i++) {
                cur = next(cur);
            }
            p1 += cur;
        }
        System.out.println(p1);

        long best = Long.MIN_VALUE;
        int ccc = 0;
        for (int first = -9; first <= 9; first++) {
            // -9 -> 0 .. 9
            // -8 -> -1 .. 9
            // -3 -> -6 ..9
            // 0 -> -9 .. 9
            // 1 -> -9 .. 8
            for (int second = Math.max(-9, -9 - first); second <= Math.min(9, 9 - first); second++) {
                int total = first + second;
                for (int third = Math.max(-9, -9 - total); third <= Math.min(9, 9 - total); third++) {
                    total = first + second + third;
                    for (int fourth = Math.max(-9, -9 - total); fourth <= Math.min(9, 9 - total); fourth++) {
                        if (++ccc % 500 == 0) {
                            System.out.println("ccc = " + ccc + "; best = " + best);
                        }
                        MonkeySeller monkey = new MonkeySeller(first, second, third, fourth);
                        long bananas = countBananas(seeds, monkey);
                        best = Math.max(best, bananas);
                    }
                }
            }
        }
        System.out.println(best);
        System.exit(0);

        MonkeySeller monkey = new MonkeySeller(-2, 1, -1, 3);
        long bananas = countBananas(seeds, monkey);
        System.out.println(bananas);
    }

    private static long countBananas(List<Integer> seeds, MonkeySeller monkey) {
        long bananas = 0;
        for (int seed : seeds) {
            monkey.reset();
            long cur = seed;
            for (int i = 0; i < 2001; i++) {
                long next = next(cur);
                int diff = (int) ((next % 10L) - (cur % 10L));
                if (monkey.checkNum(diff)) {
                    int p3 = (int) (next % 10L);
//                    System.out.printf("%d hit %d%n", seed, p3);
                    bananas += p3;
                    break;
                }
                cur = next;
            }
        }
        return bananas;
    }

    static class MonkeySeller {
        int[] sequence;
        boolean[] hits;

        public MonkeySeller(int one, int two, int three, int four) {
            sequence = new int[]{one, two, three, four};
            hits = new boolean[sequence.length];
        }

        public boolean checkNum(int num) {
            for (int i = 3; i >= 0; i--) {
                hits[i] = num == sequence[i] && (i == 0 || hits[i - 1]);
            }
            return hits[3];
        }

        public void reset() {
            for (int i = 3; i >= 0; i--) {
                hits[i] = false;
            }
        }
    }

    public static long next(long cur) {
        long tmp = cur * 64L;
        cur = cur ^ tmp; // mix
        cur = cur % 16777216; // prune
        tmp = cur / 32;
        cur = cur ^ tmp; // mix
        cur = cur % 16777216; // prune
        tmp = cur * 2048;
        cur = cur ^ tmp; // mix
        cur = cur % 16777216; // prune
        return cur;
    }
}