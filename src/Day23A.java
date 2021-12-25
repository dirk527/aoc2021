import java.util.*;

public class Day23A {

    private static final char[] EXAMPLE = {'.', '.', '.', '.', '.', '.', '.', 'B', 'A', 'C', 'D', 'B', 'C', 'D', 'A'};
    private static final char[] INPUT = {'.', '.', '.', '.', '.', '.', '.', 'D', 'B', 'A', 'C', 'C', 'B', 'D', 'A'};

    /*
              #############
              #01.2.3.4.56#
              ###B#C#B#D###
                #A#D#C#A#
                #########

             */
    public static void main(String[] args) {
// for debugging, define your state similar to
//        State initial = new State(new char[]
//                {'.', '.', '.', '.', '.', '.', '.', 'A', 'A', 'B', 'B', 'C', 'C', 'D', 'D'}, 12521
//        );
        long start = System.currentTimeMillis();
        State initial = new State(INPUT, 0, null);
        System.out.println("---Initial---");
        System.out.println(initial);
        PriorityQueue<State> curMoves = new PriorityQueue<>(Comparator.comparingLong(value -> value.energy));
        curMoves.add(initial);
        boolean debug = false;
        int moves = 0;
        while (!curMoves.peek().done()) {
            State best = curMoves.poll();
            // equals is only defined on chars, not energy; so removing all ways to get here with higher energy
            curMoves.removeIf(state -> state.equals(best));
            moves++;
            if (moves % 10000 == 0) {
                System.out.println("queue: " + curMoves.size() + "; moves = " + moves);
                System.out.println(best);
            }
            if (debug) {
                ArrayList<State> list = new ArrayList<>(best.possibleMoves());
                list.sort(Comparator.comparingLong(value -> value.energy));
                for (State state : list) {
                    System.out.println(state);
                }
            } else {
                curMoves.addAll(best.possibleMoves());
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("*** found end state ***");
        State state = curMoves.peek();
        long energy = state.energy;
        while (state != null) {
            System.out.println(state);
            state = state.prev;
        }
        System.out.println("needed " + moves + " steps; Set has " + curMoves.size() + " entries");
        System.out.println("time: " + (end - start) + "ms\n");
        System.out.println(energy);
    }

    @SuppressWarnings("LongLiteralEndingWithLowercaseL")
    private static class State {
        public static final char[] END_STATE =
                {'.', '.', '.', '.', '.', '.', '.', 'A', 'A', 'B', 'B', 'C', 'C', 'D', 'D'};
        private final State prev;

        char[] data; // 15 elements: first seven are hallway, then ABCD first upper then lower
        long energy;

        public State(char[] data, long energy, State prev) {
            this.data = data;
            this.energy = energy;
            this.prev = prev;
        }

        private char aUp() {
            return data[7];
        }

        private char aLow() {
            return data[8];
        }

        private char bUp() {
            return data[9];
        }

        private char bLow() {
            return data[10];
        }

        private char cUp() {
            return data[11];
        }

        private char cLow() {
            return data[12];
        }

        private char dUp() {
            return data[13];
        }

        private char dLow() {
            return data[14];
        }

        private char hallway(int i) {
            return data[i];
        }

        public List<State> possibleMoves() {
            List<State> ret = new ArrayList<>();
            // a upper
            if (aUp() != '.' && (aUp() != 'A' || aLow() != 'A')) {
                colA(ret, 7, 0);
            }
            if (aUp() == '.' && aLow() != '.' && aLow() != 'A') {
                colA(ret, 8, 1);
            }
            if (bUp() != '.' && (bUp() != 'B' || bLow() != 'B')) {
                colB(ret, 9, 0);
            }
            if (bUp() == '.' && bLow() != '.' && bLow() != 'B') {
                colB(ret, 10, 1);
            }
            if (cUp() != '.' && (cUp() != 'C' || cLow() != 'C')) {
                colC(ret, 11, 0);
            }
            if (cUp() == '.' && cLow() != '.' && cLow() != 'C') {
                colC(ret, 12, 1);
            }
            if (dUp() != '.' && (dUp() != 'D' || dLow() != 'D')) {
                colD(ret, 13, 0);
            }
            if (dUp() == '.' && dLow() != '.' && dLow() != 'D') {
                colD(ret, 14, 1);
            }

            if (hallway(0) != '.' && hallway(1) == '.') {
                hallwayToTarget(ret, 0, 'A', 7, 3);
                if (hallway(2) == '.') {
                    hallwayToTarget(ret, 0, 'B', 9, 5);
                    if (hallway(3) == '.') {
                        hallwayToTarget(ret, 0, 'C', 11, 7);
                        if (hallway(4) == '.') {
                            hallwayToTarget(ret, 0, 'D', 13, 9);
                        }
                    }
                }
            }
            if (hallway(1) != '.') {
                hallwayToTarget(ret, 1, 'A', 7, 2);
                if (hallway(2) == '.') {
                    hallwayToTarget(ret, 1, 'B', 9, 4);
                    if (hallway(3) == '.') {
                        hallwayToTarget(ret, 1, 'C', 11, 6);
                        if (hallway(4) == '.') {
                            hallwayToTarget(ret, 1, 'D', 13, 8);
                        }
                    }
                }
            }
            if (hallway(2) != '.') {
                hallwayToTarget(ret, 2, 'A', 7, 2);
                hallwayToTarget(ret, 2, 'B', 9, 2);
                if (hallway(3) == '.') {
                    hallwayToTarget(ret, 2, 'C', 11, 4);
                    if (hallway(4) == '.') {
                        hallwayToTarget(ret, 2, 'D', 13, 6);
                    }
                }
            }
            if (hallway(3) != '.') {
                hallwayToTarget(ret, 3, 'B', 9, 2);
                hallwayToTarget(ret, 3, 'C', 11, 2);
                if (hallway(2) == '.') {
                    hallwayToTarget(ret, 3, 'A', 7, 4);
                }
                if (hallway(4) == '.') {
                    hallwayToTarget(ret, 3, 'D', 13, 4);
                }
            }
            if (hallway(4) != '.') {
                hallwayToTarget(ret, 4, 'D', 13, 2);
                hallwayToTarget(ret, 4, 'C', 11, 2);
                if (hallway(3) == '.') {
                    hallwayToTarget(ret, 4, 'B', 9, 4);
                    if (hallway(2) == '.') {
                        hallwayToTarget(ret, 4, 'A', 7, 6);
                    }
                }
            }
            if (hallway(5) != '.') {
                hallwayToTarget(ret, 5, 'D', 13, 2);
                if (hallway(4) == '.') {
                    hallwayToTarget(ret, 5, 'C', 11, 4);
                    if (hallway(3) == '.') {
                        hallwayToTarget(ret, 5, 'B', 9, 6);
                        if (hallway(2) == '.') {
                            hallwayToTarget(ret, 5, 'A', 7, 8);
                        }
                    }
                }
            }
            if (hallway(6) != '.' && hallway(5) == '.') {
                hallwayToTarget(ret, 6, 'D', 13, 3);
                if (hallway(4) == '.') {
                    hallwayToTarget(ret, 6, 'C', 11, 5);
                    if (hallway(3) == '.') {
                        hallwayToTarget(ret, 6, 'B', 9, 7);
                        if (hallway(2) == '.') {
                            hallwayToTarget(ret, 6, 'A', 7, 9);
                        }
                    }
                }
            }

            return ret;
        }

        private void hallwayToTarget(List<State> ret, int from, char mover, int upperIdx, int distToUpper) {
            if (data[from] == mover) {
                int lowerIdx = upperIdx + 1;
                if (data[upperIdx] == '.' && data[lowerIdx] == '.') {
                    ret.add(move(from, lowerIdx, distToUpper + 1));
                } else if (data[upperIdx] == '.' && data[lowerIdx] == mover) {
                    ret.add(move(from, upperIdx, distToUpper));
                }
            }
        }

        private void colA(List<State> ret, int from, int add) {
            if (hallway(1) == '.') {
                ret.add(move(from, 1, 2 + add));
                if (hallway(0) == '.') {
                    ret.add(move(from, 0, 3 + add));
                }
            }
            if (hallway(2) == '.') {
                ret.add(move(from, 2, 2 + add));
                if (hallway(3) == '.') {
                    ret.add(move(from, 3, 4 + add));
                    if (hallway(4) == '.') {
                        ret.add(move(from, 4, 6 + add));
                        if (hallway(5) == '.') {
                            ret.add(move(from, 5, 8 + add));
                            if (hallway(6) == '.') {
                                ret.add(move(from, 6, 9 + add));
                            }
                        }
                    }
                }
            }
        }

        private void colB(List<State> ret, int from, int add) {
            if (hallway(2) == '.') {
                ret.add(move(from, 2, 2 + add));
                if (hallway(1) == '.') {
                    ret.add(move(from, 1, 4 + add));
                    if (hallway(0) == '.') {
                        ret.add(move(from, 0, 5 + add));
                    }
                }
            }
            if (hallway(3) == '.') {
                ret.add(move(from, 3, 2 + add));
                if (hallway(4) == '.') {
                    ret.add(move(from, 4, 4 + add));
                    if (hallway(5) == '.') {
                        ret.add(move(from, 5, 6 + add));
                        if (hallway(6) == '.') {
                            ret.add(move(from, 6, 7 + add));
                        }
                    }
                }
            }
        }

        private void colC(List<State> ret, int from, int add) {
            if (hallway(3) == '.') {
                ret.add(move(from, 3, 2 + add));
                if (hallway(2) == '.') {
                    ret.add(move(from, 2, 4 + add));
                    if (hallway(1) == '.') {
                        ret.add(move(from, 1, 6 + add));
                        if (hallway(0) == '.') {
                            ret.add(move(from, 0, 7 + add));
                        }
                    }
                }
            }
            if (hallway(4) == '.') {
                ret.add(move(from, 4, 2 + add));
                if (hallway(5) == '.') {
                    ret.add(move(from, 5, 4 + add));
                    if (hallway(6) == '.') {
                        ret.add(move(from, 6, 5 + add));
                    }
                }
            }
        }

        private void colD(List<State> ret, int from, int add) {
            if (hallway(4) == '.') {
                ret.add(move(from, 4, 2 + add));
                if (hallway(3) == '.') {
                    ret.add(move(from, 3, 4 + add));
                    if (hallway(2) == '.') {
                        ret.add(move(from, 2, 6 + add));
                        if (hallway(1) == '.') {
                            ret.add(move(from, 1, 8 + add));
                            if (hallway(0) == '.') {
                                ret.add(move(from, 0, 9 + add));
                            }
                        }
                    }
                }
            }
            if (hallway(5) == '.') {
                ret.add(move(from, 5, 2 + add));
                if (hallway(6) == '.') {
                    ret.add(move(from, 6, 3 + add));
                }
            }
        }

        private State move(int from, int to, int steps) {
            return new State(swap(data, from, to), energy + cost(data[from], steps), this);
        }

        private long cost(char mover, int steps) {
            return steps * switch (mover) {
                case 'A' -> 1l;
                case 'B' -> 10l;
                case 'C' -> 100l;
                case 'D' -> 1000l;
                default -> throw new IllegalStateException("Unexpected value: " + mover);
            };
        }

        public boolean done() {
            return Arrays.equals(data, END_STATE);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            State state = (State) o;
            return Arrays.equals(data, state.data);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(data);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("############# ");
            sb.append(energy);
            sb.append("\n#");
            sb.append(data[0]);
            sb.append(data[1]);
            sb.append('.');
            sb.append(data[2]);
            sb.append('.');
            sb.append(data[3]);
            sb.append('.');
            sb.append(data[4]);
            sb.append('.');
            sb.append(data[5]);
            sb.append(data[6]);
            sb.append("#\n###");
            sb.append(data[7]);
            sb.append('#');
            sb.append(data[9]);
            sb.append('#');
            sb.append(data[11]);
            sb.append('#');
            sb.append(data[13]);
            sb.append("###\n  #");
            sb.append(data[8]);
            sb.append('#');
            sb.append(data[10]);
            sb.append('#');
            sb.append(data[12]);
            sb.append('#');
            sb.append(data[14]);
            sb.append("#\n  #########   {'");
            for (int i = 0; i < data.length; i++) {
                sb.append(data[i]);
                if (i < data.length - 1) {
                    sb.append("', '");
                }
            }
            sb.append("'}, ");
            sb.append(energy);
            sb.append("\n");
            return sb.toString();
        }
    }

    private static char[] swap(char[] in, int a, int b) {
        char[] x = in.clone();
        char t = x[a];
        x[a] = x[b];
        x[b] = t;
        return x;
    }
}
