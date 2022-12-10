package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day10 {
    public static void main(String[] args) throws IOException {
        String filename = "day10.txt";

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String s;
        int cycle = 0;
        int x = 1;
        int strength = 0;
        List<StringBuilder> crt = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            crt.add(new StringBuilder());
        }
        while ((s = br.readLine()) != null) {
            if (s.equals("noop")) {
                cycle++;
                strength += calculateStrength(cycle, x);
                addPixel(cycle, x, crt);
            } else {
                String[] comps = s.split(" ");
                cycle++;
                strength += calculateStrength(cycle, x);
                addPixel(cycle, x, crt);
                cycle++;
                strength += calculateStrength(cycle, x);
                addPixel(cycle, x, crt);
                x += Integer.parseInt(comps[1]);
            }
        }
        System.out.println(strength);
        for (StringBuilder sb : crt) {
            System.out.println(sb);
        }
    }

    private static int calculateStrength(int cycle, int x) {
        if ((cycle - 20) % 40 == 0) {
            return cycle * x;
        }
        return 0;
    }

    private static void addPixel(int cycle, int spritePos, List<StringBuilder> crt) {
        int vPos = ((cycle - 1) % 40);
        int diff = Math.abs(spritePos - vPos);
        int hPos = (cycle - 1) / 40;
        crt.get(hPos).append(diff < 2 ? '#' : '.');
    }
}