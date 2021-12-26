import java.util.*;

public class Day23B {

    private static final char[] EXAMPLE = {
            '.', '.', '.', '.', '.', '.', '.',
            'B', 'D', 'D', 'A',
            'C', 'C', 'B', 'D',
            'B', 'B', 'A', 'C',
            'D', 'A', 'C', 'A'};
    private static final char[] INPUT = {'.', '.', '.', '.', '.', '.', '.',
            'D', 'D', 'D', 'B',
            'A', 'C', 'B', 'C',
            'C', 'B', 'A', 'B',
            'D', 'A', 'C', 'A'};

    /*
              #############
              #01.2.3.4.56#
              ###B#C#B#D###
                #A#D#C#A#
                #########
             */
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
//        for debugging, define state like this
//        State initial = new State(new char[]
//                {'A', 'A', '.', 'C', 'B', 'B', 'D', 'B', 'D', 'D', 'A', '.', '.', 'B', 'D', '.', '.', 'C', 'C', '.', '.', 'C', 'A'}, 3988
//                , null
//        );
        State initial = new State(INPUT, 0, null);
        System.out.println("---Initial---");
        System.out.println(initial);
        PriorityQueue<State> curMoves = new PriorityQueue<>(Comparator.comparingLong(value -> value.energy));
        HashSet<State> handled = new HashSet<>();
        curMoves.add(initial);
        boolean debug = false;
        int moves = 0;
        long checkpoint = System.currentTimeMillis();
        int discarded = 0;
        while (!curMoves.peek().done()) {
            State best = curMoves.poll();

            // equals is only defined on the chars, not energy
            if (handled.contains(best)) {
                discarded++;
                continue;
            }
            handled.add(best);

            moves++;
            if (moves % 10000 == 0) {
                long curTime = System.currentTimeMillis();
                System.out.println("queue: " + curMoves.size() + "; moves = " + moves + "; discarded = " + discarded + "; time = " +
                        (curTime-checkpoint) + "ms");
                System.out.println(best);
                checkpoint = curTime;
            }
            if (debug) {
                ArrayList<State> list = new ArrayList<>(best.possibleMoves());
                list.sort(Comparator.comparingLong(value -> value.energy));
                for (State state : list) {
                    System.out.println(state);
                }
                System.exit(0);
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
        public static final char[] END_STATE = {
                '.', '.', '.', '.', '.', '.', '.',
                'A', 'A', 'A', 'A',
                'B', 'B', 'B', 'B',
                'C', 'C', 'C', 'C',
                'D', 'D', 'D', 'D'};
        private State prev;

        char[] data; // 23 elements: first seven are hallway, then ABCD first upper then lower
        long energy;

        public State(char[] data, long energy, State prev) {
            this.data = data;
            this.energy = energy;
//            this.prev = prev;
        }

        private char hallway(int i) {
            return data[i];
        }

        public List<State> possibleMoves() {
            List<State> ret = new ArrayList<>();

            col(ret, 'A', 7);
            col(ret, 'B', 11);
            col(ret, 'C', 15);
            col(ret, 'D', 19);

            if (hallway(0) != '.' && hallway(1) == '.') {
                hallwayToTarget(ret, 0, 'A', 3);
                if (hallway(2) == '.') {
                    hallwayToTarget(ret, 0, 'B', 5);
                    if (hallway(3) == '.') {
                        hallwayToTarget(ret, 0, 'C', 7);
                        if (hallway(4) == '.') {
                            hallwayToTarget(ret, 0, 'D', 9);
                        }
                    }
                }
            }
            if (hallway(1) != '.') {
                hallwayToTarget(ret, 1, 'A', 2);
                if (hallway(2) == '.') {
                    hallwayToTarget(ret, 1, 'B', 4);
                    if (hallway(3) == '.') {
                        hallwayToTarget(ret, 1, 'C', 6);
                        if (hallway(4) == '.') {
                            hallwayToTarget(ret, 1, 'D', 8);
                        }
                    }
                }
            }
            if (hallway(2) != '.') {
                hallwayToTarget(ret, 2, 'A', 2);
                hallwayToTarget(ret, 2, 'B', 2);
                if (hallway(3) == '.') {
                    hallwayToTarget(ret, 2, 'C', 4);
                    if (hallway(4) == '.') {
                        hallwayToTarget(ret, 2, 'D', 6);
                    }
                }
            }
            if (hallway(3) != '.') {
                hallwayToTarget(ret, 3, 'B', 2);
                hallwayToTarget(ret, 3, 'C', 2);
                if (hallway(2) == '.') {
                    hallwayToTarget(ret, 3, 'A', 4);
                }
                if (hallway(4) == '.') {
                    hallwayToTarget(ret, 3, 'D', 4);
                }
            }
            if (hallway(4) != '.') {
                hallwayToTarget(ret, 4, 'D', 2);
                hallwayToTarget(ret, 4, 'C', 2);
                if (hallway(3) == '.') {
                    hallwayToTarget(ret, 4, 'B', 4);
                    if (hallway(2) == '.') {
                        hallwayToTarget(ret, 4, 'A', 6);
                    }
                }
            }
            if (hallway(5) != '.') {
                hallwayToTarget(ret, 5, 'D', 2);
                if (hallway(4) == '.') {
                    hallwayToTarget(ret, 5, 'C', 4);
                    if (hallway(3) == '.') {
                        hallwayToTarget(ret, 5, 'B', 6);
                        if (hallway(2) == '.') {
                            hallwayToTarget(ret, 5, 'A', 8);
                        }
                    }
                }
            }
            if (hallway(6) != '.' && hallway(5) == '.') {
                hallwayToTarget(ret, 6, 'D', 3);
                if (hallway(4) == '.') {
                    hallwayToTarget(ret, 6, 'C', 5);
                    if (hallway(3) == '.') {
                        hallwayToTarget(ret, 6, 'B', 7);
                        if (hallway(2) == '.') {
                            hallwayToTarget(ret, 6, 'A', 9);
                        }
                    }
                }
            }

            return ret;
        }

        private void col(List<State> ret, char target, int startIdx) {
            int offset = 0;
            while (offset < 4) {
                int targetIdx = startIdx + offset;
                if (data[targetIdx] != '.') {
                    break;
                }
                offset++;
            }
            if (offset < 4) {
                boolean move;
                if (data[startIdx + offset] != target) {
                    move = true;
                } else {
                    move = false;
                    for (int i = offset + 1; i < 4; i++) {
                        if (data[startIdx + i] != target) {
                            move = true;
                            break;
                        }
                    }
                }
                if (move) {
                    switch (target) {
                        case 'A' -> colA(ret, offset + startIdx, offset);
                        case 'B' -> colB(ret, offset + startIdx, offset);
                        case 'C' -> colC(ret, offset + startIdx, offset);
                        case 'D' -> colD(ret, offset + startIdx, offset);
                    }
                }
            }
        }

        private void hallwayToTarget(List<State> ret, int from, char mover, int distToUpper) {
            int upperIdx = switch (mover) {
                case 'A' -> 7;
                case 'B' -> 11;
                case 'C' -> 15;
                case 'D' -> 19;
                default -> throw new IllegalArgumentException();
            };
            if (data[from] == mover) {
                int stepsInCol = 0;
                while (stepsInCol < 4) {
                    if (data[upperIdx + stepsInCol] != '.') {
                        break;
                    }
                    stepsInCol++;
                }
                if (stepsInCol == 0) {
                    return;
                }
                if (stepsInCol == 4) {
                    ret.add(move(from, upperIdx + 3, distToUpper + 3));

                } else {
                    for (int i = stepsInCol; i < 4; i++) {
                        if (data[upperIdx + i] != mover) {
                            return;
                        }
                    }
                    ret.add(move(from, upperIdx + stepsInCol - 1, distToUpper + stepsInCol - 1));
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
            sb.append(data[11]);
            sb.append('#');
            sb.append(data[15]);
            sb.append('#');
            sb.append(data[19]);
            sb.append("###\n  #");
            sb.append(data[8]);
            sb.append('#');
            sb.append(data[12]);
            sb.append('#');
            sb.append(data[16]);
            sb.append('#');
            sb.append(data[20]);
            sb.append("#\n  #");
            sb.append(data[9]);
            sb.append('#');
            sb.append(data[13]);
            sb.append('#');
            sb.append(data[17]);
            sb.append('#');
            sb.append(data[21]);
            sb.append("#\n  #");
            sb.append(data[10]);
            sb.append('#');
            sb.append(data[14]);
            sb.append('#');
            sb.append(data[18]);
            sb.append('#');
            sb.append(data[22]);
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
