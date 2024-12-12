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
        // read and parse
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

        // consolidate into Plants that represent contiguous areas
        int id = 0; // id is only for easier debugging
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

        // debugging output
        for (int r = 0; r < grid.size(); r++) {
            for (int c = 0; c < grid.get(r).size(); c++) {
                System.out.print(plants.get(new Pos(r, c)));
            }
            System.out.println();
        }

        int p1 = 0;
        int p2 = 0;
        for (Plants field : new HashSet<>(plants.values())) {
            int circ = field.circumference();
            int fences = field.countSides();
            System.out.printf("field %4d: %5d * %5d or %4d with discount%n", field.id, field.size(), circ, fences);
            p1 += circ * field.size();
            p2 += fences * field.size();
        }

        System.out.println("p1: " + p1);
        System.out.println("p2: " + p2);
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

        public int circumference() {
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
            return circ;
        }

        public int countSides() {
            int sides = 0;
            // from left to right: count horizontal fences by counting the left corners
            for (int r = minRow; r <= maxRow + 1; r++) {
                for (int c = minCol; c <= maxCol; c++) {
                    Plants cur = plants.get(new Pos(r, c));
                    Plants left = plants.get(new Pos(r, c - 1));
                    Plants up = plants.get(new Pos(r - 1, c));
                    Plants leftUp = plants.get(new Pos(r - 1, c - 1));
                    boolean possibleLeftCorner = (left != cur && (cur == this || left == this)) ||
                            (up != leftUp && (up == this || leftUp == this));
                    if (possibleLeftCorner) {
                        if ((up == this || cur == this) && up != cur) {
                            sides++;
                        }
                    }
                }
            }
            // from top to bottom: count vertical fences by counting the top corners
            for (int c = minCol; c <= maxCol + 1; c++) {
                for (int r = minRow; r <= maxRow; r++) {
                    Plants cur = plants.get(new Pos(r, c));
                    Plants up = plants.get(new Pos(r - 1, c));
                    Plants left = plants.get(new Pos(r, c - 1));
                    Plants leftUp = plants.get(new Pos(r - 1, c - 1));
                    boolean possibleUpperCorner = (up != cur && (cur == this || up == this)) ||
                            (left != leftUp && (left == this || leftUp == this));
                    if (possibleUpperCorner) {
                        if ((left == this || cur == this) && left != cur) {
                            sides++;
                        }
                    }
                }
            }
            return sides; // * members.size();
        }

        public int size() {
            return members.size();
        }

        @Override
        public String toString() {
            return "%3d ".formatted(id);
        }
    }
}