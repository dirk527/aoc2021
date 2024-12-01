package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Day01 {

    public static void main(String[] args) throws IOException {
//        BufferedReader br = new BufferedReader(new FileReader("01-sample.txt"));
        BufferedReader br = new BufferedReader(new FileReader("1-in"));

        String s;
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        HashMap<Integer, Integer> count = new HashMap<>();
        while ((s = br.readLine()) != null) {
            String[] tokens = s.split(" +");
            left.add(Integer.parseInt(tokens[0]));
            int r = Integer.parseInt(tokens[1]);
            right.add(r);
            count.put(r, count.getOrDefault(r, 0) + 1);
        }
        left.sort(Integer::compareTo);
        right.sort(Integer::compareTo);

        int score1 = 0;
        int score2 = 0;
        for (int i = 0; i < left.size(); i++) {
            Integer l = left.get(i);
            score1 += Math.abs(l - right.get(i));
            Integer c = count.get(l);
            if (c == null) {
                c = 0;
            }
            score2 += l * c;
        }

        System.out.println(score1);
        System.out.println(score2);
    }
}