package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day18 {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("18-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("18-sample.txt"));

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
        Pos cur = new Pos(0, 0);
        Trench trench = new Trench();
        Direction first = null;
        Direction prev = null;
        for (String command : commands) {
            System.out.print(command);
            String[] cmdParts = command.split(" ");
            Direction dir = Direction.find(part == Part.ONE ? cmdParts[0] : cmdParts[2].substring(7, 8));
            if (first == null) {
                first = dir;
            }
            long steps = part == Part.ONE ? Long.parseLong(cmdParts[1]) : Long.parseLong(cmdParts[2].substring(2, 7), 16);
            System.out.println(" " + dir + " " + steps);
            if (prev != null) {
                trench.put(cur, prev.symbol(dir));
            }
            if (dir == Direction.UP || dir == Direction.DOWN) {
                for (int i = 0; i < steps; i++) {
                    cur = cur.move(dir);
                    trench.put(cur, dir.symbol(dir));
                    miny = Math.min(miny, cur.y);
                    maxy = Math.max(maxy, cur.y);
                }
            } else {
                cur = new Pos(cur.x + dir.xOffset * steps, cur.y);
                minx = Math.min(minx, cur.x);
                maxx = Math.max(maxx, cur.x);
            }
            prev = dir;
        }
        trench.put(cur, prev.symbol(first));

        System.out.println("minx = " + minx + "; maxx = " + maxx + "; miny = " + miny + "; maxy = " + maxy);
        if (part == Part.ONE) {
            for (long y = maxy; y >= miny; y--) {
                long lastX = minx - 1;
                boolean intrench = false;

                for (var event : trench.eventsAt(y).entrySet()) {
                    char c = event.getValue();
                    for (int i = 0; i < event.getKey() - lastX - 1; i++) {
                        System.out.print(intrench ? "-" : ".");
                    }
                    if (c == 'F' || c == '7') {
                        intrench = !intrench;
                    }
                    if (c == 'J' || c == 'L') {
                        intrench = !intrench;
                    }
                    System.out.print(c);
                    lastX = event.getKey();
                }
                System.out.println();
            }
        }

        long lagoon = 0;
        for (long y = maxy; y >= miny; y--) {
            if (y % 100000 == 0) {
                System.out.println("y=" + y);
            }
            boolean inside = false;
            boolean inTrench = false;
            boolean lastAdded = false;
            long lastX = minx - 1;
            for (var event : trench.eventsAt(y).entrySet()) {
//                System.out.print(event.getKey() + " - " + event.getValue());
                if (inTrench || inside) {
                    long add = event.getKey() - lastX;
//                    System.out.print(" add " + add);
                    lagoon += add;
                    if (!lastAdded) {
//                        System.out.print(" add startpoint ");
                        lagoon++;
                    }
                    lastAdded = true;
                } else {
                    lastAdded = false;
                }
                char c = event.getValue();
                if (c == '|') {
                    inside = !inside;
                }
                if (c == 'F' || c == '7') {
                    inside = !inside;
                    inTrench = !inTrench;
                }
                if (c == 'J' || c == 'L') {
                    inTrench = !inTrench;
                }
                lastX = event.getKey();
//                System.out.println();
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

    static class Trench {
        HashMap<Long, TreeMap<Long, Character>> lines = new HashMap<>();

        public void put(Pos cur, char c) {
//            System.out.println("Trench put cur = " + cur + ", c = " + c);
            lines.computeIfAbsent(cur.y, y -> new TreeMap<>()).put(cur.x, c);
        }

        public TreeMap<Long, Character> eventsAt(long y) {
            return lines.get(y);
        }
    }
}