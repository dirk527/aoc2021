package aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day5A {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String s;
        List<Line> lines = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d+),(\\d+) -> (\\d+),(\\d+)");
        while ((s = br.readLine()) != null) {
            Matcher m = pattern.matcher(s);
            if (m.find()) {
                lines.add(new Line(m.group(1), m.group(2), m.group(3), m.group(4)));
            } else {
                System.out.println("Äh, Mist");
                System.out.println(s);
                System.exit(1);
            }
        }
        lines.removeIf(line -> !line.horizOrVert());

        HashMap<Point, Point> points = new HashMap<>();
        lines.forEach(line -> line.markPoints(points));

        int count = 0;
        for (Point p: points.keySet()) {
            if (p.count > 1) {
                count++;
            }
        }

        System.out.println(count);
    }

    public static class Line {
        int x1, x2, y1, y2;

        public Line(String x1, String y1, String x2, String y2) {
            this.x1 = Integer.parseInt(x1);
            this.x2 = Integer.parseInt(x2);
            this.y1 = Integer.parseInt(y1);
            this.y2 = Integer.parseInt(y2);
        }

        public boolean horizOrVert() {
            return x1 == x2 || y1 == y2;
        }

        public void markPoints(HashMap<Point, Point> points) {
            if (x1 == x2) {
                for (int y = Math.min(y1,y2); y <= Math.max(y1,y2); y++) {
                    mark(points, x1, y);
                }
            } else {
                for (int x = Math.min(x1,x2); x<=Math.max(x1,x2); x++) {
                    mark(points, x, y1);
                }
            }
        }

        private void mark(HashMap<Point, Point> points, int x, int y) {
            Point p = new Point(x, y);
            if (points.containsKey(p)) {
                points.get(p).add();
            } else {
                points.put(p, p);
            }
        }
    }

    public static class Point implements Comparable<Point> {
        int x, y, count;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
            count = 1;
        }

        public void add() {
            count++;
        }

        @Override
        public int compareTo(Point o) {
            return count - o.count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
