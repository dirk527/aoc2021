package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.PriorityQueue;

public class Day1 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("day1a.txt"));
        String s;
        PriorityQueue<Integer> elves = new PriorityQueue<>((o1, o2) -> o2 - o1);
        int curElf = 0;
        while ((s = br.readLine()) != null) {
            if (s.isEmpty()) {
                elves.add(curElf);
                curElf = 0;
            } else {
                curElf += Integer.parseInt(s);
            }
        }
        // First part
        Integer top = elves.remove();
        System.out.println(top);
        // Second part
        System.out.println(top + elves.remove() + elves.remove());
    }
}
