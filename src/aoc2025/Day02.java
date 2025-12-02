package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class Day02 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("02-in"));

        long result1 = 0;
        long result2 = 0;
        String s = br.readLine();
        for (String input : s.split(",")) {
            String[] nums = input.split("-");
            String n1 = nums[0];
            String n2 = nums[1];
            Range range = new Range(Long.parseLong(n1), Long.parseLong(n2));
            if (n1.charAt(0) == '0' || n2.charAt(0) == '0') {
                System.out.println("*** error " + n1 + " " + n2);
            }

            // part 1
            // inspected the input: the number of digits always differs by 0 or 1
            // so this is good enough: start from the first half (rounded down) of the first number string
            long start = n1.length() == 1 ? 1 : Long.parseLong(n1.substring(0, n1.length() / 2));
            // to the first half (rounded up) of the second number string
            long end = Long.parseLong(n2.substring(0, n2.length() / 2 + n2.length() % 2));
//            System.out.printf("%25s %6d %6d%n", input, start, end);
            for (long patternCandidate = start; patternCandidate <= end; patternCandidate++) {
                long mult = (long) Math.pow(10, Math.floor(Math.log10(patternCandidate) + 1));
                long candidate = patternCandidate * mult + patternCandidate;
                if (range.contains(candidate)) {
                    result1 += candidate;
                }
            }

            // part 2
            // try all patterns of a length 1..maxDigits
            int maxDigits = n2.length() / 2;
            // we might try the same number multiple times, e.g. pattern "3" * 4 = "3333" which is the same as
            // "33" * 2 = "3333". So just use a set.
            Set<Long> hits = new TreeSet<>();
            for (int nDigits = 1; nDigits <= maxDigits; nDigits++) {
                long mult = (long) Math.pow(10, nDigits);
                long firstPat = (long) Math.pow(10, nDigits - 1);
                // try all patterns of length nDigits, e.g. for nDigits = 2: firstPat = 10 and mult = 100.
                for (long pattern = firstPat; pattern < mult; pattern++) {
                    long cand = pattern; // start with pattern, because the puzzle says it must be repeated
                    for (int i = 1; i < n2.length() / nDigits; i++) {
                        // add pattern to the end only as long as the total length is less than the second number
                        cand = cand * mult + pattern;
                        if (range.contains(cand)) {
                            hits.add(cand);
                        }
                    }
                }
            }
            result2 += hits.stream().reduce(0L, Long::sum);
        }

        System.out.println(result1);
        System.out.println(result2);
    }

    private static class Range {
        long min;
        long max;

        public Range(long min, long max) {
            this.min = min;
            this.max = max;
        }

        public boolean contains(long value) {
            return value >= min && value <= max;
        }
    }
}