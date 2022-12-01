package aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Day4B {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String seq = br.readLine();

        List<Board> boards = new ArrayList<>();
        String s;
        Board cur = null;
        while ((s = br.readLine()) != null) {
            if (s.isEmpty()) {
                cur = new Board();
                boards.add(cur);
            } else {
                cur.addLine(s);
            }
        }

        String[] callout = seq.split(",");
        int bingos = 0;
        for (String call : callout) {
            System.out.println("call = " + call);
            int callNr = Integer.parseInt(call);
            for (Iterator<Board> it = boards.iterator(); it.hasNext(); ) {
                Board board = it.next();
                board.called(callNr);
                if (board.bingo()) {
                    if (boards.size() == 1) {
                        System.out.println(boards.get(0).score() * callNr);
                        return;
                    }
                    it.remove();
                }
            }
        }
    }

    private static class Board {
        int[][] numbers;
        boolean[][] hit;
        int row = 0;

        public void addLine(String s) {
            s = s.replaceAll(" +", " ");
            s = s.replaceFirst("^ ", "");
            String[] tok = s.split(" ", 0);
            if (row == 0) {
                numbers = new int[tok.length][tok.length];
                hit = new boolean[tok.length][tok.length];
            }

            for (int col = 0; col < tok.length; col++) {
                numbers[row][col] = Integer.parseInt(tok[col]);
            }

            row++;
        }

        public void called(int call) {
            for (int row = 0; row < numbers.length; row++) {
                for (int col = 0; col < numbers.length; col++) {
                    if (numbers[row][col] == call) {
                        hit[row][col] = true;
                    }
                }
            }
        }

        public boolean bingo() {
            row:
            for (int row = 0; row < numbers.length; row++) {
                for (int col = 0; col < numbers.length; col++) {
                    if (!hit[row][col]) {
                        continue row;
                    }
                }
                return true;
            }

            col:
            for (int col = 0; col < numbers.length; col++) {
                for (int row = 0; row < numbers.length; row++) {
                    if (!hit[row][col]) {
                        continue col;
                    }
                }
                return true;
            }

            return false;
        }

        public int score() {
            int sum = 0;
            for (int row = 0; row < numbers.length; row++) {
                for (int col = 0; col < numbers.length; col++) {
                    if (!hit[row][col]) {
                        sum += numbers[row][col];
                    }
                }
            }
            return sum;
        }
    }
}