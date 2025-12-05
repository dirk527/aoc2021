package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Day05 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("05-in"));
        String s;
        List<Range> fresh = new ArrayList<>();
        while (!(s = br.readLine()).isEmpty()) {
            String[] nums = s.split("-");
            String n1 = nums[0];
            String n2 = nums[1];
            fresh.add(new Range(Long.parseLong(n1), Long.parseLong(n2)));
        }

        // Part 1: just test all the inputs against all the ranges
        long result1 = 0;
        while ((s = br.readLine()) != null) {
            long candidate = Long.parseLong(s);
            if (fresh.stream().anyMatch(r -> r.contains(candidate))) {
                result1++;
            }
        }
        System.out.println(result1);

        // Part 2: sort the ranges by minimum; merge any neighbours that overlap
        fresh.sort(Comparator.comparingLong(r -> r.min));
        for (int idx = 0; idx < fresh.size() - 1; ) {
            Range range1 = fresh.get(idx);
            Range range2 = fresh.get(idx + 1);
            if (range1.contains(range2.min)) {
                range1.max = Math.max(range1.max, range2.max);
                range1.min = Math.min(range1.min, range2.min);
                fresh.remove(idx + 1);
            } else {
                idx++;
            }
        }
        AtomicLong result2 = new AtomicLong();
        fresh.forEach(r -> result2.addAndGet(r.max - r.min));
        System.out.println(result2.get());
    }

    private static class Range {
        long min;  // inclusive
        long max;  // exclusive

        public Range(long min, long maxIncl) {
            this.min = min;
            this.max = maxIncl + 1;
        }

        public boolean contains(long value) {
            return value >= min && value < max;
        }
    }
}