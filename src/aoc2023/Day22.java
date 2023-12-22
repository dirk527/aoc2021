package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day22 {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("22-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("22-sample.txt"));

        List<Brick> bricks = parse(br);

        letSettleDown(bricks);

        HashMap<Brick, List<Brick>> supports = new HashMap<>();
        HashMap<Brick, List<Brick>> supportedBy = new HashMap<>();
        calculateSupports(bricks, supports, supportedBy);

        System.out.println(calculatePart1(bricks, supports, supportedBy));
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        System.out.println(calculatePart2(bricks, supportedBy));
        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (endTime - p1Time) / 1000f);

    }

    private static List<Brick> parse(BufferedReader br) throws IOException {
        Pattern pat = Pattern.compile("(\\d+),(\\d+),(\\d+)~(\\d+),(\\d+),(\\d+)");
        String s;
        List<Brick> bricks = new ArrayList<>();
        char lbl = 'A';
        while ((s = br.readLine()) != null) {
            Matcher mat = pat.matcher(s);
            if (mat.matches()) {
                int xa = Integer.parseInt(mat.group(1));
                int xb = Integer.parseInt(mat.group(4));
                int ya = Integer.parseInt(mat.group(2));
                int yb = Integer.parseInt(mat.group(5));
                int za = Integer.parseInt(mat.group(3));
                int zb = Integer.parseInt(mat.group(6));
                bricks.add(new Brick(Math.min(xa, xb), Math.max(xa, xb),
                        Math.min(ya, yb), Math.max(ya, yb),
                        Math.min(za, zb), Math.max(za, zb), lbl++));
            } else {
                throw new IllegalArgumentException(s);
            }
        }
        return bricks;
    }

    private static void letSettleDown(List<Brick> bricks) {
        boolean moved;
        do {
            moved = false;
            bricks.sort(Comparator.comparingInt((Brick b) -> b.z1));
            for (Brick moveCand : bricks) {
                if (moveCand.z1 == 1) { // already on the ground
                    continue;
                }
                boolean can = true;
                for (Brick blockCand : bricks) {
                    if (blockCand == moveCand) {
                        continue;
                    }
                    if (blockCand.blocks(moveCand)) {
                        can = false;
                        break;
                    }
                }
                if (can) {
                    moveCand.z1--;
                    moveCand.z2--;
                    moved = true;
                }
            }
        } while (moved);
    }

    private static void calculateSupports(List<Brick> bricks, HashMap<Brick, List<Brick>> supports,
                                          HashMap<Brick, List<Brick>> supportedBy) {
        for (Brick b : bricks) {
            for (Brick blockCand : bricks) {
                if (b == blockCand) {
                    continue;
                }
                if (blockCand.blocks(b)) {
                    supports.computeIfAbsent(blockCand, brick -> new ArrayList<>()).add(b);
                    supportedBy.computeIfAbsent(b, brick -> new ArrayList<>()).add(blockCand);
                }
            }
        }
    }

    private static int calculatePart1(List<Brick> bricks, HashMap<Brick, List<Brick>> supports, HashMap<Brick, List<Brick>> supportedBy) {
        int count = 0;
        for (Brick b : bricks) {
            List<Brick> l = supports.get(b);
            if (l != null && !l.isEmpty()) {
//                System.out.print("Brick " + b + " supports: ");
//                for (Brick b2 : l) {
//                    System.out.print(b2.lbl+" ");
//                }
//                System.out.println();

                boolean canRemove = true;
                for (Brick b2 : l) {
                    if (supportedBy.get(b2).size() == 1) {
                        canRemove = false;
                        break;
                    }
                }
                if (canRemove) {
//                    System.out.println("everything is supported by something else as well, can remove");
                    count++;
                }
            } else {
//                System.out.println("supports nothing, could remove " + b);
                count++;
            }
        }
        return count;
    }

    private static long calculatePart2(List<Brick> bricks, HashMap<Brick, List<Brick>> supportedBy) {
        long sum = 0;
        for (Brick firstDisintegrated : bricks) {
            Set<Brick> moved = new HashSet<>();
            moved.add(firstDisintegrated);  // well, it's moved away in a sense
            boolean moreMoved;
            do {
                moreMoved = false;
                for (Brick mightMove : bricks) {
                    if (moved.contains(mightMove) || mightMove.z1 == 1) {
                        continue;
                    }
                    List<Brick> supp = supportedBy.get(mightMove);
                    boolean stillSupported = false;
                    for (Brick suppCand : supp) {
                        if (!moved.contains(suppCand)) {
                            stillSupported = true;
                            break;
                        }
                    }
                    if (!stillSupported) {
                        moved.add(mightMove);
                        moreMoved = true;
                    }
                }
            } while (moreMoved);
            moved.remove(firstDisintegrated);
//            System.out.printf("disintegrating %s causes %d to fall\n", firstDisintegrated.lbl, moved.size());
            sum += moved.size();
        }
        return sum;
    }

    static class Brick {
        private final char lbl;
        // x1 is always <= x2 etc.
        int x1, x2, y1, y2, z1, z2;

        public Brick(int x1, int x2, int y1, int y2, int z1, int z2, char c) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.z1 = z1;
            this.z2 = z2;
            this.lbl = c;
        }

        @Override
        public String toString() {
            return String.format("%s %d,%d,%s~%d,%d,%d", lbl, x1, y1, z1, x2, y2, z2);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Brick brick)) return false;

            return lbl == brick.lbl;
        }

        @Override
        public int hashCode() {
            return lbl;
        }

        public boolean blocks(Brick moveCand) {
            if (moveCand.z1 != z2 + 1) {
                return false;
            }
            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    if (x >= moveCand.x1 && x <= moveCand.x2 &&
                            y >= moveCand.y1 && y <= moveCand.y2) {
//                        System.out.printf("%s blocks %s\n", lbl, moveCand.lbl);
                        return true;
                    }
                }
            }
            return false;
        }
    }
}