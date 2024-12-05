package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Day05 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("05-in"));
        String s;
        List<Constraint> constraints = new ArrayList<>();
        while (!(s = br.readLine()).isEmpty()) {
            String[] parts = s.split("\\|");
            constraints.add(new Constraint(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
        }
        List<List<Integer>> printRuns = new ArrayList<>();
        int p1 = 0;
        while ((s = br.readLine()) != null) {
            System.out.println();
            constraints.forEach(Constraint::reset);
            String[] parts = s.split(",");
            for (String part : parts) {
                int cur = Integer.parseInt(part);
                System.out.println("-> " + cur);
                constraints.forEach(c -> c.print(cur));
            }
            constraints.forEach(System.out::println);
//            Optional<Boolean> allHappy = constraints.stream().map(Constraint::happy).reduce((a, b) -> a && b);
            AtomicBoolean allHappy = new AtomicBoolean(true);
            constraints.forEach(c -> allHappy.set(allHappy.get() && c.happy()));
            if (allHappy.get()) {
                int middle = Integer.parseInt(parts[parts.length / 2]);
                System.out.println("*** yo " + middle);
                p1 += middle;
            }
        }

        System.out.println(p1);
    }

    static class Constraint {
        int one;
        int two;
        boolean onePrinted;
        boolean twoPrinted;
        boolean happy = true;

        public Constraint(int one, int two) {
            this.one = one;
            this.two = two;
        }

        public void print(int page) {
            if (page == one) {
                onePrinted = true;
                if (twoPrinted) {
                    happy = false;
                }
                System.out.println(this + " onePrinted");
            } else if (page == two) {
                twoPrinted = true;
                System.out.println(this + " twoPrinted");
            }
        }

        public boolean happy() {
            return happy;
        }

        @Override
        public String toString() {
            return "Constraint [" + (happy ? "*" : "-") + " one=" + one + (onePrinted ? "*" : "-") + ", two=" + two + (twoPrinted ? "*" : "-") + "]";
        }

        public void reset() {
            onePrinted = false;
            twoPrinted = false;
            happy = true;
        }
    }
}