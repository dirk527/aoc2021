package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Day18 {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
//        BufferedReader br = new BufferedReader(new FileReader("18-input.txt"));
        BufferedReader br = new BufferedReader(new FileReader("18-sample.txt"));

        String s;
        List<String> commands = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            commands.add(s);
        }

        System.out.println(solve(Part.ONE, commands));
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        System.out.println(solve(Part.TWO, commands));
        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (endTime - p1Time) / 1000f);
    }

    static long solve(Part part, List<String> commands) {
        long minx = 0;
        long miny = 0;
        long maxx = 0;
        long maxy = 0;
        long curx = 0;
        long cury = 0;
        Pos cur = new Pos(0, 0);
        HashMap<Pos, Character> holes = new HashMap<>();
        holes.put(cur, '*');
        Direction first = null;
        Direction prev = null;
        for (String command : commands) {
            System.out.print(command);
            String[] cmdParts = command.split(" ");
            Direction dir = Direction.find(part == Part.ONE ? cmdParts[0] : cmdParts[2].substring(7, 8));
            if (first == null) {
                first = dir;
            }
            if (prev != null) {
                holes.put(cur, prev.symbol(dir));
            }
            long steps = part == Part.ONE ? Long.parseLong(cmdParts[1]) : Long.parseLong(cmdParts[2].substring(2, 7), 16);
            System.out.println(" " + dir + " " + steps);
            for (int i = 0; i < steps; i++) {
                cur = cur.move(dir);
                holes.put(cur, dir.symbol(dir));
                minx = Math.min(minx, cur.x);
                miny = Math.min(miny, cur.y);
                maxx = Math.max(maxx, cur.x);
                maxy = Math.max(maxy, cur.y);
            }
            prev = dir;
        }
        holes.put(cur, prev.symbol(first));

        if (part == Part.ONE) {
            for (long y = maxy; y >= miny; y--) {
                for (long x = minx; x <= maxx; x++) {
                    Character c = holes.get(new Pos(x, y));
                    System.out.print(c != null ? c : " ");
                }
                System.out.println();
            }
            System.out.println(holes.size());
        }
        System.out.println("minx = " + minx + "; maxx = " + maxx + "; miny = " + miny + "; maxy = " + maxy);

        long lagoon = 0;
        for (long y = maxy; y >= miny; y--) {
                System.out.println("y= " + y);
            boolean inside = false;
            for (long x = minx; x <= maxx; x++) {
                Character c = holes.get(new Pos(x, y));
                if (c == null) {
                    if (inside) {
                        lagoon++;
                    }
                } else {
                    lagoon++;
                    if (c == '|' || c == 'F' || c == '7') {
                        inside = !inside;
                    }
                }
            }
        }
        return lagoon;
    }

    record Pos(long x, long y) {
        public Pos move(Direction dir) {
            return new Pos(x + dir.xOffset, y + dir.yOffset);
        }
    }

    enum Direction {
        UP(0, 1), RIGHT(1, 0), DOWN(0, -1), LEFT(-1, 0);

        final int xOffset;
        final int yOffset;

        Direction(int xOffset, int yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }


        public static Direction find(String in) {
            return switch (in) {
                case "R", "0" -> RIGHT;
                case "D", "1" -> DOWN;
                case "L", "2" -> LEFT;
                case "U", "3" -> UP;
                default -> throw new IllegalArgumentException();
            };
        }

        public char symbol(Direction next) {
            return switch (this) {
                case UP -> {
                    if (next == this) {
                        yield '|';
                    } else if (next == LEFT) {
                        yield '7';
                    } else if (next == RIGHT) {
                        yield 'F';
                    } else {
                        throw new IllegalStateException();
                    }
                }
                case DOWN -> {
                    if (next == this) {
                        yield '|';
                    } else if (next == RIGHT) {
                        yield 'L';
                    } else if (next == LEFT) {
                        yield 'J';
                    } else {
                        throw new IllegalStateException();
                    }
                }
                case LEFT -> {
                    if (next == this) {
                        yield '-';
                    } else if (next == UP) {
                        yield 'L';
                    } else if (next == DOWN) {
                        yield 'F';
                    } else {
                        throw new IllegalStateException();
                    }
                }
                case RIGHT -> {
                    if (next == this) {
                        yield '-';
                    } else if (next == UP) {
                        yield 'J';
                    } else if (next == DOWN) {
                        yield '7';
                    } else {
                        throw new IllegalStateException();
                    }
                }
            };
        }
    }

    enum Part {ONE, TWO}
}