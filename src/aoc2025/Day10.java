package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day10 {
    public static void main(String[] args) throws IOException {
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
            for (String part : parts) {
                if (part.charAt(0) == '[') {
                    String lights = part.substring(1, part.length() - 1);
                    targetLen = lights.length();
                    lights = lights.replaceAll("#", "1");
                    lights = lights.replaceAll("\\.", "0");
                    target = Long.parseLong(lights, 2);
                    System.out.printf("lights %20s long %3d binary %15s\n", lights, target, Long.toBinaryString(target));
                } else if (part.charAt(0) == '(') {
                    long button = 0;
                    String numbers = part.substring(1, part.length() - 1);
                    String[] nums = numbers.split(",");
                    for (String num : nums) {
                        button = button ^ ((long)Math.pow(2, targetLen - Integer.parseInt(num) - 1));
                    }
                    buttons.add(button);
                    System.out.printf("button %20s long %3d binary %15s\n", numbers, button, Long.toBinaryString(button));
                }
            }
            allMachines.add(new Machine(target, buttons));
        }
        System.out.println(allMachines);
        long startTime = System.currentTimeMillis();

        // Part 1: BFS
        long result = 0;
        for (Machine machine : allMachines) {
            result += bfs(machine);
        }
        long p1Time = System.currentTimeMillis();
        System.out.printf("result1: %10d in %10d milliseconds\n", result, p1Time - startTime);

        long p2Time = System.currentTimeMillis();
        System.out.printf("result2: %10d in %10d milliseconds\n", result, p2Time - p1Time);
    }

    static int bfs(Machine m) {
        Deque<State> deque = new LinkedList<>();
        deque.add(new State(0, -1, 0));
        while (!deque.isEmpty()) {
            State s = deque.poll();
            if (s.lights == m.target) {
                return s.numPresses;
            }
            List<Long> buttons = m.buttons;
            for (int i = 0; i < buttons.size(); i++) {
                if (i == s.lastBtnPressed) {
                    continue;
                }
                long nLights = s.lights ^ buttons.get(i);
                deque.add(new State(s.numPresses + 1, i, nLights));
            }
        }
        return -1;
    }

    record State(int numPresses, int lastBtnPressed, long lights) {
    }

    record Machine(long target, List<Long> buttons) {
    }

    /*
    a   b     c   d     e     f
    (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}

    3 = e + f
    5 = b + f
    4 = c + d + e
    7 = a + b + d

     */
}