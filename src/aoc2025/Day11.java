package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Day11 {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        boolean example = false;
        BufferedReader br;
        if (example) {
            br = new BufferedReader(new FileReader("11-ex"));
        } else {
            br = new BufferedReader(new FileReader("11-in"));
        }

        // Read and parse the input
        String s;
        HashMap<String, List<String>> vertices = new HashMap<>();
        while ((s = br.readLine()) != null) {
            String[] parts = s.split(" ");
            String src =  parts[0].substring(0, parts[0].length() - 1);
            List<String> targets = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                targets.add(parts[i]);
            }
            vertices.put(src, targets);
        }
        System.out.println(vertices);

        HashMap<String, Long> nPaths = new HashMap<>();
        calculate(nPaths, vertices, "you");
        System.out.println(nPaths.get("you"));
    }

    private static Long calculate(HashMap<String, Long> cache, HashMap<String, List<String>> v, String cur) {
        if (cache.containsKey(cur)) {
            return cache.get(cur);
        }
        if (cur.equals("out")) {
            return 1L;
        }
        AtomicLong result = new AtomicLong();
        v.get(cur).forEach(target -> result.addAndGet(calculate(cache, v, target)));
        cache.put(cur, result.get());
        return result.get();
    }
}