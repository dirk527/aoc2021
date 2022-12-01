package aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day5B {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String s;
        List<Line> lines = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d+),(\\d+) -> (\\d+),(\\d+)");
        while ((s = br.readLine()) != null) {
            Matcher m = pattern.matcher(s);
            if (m.find()) {
                lines.add(new Line(m.group(3), m.group(4), m.group(1), m.group(2)));
            } else {
                System.out.println("Äh, Mist");
                System.out.println(s);
                System.exit(1);
            }
        }

        HashMap<Point, Point> points = new HashMap<>();
        lines.forEach(line -> line.markPoints(points));

        int count = 0;
        for (Point p : points.keySet()) {
            if (p.count > 1) {
                count++;
            }
        }

        System.out.println(count);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                Point p = new Point(y, x);
                if (points.containsKey(p)) {
                    System.out.print(points.get(p).count);
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }

    public static class Line {
        int x1, x2, y1, y2;

        public Line(String x1, String y1, String x2, String y2) {
            this.x1 = Integer.parseInt(x1);
            this.x2 = Integer.parseInt(x2);
            this.y1 = Integer.parseInt(y1);
            this.y2 = Integer.parseInt(y2);
        }

        public void markPoints(HashMap<Point, Point> points) {
            int xDiff = Integer.compare(x2, x1);
            int yDiff = Integer.compare(y2, y1);
            int x = x1;
            int y = y1;
            while (x != x2 || y != y2) {
                mark(points, x, y);
                x += xDiff;
                y += yDiff;
            }
            mark(points, x, y); // endpoint
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

    public static class Point {
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
