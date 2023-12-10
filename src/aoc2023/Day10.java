package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static aoc2023.Day10.Direction.*;
import static aoc2023.Day10.State.*;

public class Day10 {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("10-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("10-sample.txt"));

        // The input coordinates are row, col and stored in class Coord
        // these are mapped onto a 45-degree-rotated coordinate System that is in x,y
        // and stored in class Point. Hard to explain, there's a picture next to this file.

        String s;
        int row = 0;
        int startRow = -2;
        int startCol = -2;

        HashMap<Point, PipeConnection> pipesAt = new HashMap<>();
        List<String> originalLines = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            originalLines.add(s);
            for (int col = 0; col < s.length(); col++) {
                char c = s.charAt(col);
                if (c == '.') {
                    continue;
                } else if (c == 'S') {
                    startRow = row;
                    startCol = col;
                } else {
                    PipeDefinition def = PipeDefinition.find(c);
                    Point p1 = def.d1.convert(row, col);
                    Point p2 = def.d2.convert(row, col);
                    Pipe pipe = new Pipe(row, col, def, p1, p2);
                    PipeConnection pc = pipesAt.get(p1);
                    if (pc == null) {
                        pc = new PipeConnection(pipe);
                        pipesAt.put(p1, pc);
                    } else {
                        pc.addPipe(pipe);
                    }
                    pc = pipesAt.get(p2);
                    if (pc == null) {
                        pc = new PipeConnection(pipe);
                        pipesAt.put(p2, pc);
                    } else {
                        pc.addPipe(pipe);
                    }
                }
            }
            row++;
        }

        if (startRow == -2) {
            throw new IllegalStateException();
        }

