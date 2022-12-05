package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day5 {
    public static void main(String[] args) throws IOException {
        String filename = "day5.txt";

        BufferedReader br = new BufferedReader(new FileReader(filename));
        int count = -1;
        String s;
        Pattern pat = Pattern.compile("^\\s*\\d");
        while ((s = br.readLine()) != null) {
            Matcher m = pat.matcher(s);
            if (m.find()) {
                String[] numbers = s.split("\\s");
                count = Integer.parseInt(numbers[numbers.length - 1]);
                break;
            }
        }
        br.close();
        System.out.println(count + " stacks");

        ArrayList<LinkedList<Character>> stacks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            stacks.add(new LinkedList<>());
        }
        br = new BufferedReader(new FileReader(filename));
        while ((s = br.readLine()) != null) {
            if (s.isEmpty()) {
                break;
            }
            Matcher m = pat.matcher(s);
            if (!m.find()) {
                for (int i = 0; i < count; i++) {
                    int idx = i * 4 + 1;
                    if (idx < s.length()) {
                        char c = s.charAt(idx);
                        if (c != ' ') {
                            stacks.get(i).addLast(c);
                        }
                    }
                }
            }
        }
        System.out.println(stacks);

        pat = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");
        while ((s = br.readLine()) != null) {
            Matcher m = pat.matcher(s);
            if (!m.find()) {
                System.out.println(s + ": no match");
                throw new IllegalStateException();
            }
            int number = Integer.parseInt(m.group(1));
            int from = Integer.parseInt(m.group(2)) - 1;
            int to = Integer.parseInt(m.group(3)) - 1;

            for (int i = number - 1; i >= 0; i--) {
                // part 1
                // stacks.get(to).addFirst(stacks.get(from).removeFirst());
                // part 2
                stacks.get(to).addFirst(stacks.get(from).remove(i));
            }
        }
        System.out.println(stacks);

        stacks.forEach(st -> System.out.print(st.getFirst()));
    }
}
