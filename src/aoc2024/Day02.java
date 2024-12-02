package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day02 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("02-in"));
        String s;
        List<List<Integer>> reports = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            String[] tokens = s.split(" ");
            List<Integer> report = new ArrayList<>();
            for (String token : tokens) {
                report.add(Integer.parseInt(token));
            }
            reports.add(report);
        }

        int p1 = 0;
        int p2 = 0;
        out: for (List<Integer> report : reports) {
            if (isReportSafe(report)) {
                p1++;
                p2++;
                continue;
            }

            for (int i = 0; i < report.size(); i++) {
                List<Integer> shorterReport = new ArrayList<>(report);
                shorterReport.remove(i);
                if (isReportSafe(shorterReport)) {
                    p2++;
                    continue out;
                }
            }
        }

        System.out.println(p1);
        System.out.println(p2);
    }

    private static boolean isReportSafe(List<Integer> report) {
        boolean increasing = report.get(1) > report.get(0);
        for (int i = 1; i < report.size(); i++) {
            int current = report.get(i);
            int previous = report.get(i - 1);
            if (current == previous) {
                return false;
            } else if (increasing != current > previous) {
                return false;
            } else if (Math.abs(previous - current) > 3) {
                return false;
            }
        }
        return true;
    }
}