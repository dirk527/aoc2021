package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 {
    private static int minx = Integer.MAX_VALUE;
    private static int maxx = Integer.MIN_VALUE;
    private static int maxDist = Integer.MIN_VALUE;
    private static boolean inited = false;

    public static void main(String[] args) throws IOException {
        // Initialize
        long begin = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("day15.txt"));

        Pattern pat = Pattern.compile("^Sensor at x=([0-9-]+), y=([0-9-]+): closest beacon is at x=([0-9-]+), y=([0-9-]+)");
        HashSet<Beacon> beacons = new HashSet<>();
        List<Sensor> sensors = new ArrayList<>();
        String s;
        while ((s = br.readLine()) != null) {
            Matcher m = pat.matcher(s);
            m.find();
            Sensor sensor = new Sensor(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
            sensors.add(sensor);
            beacons.add(sensor.beacon);
        }
        inited = true; // hack so that new Beacon() below does not change maxx

        // Part 1: serial scan
//        int y = 10;
        int y = 2000000;
        int count = 0;
        out:
        for (int x = minx - maxDist; x < maxx + maxDist; x++) {
            if (beacons.contains(new Beacon(x, y))) {
                continue;
            }
            for (Sensor sensor : sensors) {
                if (sensor.impossible(x, y)) {
                    count++;
                    continue out;
                }
            }
        }
        System.out.println("\npart 1: " + count);

        // Part 2: complete scan is too slow, calculate x ranges for each sensor that it blocks in the current row
        List<Range> ranges = new ArrayList<>();
        int search = 4000000;
//        int search = 20;
        out2:
        for (y = 0; y <= search; y++) {
            ranges.clear();
            // 2.1 calculate the ranges
            for (Sensor sensor : sensors) {
                Range range = sensor.calcImpossibleRange(y);
                if (range != null) {
                    ranges.add(range);
                }
            }
            // 2.2. consolidate the first range in the search window with its successors until that is not possible
            ranges.sort((a, b) -> {
                int ret = a.min - b.min;
                if (ret == 0) {
                    ret = Math.abs((a.max - a.min) - (b.max - b.min));
                }
                return ret;
            });
            int i = 0;
            while (ranges.get(i).max < 0) {
                i++;
            }
            Range r = ranges.get(i);
            while (i < ranges.size() - 1) {
                i++;
                Range c = ranges.get(i);
                if (r.max >= c.min) {
                    r.min = Math.min(r.min, c.min);
                    r.max = Math.max(r.max, c.max);
                } else {
                    break;
                }
            }
            // 2.3. if there is another range left, there must be a single space available according to the spec
            if (i < ranges.size() - 1) {
                Range c = ranges.get(i + 1);
                if (r.max + 2 == c.min) {
                    int x = r.max + 1;
                    System.out.println("part 2: " + (x * 4000000L + y));
                    break out2;
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println((end-begin) + " millis");
    }

    static class Sensor {
        int x, y;
        Beacon beacon;
        int beaconDist;

        public Sensor(int x, int y, int bx, int by) {
            this.x = x;
            this.y = y;
            if (!inited) {
                minx = Math.min(minx, x);
                maxx = Math.max(maxx, x);
            }
            this.beacon = new Beacon(bx, by);
            beaconDist = Math.abs(x - bx) + Math.abs(y - by);
            maxDist = Math.max(maxDist, beaconDist);
        }

        public boolean impossible(int qx, int qy) {
            int qd = Math.abs(qx - x) + Math.abs(qy - y);
            return qd <= beaconDist;
        }

        public Range calcImpossibleRange(int qy) {
            int diff = beaconDist - Math.abs(qy - y);
            if (diff < 1) {
                return null;
            }
            return new Range(x - diff, x + diff);
        }

        @Override
        public String toString() {
            return "S@" + x + "," + y + " | nb " + beacon.x + "," + beacon.y;
        }
    }

    private static class Beacon {
        int x, y;

        public Beacon(int x, int y) {
            this.x = x;
            this.y = y;
            if (!inited) {
                minx = Math.min(minx, x);
                maxx = Math.max(maxx, x);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Beacon beacon)) return false;

            if (x != beacon.x) return false;
            return y == beacon.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }

    private static class Range {
        private int min;
        private int max;

        private Range(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            return "[" + min + " -> " + max + "]";
        }
    }
}