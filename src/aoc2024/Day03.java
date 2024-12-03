package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("03-in"));
        Pattern pat1 = Pattern.compile("mul\\(([0-9]+),([0-9]+)\\)");
        Pattern pat2 = Pattern.compile("do\\(\\)|don't\\(\\)|mul\\(([0-9]+),([0-9]+)\\)");
        String s;
        int p1 = 0;
        int p2 = 0;
        boolean enabled = true;
        while ((s = br.readLine()) != null) {
            Matcher mat = pat1.matcher(s);
            while (mat.find()) {
                p1 += Integer.parseInt(mat.group(1)) * Integer.parseInt(mat.group(2));
            }

            mat = pat2.matcher(s);
            while (mat.find()) {
                String match = mat.group(0);
                if (match.equals("do()")) {
                    enabled = true;
                } else if (match.equals("don't()")) {
                    enabled = false;
                } else if (enabled) {
                    p2 += Integer.parseInt(mat.group(1)) * Integer.parseInt(mat.group(2));
                }
            }
        }

        System.out.println(p1);
        System.out.println(p2);
    }
}