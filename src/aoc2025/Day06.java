package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day06 {
    public static void main(String[] args) throws IOException {
        // Read file into a grid
        BufferedReader br = new BufferedReader(new FileReader("06-in"));
        String s;
        List<String> grid = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            grid.add(s);
        }

        // Find columns that contain only spaces
        List<Integer> delimCols = new ArrayList<>();
        out:for (int c = 0; c < grid.getFirst().length(); c++) {
            for (String string : grid) {
                if (string.charAt(c) != ' ') {
                    continue out;
                }
            }
            delimCols.add(c);
        }

        char[] operators = grid.getLast().toCharArray();

        // Part 1: parse numbers horizontally
        List<List<Long>> numbers = new ArrayList<>();
        for (int r = 0; r < grid.size() - 1; r++) {
            List<Long> cur = new ArrayList<>();
            int prevDelim = 0;
            for (int delim : delimCols) {
                cur.add(Long.parseLong(grid.get(r).substring(prevDelim, delim).trim()));
                prevDelim = delim;
            }
            cur.add(Long.parseLong(grid.get(r).substring(prevDelim).trim()));
            numbers.add(cur);
        }

        // and process them vertically
        long result1 = 0;
        int idx1 = 0;
        for (char c1 : operators) {
            if (c1 == '+') {
                long res = 0;
                for (int r1 = 0; r1 < numbers.size(); r1++) {
                    res += numbers.get(r1).get(idx1);
                }
                result1 += res;
                idx1++;
            } else if (c1 == '*') {
                long res = 1;
                for (int r1 = 0; r1 < numbers.size(); r1++) {
                    res *= numbers.get(r1).get(idx1);
                }
                result1 += res;
                idx1++;
            }
        }
        System.out.println(result1);

        // Part 2: parse numbers vertically
        List<List<Long>> cephNumbers = new ArrayList<>();
        int maxLen = grid.stream().mapToInt(String::length).max().getAsInt();
        delimCols.add(maxLen);
        int prevDelim = -1;
        for (int delim : delimCols) {
            List<Long> curNumbers = new ArrayList<>();
            for (int c = prevDelim + 1; c < delim; c++) {
                long cur = 0;
                for (int r = 0; r < grid.size() - 1; r++) {
                    if (c < grid.get(r).length()) {
                        char digit = grid.get(r).charAt(c);
                        if (digit != ' ') {
                            cur = cur * 10 + digit - '0';
                        }
                    }
                }
                curNumbers.add(cur);
            }
            cephNumbers.add(curNumbers);
            prevDelim = delim;
        }

        // And process them horizontally, since we were building them that way...
        long result2 = 0;
        int idx = 0;
        for (char c : operators) {
            if (c == '+') {
                long result = 0;
                for (Long l: cephNumbers.get(idx)) {
                    result += l;
                }
                result2 += result;
                idx++;
            } else if (c == '*') {
                long result = 1;
                for (Long l: cephNumbers.get(idx)) {
                    result *= l;
                }
                result2 += result;
                idx++;
            }
        }
        System.out.println(result2);
    }
}