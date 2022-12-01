package aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day6 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String input = br.readLine();
        String[] numbers = input.split(",");

        long[] count = new long[9];
        for (String s : numbers) {
            count[Integer.parseInt(s)]++;
        }

        for (int day = 0; day < 256; day++) {
            long zero = count[0];
            for (int i = 0; i < 8; i++) {
                count[i] = count[i + 1];
            }
            count[6] += zero;
            count[8] = zero;
        }

        long sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += count[i];
        }
        System.out.println(sum);
    }
}
