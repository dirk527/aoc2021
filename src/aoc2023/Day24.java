package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day24 {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("24-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("24-sample.txt"));

        // 19, 13, 30 @ -2,  1, -2
        Pattern pat = Pattern.compile("([0-9-]+), +([0-9-]+), +([0-9-]+) +@ +([0-9-]+), +([0-9-]+), +([0-9-]+)");
        List<Hailstone> hail = new ArrayList<>();
        String s;
        while ((s = br.readLine()) != null) {
            Matcher mat = pat.matcher(s);
            if (mat.matches()) {
                hail.add(new Hailstone(Long.parseLong(mat.group(1)), Long.parseLong(mat.group(2)),
                        Long.parseLong(mat.group(3)), Long.parseLong(mat.group(4)), Long.parseLong(mat.group(5)),
                        Long.parseLong(mat.group(6))));
            } else {
                throw new IllegalArgumentException(s);
            }
        }

        // part 1: ignore z, find intersections. Hail is a linear equation f(x) = ax + b
        // a and b are methods in Hailstone.

        // for sample
        //double min = 7;
        //double max = 27;
        double min = 200000000000000d;
        double max = 400000000000000d;

        int count = 0;
        for (int i = 0; i < hail.size(); i++) {
            Hailstone one = hail.get(i);
            for (int j = i + 1; j < hail.size(); j++) {
                Hailstone two = hail.get(j);
                double x = (two.b() - one.b()) / (one.a() - two.a());
                double y = one.a() * x + one.b();
                boolean inBounds = x >= min && x <= max && y >= min && y <= max;
                boolean futureOne = Math.signum(x - one.sx) == Math.signum(one.vx);
                boolean futureTwo = Math.signum(x - two.sx) == Math.signum(two.vx);
//                System.out.printf("%s %s: %7.3f, %7.3f %b %b %b\n", one, two, x, y, inBounds, futureOne, futureTwo);
                if (inBounds && futureOne && futureTwo) {
                    count++;
                }
            }
        }
        System.out.println(count);

    }

    static class Hailstone {
        double sx, sy, sz;
        double vx, vy, vz;

        public Hailstone(long sx, long sy, long sz, long vx, long vy, long vz) {
            this.sx = sx;
            this.sy = sy;
            this.sz = sz;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
        }

        @Override
        public String toString() {
            return String.format("%.0f, %.0f @ %.0f, %.0f   %f * x +%f", sx, sy, vx, vy, a(), b());
        }

        private double b() {
            return sy - sx * (vy / vx);
        }

        private double a() {
            return vy / vx;
        }
    }
}