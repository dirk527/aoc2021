package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day24 {
    public static void main(String[] args) throws IOException {
        System.out.println(fromBinary(calc24ex()));
        System.out.println(fromBinary(calc24ex2()));
        // System.out.println(fromBinary(calc24in()));

        String fileName = "24-in";
        crossCompile(fileName);

        // part1:
        // - input goes into a file called 24-in
        // - compile and run this class
        // - put the output into a method calc24in() in this class
        // - compile and run this class
        //
        // part2:
        // - refer to the diagram Day24.png
        // - look at the cross-compiled program, find the first line where the canonical name cannot be found
        // - think and look, put the wrong outputs into the HashMap switched, below
        // - repeat until you found 4 problems, the answer for part 2 is in the comment at the end of the generated method
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

        HashMap<String, String> switched = new HashMap<>();
        //        switched.put("vdc", "z12");
        //        switched.put("z12", "vdc");
        //        switched.put("z21", "nhn");
        //        switched.put("nhn", "z21");
        //        switched.put("khg", "tvb");
        //        switched.put("tvb", "khg");
        //        switched.put("gst", "z33");
        //        switched.put("z33", "gst");

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
        HashMap<String, Gate> gates = new HashMap<>();
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
                if (switched.containsKey(target)) {
                    target = switched.get(target);
                }
                if (target.startsWith("z")) {
                    String zIdx = target.substring(1);
                    if (zIdx.startsWith("0")) {
                        zIdx = zIdx.substring(1);
                    }
                    target = "z[" + zIdx + "]";
                }
                String operand1 = m.group(1);
                String operand2 = m.group(3);
                gates.put(target, new Gate(operand1, operand2, op, target));
            }
        }

        HashMap<String, String> byTarget = new HashMap<>();
        for (Map.Entry<String, Gate> entry : gates.entrySet()) {
            Gate gate = entry.getValue();
            if ((gate.op1.startsWith("x") && gate.op2.startsWith("y")) ||
                    (gate.op1.startsWith("y") && gate.op2.startsWith("x"))) {
                String num = gate.op1.substring(1);
                if (num.equals(gate.op2.substring(1))) {
                    if (gate.op.equals("^")) {
                        if (!num.equals("00")) {
                            gate.setCanonicalName("a" + num);
                            byTarget.put(gate.target, gate.canonicalName);
                        }
                    } else if (gate.op.equals("&")) {
                        if (num.equals("00")) {
                            gate.setCanonicalName("f" + num);
                            byTarget.put(gate.target, gate.canonicalName);
                        } else {
                            gate.setCanonicalName("e" + num);
                            byTarget.put(gate.target, gate.canonicalName);
                        }
                    }
                }
            }
        }
        boolean changed;
        do {
            changed = false;
            for (Map.Entry<String, Gate> entry : gates.entrySet()) {
                Gate gate = entry.getValue();
                switch (gate.op) {
                    case "&" -> {
                        String canon1 = byTarget.get(gate.op1);
                        String canon2 = byTarget.get(gate.op2);
                        if (canon1 != null && canon1.startsWith("a") && canon2 != null && canon2.startsWith("f")) {
                            int num1 = Integer.parseInt(canon1.substring(1));
                            int num2 = Integer.parseInt(canon2.substring(1));
                            if (num1 == num2 + 1 && gate.canonicalName == null) {
                                gate.setCanonicalName("d" + canon1.substring(1));
                                byTarget.put(gate.target, gate.canonicalName);
                                changed = true;
                            }
                        }
                        if (canon1 != null && canon1.startsWith("f") && canon2 != null && canon2.startsWith("a")) {
                            int num1 = Integer.parseInt(canon2.substring(1));
                            int num2 = Integer.parseInt(canon1.substring(1));
                            if (num1 == num2 + 1 && gate.canonicalName == null) {
                                gate.setCanonicalName("d" + canon2.substring(1));
                                byTarget.put(gate.target, gate.canonicalName);
                                changed = true;
                            }
                        }
                    }
                    case "|" -> {
                        String canon1 = byTarget.get(gate.op1);
                        String canon2 = byTarget.get(gate.op2);
                        if ((canon1 != null && canon1.startsWith("d") && canon2 != null && canon2.startsWith("e")) ||
                                (canon1 != null && canon1.startsWith("e") && canon2 != null && canon2.startsWith("d"))) {
                            if (canon1.substring(1).equals(canon2.substring(1)) && gate.canonicalName == null) {
                                gate.setCanonicalName("f" + canon1.substring(1));
                                byTarget.put(gate.target, gate.canonicalName);
                                changed = true;
                            }
                        }
                    }
                    case "^" -> {
                        String canon1 = byTarget.get(gate.op1);
                        String canon2 = byTarget.get(gate.op2);
                        if (canon1 != null && canon1.startsWith("a") && canon2 != null && canon2.startsWith("f")) {
                            int num1 = Integer.parseInt(canon1.substring(1));
                            int num2 = Integer.parseInt(canon2.substring(1));
                            if (num1 == num2 + 1 && gate.canonicalName == null) {
                                gate.setCanonicalName("z" + canon1.substring(1));
                                byTarget.put(gate.target, gate.canonicalName);
                                changed = true;
                            }
                        }
                        if (canon1 != null && canon1.startsWith("f") && canon2 != null && canon2.startsWith("a")) {
                            int num1 = Integer.parseInt(canon2.substring(1));
                            int num2 = Integer.parseInt(canon1.substring(1));
                            if (num1 == num2 + 1 && gate.canonicalName == null) {
                                gate.setCanonicalName("z" + canon2.substring(1));
                                byTarget.put(gate.target, gate.canonicalName);
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);

        for (Map.Entry<String, Gate> entry : gates.entrySet()) {
            Gate gate = entry.getValue();
            if (gate.canonicalName == null) {
                gate.canonicalName = String.format("*** %s %s %s", byTarget.get(gate.op1), gate.op, byTarget.get(gate.op2));
            }
        }

        List<Gate> gateList = new ArrayList<>(gates.values());
        gateList.sort((g1, g2) -> {
            if (g1.canonicalName == null && g2.canonicalName != null) {
                return -1;
            }
            if (g1.canonicalName != null && g2.canonicalName == null) {
                return 1;
            }
            if (g1.canonicalName != null && g2.canonicalName != null) {
                return g1.canonicalName.compareTo(g2.canonicalName);
            }
            return g1.target.compareTo(g2.target);
        });

        while (!gateList.isEmpty()) {
            gateList.removeIf(g -> g.output(seen));
        }

        System.out.println("// outputs that had to be switched:");
        System.out.print("// ");
        System.out.println(switched.keySet().stream().sorted().collect(Collectors.joining(",")));

        System.out.println("return z;\n}");
    }

    static class Gate {
        private String op1;
        private String op2;
        private String op;
        private String target;
        private String canonicalName;

        public Gate(String op1, String op2, String op, String target) {
            this.op1 = op1;
            this.op2 = op2;
            this.op = op;
            this.target = target;
        }

        public void setCanonicalName(String canonicalName) {
            this.canonicalName = canonicalName;
        }

        public boolean output(Set<String> seen) {
            if (!seen.contains(op1) || !seen.contains(op2)) {
                return false;
            }
            if (!target.startsWith("z") && !seen.contains(target)) {
                seen.add(target);
                System.out.print("int ");
            }
            String comment = canonicalName != null ? "  // " + canonicalName : "";
            System.out.printf("%s = %s %s %s;%s%n", target, op1, op, op2, comment);
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