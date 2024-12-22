package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Day21 {

    record Pos(int x, int y) {
    }

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

        HashMap<Character, Pos> directionalPad = new HashMap<>();
        directionalPad.put('^', new Pos(1, 0));
        directionalPad.put('A', new Pos(2, 0));
        directionalPad.put('<', new Pos(0, 1));
        directionalPad.put('v', new Pos(1, 1));
        directionalPad.put('>', new Pos(2, 1));

        long p1 = 0;
        for (String code : codes) {
            List<String> moves = movesOnPad(numericPad, new Pos(0, 3), code);
            for (int i = 0; i < 25; i++) {
                System.out.println("i = " + i);
                List<String> newMoves = new ArrayList<>();
                for (String move : moves) {
                    newMoves.addAll(movesOnPad(directionalPad, new Pos(0, 0), move));
                }
                moves = newMoves;
            }
            ArrayList<String> all = new ArrayList<>(moves);
            all.sort(Comparator.comparingInt(String::length));
            long codeNum = Long.parseLong(code.substring(0, 3));
            long complexity = codeNum * all.getFirst().length();
            p1 += complexity;
            System.out.printf("%s %s %d * %d = %d%n", code, all.getFirst(), all.getFirst().length(), codeNum, complexity);
        }
        System.out.println(p1);

        /*
        | 7 | 8 | 9 |                       | ^ | A |
        | 4 | 5 | 6 |                   | < | v | > |
        | 1 | 2 | 3 |
            | 0 | A |
         */

        /*
                   3                          7          9                 A
               ^   A       ^^        <<       A     >>   A        vvv      A
           <   A > A   <   AA  v <   AA >>  ^ A  v  AA ^ A  v <   AAA >  ^ A
        v<<A>>^AvA^Av<<A>>^AAv<A<A>>^AAvAA^<A>Av<A>^AA<A>Av<A<A>>^AAAvA^<A>A  ich

                   3                      7          9                 A
               ^   A         <<      ^^   A     >>   A        vvv      A
           <   A > A  v <<   AA >  ^ AA > A  v  AA ^ A   < v  AAA >  ^ A
        <v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A      richtig
         */
    }

    private static List<String> movesOnPad(HashMap<Character, Pos> pad, Pos crash, String code) {
        Pos curPos = pad.get('A');
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
                        // TODO maybe interleaved?! but those must be worse right?!
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
        // TODO maybe filter out all that are longer than optimal?
        return ret.stream().map(StringBuilder::toString).toList();
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