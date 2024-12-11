package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Day11 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("11-in"));
        String s = br.readLine();
        String[] numbers = s.split(" ");

        Line line = new Line();
        for (String number : numbers) {
            line.addStone(Long.parseLong(number));
        }

//        for (int i = 0; i < 25; i++) {
//            line.blink();
//        }

        line.calculate(45);
        System.out.println(line.size());

//        System.out.println(p2);
    }

    static HashMap<Simulation, Line> cache = new HashMap<>();

    record Simulation(long number, int steps){}

    static class Stone {
        Stone prev;
        Stone next;
        long number;

        public Stone(long l) {
            number = l;
        }
    }

    static class Line {
        Stone first;
        Stone last;

        public void addStone(long l) {
            Stone stone = new Stone(l);
            if (first == null) {
                first = stone;
                last = stone;
            } else {
                last.next = stone;
                stone.prev = last;
                last = stone;
            }
        }

        public void blink() {
            for (Stone cur = first; cur != null; cur = cur.next) {
                if (cur.number == 0) {
                    cur.number = 1;
                } else {
                    int digits = (int) Math.log10(cur.number) + 1;
                    if (digits % 2 == 0) {
                        long divisor = (long) Math.pow(10, digits / 2);
                        Stone left = new Stone(cur.number / divisor);
                        cur.number = cur.number % divisor;
                        left.prev = cur.prev;
                        left.next = cur;
                        if (left.prev != null) {
                            left.prev.next = left;
                        } else {
                            first = left;
                        }
                        left.next.prev = left;
                    } else {
                        cur.number *= 2024L;
                    }
                }
            }
        }

        public void println() {
            for (Stone cur = first; cur != null; cur = cur.next) {
                System.out.print(cur.number);
                System.out.print(" ");
            }
            System.out.println();
        }

        public long size() {
            long ret = 0;
            for (Stone cur = first; cur != null; cur = cur.next) {
                ret++;
            }
            return ret;
        }

        public void calculate(int blinks) {
            if (blinks == 0) {
                return;
            }
            for (Stone cur = first; cur != null; cur = cur.next) {
                Simulation sim = new Simulation(cur.number, blinks);
                Line cached = cache.get(sim);

                if (cached == null) {
                    Line next = new Line();
                    next.addStone(cur.number);
                    next.calculate(blinks - 1);
                    next.blink();
                    cache.put(sim, next);
                    cached = next;
                }

                Line copy = new Line();
                for (Stone inner = cached.first; inner != null; inner = inner.next) {
                    copy.addStone(inner.number);
                }
                copy.first.prev = cur.prev;
                copy.last.next = cur.next;
                if (cur.prev != null) {
                    cur.prev.next = copy.first;
                } else {
                    first = copy.first;
                }
                if (cur.next != null) {
                    cur.next.prev = copy.last;
                } else {
                    last = copy.last;
                }
            }
        }
    }
}