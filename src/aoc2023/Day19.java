package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19 {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("19-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("19-sample.txt"));

        String s;
        boolean parsingMetals = false;
        Pattern metalPat = Pattern.compile("\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)}");
        Pattern workflowPat = Pattern.compile("([a-z]+)\\{([^}]+)}");
        HashMap<String, Workflow> workflows = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            if (s.isEmpty()) {
                parsingMetals = true;
            } else if (parsingMetals) {
                Matcher mat = metalPat.matcher(s);
                if (mat.matches()) {
                    long[] vals = new long[]{
                            Long.parseLong(mat.group(1)),
                            Long.parseLong(mat.group(2)),
                            Long.parseLong(mat.group(3)),
                            Long.parseLong(mat.group(4))
                    };
                    parts.add(new Part(vals));
                } else {
                    throw new IllegalArgumentException(s);
                }
            } else {
                Matcher mat = workflowPat.matcher(s);
                if (mat.matches()) {
                    workflows.put(mat.group(1), new Workflow(mat.group(2)));
                } else {
                    throw new IllegalArgumentException(s);
                }
            }
        }

        System.out.println(sortParts(workflows, parts));
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        System.out.println(sortRange(workflows, 1, 4001));
        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (endTime - p1Time) / 1000f);
    }

    static long sortParts(HashMap<String, Workflow> rules, List<Part> parts) {
        List<Part> accepted = new ArrayList<>();

        for (Part part : parts) {
            String cur = "in";
            while (!"R".equals(cur) && !"A".equals(cur)) {
                Workflow wf = rules.get(cur);
                cur = wf.apply(part);
            }
            if ("A".equals(cur)) {
                accepted.add(part);
            }
        }

        long sum = 0;
        for (Part a : accepted) {
            sum += a.rating();
        }
        return sum;
    }

    private static long sortRange(HashMap<String, Workflow> workflows, int min, int max) {
        LinkedList<WorkItem> todo = new LinkedList<>();
        todo.add(new WorkItem("in", new PartRange(new long[]{min, min, min, min}, new long[]{max, max, max, max})));
        long sum = 0;
        while (!todo.isEmpty()) {
            WorkItem cur = todo.remove();
            if (cur.workflowLabel.equals("A")) {
                sum += cur.range.combinations();
            } else if (cur.workflowLabel.equals("R")) {
                // reject!
            } else {
                workflows.get(cur.workflowLabel).apply(cur.range, todo);
            }
        }
        return sum;
    }

    static class Rule {
        int idx;
        boolean less;
        long val;
        String out;

        public Rule(String r) {
            String[] rp = r.split(":");
            char what = rp[0].charAt(0);
            idx = switch (what) {
                case 'x' -> 0;
                case 'm' -> 1;
                case 'a' -> 2;
                case 's' -> 3;
                default -> throw new IllegalArgumentException(r);
            };
            less = rp[0].charAt(1) == '<';
            val = Long.parseLong(rp[0].substring(2));
            out = rp[1];
        }

        public String apply(Part part) {
            if (less) {
                return part.vals[idx] < val ? out : null;
            } else {
                return part.vals[idx] > val ? out : null;
            }
        }

        public PartRange apply(PartRange in, LinkedList<WorkItem> work) {
            if (less) {
                // in=5-20 val = 10 -> work 5-10 ret 10-20
                // in=5-20 val = 5  -> #         ret 5-20
                // in=5-20 val = 6  -> work 5-6  ret 6-20
                // in=5-20 val = 19 -> work 5-19 ret 19-20
                // in=5-20 val = 20 -> work 5-20 #

                if (in.min[idx] < val && in.max[idx] > val) {
                    // need to split
                    long[] max = Arrays.copyOf(in.max, 4);
                    long[] min = Arrays.copyOf(in.min, 4);
                    max[idx] = val;
                    work.add(new WorkItem(out, new PartRange(in.min, max)));
                    min[idx] = val;
                    return new PartRange(min, in.max);
                } else if (in.max[idx] <= val) {
                    work.add(new WorkItem(out, in));
                    return null;
                } else {
                    return in;
                }
            } else { // greater
                // in=5-20 val = 10 -> work 11-20 ret 5-11
                // in=5-20 val = 6  -> work 7-20  ret 5-7
                // in=5-20 val = 5  -> work 6-20  ret 5-6
                // in=5-20 val = 4  -> work 5-20  #
                // in=5-20 val = 18 -> work 19-20 ret 5-19
                // in=5-20 val = 19 -> #          ret 5-20
                // in=5-20 val = 20 -> #          ret 5-20

                if (in.min[idx] < val + 1 && in.max[idx] > val + 1) {
                    // need to split
                    long[] max = Arrays.copyOf(in.max, 4);
                    long[] min = Arrays.copyOf(in.min, 4);
                    min[idx] = val + 1;
                    work.add(new WorkItem(out, new PartRange(min, in.max)));
                    max[idx] = val + 1;
                    return new PartRange(in.min, max);
                } else if (in.min[idx] > val) {
                    work.add(new WorkItem(out, in));
                    return null;
                } else {
                    return in;
                }
            }
        }
    }

    static class Workflow {
        List<Rule> rules = new ArrayList<>();
        String catchAll;

        public Workflow(String in) {
            String[] rs = in.split(",");
            for (int i = 0; i < rs.length - 1; i++) {
                rules.add(new Rule(rs[i]));
            }
            catchAll = rs[rs.length - 1];
        }

        public String apply(Part part) {
            for (Rule rule : rules) {
                String result = rule.apply(part);
                if (result != null) {
                    return result;
                }
            }
            return catchAll;
        }

        public void apply(PartRange in, LinkedList<WorkItem> out) {
            PartRange open = in;
            for (Rule rule : rules) {
                if (open == null) {
                    return;
                }
                open = rule.apply(open, out);
            }
            if (open != null) {
                out.add(new WorkItem(catchAll, open));
            }
        }
    }

    record Part(long[] vals) {
        public long rating() {
            return vals[0] + vals[1] + vals[2] + vals[3];
        }
    }

    record PartRange(long[] min, long[] max) {
        // range is inclusive at min, exclusive at max
        public long combinations() {
            long ret = 1;
            for (int i = 0; i < 4; i++) {
                ret *= max[i] - min[i];
            }
            return ret;
        }

        @Override
        public String toString() {
            return String.format("%d-%d %d-%d %d-%d %d-%d", min[0], max[0], min[1], max[1], min[2], max[2], min[3], max[3]);
        }
    }

    record WorkItem(String workflowLabel, PartRange range) {
        @Override
        public String toString() {
            return workflowLabel + ": " + range;
        }
    }
}