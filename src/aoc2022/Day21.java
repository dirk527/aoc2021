package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static aoc2022.Day21.Operation.*;

public class Day21 {
    static HashMap<String, Monkey> monkeys = new HashMap<>();
    static List<Monkey> humanBranch = new ArrayList<>();
    static Monkey human;

    public static void main(String[] args) throws IOException {
        // Initialize
        long begin = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("day21.txt"));
        String s;
        while ((s = br.readLine()) != null) {
            String[] sts = s.split(" ");
            String id = sts[0].substring(0, 4);
            if (sts.length == 2) {
                monkeys.put(id, new Monkey(id, sts[1]));
            } else {
                monkeys.put(id, new Monkey(id, sts[2], sts[1], sts[3]));
            }
        }

        // calculate
        System.out.println("part 1: " + monkeys.get("root").calculate());
        long pt1 = System.currentTimeMillis() - begin;
        System.out.println(pt1 + "ms");

        // find the monkeys that are in the adjustable branch
        Monkey root = monkeys.get("root");
        human = monkeys.get("humn");
        human.value = null;
        humanBranch.add(human);
        findHumanBranch(root);
        System.out.println(humanBranch);
        // adjust the null value
        root.adjustHuman(null);
        System.out.println("part 2: " + human.value);

        long pt2 = System.currentTimeMillis() - begin + pt1;
        System.out.println(pt2 + "ms");
    }

    private static boolean findHumanBranch(Monkey cur) {
        if (cur.op == VALUE) {
            return false;
        }
        if (cur.arg1.equals("humn")) {
            humanBranch.add(cur);
            return true;
        } else {
            if (findHumanBranch(monkeys.get(cur.arg1))) {
                humanBranch.add(cur);
                return true;
            }
        }
        if (cur.arg2.equals("humn")) {
            humanBranch.add(cur);
            return true;
        } else {
            if (findHumanBranch(monkeys.get(cur.arg2))) {
                humanBranch.add(cur);
                return true;
            }
        }
        return false;
    }

    enum Operation {
        VALUE, PLUS, MINUS, MULTIPLY, DIVIDE;
    }

    static class Monkey {
        String id;
        Long value;
        String arg1;
        Operation op;
        String arg2;

        public Monkey(String id, String in) {
            this.id = id;
            value = Long.valueOf(in);
            op = VALUE;
        }

        public Monkey(String id, String op, String arg1, String arg2) {
            this.id = id;
            this.op = switch (op) {
                case "+" -> PLUS;
                case "-" -> MINUS;
                case "*" -> MULTIPLY;
                case "/" -> DIVIDE;
                default -> throw new IllegalArgumentException();
            };
            this.arg1 = arg1;
            this.arg2 = arg2;
        }

        public long calculate() {
            if (op == VALUE) {
                return value;
            }
            long m1 = monkeys.get(arg1).calculate();
            long m2 = monkeys.get(arg2).calculate();
            return switch (op) {
                case PLUS -> m1 + m2;
                case MINUS -> m1 - m2;
                case MULTIPLY -> m1 * m2;
                case DIVIDE -> m1 / m2;
                default -> throw new IllegalStateException();
            };
        }

        public void adjustHuman(Long desired) {
            if (desired == null) {
                // we're the root monkey
                Monkey m1 = monkeys.get(arg1);
                Monkey m2 = monkeys.get(arg2);
                if (humanBranch.contains(m1)) {
                    m1.adjustHuman(m2.calculate());
                } else {
                    m2.adjustHuman(m1.calculate());
                }
                return;
            }
            if (op == VALUE) {
                if (this == human) {
                    value = desired;
                    return;
                } else {
                    throw new IllegalStateException();
                }
            }
            Monkey m1 = monkeys.get(arg1);
            Monkey m2 = monkeys.get(arg2);
            boolean m1h = humanBranch.contains(m1);
            switch (op) {
                case VALUE -> throw new IllegalStateException();
                case PLUS -> {
                    if (m1h) {
                        m1.adjustHuman(desired - m2.calculate());
                    } else {
                        m2.adjustHuman(desired - m1.calculate());
                    }
                }
                case MINUS -> {
                    // d = m1 - m2
                    // m1 = d - m2
                    // m2 = m1 - d
                    if (m1h) {
                        m1.adjustHuman(desired + m2.calculate());
                    } else {
                        m2.adjustHuman(m1.calculate() - desired);
                    }
                }
                case MULTIPLY -> {
                    if (m1h) {
                        m1.adjustHuman(desired / m2.calculate());
                    } else {
                        m2.adjustHuman(desired / m1.calculate());
                    }
                }
                case DIVIDE -> {
                    // d = m1 / m2
                    // m1 = d * ms
                    // m2 = m1 / d
                    if (m1h) {
                        m1.adjustHuman(desired * m2.calculate());
                    } else {
                        m2.adjustHuman(m1.calculate() / desired);
                    }
                }
            }
        }

        @Override
        public String toString() {
            return id + ": " + (op == VALUE ? value : arg1 + " " + op + " " + arg2);
        }
    }
}