        // Find the connections from the starting coordinates
        Point start = null;
        PipeConnection startConnection = null;
        Point end = null;
        for (Direction d : Direction.values()) {
            Point point = d.convert(startRow, startCol);
            PipeConnection pc = pipesAt.get(point);
            if (pc != null) {
                if (pc.p2 != null) {
                    throw new IllegalStateException();
                }
                if (start == null) {
                    start = point;
                    startConnection = pc;
                } else if (end == null) {
                    end = point;
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        // for part 2, map the coordinates to states
        HashMap<Coord, State> states = new HashMap<>();
        states.put(new Coord(startRow, startCol), START);

        // traverse through the pass
        Point prev = start;
        Point cur = startConnection.p1.follow(start, states);
        PipeConnection curConnection = pipesAt.get(cur);
        int steps = 1;
        while (!cur.equals(end)) {
            steps++;
            // follow with null does not change state for part 2
            Point cand = curConnection.p1.follow(cur, null);
            if (cand.equals(prev)) {
                cand = curConnection.p2.follow(cur, states);
            } else {
                curConnection.p1.follow(cur, states);
            }
            prev = cur;
            cur = cand;
            curConnection = pipesAt.get(cur);
        }

        // part 1 is done
        System.out.println((steps + 1) / 2);

        // figure out if outside means LEFT or RIGHT of the path
        // just look for the first occurence in sequence
        State outside = null;
        out:
        for (int r = -1; row <= originalLines.size(); r++) {
            for (int c = -1; c <= originalLines.getFirst().length(); c++) {
                State st = states.get(new Coord(r, c));
                if (st == LEFT || st == RIGHT) {
                    outside = st;
                    break out;
                }
            }
        }
        State inside = outside == LEFT ? RIGHT : LEFT;

        // the inside needs to expand to fill neighboring empty coords
        HashSet<Coord> handled = new HashSet<>();
        Deque<Coord> todo = new LinkedList<>();
        for (int r = -1; r <= originalLines.size(); r++) {
            for (int c = -1; c <= originalLines.getFirst().length(); c++) {
                Coord here = new Coord(r, c);
                State st = states.get(here);
                if (st == inside) {
                    handled.add(here);
                    todo.addFirst(new Coord(r - 1, c));
                    todo.addFirst(new Coord(r + 1, c));
                    todo.addFirst(new Coord(r, c - 1));
                    todo.addFirst(new Coord(r, c + 1));
                }
            }
        }
        while (!todo.isEmpty()) {
            Coord here = todo.removeLast();
            if (handled.contains(here)) {
                continue;
            }
            State st = states.get(here);
            if (st == null) {
                states.put(here, inside);
                int r = here.row;
                int c = here.col;
                todo.addFirst(new Coord(r - 1, c));
                todo.addFirst(new Coord(r + 1, c));
                todo.addFirst(new Coord(r, c - 1));
                todo.addFirst(new Coord(r, c + 1));
            }
            handled.add(here);
        }

        // count the inside state occurences
        int count = 0;
        for (int r = -1; r <= originalLines.size(); r++) {
            for (int c = -1; c <= originalLines.getFirst().length(); c++) {
                if (states.get(new Coord(r, c)) == inside) {
                    count++;
                }
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println(count);
        System.out.printf("%5.3f sec\n", (endTime - startTime) / 1000f);
    }

    private static void printStates(List<String> originalLines, HashMap<Coord, State> states) {
        for (int row = -1; row <= originalLines.size(); row++) {
            for (int col = -1; col <= originalLines.getFirst().length(); col++) {
                State s = states.get(new Coord(row, col));
                System.out.print(switch (s) {
                    case LEFT -> "1";
                    case RIGHT -> "2";
                    case PATH, START -> originalLines.get(row).charAt(col);
                    case null -> " ";
                });
            }
            System.out.println();
        }
    }

    /**
     * update the state only if the new state is bigger, so do not overwrite PATH or START
     */
    static void maybeUpdateState(State n, HashMap<Coord, State> state, Coord coord) {
        State s = state.get(coord);
        if (s == null || n.compareTo(s) < 0) {
            state.put(coord, n);
        }
    }

    /**
     * Coordinates in the original coordinate system
     */
    record Coord(int row, int col) {
    }

    enum State {
        START,
        PATH,
        LEFT,
        RIGHT
    }

    enum Direction {
        NORTH(0, 0),
        EAST(1, 0),
        SOUTH(1, 1),
        WEST(0, 1);

        final int x;
        final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Point convert(int row, int col) {
            return new Point(row + col + x, row - col + y);
        }
    }

    enum PipeDefinition {
        BAR('|', NORTH, SOUTH),
        MINUS('-', EAST, WEST),
        L('L', NORTH, EAST),
        J('J', NORTH, WEST),
        SEVEN('7', SOUTH, WEST),
        F('F', SOUTH, EAST);

        private final char c;
        private final Direction d1;
        private final Direction d2;

        PipeDefinition(char c, Direction d1, Direction d2) {
            this.c = c;
            this.d1 = d1;
            this.d2 = d2;
        }

        public static PipeDefinition find(char in) {
            for (PipeDefinition cand : values()) {
                if (cand.c == in) {
                    return cand;
                }
            }
            throw new IllegalArgumentException();
        }

        // central method for part 2: mark what is LEFT, RIGHT or part of the PATH
        public void modifyStates(Point start, int row, int col, HashMap<Coord, State> state) {
            maybeUpdateState(PATH, state, new Coord(row, col));
            switch (this) {
                case BAR -> {
                    State east, west;
                    if (NORTH.convert(row, col).equals(start)) {
                        east = LEFT;
                        west = RIGHT;
                    } else if (SOUTH.convert(row, col).equals(start)) {
                        east = RIGHT;
                        west = LEFT;
                    } else {
                        throw new IllegalArgumentException();
                    }
                    maybeUpdateState(east, state, new Coord(row, col + 1));
                    maybeUpdateState(west, state, new Coord(row, col - 1));
                }
                case MINUS -> {
                    State north, south;
                    if (WEST.convert(row, col).equals(start)) {
                        north = LEFT;
                        south = RIGHT;
                    } else if (EAST.convert(row, col).equals(start)) {
                        north = RIGHT;
                        south = LEFT;
                    } else {
                        throw new IllegalArgumentException();
                    }
                    maybeUpdateState(north, state, new Coord(row - 1, col));
                    maybeUpdateState(south, state, new Coord(row + 1, col));
                }
                case L -> {
                    State inner, outer;
                    if (NORTH.convert(row, col).equals(start)) {
                        inner = LEFT;
                        outer = RIGHT;
                    } else if (EAST.convert(row, col).equals(start)) {
                        inner = RIGHT;
                        outer = LEFT;
                    } else {
                        throw new IllegalArgumentException();
                    }
                    maybeUpdateState(inner, state, new Coord(row - 1, col + 1));
                    maybeUpdateState(outer, state, new Coord(row, col - 1));
                    maybeUpdateState(outer, state, new Coord(row + 1, col - 1));
                    maybeUpdateState(outer, state, new Coord(row + 1, col));
                }
                case J -> {
                    State inner, outer;
                    if (WEST.convert(row, col).equals(start)) {
                        inner = LEFT;
                        outer = RIGHT;
                    } else if (NORTH.convert(row, col).equals(start)) {
                        inner = RIGHT;
                        outer = LEFT;
                    } else {
                        throw new IllegalArgumentException();
                    }
                    maybeUpdateState(inner, state, new Coord(row - 1, col - 1));
                    maybeUpdateState(outer, state, new Coord(row, col + 1));
                    maybeUpdateState(outer, state, new Coord(row + 1, col + 1));
                    maybeUpdateState(outer, state, new Coord(row + 1, col));
                }
                case SEVEN -> {
                    State inner, outer;
                    if (SOUTH.convert(row, col).equals(start)) {
                        inner = LEFT;
                        outer = RIGHT;
                    } else if (WEST.convert(row, col).equals(start)) {
                        inner = RIGHT;
                        outer = LEFT;
                    } else {
                        throw new IllegalArgumentException();
                    }
                    maybeUpdateState(inner, state, new Coord(row + 1, col - 1));
                    maybeUpdateState(outer, state, new Coord(row - 1, col));
                    maybeUpdateState(outer, state, new Coord(row - 1, col + 1));
                    maybeUpdateState(outer, state, new Coord(row, col + 1));
                }
                case F -> {
                    State inner, outer;
                    if (EAST.convert(row, col).equals(start)) {
                        inner = LEFT;
                        outer = RIGHT;
                    } else if (SOUTH.convert(row, col).equals(start)) {
                        inner = RIGHT;
                        outer = LEFT;
                    } else {
                        throw new IllegalArgumentException();
                    }
                    maybeUpdateState(inner, state, new Coord(row + 1, col + 1));
                    maybeUpdateState(outer, state, new Coord(row - 1, col));
                    maybeUpdateState(outer, state, new Coord(row - 1, col - 1));
                    maybeUpdateState(outer, state, new Coord(row, col - 1));
                }
            }
        }
    }

    // in the rotated coordinate system for connection points
    record Point(int x, int y) {
    }

    record Pipe(int row, int col, PipeDefinition def, Point p1, Point p2) {
        public Point follow(Point start, HashMap<Coord, State> state) {
            if (state != null) {
                def.modifyStates(start, row, col, state);
            }
            if (p1.equals(start)) {
                return p2;
            }
            if (p2.equals(start)) {
                return p1;
            }
            throw new IllegalArgumentException();
        }
    }

    static class PipeConnection {
        Pipe p1, p2;

        public PipeConnection(Pipe p1) {
            this.p1 = p1;
        }

        public void addPipe(Pipe p2) {
            if (this.p2 != null) {
                throw new IllegalStateException();
            }
            this.p2 = p2;
        }
    }
}