package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day24 {
    public static void main(String[] args) throws IOException {
        System.out.println(fromBinary(calc24ex()));
        System.out.println(fromBinary(calc24ex2()));
//        System.out.println(fromBinary(calc24in()));

        String fileName = "24-in";
        crossCompile(fileName);
    }

    private static long fromBinary(int[] ex) {
        long exResult = 0;
        for (int i = ex.length - 1; i >= 0; i--) {
            exResult *= 2;
            exResult += ex[i];
        }
        return exResult;
    }

    private static void crossCompile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String s;
        Pattern inputPat = Pattern.compile("([xy]\\d\\d): (\\d)");
        Pattern gatePat = Pattern.compile("(...) (AND|OR|XOR) (...) -> (...)");

        int zMax = 0;
        while ((s = br.readLine()) != null) {
            Matcher m = gatePat.matcher(s);
            if (m.matches()) {
                if (m.group(4).startsWith("z")) {
                    zMax = Math.max(zMax, Integer.parseInt(m.group(4).substring(1)));
                }
            }
        }
        br.close();
        br = new BufferedReader(new FileReader(fileName));

        System.out.println("private static int[] calc" + fileName.replaceAll("-", "") + "() {");

        Set<String> seen = new HashSet<>();
        while (!(s = br.readLine()).isEmpty()) {
            Matcher m = inputPat.matcher(s);
            if (m.matches()) {
                seen.add(m.group(1));
                System.out.printf("int %s = %s;%n", m.group(1), m.group(2));
            }
        }
        System.out.println("int[] z = new int[" + (zMax + 1) + "];");
        Set<Gate> deferred = new HashSet<>();
        while ((s = br.readLine()) != null) {
            Matcher m = gatePat.matcher(s);
            if (m.matches()) {
                String op = switch (m.group(2)) {
                    case "AND" -> "&";
                    case "XOR" -> "^";
                    case "OR" -> "|";
                    default -> "ERROR";
                };
                String target = m.group(4);
                if (target.startsWith("z")) {
                    String zIdx = target.substring(1);
                    if (zIdx.startsWith("0")) {
                        zIdx = zIdx.substring(1);
                    }
                    target = "z[" + zIdx + "]";
                }
                deferred.add(new Gate(m.group(1), m.group(3), op, target));
                deferred.removeIf(gate -> gate.output(seen));
            }
        }
        while (!deferred.isEmpty()) {
            deferred.removeIf(gate -> gate.output(seen));
        }
        System.out.println("return z;\n}");
    }

    record Gate(String op1, String op2, String op, String target) {
        public boolean output(Set<String> seen) {
            if (!seen.contains(op1) || !seen.contains(op2)) {
                return false;
            }
            if (!target.startsWith("z") && !seen.contains(target)) {
                seen.add(target);
                System.out.print("int ");
            }
            System.out.printf("%s = %s %s %s;%n", target, op1, op, op2);
            return true;
        }
    }

    private static int[] calc24ex() {
        int x00 = 1;
        int x01 = 1;
        int x02 = 1;
        int y00 = 0;
        int y01 = 1;
        int y02 = 0;
        int[] z = new int[3];
        z[00] = x00 & y00;
        z[01] = x01 ^ y01;
        z[02] = x02 | y02;
        return z;
    }

    private static int[] calc24ex2() {
        int x00 = 1;
        int x01 = 0;
        int x02 = 1;
        int x03 = 1;
        int x04 = 0;
        int y00 = 1;
        int y01 = 1;
        int y02 = 1;
        int y03 = 1;
        int y04 = 1;
        int[] z = new int[13];
        int tnw = y02 | x01;
        int fst = x00 | x03;
        int djm = y00 & y03;
        int psh = y03 | y00;
        int frj = tnw | fst;
        int vdt = x03 | x00;
        int kjc = x04 & y00;
        int rvg = kjc & fst;
        int fgs = y04 | y02;
        int bfw = vdt | tnw;
        int pbm = y01 & x02;
        int tgd = psh ^ fgs;
        z[1] = tgd ^ rvg;
        int kpj = pbm | djm;
        int ffh = x03 ^ y03;
        z[10] = bfw & frj;
        int ntg = x00 ^ y04;
        int kwq = ntg | kjc;
        z[5] = kwq | kpj;
        int qhw = djm | pbm;
        int mjb = ntg ^ fgs;
        z[0] = bfw ^ mjb;
        z[9] = qhw ^ tgd;
        z[4] = frj ^ qhw;
        int nrd = y03 | x01;
        int bqk = ffh | nrd;
        z[6] = bfw | bqk;
        z[8] = bqk | frj;
        z[7] = bqk | frj;
        int hwm = nrd & vdt;
        int wpb = nrd ^ fgs;
        z[3] = hwm & bqk;
        z[12] = tgd ^ rvg;
        int gnj = tnw | pbm;
        z[2] = gnj & wpb;
        z[11] = gnj & tgd;
        return z;
    }




}