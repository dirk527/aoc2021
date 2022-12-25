package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class Day25 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("day25.txt"));
        String s;
        long sum = 0;
        while ((s = br.readLine()) != null) {
            long normal = fromSnafu(s);
            System.out.println(s + "\t" + normal);
            sum += normal;
        }
        System.out.println("\n " + sum + "\t" + toSnafu(sum));
    }

    static long fromSnafu(String s) {
        long sum = 0;
        for (int i = 0; i < s.length(); i++) {
            sum *= 5;
            sum += switch (s.charAt(i)) {
                case '2' -> 2;
                case '1' -> 1;
                case '0' -> 0;
                case '-' -> -1;
                case '=' -> -2;
                default -> throw new IllegalArgumentException();
            };
        }
        return sum;
    }

    static String toSnafu(long num) {
        LinkedList<Character> chars = new LinkedList<>();
        long carry = 0;
        while (num > 0 || carry > 0) {
            int digit = (int) (num % 5 + carry);
            carry = 0;
            if (digit >= 5) {
                carry++;
                digit = digit - 5;
                assert digit < 5;
            }
            switch (digit) {
                case 0, 1, 2 -> chars.addFirst(Character.forDigit(digit, 10));
                case 3 -> {
                    chars.addFirst('=');
                    carry++;
                }
                case 4 -> {
                    chars.addFirst('-');
                    carry++;
                }
            }
            num = num / 5;
        }
        StringBuilder sb = new StringBuilder();
        chars.forEach(sb::append);
        return sb.toString();
    }
}