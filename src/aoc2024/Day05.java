package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
        while ((s = br.readLine()) != null) {
            List<Integer> run = new ArrayList<>();
            String[] parts = s.split(",");
            for (String part : parts) {
                run.add(Integer.parseInt(part));
            }
            printRuns.add(run);
        }

        int p1 = 0;
        List<List<Integer>> failedRuns = new ArrayList<>();
        for (List<Integer> run : printRuns) {
            constraints.forEach(Constraint::reset);
            for (int cur : run) {
                constraints.forEach(c -> c.print(cur));
            }
            Optional<Boolean> allHappy = constraints.stream()
                    .map(Constraint::happy)
                    .reduce((a, b) -> a && b);
            if (allHappy.get()) {
                p1 += run.get(run.size() / 2);
            } else {
                failedRuns.add(run);
            }
        }

        System.out.println(p1);

        Comparator<Integer> comp = (i1, i2) -> {
            for (Constraint c : constraints) {
                int cur = c.compare(i1, i2);
                if (cur != 0) {
                    return cur;
                }
            }
            return 0;
        };
        int p2 = 0;
        for (List<Integer> run : failedRuns) {
            run.sort(comp);
            p2 += run.get(run.size() / 2);
        }
        System.out.println(p2);
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
            } else if (page == two) {
                twoPrinted = true;
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

        public int compare(Integer i1, Integer i2) {
            if (i1 == one && i2 == two) {
                return -1;
            }
            if (i1 == two && i2 == one) {
                return 1;
            }
            return 0;
        }
    }
}