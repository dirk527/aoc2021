package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Day12 {
    static HashMap<Pos, Plants> plants = new HashMap<>();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("12-in"));
        String s;
        List<List<Character>> grid = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            List<Character> line = new ArrayList<>();
            char[] charArray = s.toCharArray();
            for (int col = 0, charArrayLength = charArray.length; col < charArrayLength; col++) {
                char c = charArray[col];
                line.add(c);
            }
            grid.add(line);
        }

        int id = 0;
        for (int r = 0; r < grid.size(); r++) {
            for (int c = 0; c < grid.get(r).size(); c++) {
                char cur = grid.get(r).get(c);
                Pos curPos = new Pos(r, c);
                if (c > 0 && grid.get(r).get(c - 1) == cur) {
                    Pos leftPos = new Pos(r, c - 1);
                    plants.get(leftPos).addPos(curPos);
                    plants.put(curPos, plants.get(leftPos));
                }
                if (r > 0 && grid.get(r - 1).get(c) == cur) {
                    Pos upPos = new Pos(r - 1, c);
                    Plants curPlants = plants.get(curPos);
                    Plants upPlants = plants.get(upPos);
                    if (curPlants != null) {
                        if (curPlants != upPlants) {
                            for (Pos pos : curPlants.members) {
                                upPlants.addPos(pos);
                                plants.put(pos, upPlants);
                            }
                        }
                    } else {
                        upPlants.addPos(curPos);
                        plants.put(curPos, upPlants);
                    }
                }
                if (plants.get(curPos) == null) {
                    Plants curPlants = new Plants(id++);
                    curPlants.addPos(curPos);
                    plants.put(curPos, curPlants);
                }
            }
        }
        for (int r = 0; r < grid.size(); r++) {
            for (int c = 0; c < grid.get(r).size(); c++) {
                System.out.print(plants.get(new Pos(r,c)));
            }
            System.out.println();
        }

        int p1 = 0;
        for (Plants field : new HashSet<>(plants.values())) {
            int cost = field.fenceCost();
            System.out.printf("field %3d: %8d * %2d%n", field.id, cost, field.members.size());
            p1 += cost;
        }
        System.out.println(p1);
        int p2 = 0;
        System.out.println(p2);
    }

    record Pos(int row, int col) {
    }

    static class Plants {
        int id;
        int minRow = Integer.MAX_VALUE;
        int maxRow = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE;
        int maxCol = Integer.MIN_VALUE;
        HashSet<Pos> members = new HashSet<>();

        public Plants(int id) {
            this.id = id;
        }

        public void addPos(Pos pos) {
            members.add(pos);
            minRow = Math.min(minRow, pos.row);
            maxRow = Math.max(maxRow, pos.row);
            minCol = Math.min(minCol, pos.col);
            maxCol = Math.max(maxCol, pos.col);
        }

        public int fenceCost() {
            int circ = 0;
            for (int r = minRow; r <= maxRow; r++) {
                Plants prev = null;
                for (int c = minCol; c <= maxCol; c++) {
                    Plants cur = plants.get(new Pos(r, c));
                    if (prev != cur && (prev == this || cur == this)) {
                        circ++;
                    }
                    prev = cur;
                }
                if (prev == this) {
                    circ++;
                }
            }
            for (int c = minCol; c <= maxCol; c++) {
                Plants prev = null;
                for (int r = minRow; r <= maxRow; r++) {
                    Plants cur = plants.get(new Pos(r, c));
                    if (prev != cur && (prev == this || cur == this)) {
                        circ++;
                    }
                    prev = cur;
                }
                if (prev == this) {
                    circ++;
                }
            }
            return circ * members.size();
        }

        @Override
        public String toString() {
            return "%3d ".formatted(id);
        }
    }
}