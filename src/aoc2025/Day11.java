package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Day11 {
    private static HashMap<String, List<String>> vertices;

    public static void main(String[] args) throws IOException {
        // Read and parse the input
        BufferedReader br = new BufferedReader(new FileReader("11-in"));
        String s;
        vertices = new HashMap<>();
        while ((s = br.readLine()) != null) {
            String[] parts = s.split(" ");
            String src = parts[0].substring(0, parts[0].length() - 1);
            List<String> targets = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                targets.add(parts[i]);
            }
            vertices.put(src, targets);
        }

        // Part 1: no required nodes
        HashMap<String, Long> nPaths = new HashMap<>();
        calculate(nPaths, List.of(), "you");
        System.out.println(nPaths.get("you"));

        // Part 2: require dac and fft - the keys in nPaths are current node plus the not-yet-reached required nodes
        nPaths.clear();
        calculate(nPaths, new ArrayList<>(List.of("dac", "fft")), "svr");
        System.out.println(nPaths.get("svrdacfft"));
    }

    private static Long calculate(HashMap<String, Long> cache, List<String> required, String cur) {
        StringBuilder sb = new StringBuilder(cur);
        for (String r : required) {
            sb.append(r);
        }
        String cacheKey = sb.toString();
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        if (cur.equals("out")) {
            return required.isEmpty() ? 1L : 0L;
        }
        // required is part of the cache key and it being empty is the end condition
        // so if cur is one of the required ones, remove it for the recursion (and add it back after)
        boolean foundOne = required.contains(cur);
        if (foundOne) {
            required.remove(cur);
        }
        AtomicLong result = new AtomicLong();
        vertices.get(cur).forEach(target -> result.addAndGet(calculate(cache, required, target)));
        cache.put(cacheKey, result.get());
        if (foundOne) {
            required.add(cur);
        }
        return result.get();
    }
}