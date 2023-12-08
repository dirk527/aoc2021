package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day08 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("08-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("08-sample.txt"));

        String rl = br.readLine();
        br.readLine();

        // AAA = (BBB, CCC)
        Pattern pat = Pattern.compile("([A-Z12]{3}) = \\(([A-Z12]{3}), ([A-Z12]{3})\\)");
        String s;
        HashMap<String, Node> nodes = new HashMap<>();
        List<Node> starts = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            Matcher mat = pat.matcher(s);
            if (mat.matches()) {
                String label = mat.group(1);
                char last = label.charAt(2);
                Node n = new Node(label, mat.group(2), mat.group(3), last == 'Z');
                nodes.put(label, n);
                if (last == 'A') {
                    starts.add(n);
                }
            } else {
                throw new IllegalArgumentException(s);
            }
        }

        long startTime = System.currentTimeMillis();

        Node cur = nodes.get("AAA");
        Node finish = nodes.get("ZZZ");
        long steps = 0;
        while (cur != finish) {
            int idx = (int) (steps++ % rl.length());
            boolean left = rl.charAt(idx) == 'L';
            if (left) {
                cur = nodes.get(cur.left());
            } else {
                cur = nodes.get(cur.right());
            }
        }

        long part1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (part1Time - startTime) / 1000f);
        System.out.println(steps);

        Node[] curs = starts.toArray(new Node[0]);
        Map<State, Long>[] stateToStep = new Map[starts.size()];
        Map<Long, State>[] stepToState = new Map[starts.size()];
        Period[] periods = new Period[starts.size()];
        for (int i = 0; i < stateToStep.length; i++) {
            stateToStep[i] = new HashMap<>();
            stepToState[i] = new HashMap<>();
        }
        steps = 0;
        boolean allFound = false;
        while (!allFound) {
            int idx = (int) (steps % rl.length());
            boolean left = rl.charAt(idx) == 'L';
            for (int i = 0; i < curs.length; i++) {
                if (periods[i] != null) {
                    continue;
                }
                State curState = new State(idx, curs[i]);
                stepToState[i].put(steps, curState);
                if (stateToStep[i].containsKey(curState)) {
                    periods[i] = new Period(curState, stateToStep[i].get(curState), steps - stateToStep[i].get(curState));
                    allFound = true;
                    for (int j = 0; j < curs.length; j++) {
                        if (periods[j] == null) {
                            allFound = false;
                            break;
                        }
                    }
                } else {
                    stateToStep[i].put(curState, steps);
                }
                if (left) {
                    curs[i] = nodes.get(curs[i].left());
                } else {
                    curs[i] = nodes.get(curs[i].right());
                }
            }
            steps++;
        }

        long[] finishingOffsets = new long[starts.size()]; // in the input, every start has only one end in its period
        for (int i = 0; i < periods.length; i++) {
            Period period = periods[i];
            steps = 0;
            while (steps < period.period) {
                long key = period.first + steps;
                cur = stepToState[i].get(key).n();

                if (cur.isEnd) {
                    finishingOffsets[i] = steps;
                    break;
                }
                steps++;
            }
        }

        steps = periods[0].first + finishingOffsets[0];
        do {
            for (int i = 0; i < curs.length; i++) {
                Period period = periods[i];
                curs[i] = stepToState[i].get(period.first + ((steps - period.first) % period.period)).n();
            }
            steps += periods[0].period;
        } while (!allEnd(curs));

        long part2Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (part2Time - part1Time) / 1000f);
        System.out.println(steps - periods[0].period);
    }

    private static boolean allEnd(Node[] curs) {
        for (Node cur : curs) {
            if (!cur.isEnd) {
                return false;
            }
        }
        return true;
    }

    private record Node(String label, String left, String right, boolean isEnd) {
    }

    private record State(int idx, Node n) {
    }

    private record Period(State start, long first, long period) {
    }
}