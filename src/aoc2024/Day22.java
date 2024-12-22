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
            for (int i = 0; i<2000; i++) {
                cur = next(cur);
            }
            p1 += cur;
        }
        System.out.println(p1);
    }

    public static long next(long cur) {
        /*
        Calculate the result of multiplying the secret number by 64. Then, mix this result into the secret number. Finally, prune the secret number.
        Calculate the result of dividing the secret number by 32. Round the result down to the nearest integer. Then, mix this result into the secret number. Finally, prune the secret number.
        Calculate the result of multiplying the secret number by 2048. Then, mix this result into the secret number. Finally, prune the secret number.
        Each step of the above process involves mixing and pruning:

        To mix a value into the secret number, calculate the bitwise XOR of the given value and the secret number. Then, the secret number becomes the result of that operation. (If the secret number is 42 and you were to mix 15 into the secret number, the secret number would become 37.)
        To prune the secret number, calculate the value of the secret number modulo 16777216. Then, the secret number becomes the result of that operation. (If the secret number is 100000000 and you were to prune the secret number, the secret number would become 16113920.)
         */
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