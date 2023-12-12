package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Day12 {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("12-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("12-sample.txt"));

        String s;
        List<State> p1 = new ArrayList<>();
        List<State> p2 = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            String[] parts = s.split(" ");
            List<Integer> list = new ArrayList<>();
            for (String s2 : parts[1].split(",")) {
                list.add(Integer.parseInt(s2));
            }
            String withSentinel = parts[0] + ".";
            p1.add(new State(withSentinel.toCharArray(), list));

            String expanded = parts[0] + "?" + parts[0] + "?" + parts[0] + "?" + parts[0] + "?" + parts[0] + ".";
            List<Integer> list2 = new ArrayList<>();
            list2.addAll(list);
            list2.addAll(list);
            list2.addAll(list);
            list2.addAll(list);
            list2.addAll(list);
            p2.add(new State(expanded.toCharArray(), list2));
        }

        long sum = 0;
        for (State l: p1) {
            sum += l.countPossibilities();
        }
        System.out.println(sum);
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        sum = 0;
        for (State l: p2) {
            sum += l.countPossibilities();
        }
        long endTime = System.currentTimeMillis();
        System.out.println(sum);
        System.out.printf("%5.3f sec\n", (endTime - p1Time) / 1000f);
    }

    private static int countUnknown(char[] ground) {
        int u = 0;
        for (char c : ground) {
            if (c == '?') {
                u++;
            }
        }
        return u;
    }

    static class State {
        private final char[] ground;
        private final int groundIdx;

        private final List<Integer> numbers;
        private final int numberIdx;

        private final int unknownCount;
        private final int curGroup;

        private final HashMap<State, Long> cache;

        public State(char[] ground, List<Integer> numbers) {
            this(ground, numbers, countUnknown(ground), 0, 0, 0, new HashMap<>());
        }

        private State(char[] ground, List<Integer> numbers, int unknownCount, int curGroup, int groundIdx, int numberIdx, HashMap<State, Long> cache) {
            this.ground = ground;
            this.numbers = numbers;
            this.unknownCount = unknownCount;
            this.curGroup = curGroup;
            this.groundIdx = groundIdx;
            this.numberIdx = numberIdx;
            this.cache = cache;
        }

        public long countPossibilities() {
            if (cache.containsKey(this)) {
                return cache.get(this);
            }
            long ret = -1;
            if (groundIdx == ground.length) {  // no more ground left, so no further recursion
                if (curGroup == 0 && numberIdx == numbers.size()) {
                    // valid end state: all numbers were used up
                    ret = 1;
                } else {
                    ret = 0;
                }
            } else {
                if (ground[groundIdx] == '.') {
                    ret = stepEmpty(0);
                } else if (ground[groundIdx] == '#') {
                    ret = stepSpring(0);
                } else if (ground[groundIdx] == '?') {
                    ret = stepSpring(-1) + stepEmpty(-1);
                }
            }
            if (ret < 0) {
                throw new IllegalStateException();
            }
            cache.put(this, ret);
            return ret;
        }

        private long stepSpring(int unknownOffset) {
            if (numberIdx == numbers.size() || curGroup >= numbers.get(numberIdx)) {
                // cutoff: trying to parse as a sprint, but no numbers left or group is already full
                return 0;
            } else {
                return new State(ground, numbers, unknownCount + unknownOffset, curGroup + 1, groundIdx + 1, numberIdx, cache).countPossibilities();
            }
        }

        private long stepEmpty(int unknownOffset) {
            if (curGroup > 0) {
                // first step onto empty
                if (numbers.get(numberIdx) == curGroup) {
                    // used up one number correctly
                    return new State(ground, numbers, unknownCount + unknownOffset, 0, groundIdx + 1, numberIdx + 1, cache).countPossibilities();
                } else {
                    // tried to use up a number incorrectly
                    return 0;
                }
            } else {
                return new State(ground, numbers, unknownCount + unknownOffset, 0, groundIdx + 1, numberIdx, cache).countPossibilities();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof State state)) return false;

            if (groundIdx != state.groundIdx) return false;
            if (numberIdx != state.numberIdx) return false;
            if (unknownCount != state.unknownCount) return false;
            return curGroup == state.curGroup;
        }

        @Override
        public int hashCode() {
            int result = groundIdx;
            result = 31 * result + numberIdx;
            result = 31 * result + unknownCount;
            result = 31 * result + curGroup;
            return result;
        }
    }
}