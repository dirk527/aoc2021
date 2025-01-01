package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Day21 {
    private static final HashMap<Character, Pos> dirPad = new HashMap<>();
    private static final Pos dirPadCrash = new Pos(0, 0);
    private static HashMap<Key, Long> bestOnDirPadCache = new HashMap<>();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("21-in"));

        List<String> codes = new ArrayList<>();
        String s;
        while ((s = br.readLine()) != null) {
            codes.add(s);
        }

        /*
        | 7 | 8 | 9 |
        | 4 | 5 | 6 |
        | 1 | 2 | 3 |
            | 0 | A |
         */
        HashMap<Character, Pos> numericPad = new HashMap<>();
        numericPad.put('7', new Pos(0, 0));
        numericPad.put('8', new Pos(1, 0));
        numericPad.put('9', new Pos(2, 0));
        numericPad.put('4', new Pos(0, 1));
        numericPad.put('5', new Pos(1, 1));
        numericPad.put('6', new Pos(2, 1));
        numericPad.put('1', new Pos(0, 2));
        numericPad.put('2', new Pos(1, 2));
        numericPad.put('3', new Pos(2, 2));
        numericPad.put('0', new Pos(1, 3));
        numericPad.put('A', new Pos(2, 3));
        Pos numericPadCrash = new Pos(0, 3);

        dirPad.put('^', new Pos(1, 0));
        dirPad.put('A', new Pos(2, 0));
        dirPad.put('<', new Pos(0, 1));
        dirPad.put('v', new Pos(1, 1));
        dirPad.put('>', new Pos(2, 1));

        long p1 = 0;
        long p2 = 0;
        for (String code : codes) {
            List<String> numPadMoves = movesOnPad(numericPad, numericPadCrash, code, numericPad.get('A'));
            List<String> moves = numPadMoves;
            int dirPadCount = 2;
            for (int i = 0; i < dirPadCount; i++) {
                List<String> newMoves = new ArrayList<>();
                for (String move : moves) {
                    newMoves.addAll(movesOnPad(dirPad, dirPadCrash, move, dirPad.get('A')));
                }
                moves = newMoves;
            }
            ArrayList<String> all = new ArrayList<>(moves);
            all.sort(Comparator.comparingInt(String::length));
            long codeNum = Long.parseLong(code.substring(0, 3));
            long complexity = codeNum * all.getFirst().length();
            p1 += complexity;

            long len = Long.MAX_VALUE;
            for (String numPadMove : numPadMoves) {
                char prev = 'A';
                long candLen = 0;
                for (char c : numPadMove.toCharArray()) {
                    candLen += calcBestOnDirPad(25, c, prev);
                    prev = c;
                }
                len = Math.min(len, candLen);
            }
            complexity = codeNum * len;
            p2 += complexity;
        }
        System.out.println(p1);
        System.out.println(p2);

        /*
        | 7 | 8 | 9 |                       | ^ | A |
        | 4 | 5 | 6 |                   | < | v | > |
        | 1 | 2 | 3 |
            | 0 | A |
         */

        /*
        Proof that order of movement matters some pads down:

      0             3                          7          9                 A
      1         ^   A       ^^        <<       A     >>   A        vvv      A
      2     <   A > A   <   AA  v <   AA >>  ^ A  v  AA ^ A  v <   AAA >  ^ A
      3  v<<A>>^AvA^Av<<A>>^AAv<A<A>>^AAvAA^<A>Av<A>^AA<A>Av<A<A>>^AAAvA^<A>A  longer

      0             3                      7          9                 A
      1         ^   A         <<      ^^   A     >>   A        vvv      A
      2     <   A > A  v <<   AA >  ^ AA > A  v  AA ^ A   < v  AAA >  ^ A
      3  <v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A      shorter
         */
    }

    private static long calcBestOnDirPad(int padCount, char move, char start) {
        Key key = new Key(padCount, move, start);
        if (bestOnDirPadCache.containsKey(key)) {
            return bestOnDirPadCache.get(key);
        }
        long ret = Long.MAX_VALUE;
        for (String moveCand : movesOnPad(dirPad, dirPadCrash, String.valueOf(move), dirPad.get(start))) {
            long candLen = 0;
            if (padCount == 1) {
                candLen = moveCand.length();
            } else {
                char prev = 'A';
                for (int i = 0; i < moveCand.length(); i++) {
                    char c = moveCand.charAt(i);
                    candLen += calcBestOnDirPad(padCount - 1, c, prev);
                    prev = c;
                }
            }
            ret = Math.min(ret, candLen);
        }
        bestOnDirPadCache.put(key, ret);
        return ret;
    }

    private static List<String> movesOnPad(HashMap<Character, Pos> pad, Pos crash, String code, Pos start) {
        Pos curPos = start;
        List<StringBuilder> ret = new ArrayList<>();
        ret.add(new StringBuilder());
        for (Character c : code.toCharArray()) {
            Pos nextPos = pad.get(c);
            if (nextPos == null) {
                System.out.println("*****");
            }
            List<List<Direction>> sequences = new ArrayList<>(2);
            for (Direction dir : Direction.values()) {
                List<Direction> m = dir.calcMoves(curPos, nextPos);
                if (!m.isEmpty()) {
                    sequences.add(m);
                }
            }

            if (sequences.size() == 2) {
                boolean firstFirstPossible = true;
                int x = curPos.x;
                int y = curPos.y;
                for (Direction d : sequences.get(0)) {
                    x += d.x;
                    y += d.y;
                    if (crash.x == x && crash.y == y) {
                        firstFirstPossible = false;
                        break;
                    }
                }
                boolean secondFirstPossible = true;
                x = curPos.x;
                y = curPos.y;
                for (Direction d : sequences.get(1)) {
                    x += d.x;
                    y += d.y;
                    if (crash.x == x && crash.y == y) {
                        secondFirstPossible = false;
                        break;
                    }
                }
                if (firstFirstPossible && !secondFirstPossible) {
                    for (StringBuilder sb : ret) {
                        sequences.get(0).forEach(d -> sb.append(d.c));
                        sequences.get(1).forEach(d -> sb.append(d.c));
                        sb.append('A');
                    }
                } else if (!firstFirstPossible && secondFirstPossible) {
                    for (StringBuilder sb : ret) {
                        sequences.get(1).forEach(d -> sb.append(d.c));
                        sequences.get(0).forEach(d -> sb.append(d.c));
                        sb.append('A');
                    }
                } else {
                    List<StringBuilder> add = new ArrayList<>();
                    for (StringBuilder sb : ret) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(sb.toString());
                        sequences.get(0).forEach(d -> sb.append(d.c));
                        sequences.get(1).forEach(d -> sb.append(d.c));
                        sb.append('A');
                        sequences.get(1).forEach(d -> sb2.append(d.c));
                        sequences.get(0).forEach(d -> sb2.append(d.c));
                        sb2.append('A');
                        add.add(sb2);
                    }
                    ret.addAll(add);
                }
            } else {
                for (StringBuilder sb : ret) {
                    if (!sequences.isEmpty()) {
                        sequences.getFirst().forEach(d -> sb.append(d.c));
                    }
                    sb.append('A');
                }
            }
            curPos = nextPos;
        }
        return ret.stream().map(StringBuilder::toString).toList();
    }

    record Pos(int x, int y) {
    }

    record Key(int count, char move, char start) {
    }

    enum Direction {
        UP(0, -1, '^'),
        DOWN(0, 1, 'v'),
        LEFT(-1, 0, '<'),
        RIGHT(1, 0, '>');

        int x;
        int y;
        char c;

        Direction(int x, int y, char c) {
            this.x = x;
            this.y = y;
            this.c = c;
        }

        List<Direction> calcMoves(Pos start, Pos end) {
            List<Direction> moves = new ArrayList<>();
            switch (this) {
                case UP -> {
                    for (int i = start.y; i > end.y; i--) {
                        moves.add(UP);
                    }
                }
                case DOWN -> {
                    for (int i = start.y; i < end.y; i++) {
                        moves.add(DOWN);
                    }
                }
                case LEFT -> {
                    for (int i = start.x; i > end.x; i--) {
                        moves.add(LEFT);
                    }
                }
                case RIGHT -> {
                    for (int i = start.x; i < end.x; i++) {
                        moves.add(RIGHT);
                    }
                }
            }
            return moves;
        }
    }
}