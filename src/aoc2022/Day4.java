package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day4 {
    public static void main(String[] args) throws IOException {
        List<Pair> input = read("day4.txt");
        int contain = 0;
        int overlap = 0;
        for (Pair p : input) {
            if (p.one.contains(p.two) || p.two.contains(p.one)) {
                contain++;
            }
            if (p.overlap()) {
                overlap++;
            }
        }
        System.out.println(contain);
        System.out.println(overlap);
    }

    private static List<Pair> read(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        List<Pair> ret = new ArrayList<>();
        String s;
        Pattern pat = Pattern.compile("(\\d+)-(\\d+),(\\d+)-(\\d+)");
        while ((s = br.readLine()) != null) {
            Matcher m = pat.matcher(s);
            m.find();
            ret.add(new Pair(new Range(m.group(1), m.group(2)), new Range(m.group(3), m.group(4))));
        }
        return ret;
    }

    private record Range (int min, int max){
        public Range(String min, String max) {
            this(Integer.parseInt(min), Integer.parseInt(max));
        }

        public boolean contains(Range other) {
            return other.min >= min && other.max <= max;
        }
    }

    private record Pair (Range one, Range two) {
        public boolean overlap() {
            return !(one.max < two.min || two.max < one.min);
        }
    }
}
