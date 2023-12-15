package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("15-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("15-sample.txt"));

        System.out.println(hash("HASH"));

        String s;
        List<String> steps = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            steps.addAll(Arrays.asList(s.split(",")));
        }

        long sum = 0;
        for (String input : steps) {
            sum += hash(input);
        }

        System.out.println(sum);
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        Pattern pat = Pattern.compile("([a-z]+)([-=])([0-9]*)");
        List<List<Lens>> boxes = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            boxes.add(new ArrayList<>());
        }
        for (String input : steps) {
            Matcher mat = pat.matcher(input);
            if (mat.matches()) {
                String label = mat.group(1);
                String operation = mat.group(2);
                int hash = hash(label);
                List<Lens> box = boxes.get(hash);
                if (operation.equals("=")) {
                    int focal = Integer.parseInt(mat.group(3));
                    boolean found = false;
                    for (Lens lens : box) {
                        if (lens.label.equals(label)) {
                            lens.focal = focal;
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        box.add(new Lens(label, focal));
                    }
                } else if (operation.equals("-")) {
                    int remove = -1;
                    for (int idx = 0; idx < box.size(); idx++) {
                        Lens lens = box.get(idx);
                        if (lens.label.equals(label)) {
                            remove = idx;
                            break;
                        }
                    }
                    if (remove != -1) {
                        box.remove(remove);
                    }
                } else {
                    throw new IllegalArgumentException("unknown op " + operation);
                }
            } else {
                throw new IllegalArgumentException("no match " + input);
            }
        }
        sum = 0;
        for (int bi = 0; bi < boxes.size(); bi++) {
            List<Lens> box = boxes.get(bi);
            for (int idx = 0; idx < box.size(); idx++) {
                Lens lens = box.get(idx);
                sum += (bi + 1) * (idx + 1) * lens.focal;
            }
        }

        System.out.println(sum);
        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (endTime - p1Time) / 1000f);
    }

    static int hash(String in) {
        int c = 0;
        for (int i = 0; i < in.length(); i++) {
            c += in.charAt(i);
            c *= 17;
            c %= 256;
        }
        return c;
    }

    static class Lens {
        String label;
        long focal;

        public Lens(String label, long focal) {
            this.label = label;
            this.focal = focal;
        }
    }
}