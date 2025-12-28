package aoc2021;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Day13 {
    private static final String EXAMPLE = "day13-1.txt";
    private static final String REAL = "day13-2.txt";

    private static final String FOLD_X = "fold along x=";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(REAL));
        String input;
        HashSet<Point> points = new HashSet<>();
        List<Fold> folds = new ArrayList<>();
        while (!(input = br.readLine()).isEmpty()) {
            String[] parts = input.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            points.add(new Point(x, y));
        }
        while ((input = br.readLine()) != null) {
            if (input.startsWith(FOLD_X)) {
                folds.add(new Fold(Integer.parseInt(input.substring(FOLD_X.length())), true));
            } else {
                folds.add(new Fold(Integer.parseInt(input.substring(FOLD_X.length())), false));
            }
        }

        System.out.println(points);
        System.out.println(folds);

        Fold f = folds.removeFirst();
        points = f.apply(points);
        System.out.printf("result 1: %d\n", points.size());
        for (Fold fold : folds) {
            points = fold.apply(points);
        }

        JFrame viz = new JFrame("Day 13");
        viz.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viz.setSize(450, 120);
        HashSet<Point> finalPoints = points;
        int s = 10;
        viz.setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.BLACK);
                for (Point p : finalPoints) {
                    g.fillRect(s * p.x, s * p.y, s, s);
                }
            }
        });
        viz.setVisible(true);
    }

    record Point(int x, int y) {
    }

    record Fold(int coord, boolean x) {
        HashSet<Point> apply(HashSet<Point> points) {
            HashSet<Point> ret = new HashSet<>();
            for (Point p : points) {
                if (x) {
                    int xx = p.x < coord ? p.x : 2 * coord - p.x;
                    ret.add(new Point(xx, p.y));
                } else {
                    int yy = p.y < coord ? p.y : 2 * coord - p.y;
                    ret.add(new Point(p.x, yy));
                }
            }
            return ret;
        }
    }
}