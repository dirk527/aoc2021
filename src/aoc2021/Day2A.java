package aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day2A {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        int horiz = 0;
        int depth = 0;
        String s;
        while ((s = br.readLine()) != null) {
            String[] tok = s.split(" ");
            int arg = Integer.valueOf(tok[1]);
            switch (tok[0]) {
                case "forward" -> horiz += arg;
                case "down" -> depth += arg;
                case "up" -> depth -= arg;
            }
//            System.out.println("horiz = " + horiz + "; depth = " + depth);
        }
        System.out.println(horiz * depth);
    }
}
