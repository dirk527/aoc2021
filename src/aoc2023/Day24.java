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

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
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
        BigDecimal[][] matrix = new BigDecimal[4][5];
        for (int i = 0; i < 4; i++) {
            Hailstone two = hail.get(i + 1);
            matrix[i][0] = BigDecimal.valueOf(one.sy).subtract(BigDecimal.valueOf(two.sy));
            matrix[i][1] = BigDecimal.valueOf(two.sx).subtract(BigDecimal.valueOf(one.sx));
            matrix[i][2] = BigDecimal.valueOf(two.vy).subtract(BigDecimal.valueOf(one.vy));
            matrix[i][3] = BigDecimal.valueOf(one.vx).subtract(BigDecimal.valueOf(two.vx));
            matrix[i][4] = BigDecimal.valueOf(one.sy).multiply(BigDecimal.valueOf(one.vx)).subtract(BigDecimal.valueOf(one.sx).multiply(BigDecimal.valueOf(one.vy))).subtract(BigDecimal.valueOf(two.sy).multiply(BigDecimal.valueOf(two.vx))).add(BigDecimal.valueOf(two.sx).multiply(BigDecimal.valueOf(two.vy)));
        }

        gauss(matrix);

        BigDecimal rsy = matrix[3][4].divide(matrix[3][3], MathContext.DECIMAL128);
        BigDecimal rsx = (matrix[2][4].subtract(matrix[2][3].multiply(rsy))).divide(matrix[2][2], MathContext.DECIMAL128);
        BigDecimal rvy = (matrix[1][4].subtract(matrix[1][3].multiply(rsy)).subtract(matrix[1][2].multiply(rsx))).divide(matrix[1][1], MathContext.DECIMAL128);
        BigDecimal rvx = (matrix[0][4].subtract(matrix[0][3].multiply(rsy)).subtract(matrix[0][2].multiply(rsx)).subtract(matrix[0][1].multiply(rvy)).divide(matrix[0][0], MathContext.DECIMAL128));

        BigDecimal t1 = (BigDecimal.valueOf(one.sx).subtract(rsx)).divide(rvx.subtract(BigDecimal.valueOf(one.vx)), MathContext.DECIMAL128);
        BigDecimal z1 = BigDecimal.valueOf(one.sz).add(t1.multiply(BigDecimal.valueOf(one.vz)));
        Hailstone two = hail.get(1);
        BigDecimal t2 = (BigDecimal.valueOf(two.sx).subtract(rsx)).divide(rvx.subtract(BigDecimal.valueOf(two.vx)), MathContext.DECIMAL128);
        BigDecimal z2 = BigDecimal.valueOf(two.sz).add(t2.multiply(BigDecimal.valueOf(two.vz)));
        BigDecimal rvz = (z2.subtract(z1)).divide(t2.subtract(t1), MathContext.DECIMAL128);
        BigDecimal rsz = z1.subtract(rvz.multiply(t1));

//        System.out.printf("t1=%f t2=%f z1=%f z2=%f\n", t1, t2, z1, z2);
//        System.out.printf("rsx=%f rsy=%f rsz=%f rvx=%f rvy=%f rvz=%f\n", rsx, rsy, rsz, rvx, rvy, rvz);

        System.out.println(rsx.add(rsy).add(rsz).longValue());
    }

    static void gauss(BigDecimal[][] matrix) {
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
                    BigDecimal factor = matrix[i][pivotCol].divide(matrix[pivotRow][pivotCol], MathContext.DECIMAL128);
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