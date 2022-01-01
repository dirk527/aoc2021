import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day8B {
    public static final String EXAMPLE = "day8-1.txt";
    public static final String REAL = "day8-2.txt";
    public static final String SMALL = "day8-0.txt";

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(REAL));
        String input;
        int sum = 0;
        while ((input = br.readLine()) != null) {
            sum += solve(input.split(" "));
        }
        System.out.println(sum);
    }

    private static int solve(String[] combinations) {
        Map<Character, Constraint> constraints = new HashMap<>();
        for (char c = 'a'; c < 'h'; c++) {
            constraints.put(c, new Constraint(c));
        }

        int[] wireCount = new int[7];
        for (int idx = 0; idx < 10; idx++) {
            String combi = combinations[idx];
            int len = combi.length();
            for (char c : combi.toCharArray()) {
                wireCount[c - 'a']++;
            }
            if (len == 2) { // digit 1
                List<Character> to = List.of('c', 'f');
                for (int i = 0; i < len; i++) {
                    constraints.get(combi.charAt(i)).constrainTo(to);
                }
            } else if (len == 3) { // digit 7
                List<Character> to = List.of('a', 'c', 'f');
                for (int i = 0; i < len; i++) {
                    constraints.get(combi.charAt(i)).constrainTo(to);
                }
            } else if (len == 4) { // digit 4
                List<Character> to = List.of('b', 'c', 'd', 'f');
                for (int i = 0; i < len; i++) {
                    constraints.get(combi.charAt(i)).constrainTo(to);
                }
            }
        }
        for (char c = 'a'; c < 'h'; c++) {
            int count = wireCount[c - 'a'];
            if (count == 4) {
                constraints.get(c).constrainTo(List.of('e'));
            } else if (count == 6) {
                constraints.get(c).constrainTo(List.of('b'));
            } else if (count == 9) {
                constraints.get(c).constrainTo(List.of('f'));
            }
        }
        List<Character> unique = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            unique.clear();
            constraints.forEach((character, constraint) -> {
                if (constraint.possible.size() == 1) {
                    unique.add(constraint.possible.get(0));
                }
            });
            constraints.forEach((character, constraint) -> constraint.removeIfPossible(unique));
        }
        char[] translation = new char[7];
        for (char c = 'a'; c < 'h'; c++) {
            if (constraints.get(c).possible.size() != 1) {
                throw new IllegalStateException();
            }
            translation[c - 'a'] = constraints.get(c).possible.get(0);
        }
        int ret = 0;
        for (int idx = 11; idx < 15; idx++) {
            String combi = combinations[idx];
            TreeSet<Character> ts = new TreeSet<>();
            for (int i = 0; i < combi.length(); i++) {
                ts.add(translation[combi.charAt(i) - 'a']);
            }
            StringBuilder sb = new StringBuilder();
            for (Character c : ts) {
                sb.append(c);
            }
            String translated = sb.toString();
            int digit = -1;
            switch(translated) {
                case "abcefg" -> digit = 0;
                case "cf" -> digit = 1;
                case "acdeg" -> digit = 2;
                case "acdfg" -> digit = 3;
                case "bcdf" -> digit = 4;
                case "abdfg" -> digit = 5;
                case "abdefg" -> digit = 6;
                case "acf" -> digit = 7;
                case "abcdefg" -> digit = 8;
                case "abcdfg" -> digit = 9;
                default -> throw new IllegalStateException(translated + " is not a digit");
            }
            ret =  ret * 10 + digit;
        }
        return ret;
    }

    private static class Constraint {
        char wire;
        List<Character> possible;

        public Constraint(char wire) {
            this.wire = wire;
            possible = new ArrayList<>(7);
            for (char c = 'a'; c < 'h'; c++) {
                possible.add(c);
            }
        }

        public void constrainTo(List<Character> retain) {
            possible.retainAll(retain);
        }

        public void removeIfPossible(List<Character> remove) {
            if (possible.size() == 1 && remove.contains(possible.get(0))) {
                return;
            }
            possible.removeAll(remove);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder().append("wire ").append(wire).append(": ");
            for (char c = 'a'; c < 'h'; c++) {
                if (possible.contains(c)) {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
    }
}
