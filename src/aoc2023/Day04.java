package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day04 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("04-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("04-sample.txt"));

        Pattern pat = Pattern.compile("Card *(\\d+): ([0-9 ]+) \\| ([0-9 ]+)");
        List<Card> cards = new ArrayList<>();
        String s;
        while ((s = br.readLine()) != null) {
            Matcher mat = pat.matcher(s);
            if (mat.matches()) {
                cards.add(new Card(mat.group(2), mat.group(3)));
            }
        }

        int p1 = 0;
        int p2 = 0;
        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            p1 += c.score();
            p2 += c.instances();
            for (int j = i + 1; j <= i + c.countMatches() && j < cards.size(); j++) {
                cards.get(j).addInstances(c.instances());
            }
        }
        System.out.println(p1);
        System.out.println(p2);
    }

    private static class Card {
        private HashSet<Integer> winning = new HashSet<>();
        private HashSet<Integer> having = new HashSet<>();
        private int instances;

        public Card(String winning, String having) {
            for (String w : winning.split(" ")) {
                if (!w.isEmpty()) {
                    int e = Integer.parseInt(w);
                    this.winning.add(e);
                }
            }
            for (String h : having.split(" ")) {
                if (!h.isEmpty()) {
                    int e = Integer.parseInt(h);
                    this.having.add(e);
                }
            }
            instances = 1;
        }

        private void addInstances(int count) {
            instances += count;
        }

        public int score() {
            int m = countMatches();
            if (m == 0) {
                return 0;
            } else {
                return 1 << (m - 1);
            }
        }

        public int instances() {
            return instances;
        }

        public int countMatches() {
            int ret = 0;
            for (Integer i : having) {
                if (winning.contains(i)) {
                    ret++;
                }
            }
            return ret;
        }
    }
}