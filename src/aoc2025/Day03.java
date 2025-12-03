package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day03 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("03-in"));

        String s;
        List<List<Integer>> banks = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            List<Integer> bank = new ArrayList<>();
            for (char c : s.toCharArray()) {
                bank.add(c - '0');
            }
            banks.add(bank);
        }

        System.out.println(calculate(banks, 2));
        System.out.println(calculate(banks, 12));
    }

    private static long calculate(List<List<Integer>> banks, int nDigits) {
        long ret = 0;
        for (List<Integer> bank : banks) {
            List<Integer> result = new ArrayList<>();
            boolean success = findLargest(bank, result, nDigits, 0);
            if (!success) {
                System.out.println("did not work");
                System.exit(0);
            }
            long mult = 1;
            for (int i = result.size() - 1; i >= 0; i--) {
                ret += result.get(i) * mult;
                mult *= 10;
            }
        }
        return ret;
    }

    private static boolean findLargest(List<Integer> bank, List<Integer> soFar, int limit, int startIdx) {
        if (limit == 0) {
            return true;
        }
        // we need the largest digit that still leaves space for the rest of the digits.
        // So try 9 first, 8 next and so on.
        for (int lookFor = 9; lookFor >= 0; lookFor--) {
            // Do not look until the very end - leave enough digits for the rest
            int lastPossibleIdx = bank.size() - limit + 1;
            for (int idx = startIdx; idx < lastPossibleIdx; idx++) {
                if (bank.get(idx) == lookFor) {
                    soFar.add(lookFor);
                    if (findLargest(bank, soFar, limit - 1, idx + 1)) {
                        return true;
                    } else {
                        soFar.removeLast();
                    }
                }
            }
        }
        return false;
    }
}