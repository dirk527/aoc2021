package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day01 {
    private static final String[] NUMBERS = new String[]{"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
    private static final int INVALID = -1;

    public static void main(String[] args) throws IOException {
//        BufferedReader br = new BufferedReader(new FileReader("01-sample.txt"));
        BufferedReader br = new BufferedReader(new FileReader("01-input.txt"));

        String s;
        int sum = 0;
        while ((s = br.readLine()) != null) {
            s = s.replaceAll("[^0-9]", "");
            String two = s.charAt(0) + "" + s.charAt(s.length() - 1);
            sum += Integer.parseInt(two);
        }
        System.out.println(sum);

        sum = 0;
        br = new BufferedReader(new FileReader("01-input.txt"));
        while ((s = br.readLine()) != null) {
            int n1 = INVALID;
            int n2 = INVALID;
            for (int i = 0; n1 == INVALID; i++) {
                n1 = numberAt(s, i);
            }
            for (int i = s.length() - 1; n2 == INVALID ; i--) {
                n2 = numberAt(s, i);
            }
            sum += n1 * 10 + n2;
        }
        System.out.println(sum);
    }

    private static int numberAt(String s, int idx) {
        char c = s.charAt(idx);
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        for (int i = 0; i < NUMBERS.length; i++) {
            String number = NUMBERS[i];
            if (s.length() >= number.length() + idx && s.startsWith(number, idx)) {
                return i + 1;
            }
        }
        return INVALID;
    }
}
