package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day25 {
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("25-in"));

        List<List<Integer>> locks = new ArrayList<>();
        List<List<Integer>> keys = new ArrayList<>();

        String s;
        while ((s = br.readLine()) != null) {
            boolean lock = s.startsWith("#");
            List<Integer> cur = new ArrayList<>();
            for (int col = 0; col < 5; col++) {
                cur.add(0);
            }
            for (int i = 0; i < 5; i++) {
                s = br.readLine();
                for (int col = 0; col < 5; col++) {
                    if (s.charAt(col) == '#') {
                        cur.set(col, cur.get(col) + 1);
                    }
                }
            }
            br.readLine();
            br.readLine();
            (lock ? locks : keys).add(cur);
        }

        int ans = 0;
        for (List<Integer> key : keys) {
            for (List<Integer> lock : locks) {
                boolean fits = true;
                for (int col = 0; col < 5; col++) {
                    if (lock.get(col) + key.get(col) > 5) {
                        fits = false;
                        break;
                    }
                }
                if (fits) {
                    ans++;
                }
            }
        }
        System.out.println(ans);
    }
}