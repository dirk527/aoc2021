package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day01 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("01-in"));

        int pos = 50;
        int result1 = 0;
        int result2 = 0;
        String s;
        while ((s = br.readLine()) != null) {
            int posBefore = pos;
            int count = Integer.valueOf(s.substring(1));
            result2 += (count / 100);
            count = count % 100;
            if (s.charAt(0) == 'L') {
                count = count * -1;
            }
            pos += count;
            result2 += Math.abs(pos / 100);  // full rotations
            if (pos == 0) {
                result2++;
            }
            pos = pos % 100;
            if (pos == 0) {
                result1++;
            }
            if (pos < 0 && posBefore > 0) {
                result2++;
            }
            if (pos > 0 && posBefore < 0) {
                result2++;
            }
//            System.out.printf("%5s %4d %4d %4d %5d\n", s, count, pos, result1, result2);
        }
        System.out.println(result1);
        System.out.println(result2);
    }

}