package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Day20 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("20-ex"));

        String s;
        List<List<Character>> map = new ArrayList<>();
        int row = 0;
        Pos start = null;
        Pos end = null;
        while ((s = br.readLine()) != null) {
            List<Character> line = new ArrayList<>();
            char[] charArray = s.toCharArray();
            for (int col = 0, charArrayLength = charArray.length; col < charArrayLength; col++) {
                char c = charArray[col];
                line.add(c);
                if (c == 'S') {
                    start = new Pos(row, col);
                } else if (c == 'E') {
                    end = new Pos(row, col);
                }
            }
            map.add(line);
            row++;
        }
        System.out.println("start = " + start + "; end = " + end);

        // There's only a single path, find out how long to every square
        HashMap<Pos, Long> picos = new HashMap<>();
        long pico = 0;
        for (Pos cur = start; !cur.equals(end); ) {
            picos.put(cur, pico);
            for (Direction direction : Direction.values()) {
                int rr = cur.row + direction.r;
                int cc = cur.col + direction.c;
                Pos next = new Pos(rr, cc);
                if (picos.get(next) == null) {
                    if (map.get(rr).get(cc) != '#') {
                        cur = next;
                        pico++;
                        break;
                    }
                }
            }
        }
        picos.put(end, pico);
        System.out.println("path: " + pico);
        // best for in: 9316

        // Follow the path again, sum up the shortcuts
        HashMap<Long, AtomicInteger> counts = new HashMap<>();
        for (Pos cur = start; !cur.equals(end); ) {
            Pos next = null;
            for (Direction direction : Direction.values()) {
                int rr = cur.row + direction.r;
                int cc = cur.col + direction.c;
                Pos candNext = new Pos(rr, cc);
                if (map.get(rr).get(cc) != '#') {
                    if (picos.getOrDefault(candNext, Long.MAX_VALUE) == picos.getOrDefault(cur, -10L) + 1) {
                        next = candNext;
                    }
                } else {
                    // see if there's a useful cheat
                    for (Direction d : Direction.values()) {
                        int chr = candNext.row + d.r;
                        int chc = candNext.col + d.c;
                        Pos candAfterCheat = new Pos(chr, chc);
                        if (picos.containsKey(candAfterCheat)) {
                            long improvement = picos.get(candAfterCheat) - picos.get(cur) - 2;
                            if (improvement > 0) {
                                counts.computeIfAbsent(improvement, k -> new AtomicInteger(0)).addAndGet(1);
                            }
                        }
                    }
                }
            }
            cur = next;
        }

        long p1 = 0;
        for (Long key : counts.keySet()) {
            if (key >= 100) {
                p1 += counts.get(key).get();
            }
        }
        System.out.println(p1);

        counts.clear();
        for (Pos cur = start; !cur.equals(end); ) {
            Pos next = null;
            List<Pos> hashes = new ArrayList<>(3);
            for (Direction direction : Direction.values()) {
                int rr = cur.row + direction.r;
                int cc = cur.col + direction.c;
                Pos candNext = new Pos(rr, cc);
                if (map.get(rr).get(cc) != '#') {
                    if (picos.getOrDefault(candNext, Long.MAX_VALUE) == picos.getOrDefault(cur, -10L) + 1) {
                        next = candNext;
                    }
                } else {
                    hashes.add(candNext);
                }
            }
            HashSet<Cheat> cheats = findCheats(map, cur, hashes, picos);
            for (Cheat cheat : cheats) {
                long improvement = picos.get(cheat.end) - picos.get(cur) - cheat.dist;
                if (improvement > 0) {
                    counts.computeIfAbsent(improvement, k -> new AtomicInteger(0)).addAndGet(1);
                }
                if (improvement == 66) {
                    System.out.printf("*** 66er %s=%d ->%d-> %s=%d%n", cheat.begin, picos.get(cheat.begin), cheat.dist, cheat.end, picos.get(cheat.end));
                }
            }
            cur = next;
        }

        ArrayList<Long> keys = new ArrayList<>(counts.keySet());
        keys.sort(Long::compareTo);
        for (Long key : keys) {
            System.out.printf("%d way(s) to save %d%n", counts.get(key).get(), key);
        }
    }

    private static HashSet<Cheat> findCheats(List<List<Character>> map, Pos start, List<Pos> firstHash, HashMap<Pos, Long> picos) {
        HashSet<Cheat> foundCheats = new HashSet<>();
        PriorityQueue<State> queue = new PriorityQueue<>();
        HashSet<Pos> seen = new HashSet<>();
        HashSet<Pos> seenOnPath = new HashSet<>();
        firstHash.forEach(pos -> queue.add(new State(pos, 1)));
        while (!queue.isEmpty()) {
            State state = queue.poll();
            if (seen.contains(state.pos)) {
                continue;
            }
            if (state.dist > 19) {
                continue;
            }
            seen.add(state.pos);
            for (Direction dir : Direction.values()) {
                int hashr = state.r() + dir.r;
                int hashc = state.c() + dir.c;
                if (hashr < 1 || hashr >= map.size() - 1 || hashc < 1 || hashc >= map.get(hashr).size() - 1) {
                    continue;
                }
                Pos move = new Pos(hashr, hashc);
                if (seen.contains(move)) {
                    continue;
                }
                if (picos.get(move) != null) {
                    if (!seenOnPath.contains(move) && picos.get(move) > picos.get(start) + state.dist + 1) {
                        seenOnPath.add(move);
                        foundCheats.add(new Cheat(start, move, state.dist + 1));
                    }
                }
                queue.add(new State(move, state.dist + 1));
            }
        }
        return foundCheats;
    }


    private static void print(List<List<Character>> map, Set<Pos> onBestPath) {
        for (int r = 0; r < map.size(); r++) {
            for (int c = 0; c < map.get(r).size(); c++) {
                Character terrain = map.get(r).get(c);
                if (onBestPath.contains(new Pos(r, c))) {
                    System.out.print(terrain == '#' ? 'X' : 'O');
                } else {
                    System.out.print(terrain);
                }
            }
            System.out.println();
        }
    }

    record State(Pos pos, int dist) implements Comparable<State> {
        int r() {
            return pos.row();
        }

        int c() {
            return pos.col();
        }

        @Override
        public int compareTo(State o) {
            return dist() - o.dist();
        }
    }

    record Pos(int row, int col) {
    }

    record Cheat(Pos begin, Pos end, int dist) {
    }

    enum Direction {
        NORTH(-1, 0),
        EAST(0, 1),
        SOUTH(1, 0),
        WEST(0, -1);

        final int r;
        final int c;

        Direction(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
}