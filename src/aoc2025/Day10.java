package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day10 {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        boolean example = false;
        BufferedReader br;
        if (example) {
            br = new BufferedReader(new FileReader("10-ex"));
        } else {
            br = new BufferedReader(new FileReader("10-in"));
        }

        // Read and parse the input
        String s;
        List<Machine> allMachines = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            String[] parts = s.split(" ");
            long target = 0;
            int targetLen = -1;
            List<Long> buttons = new ArrayList<>();
            List<Long> joltages = new ArrayList<>();
            for (String part : parts) {
                String inner = part.substring(1, part.length() - 1);
                if (part.charAt(0) == '[') {
                    targetLen = inner.length();
                    inner = inner.replaceAll("#", "1");
                    inner = inner.replaceAll("\\.", "0");
                    target = Long.parseLong(inner, 2);
                    System.out.printf("lights %20s long %3d binary %15s\n", inner, target,
                            Long.toBinaryString(target));
                } else if (part.charAt(0) == '(') {
                    long button = 0;
                    String[] nums = inner.split(",");
                    for (String num : nums) {
                        button = button ^ ((long) Math.pow(2, targetLen - Integer.parseInt(num) - 1));
                    }
                    buttons.add(button);
                    System.out.printf("button %20s long %3d binary %15s\n", inner, button,
                            Long.toBinaryString(button));
                } else if (part.charAt(0) == '{') {
                    String[] jStr = inner.split(",");
                    for (String j : jStr) {
                        joltages.addFirst(Long.parseLong(j));
                    }
                }
            }
            buttons.sort(Comparator.comparingInt(l -> Long.toBinaryString(l).replaceAll("0", "").length()));
            buttons = buttons.reversed();
            allMachines.add(new Machine(target, buttons, joltages, targetLen));
        }
        System.out.println(allMachines);

        // Part 1: BFS
        long result1 = 0;
        for (Machine machine : allMachines) {
            result1 += bfs(machine);
        }
        long p1Time = System.currentTimeMillis();
        System.out.printf("result1: %10d in %10d milliseconds\n", result1, p1Time - startTime);

        long result2 = 0;
        for (Machine machine : allMachines) {
            System.out.println(machine);
            long joltage = joltage(machine, 0, new long[machine.length]);
            System.out.printf("joltage: %d\n", joltage);
            result2 += joltage;
        }
        long p2Time = System.currentTimeMillis();
        System.out.printf("result2: %10d in %10d milliseconds\n", result2, p2Time - p1Time);
    }

    static int bfs(Machine m) {
        int presses = 1;
        List<Long> curStates = new ArrayList<>();
        curStates.add(0L);
        List<Long> nextStates = new ArrayList<>();
        while (true) {
            for (Long state : curStates) {
                for (Long button : m.buttons) {
                    long nextState = state ^ button;
                    if (nextState == m.target) {
                        return presses;
                    }
                    nextStates.add(nextState);
                }
            }
            curStates = nextStates;
            nextStates = new ArrayList<>();
            presses++;
        }
    }

    static long joltage(Machine m, int buttonsAlready, long[] state) {
//        System.out.printf("bA %d state %s\n", buttonsAlready, Arrays.toString(state));
        if (buttonsAlready == m.buttons.size()) {
            return -1;
        }
        long button = m.buttons.get(buttonsAlready);
        long maxPresses = 1000;
        for (long i = 0, mask = 1; i < m.length; i++, mask <<= 1) {
            if ((button & mask) > 0) {
                maxPresses = Math.min(maxPresses, m.joltages.get((int) i) - state[(int) i]);
            }
        }
//        System.out.printf("  btn %s maxp: %d\n", Long.toBinaryString(button), maxPresses);
        for (long i = 0, mask = 1; i < m.length; i++, mask <<= 1) {
            if ((button & mask) > 0) {
                state[(int) i] += maxPresses;
            }
        }
        for (long p = maxPresses; p > 0; p--) {
            boolean fine = true;
            for (int i = 0; i < state.length; i++) {
                if (state[i] != m.joltages.get(i)) {
                    fine = false;
                    break;
                }
            }
            if (fine) {
                System.out.printf("win, button %s %dx\n", Long.toBinaryString(button), p);
                return p;
            }
            long res = joltage(m, buttonsAlready + 1, state);
            for (long i = 0, mask = 1; i < m.length; i++, mask <<= 1) {
                if ((button & mask) > 0) {
                    state[(int) i]--;
                }
            }
            if (res != -1) {
                System.out.printf("  win, button %s %dx\n", Long.toBinaryString(button), p);
                return res + p;
            }
        }
        // try 0 presses
        return joltage(m, buttonsAlready + 1, state);
    }

    record Machine(long target, List<Long> buttons, List<Long> joltages, int length) {
    }

    /*
    a   b     c   d     e     f
    (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}

    3 = e + f
    5 = b + f
    4 = c + d + e
    7 = a + b + d

    buttons=[12, 10, 3, 5, 2, 1], joltages=[7, 4, 5, 3]
    lights                 0110 long   6 binary             110
    button                    3 long   1 binary               1
    button                  1,3 long   5 binary             101
    button                    2 long   2 binary              10
    button                  2,3 long   3 binary              11
    button                  0,2 long  10 binary            1010
    button                  0,1 long  12 binary            1100

     */
}