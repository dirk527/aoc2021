package aoc2021;

import java.io.*;

public class Day1A {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String s;
        int prev = Integer.MAX_VALUE;
        int inc = 0;
        while ((s = br.readLine()) != null) {
            int cur = Integer.valueOf(s);
            if (cur > prev) {
                inc++;
            }
            prev = cur;
        }
        System.out.println(inc);
    }
}
