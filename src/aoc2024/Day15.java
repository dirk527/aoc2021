package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day15 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("15-in"));

        String s;
        List<List<Character>> map = new ArrayList<>();
        int row = 0;
        int startR = -1;
        int startC = -1;
        while (!(s = br.readLine()).isEmpty()) {
            List<Character> line = new ArrayList<>();
            char[] charArray = s.toCharArray();
            for (int col = 0, charArrayLength = charArray.length; col < charArrayLength; col++) {
                char c = charArray[col];
                line.add(c);
                if (c == '@') {
                    startR = row;
                    startC = col;
                }
            }
            map.add(line);
            row++;
        }

        StringBuilder sb = new StringBuilder();
        while ((s = br.readLine()) != null) {
            sb.append(s.replace("\n", ""));
        }
        String moves = sb.toString();

        List<List<Character>> map2 = new ArrayList<>();
        for (int r = 0; r < map.size(); r++) {
            List<Character> line = new ArrayList<>();
            for (int c = 0; c < map.get(r).size(); c++) {
                switch (map.get(r).get(c)) {
                    case '#':
                        line.add('#');
                        line.add('#');
                        break;
                    case 'O':
                        line.add('[');
                        line.add(']');
                        break;
                    case '.':
                        line.add('.');
                        line.add('.');
                        break;
                    case '@':
                        line.add('@');
                        line.add('.');
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
            map2.add(line);
        }

        print(map);
        System.out.println(moves);

        Pos bot = new Pos(startR, startC);
        for (char move : moves.toCharArray()) {
            Direction dir = Direction.fromChar(move);
            bot = simpleMove(map, bot, dir);
        }
        print(map);

        System.out.println(calcGps(map, 'O') + "\n\n");

        print(map2);
        bot = new Pos(startR, startC * 2);
        System.out.println(map2.get(bot.row).get(bot.col));
        for (char move : moves.toCharArray()) {
            Direction dir = Direction.fromChar(move);
            if (dir == Direction.EAST || dir == Direction.WEST) {
                bot = simpleMove(map2, bot, dir);
            } else {
                bot = doubleWidthMove(map2, bot, dir);
            }
//            System.out.println("\n" + move);
//            print(map2);
        }
        print(map2);

        System.out.println(calcGps(map2, '['));
    }

    private static Pos simpleMove(List<List<Character>> map, Pos bot, Direction dir) {
        int r = bot.row + dir.r;
        int c = bot.col + dir.c;
        char cur = map.get(r).get(c);
        while (cur != '.' && cur != '#') {
            r += dir.r;
            c += dir.c;
            cur = map.get(r).get(c);
        }
        if (cur == '.') {
            do {
                r -= dir.r;
                c -= dir.c;
                char next = map.get(r).get(c);
                map.get(r + dir.r).set(c + dir.c, next);
                cur = next;
            } while (cur != '@');
            map.get(r).set(c, '.');
            bot = new Pos(bot.row + dir.r, bot.col + dir.c);
        }
        return bot;
    }

    private static long calcGps(List<List<Character>> map, char target) {
        long ret = 0;
        for (int r = 0; r < map.size(); r++) {
            for (int c = 0; c < map.get(r).size(); c++) {
                if (map.get(r).get(c) == target) {
                    ret += r * 100L + c;
                }
            }
        }
        return ret;
    }

    private static Pos doubleWidthMove(List<List<Character>> map, Pos bot, Direction dir) {
        int r = bot.row + dir.r;
        int c = bot.col;
        if (dir.c != 0) {
            throw new IllegalStateException();
        }
        char cur = map.get(r).get(c);
        if (cur == '#') {
            return bot;
        }
        if (cur == '[') {
            if (canMove(map, new Pos(r, c), dir)) {
                doMove(map, new Pos(r, c), dir);
            } else {
                return bot;
            }
        } else if (cur == ']') {
            if (canMove(map, new Pos(r, c - 1), dir)) {
                doMove(map, new Pos(r, c - 1), dir);
                map.get(r).set(c, '@');
                map.get(bot.row).set(bot.col, '.');
                return new Pos(r, c);
            } else {
                return bot;
            }
        }
        map.get(r).set(c, '@');
        map.get(bot.row).set(bot.col, '.');
        return new Pos(r, c);
    }

    /**
     * crate must be the left part of a double-width crate
     */
    private static boolean canMove(List<List<Character>> map, Pos crate, Direction dir) {
        char l = map.get(crate.row + dir.r).get(crate.col);
        boolean leftOk = switch (l) {
            case '.' -> true;
            case '#' -> false;
            case '[' -> canMove(map, new Pos(crate.row + dir.r, crate.col), dir);
            case ']' -> canMove(map, new Pos(crate.row + dir.r, crate.col - 1), dir);
            default -> throw new IllegalStateException("Unexpected left value: " + l);
        };
        if (!leftOk) {
            return false;
        }
        char r = map.get(crate.row + dir.r).get(crate.col + 1);
        return switch (r) {
            case '.', ']' -> true; // ] has been checked with [ in l, above
            case '#' -> false;
            case '[' -> canMove(map, new Pos(crate.row + dir.r, crate.col + 1), dir);
            default -> throw new IllegalStateException("Unexpected right value: " + r);
        };
    }

    /**
     * crate must be the left part of a double-width crate
     */
    private static void doMove(List<List<Character>> map, Pos crate, Direction dir) {
        List<Character> targetLine = map.get(crate.row + dir.r);
        char l = targetLine.get(crate.col);
        if (l == '[') {
            doMove(map, new Pos(crate.row + dir.r, crate.col), dir);
        } else if (l == ']') {
            doMove(map, new Pos(crate.row + dir.r, crate.col - 1), dir);
        }
        char r = targetLine.get(crate.col + 1);
        if (r == '[') {
            doMove(map, new Pos(crate.row + dir.r, crate.col + 1), dir);
        }
        targetLine.set(crate.col, '[');
        targetLine.set(crate.col + 1, ']');
        map.get(crate.row).set(crate.col, '.');
        map.get(crate.row).set(crate.col + 1, '.');
    }

    private static void print(List<List<Character>> map) {
        for (List<Character> line : map) {
            for (Character c : line) {
                System.out.print(c);
            }
            System.out.println();
        }
        System.out.println();
    }

    record Pos(int row, int col) {
    }

    enum Direction {
        NORTH(-1, 0),
        EAST(0, 1),
        SOUTH(1, 0),
        WEST(0, -1);

        final int r;
        final int c;

        static Direction fromChar(char c) {
            return switch (c) {
                case '^' -> NORTH;
                case 'v' -> SOUTH;
                case '<' -> WEST;
                case '>' -> EAST;
                default -> throw new IllegalArgumentException();
            };
        }

        Direction(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
}