package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day06 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("06-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("06-sample.txt"));

        String tstr = br.readLine().substring("time: ".length());
        String dstr = br.readLine().substring("distance: ".length());

        List<Integer> ts = new ArrayList<>();
        for (String s : tstr.split(" +")) {
            if (!s.isEmpty())
                ts.add(Integer.parseInt(s));
        }
        List<Integer> ds = new ArrayList<>();
        for (String s : dstr.split(" +")) {
            if (!s.isEmpty())
                ds.add(Integer.parseInt(s));
        }

        long p1 = 1;
        for (int i = 0; i < ts.size(); i++) {
            int sum = 0;
            Integer time = ts.get(i);
            Integer record = ds.get(i);
            for (int hold = 1; hold < time; hold++) {
                long dist = calculate(time, hold);
                if (dist > record) {
                    sum++;
                }
            }
            p1 *= sum;
        }
        System.out.println(p1);

        long time = Long.parseLong(tstr.replaceAll(" ", ""));
        long record = Long.parseLong(dstr.replaceAll(" ", ""));

        // find one that's over the record - this is not guaranteed to work, but did for my input
        long over = 1;
        while (calculate(time, over) <= record) {
            over *= 2;
        }

        // find first that's not
        long left = 1;
        long right = over;
        while (left + 1 != right) {
            long mid = (right + left) / 2;
            long val = calculate(time, mid);
            if (val <= record) {
                left = mid;
            } else {
                right = mid;
            }
        }
        long first = right;

        // find first that's again under the record
        left = over;
        right = time;
        while (left + 1 != right) {
            long mid = (right + left) / 2;
            long val = calculate(time, mid);
            if (val <= record) {
                right = mid;
            } else {
                left = mid;
            }
        }
        long last = left;

        System.out.println(last - first + 1);
    }

    private static long calculate(long time, long hold) {
        return (time - hold) * hold;
    }
}