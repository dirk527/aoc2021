package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Day3 {
    public static void main(String[] args) throws IOException {
        part2();
    }

    private static void part2() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("day3.txt"));
        String s;
        int points = 0;
        Set<Character> characters1 = new HashSet<>();
        Set<Character> characters2 = new HashSet<>();
        while ((s = br.readLine()) != null) {
            characters1.clear();
            characters2.clear();
            for (int i = 0; i < s.length(); i++) {
                characters1.add(s.charAt(i));
            }
            s = br.readLine();
            for (int i = 0; i < s.length(); i++) {
                characters2.add(s.charAt(i));
            }
            s = br.readLine();
            for (int i = 0; i < s.length(); i++) {
                Character tst = s.charAt(i);
                if (characters1.contains(tst) && characters2.contains(tst)) {
                    char c = tst;
                    int add;
                    if ('a' <= c && 'z' >= c) {
                        add = c - 'a' + 1;
                    } else {
                        add = c - 'A' + 27;
                    }
                    points += add;
                    break;
                }
            }
        }
        System.out.println(points);
    }

    private static void part1() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("day3.txt"));
        String s;
        int points = 0;
        Set<Character> characters = new HashSet<>();
        while ((s = br.readLine()) != null) {
            characters.clear();
            for (int i = 0; i < s.length() / 2; i++) {
                characters.add(s.charAt(i));
            }
            for (int i = s.length() / 2; i < s.length(); i++) {
                Character tst = s.charAt(i);
                if (characters.contains(tst)) {
                    char c = tst;
                    int add;
                    if ('a' <= c && 'z' >= c) {
                        add = c - 'a' + 1;
                    } else {
                        add = c - 'A' + 27;
                    }
                    System.out.println(s + ": " + s.length() + " " + tst + " = " + add + " to " + points);
                    points += add;
                    break;
                }
            }
        }
        System.out.println(points);
    }

}
