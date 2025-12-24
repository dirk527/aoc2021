package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Day10 {
    private static final MathContext CONTEXT = MathContext.DECIMAL128;
    private static BigDecimal ALMOST_ZERO = BigDecimal.valueOf(.0000000000000001);

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        boolean example = false;
        BufferedReader br;
        if (example) {
            br = new BufferedReader(new FileReader("10-ex"));
        } else {
            br = new BufferedReader(new FileReader("10-in"));
        }

        // Read and parse the input
        String s;
        List<Machine> allMachines = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            String[] parts = s.split(" ");
            long target = 0;
            int targetLen = -1;
            List<Long> buttons = new ArrayList<>();
            List<Long> joltages = new ArrayList<>();
            for (String part : parts) {
                String inner = part.substring(1, part.length() - 1);
                if (part.charAt(0) == '[') {
                    targetLen = inner.length();
                    inner = inner.replaceAll("#", "1");
                    inner = inner.replaceAll("\\.", "0");
                    target = Long.parseLong(inner, 2);
//                    System.out.printf("lights %20s long %3d binary %15s\n", inner, target,
//                            Long.toBinaryString(target));
                } else if (part.charAt(0) == '(') {
                    long button = 0;
                    String[] nums = inner.split(",");
                    for (String num : nums) {
                        button = button ^ ((long) Math.pow(2, targetLen - Integer.parseInt(num) - 1));
                    }
                    buttons.add(button);
//                    System.out.printf("button %20s long %3d binary %15s\n", inner, button,
//                            Long.toBinaryString(button));
                } else if (part.charAt(0) == '{') {
                    String[] jStr = inner.split(",");
                    for (String j : jStr) {
                        joltages.addFirst(Long.parseLong(j));
                    }
                }
            }
//            buttons.sort(Comparator.comparingInt(l -> Long.toBinaryString(l).replaceAll("0", "").length()));
//            buttons = buttons.reversed();
            allMachines.add(new Machine(target, buttons, joltages, targetLen));
        }
