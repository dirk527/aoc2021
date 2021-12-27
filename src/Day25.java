import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day25 {
    public static final String EXAMPLE = "day25-1.txt";
    public static final String REAL = "day25-2.txt";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(REAL));
        String input;
        List<Symbol[]> rows = new ArrayList<>();
        while ((input = br.readLine()) != null) {
            Symbol[] line = new Symbol[input.length()];
            for (int i = 0; i < input.length(); i++) {
                line[i] = switch (input.charAt(i)) {
                    case '.' -> Symbol.EMPTY;
                    case 'v' -> Symbol.DOWN;
                    case '>' -> Symbol.RIGHT;
                    default -> throw new IllegalArgumentException();
                };
            }
            rows.add(line);
        }

        int numRows = rows.size();
        int numCols = rows.get(0).length;
        boolean moved = true;
        int step = 0;
        boolean[] canMove = new boolean[Math.max(numCols, numRows)];
        while (moved) {
            if (step % 10 == 0) {
                System.out.println("\nStep " + step);
                print(rows);
            }
            step++;
            moved = false;
            for (Symbol[] row : rows) {
                for (int j = 0; j < numCols; j++) {
                    canMove[j] = (row[j]) == Symbol.RIGHT && (row[(j + 1) % numCols] == Symbol.EMPTY);
                }
                for (int j = 0; j < numCols; j++) {
                    if (canMove[j]) {
                        row[j] = Symbol.EMPTY;
                        row[(j + 1) % numCols] = Symbol.RIGHT;
                        moved = true;
                    }
                }
            }
            for (int col = 0; col < numCols; col++) {
                for (int j = 0; j < numRows; j++) {
                    canMove[j] = rows.get(j)[col] == Symbol.DOWN && rows.get((j + 1) % numRows)[col] == Symbol.EMPTY;
                }
                for (int j = 0; j < numRows; j++) {
                    if (canMove[j]) {
                        rows.get(j)[col] = Symbol.EMPTY;
                        rows.get((j + 1) % numRows)[col] = Symbol.DOWN;
                        moved = true;
                    }
                }
            }
        }

        long end = System.currentTimeMillis();
        print(rows);
        System.out.println("Time: " + (end - start) + "ms");
        System.out.println("Steps: " + (step));
    }

    private static void print(List<Symbol[]> rows) {
        int numCols = rows.get(0).length;
        for (Symbol[] row : rows) {
            for (int j = 0; j < numCols; j++) {
                System.out.print(row[j]);
            }
            System.out.println();
        }
    }

    enum Symbol {
        RIGHT,
        DOWN,
        EMPTY;

        @Override
        public String toString() {
            return switch (this) {
                case RIGHT -> ">";
                case DOWN -> "v";
                case EMPTY -> ".";
            };
        }
    }
}
