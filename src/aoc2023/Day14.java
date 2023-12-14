package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static aoc2023.Day14.State.*;

public class Day14 {

    private static int rows;
    private static int cols;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("14-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("14-sample.txt"));

        String s;
        List<ArrayList<State>> ar = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            ArrayList<State> row = new ArrayList<>();
            for (int i = 0; i < s.length(); i++) {
                row.add(find(s.charAt(i)));
            }
            ar.add(row);
        }

        rows = ar.size();
        cols = ar.getFirst().size();

        Field field = new Field(ar);

        field.north();
        long sum = field.load();
        System.out.println(sum);
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        field.west();
        field.south();
        field.east();
        field.print();

        long target = 1000000000L;
        long prev = System.currentTimeMillis();
        HashMap<List<ArrayList<State>>, Long> cache = new HashMap<>();
        HashMap<Long, Long> loads = new HashMap<>();
        long cycleStart = -1;
        long cycleEnd = -1;
        for (long cycles = 2; cycles < target; cycles++) {
            if (cycles % 10000000 == 0) {
                long cur = System.currentTimeMillis();
                System.out.printf("%4.2f%% %5.3f sec\n", 100d * cycles / target, (cur - prev) / 1000f);
                prev = cur;
            }
            field.north();
            field.west();
            field.south();
            field.east();
            var copy = field.getCopy();
            if (cache.containsKey(copy)) {
                cycleStart = cache.get(copy);
                cycleEnd = cycles - 1;
                break;

            }
            cache.put(copy, cycles);
            loads.put(cycles, field.load());
        }
        long cycleLen = cycleEnd + 1 - cycleStart;
        System.out.println("loop found from " + cycleStart + " -> " + cycleEnd + " = " + cycleLen);
        long targetModulus = target % cycleLen;
        System.out.println("modulus is " + targetModulus);
        long x = cycleStart;
        while (x % cycleLen != targetModulus) {
            x++;
        }
        System.out.println(loads.get(x));

        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (endTime - p1Time) / 1000f);
    }

    private static class Field {
        private List<ArrayList<State>> field;

        public Field(List<ArrayList<State>> field) {
            this.field = field;
        }

        public void print() {
            for (List<State> ls : field) {
                ls.forEach(s -> System.out.print(s.repr));
                System.out.println();
            }
            System.out.println();
        }

        public long load() {
            long sum = 0;
            int weight = field.size();
            for (List<State> row : field) {
                int c = 0;
                for (State st : row) {
                    if (st == ROUND) {
                        c++;
                    }
                }
                sum += c * weight;
                weight--;
            }
            return sum;
        }

        public void north() {
            for (int row = 0; row < cols; row++) {
                for (int col = 0; col < cols; col++) {
                    if (field.get(row).get(col) == ROUND) {
                        int nr = row;
                        while (nr > 0 && field.get(nr - 1).get(col) == EMPTY) {
                            nr--;
                        }
                        field.get(row).set(col, EMPTY);
                        field.get(nr).set(col, ROUND);
                    }
                }
            }
        }

        public void south() {
            for (int row = rows - 1; row >= 0; row--) {
                for (int col = 0; col < cols; col++) {
                    if (field.get(row).get(col) == ROUND) {
                        int nr = row;
                        while (nr < rows - 1 && field.get(nr + 1).get(col) == EMPTY) {
                            nr++;
                        }
                        field.get(row).set(col, EMPTY);
                        field.get(nr).set(col, ROUND);
                    }
                }
            }
        }

        public void west() {
            for (int col = 0; col < cols; col++) {
                for (int row = 0; row < rows; row++) {
                    List<State> cur = field.get(row);
                    if (cur.get(col) == ROUND) {
                        int nc = col;
                        while (nc > 0 && cur.get(nc - 1) == EMPTY) {
                            nc--;
                        }
                        cur.set(col, EMPTY);
                        cur.set(nc, ROUND);
                    }
                }
            }
        }

        public void east() {
            for (int col = cols - 1; col >= 0; col--) {
                for (int row = 0; row < rows; row++) {
                    List<State> cur = field.get(row);
                    if (cur.get(col) == ROUND) {
                        int nc = col;
                        while (nc < cols - 1 && cur.get(nc + 1) == EMPTY) {
                            nc++;
                        }
                        cur.set(col, EMPTY);
                        cur.set(nc, ROUND);
                    }
                }
            }
        }

        public List<ArrayList<State>> getCopy() {
            List<ArrayList<State>> ret = new ArrayList<>();
            for (ArrayList<State> l : field) {
                ret.add((ArrayList<State>) l.clone());
            }
            return ret;
        }
    }

    enum State {
        EMPTY('.'), ROUND('O'), SQUARE('#');

        private final char repr;

        State(char repr) {
            this.repr = repr;
        }

        static State find(char c) {
            for (State s : values()) {
                if (s.repr == c) {
                    return s;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}