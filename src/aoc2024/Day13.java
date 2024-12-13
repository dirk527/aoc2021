package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day13 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("13-in"));
        String s;
        List<Machine> machines = new ArrayList<>();
        List<Machine> machines2 = new ArrayList<>();
        int ax = -1;
        int ay = -1;
        int bx = -1;
        int by = -1;
        int px = -1;
        int py = -1;
        Pattern btnPat = Pattern.compile("Button (.): X([+-]\\d+), Y([+-]\\d+)");
        Pattern prizePat = Pattern.compile("Prize: X=(\\d+), Y=(\\d+)");
        while ((s = br.readLine()) != null) {
            if (s.isEmpty()) {
                machines.add(new Machine(ax, ay, bx, by, px, py));
                machines2.add(new Machine(ax, ay, bx, by, 10000000000000L + px, 10000000000000L + py));
            }
            Matcher bm = btnPat.matcher(s);
            if (bm.matches()) {
                if (bm.group(1).equals("A")) {
                    ax = Integer.parseInt(bm.group(2));
                    ay = Integer.parseInt(bm.group(3));
                } else {
                    bx = Integer.parseInt(bm.group(2));
                    by = Integer.parseInt(bm.group(3));
                }
            }
            Matcher pm = prizePat.matcher(s);
            if (pm.matches()) {
                px = Integer.parseInt(pm.group(1));
                py = Integer.parseInt(pm.group(2));
            }
        }
        machines.add(new Machine(ax, ay, bx, by, px, py));
        machines2.add(new Machine(ax, ay, bx, by, 10000000000000L + px, 10000000000000L + py));

        System.out.println(simulate(machines));
        System.out.println(doMath(machines));

        System.out.println(doMath(machines2));
    }

    private static long doMath(List<Machine> machines) {
        long ret = 0;
        MathContext mc = MathContext.DECIMAL128;
        for (Machine m : machines) {
            BigDecimal ay = new BigDecimal(m.ay, mc);
            BigDecimal ax = new BigDecimal(m.ax, mc);
            BigDecimal by = new BigDecimal(m.by, mc);
            BigDecimal bx = new BigDecimal(m.bx, mc);
            BigDecimal py = new BigDecimal(m.priceY, mc);
            BigDecimal px = new BigDecimal(m.priceX, mc);
            BigDecimal zaehler = py.subtract(ay.multiply(px).divide(ax, mc));
            BigDecimal nenner = by.subtract(ay.multiply(bx).divide(ax, mc));
            BigDecimal b = zaehler.divide(nenner, mc);
            BigDecimal a = px.subtract(b.multiply(bx)).divide(ax, mc);

            BigDecimal little = new BigDecimal(.000000000000001d, mc); // longValue() always rounds down
            long la = a.add(little).longValue();
            long lb = b.add(little).longValue();
            if (m.ax * la + m.bx * lb == m.priceX && m.ay * la + m.by * lb == m.priceY) {
                System.out.println("*** " + la + " " + lb + " " + m);
                ret += la * 3 + lb;
            }
        }
        return ret;
    }

    private static int simulate(List<Machine> machines) {
        int ret = 0;
        for (Machine machine : machines) {
            PriorityQueue<Step> queue = new PriorityQueue<>();
            Set<Step> handled = new HashSet<>();
            queue.add(new Step(0, 0));
            int count = 0;
            while (!queue.isEmpty()) {
                Step step = queue.poll();
                count++;
                if (count % 100000 == 0) {
                    System.out.printf("%5d handled, %8d queue, %s%n", handled.size(), queue.size(), step);
                }
                int x = step.a() * machine.ax() + step.b() * machine.bx();
                int y = step.a() * machine.ay() + step.b() * machine.by();
                if (x == machine.priceX && y == machine.priceY) {
                    System.out.println("Found " + machine + " " + step);
                    ret += step.tokens();
                    break;
                }
                if (x > machine.priceX() || y > machine.priceY()) {
                    continue;
                }
                Step step1 = new Step(step.a() + 1, step.b());
                if (!handled.contains(step1)) {
                    handled.add(step1);
                    queue.add(step1);
                }
                Step step2 = new Step(step.a(), step.b() + 1);
                if (!handled.contains(step2)) {
                    handled.add(step2);
                    queue.add(step2);
                }
            }
        }
        return ret;
    }

    record Machine(int ax, int ay, int bx, int by, long priceX, long priceY) {
    }

    record Step(int a, int b) implements Comparable<Step> {
        @Override
        public int compareTo(Step o) {
            return tokens() - o.tokens();
        }

        private int tokens() {
            return 3 * a + b;
        }
    }
}