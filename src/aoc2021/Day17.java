package aoc2021;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day17 {
    public static final String EXAMPLE = "target area: x=20..30, y=-10..-5";
    public static final String REAL = "target area: x=29..73, y=-248..-194";

    public static void main(String[] args) {
        String in = REAL;
        Pattern pattern = Pattern.compile("target area: x=(\\d+)..(\\d+), y=(-?\\d+)..(-?\\d+)");
        Matcher matcher = pattern.matcher(in);
        if (!matcher.matches()) {
            System.out.println("no match");
        }
        int x1 = Integer.parseInt(matcher.group(1));
        int x2 = Integer.parseInt(matcher.group(2));
        int y1 = Integer.parseInt(matcher.group(3));
        int y2 = Integer.parseInt(matcher.group(4));

        // y velocity: will be 0 again after some time; it will have a downward velocity equal to initial upward +1
        // therefore the possible initial y velocities have an upper bound of -y1
        List<YPossibility> yPoss = new ArrayList<>();
        int maxSteps = 0;
        for (int yvel = y1; yvel <= -y1; yvel++) {
            int step = 0;
            int y = 0;
            int vel = yvel;
            int highest = 0;
            while (y >= y1) {
                step++;
                y += vel;
                vel--;
                highest = Math.max(highest, y);
                if ((y >= y1) && (y <= y2)) {
                    System.out.println("* possible yvel: " + yvel + " in steps: " + step + "; y = " + y +
                            "; highest = " + highest);
                    yPoss.add(new YPossibility(yvel, highest, step));
                    maxSteps = Math.max(maxSteps, step);
                }
            }
        }

        System.out.println();
        List<XPossibility> xPoss = new ArrayList<>();
        for (int xvel = 0; xvel <= x2; xvel++) {
            int x = 0;
            int vel = xvel;
            for (int step = 1; step <= maxSteps; step++) {
                x += vel;
                if (vel > 0) {
                    vel--;
                }
                if (x1 <= x && x <= x2) {
                    System.out.println("* possible xvel: " + xvel + " in steps: " + step + "; x = " + x);
                    xPoss.add(new XPossibility(xvel, step));
                }
            }
        }

        System.out.println();
        Set<Poss> poss = new HashSet<>();
        yPoss.sort((o1, o2) -> o2.highest - o1.highest);
        xPoss.sort((o1, o2) -> o2.steps - o1.steps);
        for (YPossibility yp : yPoss) {
            for (XPossibility xp : xPoss) {
                if (xp.steps < yp.steps) {
                    break;
                }
                if (xp.steps > yp.steps) {
                    continue;
                }
                System.out.println("(" + xp.vel + "," + yp.vel + ") -> " + yp.highest);
                poss.add(new Poss(xp.vel, yp.vel));
            }
        }
        System.out.println();

        System.out.println(poss.size());
    }

    private static class YPossibility {
        int vel;
        int highest;
        int steps;

        public YPossibility(int vel, int highest, int steps) {
            this.vel = vel;
            this.highest = highest;
            this.steps = steps;
        }
    }

    private static class XPossibility {
        int vel;
        int steps;

        public XPossibility(int vel, int steps) {
            this.vel = vel;
            this.steps = steps;
        }
    }

    private static class Poss {
        int xvel;
        int yvel;

        public Poss(int xvel, int yvel) {
            this.xvel = xvel;
            this.yvel = yvel;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Poss poss = (Poss) o;
            return xvel == poss.xvel && yvel == poss.yvel;
        }

        @Override
        public int hashCode() {
            return Objects.hash(xvel, yvel);
        }
    }
}
