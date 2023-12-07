package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day07 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("07-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("07-sample.txt"));

        Pattern pat = Pattern.compile("([^ ]*) (.*)");
        String s;
        List<Hand> hands1 = new ArrayList<>();
        List<Hand> hands2 = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            Matcher mat = pat.matcher(s);
            if (mat.matches()) {
                hands1.add(new Hand(mat.group(1), mat.group(2), false));
                hands2.add(new Hand(mat.group(1), mat.group(2), true));
            } else {
                throw new IllegalArgumentException(s);
            }
        }

        printSum(hands1);
        printSum(hands2);
    }

    private static void printSum(List<Hand> hands) {
        Collections.sort(hands);
        long sum = 0;
        long rank = hands.size();
        for (Hand h : hands) {
            sum += h.bid * rank--;
        }
        System.out.println(sum);
    }

    private enum Rank {
        FIVE_OF_A_KIND,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        THREE_OF_A_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD;

        public static Rank determine(Card[] cards) {
            EnumMap<Card, Integer> counts = new EnumMap<>(Card.class);
            counts.put(Card.JOKER, 0);
            for (Card c : cards) {
                if (!counts.containsKey(c)) {
                    counts.put(c, 1);
                } else {
                    counts.put(c, counts.get(c) + 1);
                }
            }
            int jokers = counts.remove(Card.JOKER);
            if (jokers == 5) {
                return FIVE_OF_A_KIND;
            }
            int two = 0;
            boolean three = false;
            for (var v : counts.values()) {
                if (v + jokers == 5) {
                    return FIVE_OF_A_KIND;
                }
                if (v + jokers == 4) {
                    return FOUR_OF_A_KIND;
                }
                if (v == 3) {
                    three = true;
                }
                if (v == 2) {
                    two++;
                }
            }
            if (two == 1 && three) {
                return FULL_HOUSE;
            }
            if (two == 2 && jokers == 1) {
                return FULL_HOUSE;
            }
            if (two == 1 && jokers == 2) {
                return FULL_HOUSE;
            }
            if (three) {
                return THREE_OF_A_KIND;
            }
            if (two == 1 && jokers == 1) {
                return THREE_OF_A_KIND;
            }
            if (jokers == 2) {
                return THREE_OF_A_KIND;
            }
            if (two == 2) {
                return TWO_PAIR;
            }
            if (two == 1) {
                return ONE_PAIR;
            }
            if (jokers == 1) {
                return ONE_PAIR;
            }
            if (jokers > 0) {
                System.out.println(Arrays.toString(cards));
                throw new IllegalStateException();
            }
            return HIGH_CARD;
        }
    }

    private enum Card {
        A("A"), K("K"), Q("Q"), JACK("J"), TEN("T"), NINE("9"), EIGHT("8"), SEVEN("7"),
        SIX("6"), FIVE("5"), FOUR("4"), THREE("3"), TWO("2"), JOKER("*");

        private final String disp;

        Card(String disp) {
            this.disp = disp;
        }

        public static Card findByCharJack(String in) {
            for (Card c : values()) {
                if (c.disp.equals(in)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(in);
        }

        public static Card findByCharJoker(String in) {
            if (in.equals("J")) {
                return JOKER;
            }
            return findByCharJack(in);
        }

        @Override
        public String toString() {
            return disp;
        }
    }

    private static class Hand implements Comparable<Hand> {
        private long bid;
        private Rank rank;
        private Card[] cards;

        public Hand(String hand, String bid, boolean joker) {
            this.bid = Long.parseLong(bid);
            List<Card> cs = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                if (joker) {
                    cs.add(Card.findByCharJoker(hand.substring(i, i + 1)));
                } else {
                    cs.add(Card.findByCharJack(hand.substring(i, i + 1)));
                }
            }
//            Collections.sort(cs); // actually, not. The problem states that cards stay in order.
            cards = cs.toArray(new Card[5]);
            rank = Rank.determine(cards);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Card c : cards) {
                sb.append(c);
            }
            sb.append(" ");
            sb.append(rank);
            sb.append(" ");
            sb.append(bid);
            return sb.toString();
        }

        @Override
        public int compareTo(Hand o) {
            int c = rank.compareTo(o.rank);
            if (c == 0) {
                for (int i = 0; i < 5; i++) {
                    c = cards[i].compareTo(o.cards[i]);
                    if (c != 0) {
                        return c;
                    }
                }
            }
            return c;
        }
    }
}