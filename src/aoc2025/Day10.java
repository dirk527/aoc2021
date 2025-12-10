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
            BitSet target = null;
            List<BitSet> buttons = new ArrayList<>();
            for (String part : parts) {
                if (part.charAt(0) == '[') {
                    String lights = part.substring(1, part.length() - 1);
                    target = new BitSet(lights.length());
                    for (int i = 0; i < lights.length(); i++) {
                        if (lights.charAt(i) == '#') {
                            target.set(i);
                        }
                    }
                } else if (part.charAt(0) == '(') {
                    BitSet button = new BitSet(target.size());
                    String[] nums = part.substring(1, part.length() - 1).split(",");
                    for (String num : nums) {
                        button.set(Integer.parseInt(num));
                    }
                    buttons.add(button);
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
        Deque<State> deque = new ArrayDeque<>();
        deque.add(new State(0, -1, new BitSet(m.target.size())));
        while (!deque.isEmpty()) {
            State s = deque.poll();
            if (s.lights.equals(m.target)) {
                return s.numPresses;
            }
            List<BitSet> buttons = m.buttons;
            for (int i = 0; i < buttons.size(); i++) {
                if (i == s.lastBtnPressed) {
                    continue;
                }
                BitSet btn = buttons.get(i);
                BitSet nLights = new BitSet(btn.size());
                nLights.xor(s.lights);
                nLights.xor(btn);
                deque.add(new State(s.numPresses + 1, i, nLights));
            }
        }
        return -1;
    }

    record State(int numPresses, int lastBtnPressed, BitSet lights) {
    }

    record Machine(BitSet target, List<BitSet> buttons) {
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