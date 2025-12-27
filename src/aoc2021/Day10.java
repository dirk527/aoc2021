package aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Day10 {
    private static final String EXAMPLE = "day10-1.txt";
    private static final String REAL = "day10-2.txt";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(REAL));
        String input;
        long result1 = 0L;
        List<Long> autocompleteScores = new ArrayList<>();
        Stack<AngleType> stack = new Stack<>();
        out:
        while ((input = br.readLine()) != null) {
            stack.clear();
            for (char c : input.toCharArray()) {
                if (AngleType.opening(c)) {
                    stack.push(AngleType.from(c));
                } else {
                    AngleType type = AngleType.from(c);
                    if (stack.pop() != type) {
                        result1 += type.errorScore;
                        continue out;
                    }
                }
            }

            long autocompleteScore = 0L;
            while (!stack.isEmpty()) {
                autocompleteScore *= 5L;
                autocompleteScore += stack.pop().autocompleteScore;
            }
            autocompleteScores.add(autocompleteScore);
        }

        autocompleteScores.sort(Long::compareTo);
        long result2 = autocompleteScores.get(autocompleteScores.size() / 2);
        System.out.printf("result1 %d\nresult2 %d in %d ms\n", result1, result2, System.currentTimeMillis() - start);
    }

    enum AngleType {
        ROUND(3, 1),
        SQUARE(57, 2),
        CURLY(1197, 3),
        ANGLE(25137, 4);

        final long errorScore;
        final long autocompleteScore;

        AngleType(int errorScore, long autocompleteScore) {
            this.errorScore = errorScore;
            this.autocompleteScore = autocompleteScore;
        }

        static AngleType from(char c) {
            return switch (c) {
                case '(', ')' -> ROUND;
                case '[', ']' -> SQUARE;
                case '{', '}' -> CURLY;
                case '<', '>' -> ANGLE;
                default -> throw new IllegalArgumentException("Unexpected value: " + c);
            };
        }

        static boolean opening(char c) {
            return switch (c) {
                case '(', '[', '<', '{' -> true;
                case ')', ']', '>', '}' -> false;
                default -> throw new IllegalArgumentException("Unexpected value: " + c);
            };
        }
    }
}