package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Day21 {

    record Pos(int x, int y) {
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("21-ex"));

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
            String moves = movesOnNumeric(numericPad, code);
            System.out.println(code + "\n" + moves);
            for (int i = 0; i < 2; i++) {
                moves = movesOnDirectional(directionalPad, moves);
                System.out.println(moves);
            }
            long codeNum = Long.parseLong(code.substring(0, 3));
            long complexity = codeNum * moves.length();
            p1 += complexity;
            System.out.printf("%s %s %d * %d = %d%n", code, moves, moves.length(), codeNum, complexity);
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

    private static List<String> movesOnNumeric(HashMap<Character, Pos> numericPad, String code) {
        Pos curPos = numericPad.get('A');
        StringBuilder moves = new StringBuilder();
        for (Character c : code.toCharArray()) {
            Pos nextPos = numericPad.get(c);
            // Move in the following priority: right down up left
            // That should mean we never hit the empty square
            while (!curPos.equals(nextPos) && curPos.x < nextPos.x) {
                moves.append(">");
                curPos = new Pos(curPos.x + 1, curPos.y);
            }
            while (!curPos.equals(nextPos) && curPos.y < nextPos.y) {
                moves.append("v");
                curPos = new Pos(curPos.x , curPos.y + 1);
            }
            while (!curPos.equals(nextPos) && curPos.y > nextPos.y) {
                moves.append("^");
                curPos = new Pos(curPos.x, curPos.y - 1);
            }
            while (!curPos.equals(nextPos) && curPos.x > nextPos.x) {
                moves.append("<");
                curPos = new Pos(curPos.x - 1, curPos.y);
            }
            moves.append("A");
        }
        return moves.toString();
    }

    private static String movesOnDirectional(HashMap<Character, Pos> numericPad, String code) {
        Pos curPos = numericPad.get('A');
        StringBuilder moves = new StringBuilder();
        for (Character c : code.toCharArray()) {
            Pos nextPos = numericPad.get(c);
            // Move in the following priority: right up down left
            // That should mean we never hit the empty square
            while (!curPos.equals(nextPos) && curPos.x < nextPos.x) {
                moves.append(">");
                curPos = new Pos(curPos.x + 1, curPos.y);
            }
            while (!curPos.equals(nextPos) && curPos.y > nextPos.y) {
                moves.append("^");
                curPos = new Pos(curPos.x, curPos.y - 1);
            }
            while (!curPos.equals(nextPos) && curPos.y < nextPos.y) {
                moves.append("v");
                curPos = new Pos(curPos.x , curPos.y + 1);
            }
            while (!curPos.equals(nextPos) && curPos.x > nextPos.x) {
                moves.append("<");
                curPos = new Pos(curPos.x - 1, curPos.y);
            }
            moves.append("A");
        }
        return moves.toString();
    }
}