//        System.out.println(allMachines);

        // Part 1: BFS
        long result1 = 0;
        for (Machine machine : allMachines) {
            result1 += bfs(machine);
        }
        long p1Time = System.currentTimeMillis();
        System.out.printf("result1: %10d in %10d milliseconds\n", result1, p1Time - startTime);

        long result2 = 0;
        for (Machine machine : allMachines) {
            long joltage = joltageGauss(machine);
            result2 += joltage;
        }
        long p2Time = System.currentTimeMillis();
        System.out.printf("result2: %10d in %10d milliseconds\n", result2, p2Time - p1Time);
    }

    private static int bfs(Machine m) {
        int presses = 1;
        List<Long> curStates = new ArrayList<>();
        curStates.add(0L);
        List<Long> nextStates = new ArrayList<>();
        while (true) {
            for (Long state : curStates) {
                for (Long button : m.buttons) {
                    long nextState = state ^ button;
                    if (nextState == m.target) {
                        return presses;
                    }
                    nextStates.add(nextState);
                }
            }
            curStates = nextStates;
            nextStates = new ArrayList<>();
            presses++;
        }
    }

    static long joltageSearch(Machine m, int buttonsAlready, long[] state) {
        // Yeah this works in principle, but it's way too slow for the real input...
        if (buttonsAlready == m.buttons.size()) {
            return -1;
        }
        long button = m.buttons.get(buttonsAlready);
        long maxPresses = 1000;
        for (long i = 0, mask = 1; i < m.length; i++, mask <<= 1) {
            if ((button & mask) > 0) {
                maxPresses = Math.min(maxPresses, m.joltages.get((int) i) - state[(int) i]);
            }
        }
        for (long i = 0, mask = 1; i < m.length; i++, mask <<= 1) {
            if ((button & mask) > 0) {
                state[(int) i] += maxPresses;
            }
        }
        for (long p = maxPresses; p > 0; p--) {
            boolean fine = true;
            for (int i = 0; i < state.length; i++) {
                if (state[i] != m.joltages.get(i)) {
                    fine = false;
                    break;
                }
            }
            if (fine) {
                System.out.printf("win, button %s %dx\n", Long.toBinaryString(button), p);
                return p;
            }
            long res = joltageSearch(m, buttonsAlready + 1, state);
            for (long i = 0, mask = 1; i < m.length; i++, mask <<= 1) {
                if ((button & mask) > 0) {
                    state[(int) i]--;
                }
            }
            if (res != -1) {
                System.out.printf("  win, button %s %dx\n", Long.toBinaryString(button), p);
                return res + p;
            }
        }
        // try 0 presses
        return joltageSearch(m, buttonsAlready + 1, state);
    }

    static long joltageGauss(Machine m) {
        // Prepare the input matrix: rows are equations, cols are factors, last col is joltage values
        BigDecimal[][] matrix = new BigDecimal[m.joltages.size()][m.buttons.size() + 1];
        int mask = 1;
        for (int i = 0; i < m.joltages.size(); i++) {
            for (int j = 0; j < m.buttons.size(); j++) {
                Long btn = m.buttons.get(j);
                if ((btn & mask) > 0) {
                    matrix[i][j] = BigDecimal.valueOf(1);
                } else {
                    matrix[i][j] = BigDecimal.valueOf(0);
                }
            }
            mask <<= 1;
            matrix[i][m.buttons.size()] = BigDecimal.valueOf(m.joltages.get(i));
        }
        // pseudocode on https://en.wikipedia.org/wiki/Gaussian_elimination
        // transform the matrix to row-echelon form
        gaussianElimination(matrix);
        List<Integer> numPresses = new ArrayList<>();
        for (int j = 0; j < m.buttons.size(); j++) {
            numPresses.add(null);
        }
        return findBest(matrix, notLeadingButtons(matrix), m.maxJoltage(), numPresses);
    }

    private static long findBest(BigDecimal[][] matrix, List<Integer> undeterminedCols, long maxJoltage,
            List<Integer> numPresses) {
        // if all the undetermined cols have been guessed, try to calculate a valid solution and return that
        if (undeterminedCols.isEmpty()) {
            if (calculateUniqueSolution(matrix, numPresses)) {
                long ret = numPresses.stream().mapToLong(Long::valueOf).sum();
//                System.out.printf("found %d: %s\n", ret, numPresses);
                return ret;
            } else {
                return Long.MAX_VALUE;
            }
        }

        // Otherwise, try all possible values for the first col and recurse
        long ret = Long.MAX_VALUE;
        Integer myIdx = undeterminedCols.removeFirst();
        for (int i = 0; i <= maxJoltage; i++) {
            numPresses.set(myIdx, i);
            ret = Math.min(ret, findBest(matrix, undeterminedCols, maxJoltage, numPresses));
        }
        undeterminedCols.addFirst(myIdx);
        return ret;
    }

    private static boolean calculateUniqueSolution(BigDecimal[][] matrix, List<Integer> numPresses) {
        // numPresses must already contain values for any columns that are not leading in a row
        rows: for (int row = matrix.length - 1; row >= 0; row--) {
            for (int col = 0; col < matrix[0].length - 1; col++) {
                if (!isZero(matrix[row][col])) {
                    BigDecimal val = matrix[row][matrix[row].length - 1];
                    for (int cc = col + 1; cc < matrix[row].length - 1; cc++) {
                        val = val.subtract(matrix[row][cc].multiply(BigDecimal.valueOf(numPresses.get(cc))));
                    }
                    val = val.divide(matrix[row][col], CONTEXT);
                    // This was only a success if the result is a positive integer or zero
                    BigDecimal rounded = val.setScale(0, RoundingMode.HALF_UP);
                    if (val.subtract(rounded).abs().compareTo(ALMOST_ZERO) > 0) {
                        return false;
                    }
                    if (rounded.compareTo(BigDecimal.ZERO) < 0) {
                        return false;
                    }
                    numPresses.set(col, rounded.intValueExact());
                    continue rows;
                }
            }
        }
        return true;
    }

    private static List<Integer> notLeadingButtons(BigDecimal[][] matrix) {
        // a leading button is a button that has a row in which its column is the first from the left with a non-0 value
        // we need to check if there are any buttons which do not fit
        List<Integer> buttonsNotLeading = new ArrayList<>();
        int stopSearch = matrix[0].length - 1;
        for (int row = matrix.length - 1; row >= 0; row--) {
            for (int col = 0; col < stopSearch; col++) {
                if (!isZero(matrix[row][col])) {
                    int varsLeading = stopSearch - col;
                    if (varsLeading > 1) {
                        for (int btn = col + 1; btn < stopSearch; btn++) {
                            buttonsNotLeading.add(btn);
                        }
                    }
                    stopSearch = col;
                }
            }
        }
        return buttonsNotLeading;
    }

    private static boolean isZero(BigDecimal x) {
        return x.abs().compareTo(ALMOST_ZERO) < 0;
    }

    private static void printBinaryButtons(Machine m, BigDecimal[][] matrix) {
        for (char c = 'a'; c < matrix[0].length + 'a' - 1; c++) {
            System.out.printf("%2s %10s\n", m.buttons.get(c - 'a'), Long.toBinaryString(m.buttons.get(c - 'a')));
        }
    }

    private static void debugMatrix(BigDecimal[][] matrix) {
        for (int i = 0; i < matrix[0].length - 1; i++) {
            System.out.printf(" (%2s) ", i);
        }
        System.out.println();
        for (char c = 'a'; c < matrix[0].length + 'a' - 1; c++) {
            System.out.printf("%5s ", c);
        }
        System.out.println();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(3);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);
        for (BigDecimal[] row : matrix) {
            for (BigDecimal value : row) {
                System.out.printf("%5s ", df.format(value));
            }
            System.out.println();
        }
    }

    static void gaussianElimination(BigDecimal[][] matrix) {
        // See https://en.wikipedia.org/wiki/Gaussian_elimination
        int pivotRow = 0;
        int pivotCol = 0;
        int nRows = matrix.length;
        int nCols = matrix[0].length;
        while (pivotRow < nRows && pivotCol < nCols) {
            BigDecimal max = BigDecimal.ZERO;
            int idxMax = -1;
            for (int i = pivotRow; i < nRows; i++) {
                BigDecimal cand = matrix[i][pivotCol].abs();
                if (cand.compareTo(max) > 0) {
                    max = cand;
                    idxMax = i;
                }
            }
            if (idxMax == -1 || matrix[idxMax][pivotCol].equals(BigDecimal.ZERO)) {
                // nothing to pivot in this column
                pivotCol++;
            } else {
                // swap rows idxMax and pivotRow
                BigDecimal[] tmp = matrix[pivotRow];
                matrix[pivotRow] = matrix[idxMax];
                matrix[idxMax] = tmp;
                for (int i = pivotRow + 1; i < nRows; i++) {
                    // for all lower rows, subtract so that matrix[i][pivotCol] becomes 0
                    BigDecimal factor = matrix[i][pivotCol].divide(matrix[pivotRow][pivotCol], CONTEXT);
                    matrix[i][pivotCol] = BigDecimal.ZERO;
                    for (int j = pivotCol + 1; j < nCols; j++) {
                        // only need to go right, to the left it's all zeros anyway
                        matrix[i][j] = matrix[i][j].subtract(factor.multiply(matrix[pivotRow][j]));
                    }
                }
                pivotCol++;
                pivotRow++;
            }
        }
    }

    record Machine(long target, List<Long> buttons, List<Long> joltages, int length) {
        public long maxJoltage() {
            return joltages().stream().max(Long::compareTo).get();
        }
    }
}