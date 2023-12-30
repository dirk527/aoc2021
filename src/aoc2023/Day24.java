package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day24 {
    private static final MathContext CONTEXT = MathContext.DECIMAL128;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("24-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("24-sample.txt"));

        // 19, 13, 30 @ -2,  1, -2
        Pattern pat = Pattern.compile("([0-9-]+), +([0-9-]+), +([0-9-]+) +@ +([0-9-]+), +([0-9-]+), +([0-9-]+)");
        List<Hailstone> hail = new ArrayList<>();
        String s;
        while ((s = br.readLine()) != null) {
            Matcher mat = pat.matcher(s);
            if (mat.matches()) {
                hail.add(new Hailstone(Long.parseLong(mat.group(1)), Long.parseLong(mat.group(2)),
                        Long.parseLong(mat.group(3)), Long.parseLong(mat.group(4)), Long.parseLong(mat.group(5)),
                        Long.parseLong(mat.group(6))));
            } else {
                throw new IllegalArgumentException(s);
            }
        }

        // part 1: ignore z, find intersections. Hail is a linear equation f(x) = ax + b
        // a and b are methods in Hailstone.

        // for sample
        //double min = 7;
        //double max = 27;
        double min = 200000000000000d;
        double max = 400000000000000d;

        int count = 0;
        for (int i = 0; i < hail.size(); i++) {
            Hailstone one = hail.get(i);
            for (int j = i + 1; j < hail.size(); j++) {
                Hailstone two = hail.get(j);
                double x = (two.b() - one.b()) / (one.a() - two.a());
                double y = one.a() * x + one.b();
                boolean inBounds = x >= min && x <= max && y >= min && y <= max;
                boolean futureOne = Math.signum(x - one.sx) == Math.signum(one.vx);
                boolean futureTwo = Math.signum(x - two.sx) == Math.signum(two.vx);
//                System.out.printf("%s %s: %7.3f, %7.3f %b %b %b\n", one, two, x, y, inBounds, futureOne, futureTwo);
                if (inBounds && futureOne && futureTwo) {
                    count++;
                }
            }
        }
        System.out.println(count);

        // part 2: see JPG for how I arrived at these formulas
        Hailstone one = hail.get(0);
        // turns out - Java double is 64 bit, and the numbers in the real input are too large to be accurately
        // represented. The final result is 5 off due to rounding errors. 128 Bit-BigDecimals are good enough.
        BigDecimal[][] matrix = new BigDecimal[4][5];
        for (int i = 0; i < 4; i++) {
            Hailstone two = hail.get(i + 1);
            matrix[i][0] = bd(one.sy).subtract(bd(two.sy));
            matrix[i][1] = bd(two.sx).subtract(bd(one.sx));
            matrix[i][2] = bd(two.vy).subtract(bd(one.vy));
            matrix[i][3] = bd(one.vx).subtract(bd(two.vx));
            matrix[i][4] = bd(one.sy).multiply(bd(one.vx))
                    .subtract(bd(one.sx).multiply(bd(one.vy)))
                    .subtract(bd(two.sy).multiply(bd(two.vx)))
                    .add(bd(two.sx).multiply(bd(two.vy)));
        }

        gauss(matrix);

        BigDecimal rsy = matrix[3][4].divide(matrix[3][3], CONTEXT);
        BigDecimal rsx = (matrix[2][4].subtract(matrix[2][3].multiply(rsy))).divide(matrix[2][2], CONTEXT);
        BigDecimal rvy = (matrix[1][4].subtract(matrix[1][3].multiply(rsy)).subtract(matrix[1][2].multiply(rsx))).divide(matrix[1][1], CONTEXT);
        BigDecimal rvx = (matrix[0][4].subtract(matrix[0][3].multiply(rsy)).subtract(matrix[0][2].multiply(rsx)).subtract(matrix[0][1].multiply(rvy)).divide(matrix[0][0], CONTEXT));

        BigDecimal t1 = (bd(one.sx).subtract(rsx)).divide(rvx.subtract(bd(one.vx)), CONTEXT);
        BigDecimal z1 = bd(one.sz).add(t1.multiply(bd(one.vz)));
        Hailstone two = hail.get(1);
        BigDecimal t2 = (bd(two.sx).subtract(rsx)).divide(rvx.subtract(bd(two.vx)), CONTEXT);
        BigDecimal z2 = bd(two.sz).add(t2.multiply(bd(two.vz)));
        BigDecimal rvz = (z2.subtract(z1)).divide(t2.subtract(t1), CONTEXT);
        BigDecimal rsz = z1.subtract(rvz.multiply(t1));

//        System.out.printf("t1=%f t2=%f z1=%f z2=%f\n", t1, t2, z1, z2);
//        System.out.printf("rsx=%f rsy=%f rsz=%f rvx=%f rvy=%f rvz=%f\n", rsx, rsy, rsz, rvx, rvy, rvz);

        System.out.println(rsx.add(rsy).add(rsz).longValue());
    }

    static BigDecimal bd(double in) {
        return BigDecimal.valueOf(in);
    }
    
    static void gauss(BigDecimal[][] matrix) {
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
            if (matrix[idxMax][pivotCol].equals(BigDecimal.ZERO)) {
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
            }
            pivotCol++;
            pivotRow++;
        }
    }

    static class Hailstone {
        double sx, sy, sz;
        double vx, vy, vz;

        public Hailstone(long sx, long sy, long sz, long vx, long vy, long vz) {
            this.sx = sx;
            this.sy = sy;
            this.sz = sz;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
        }

        @Override
        public String toString() {
            return String.format("%.0f, %.0f @ %.0f, %.0f   %f * x +%f", sx, sy, vx, vy, a(), b());
        }

        private double b() {
            return sy - sx * (vy / vx);
        }

        private double a() {
            return vy / vx;
        }
    }
}