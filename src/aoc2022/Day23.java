package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static aoc2022.Day23.Direction.*;

public class Day23 {
    public static void main(String[] args) throws IOException {
        // Initialize
        long begin = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("day23.txt"));
        String s;
        HashMap<Pos, Elf> elves = new HashMap<>();
        int y = 0;
        while ((s = br.readLine()) != null) {
            char[] charArray = s.toCharArray();
            for (int x = 0; x < charArray.length; x++) {
                if (charArray[x] == '#') {
                    elves.put(new Pos(x, y), new Elf());
                }
            }
            y++;
        }
        br.close();
        countEmpty(elves, "Before start");
        LinkedList<Direction> directions = new LinkedList<>(List.of(N, S, W, E));

        // simulate
        long pt1 = 0;
        HashMap<Pos, List<Elf>> proposed = new HashMap<>();
        int round;
        for (round = 0; ; round++) {
            // compute the proposed moves
            proposed.clear();
            for (var entry : elves.entrySet()) {
                Pos pos = entry.getKey();
                Elf elf = entry.getValue();
                Pos proposedPos = elf.updateProposed(pos, elves, directions);
                if (proposedPos != null) {
                    proposed.computeIfAbsent(proposedPos, k -> new LinkedList<>());
                    proposed.get(proposedPos).add(elf);
                }
            }

            // move non-clashing
            boolean anyoneMoved = false;
            for (var entry : proposed.entrySet()) {
                Pos pos = entry.getKey();
                List<Elf> list = entry.getValue();
                if (list.size() == 1) {
                    Elf elf = list.get(0);
                    Elf removed = elves.remove(elf.pos);
                    assert elf == removed;
                    elves.put(pos, elf);
                    anyoneMoved = true;
                }
            }
            if (round == 9) {
                // part 1 finished
                int c = countEmpty(elves, "After round " + round);
                System.out.println("part 1: " +c);
                pt1 = System.currentTimeMillis() - begin;
                System.out.println(pt1 + "ms");
            }
            if (!anyoneMoved) {
                // part 2 finished
                break;
            }
            directions.addLast(directions.removeFirst());
        }

        countEmpty(elves, "After finishing, round " + round);
        System.out.println("part 2: " + (round + 1));
        long pt2 = System.currentTimeMillis() - begin + pt1;
        System.out.println(pt2 + "ms");
    }

    private static int countEmpty(HashMap<Pos, Elf> elves, String print) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (var entry : elves.entrySet()) {
            Pos pos = entry.getKey();
            minX = Math.min(minX, pos.x);
            maxX = Math.max(maxX, pos.x);
            minY = Math.min(minY, pos.y);
            maxY = Math.max(maxY, pos.y);
        }
        if (print != null) {
            System.out.println(print + ": minX = " + minX + "; maxX = " + maxX + "; minY = " + minY + "; maxY = " + maxY);
        }
        int count = 0;
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                boolean elf = elves.containsKey(new Pos(x, y));
                if (print != null) {
                    System.out.print(elf ? '#' : '.');
                }
                if (!elf) {
                    count ++;
                }
            }
            if (print != null) {
                System.out.println();
            }
        }
        return count;
    }

    enum Direction {
        N(0, -1), S(0, 1), W(-1, 0), E(1, 0);

        final int x;
        final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        List<Pos> getPotentialBlockers(Pos pos) {
            List<Pos> ret = new LinkedList<>();
            Pos one = new Pos(pos.x + x, pos.y + y);
            ret.add(one);
            if (this == N || this == S) {
                ret.add(new Pos(one.x + W.x, one.y + W.y));
                ret.add(new Pos(one.x + E.x, one.y + E.y));
            } else {
                ret.add(new Pos(one.x + N.x, one.y + N.y));
                ret.add(new Pos(one.x + S.x, one.y + S.y));
            }
            return ret;
        }
    }

    record Pos(int x, int y) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pos pos)) return false;

            if (x != pos.x) return false;
            return y == pos.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }

    static class Elf {
        Pos pos;

        public Pos updateProposed(Pos pos, HashMap<Pos, Elf> elves, List<Direction> directions) {
            this.pos = pos;
            boolean anotherElfFound = false;
            Direction proposed = null;
            for (Direction d : directions) {
                List<Pos> potential = d.getPotentialBlockers(pos);
                boolean blocked = potential.stream().anyMatch(elves::containsKey);
                anotherElfFound = anotherElfFound || blocked;
                if (!blocked && proposed == null) {
                    proposed = d;
                }
            }
            if (!anotherElfFound) {
                proposed = null;
            }
            return proposed == null ? null : new Pos(pos.x + proposed.x, pos.y + proposed.y);
        }
    }
}