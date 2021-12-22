import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day3B {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        List<String> lines = new ArrayList<>();
        String s;
        while ((s = br.readLine()) != null) {
            lines.add(s);
        }

        String oxy = findCommon(new ArrayList<>(lines), 0);
        String co2 = findUncommon(lines, 0);
        System.out.println(Integer.parseInt(oxy, 2) * Integer.parseInt(co2, 2));
    }

    private static String findCommon(List<String> in, int pos) {
//        System.out.println("in = " + in + "; pos = " + pos);
        int count = 0;
        int ones = 0;
        for (String s : in) {
            if (s.charAt(pos) == '1') {
                ones++;
            }
            count++;
        }
        char search;
        if (ones == count - ones) {
            search = '1';
        } else {
            if (ones > count / 2) {
                search = '1';
            } else {
                search = '0';
            }
        }
//        System.out.println("ones = " + ones + "; count = " + count + "; search = " + search);
        in.removeIf(s -> s.charAt(pos) != search);

        if (in.size() == 1) {
            return in.get(0);
        }
        return findCommon(in, pos + 1);
    }

    private static String findUncommon(List<String> in, int pos) {
//        System.out.println("in = " + in + "; pos = " + pos);
        int count = 0;
        int ones = 0;
        for (String s : in) {
            if (s.charAt(pos) == '1') {
                ones++;
            }
            count++;
        }
        char search;
        if (ones == count - ones) {
            search = '0';
        } else {
            if (ones > count / 2) {
                search = '0';
            } else {
                search = '1';
            }
        }
//        System.out.println("ones = " + ones + "; count = " + count + "; search = " + search);
        in.removeIf(s -> s.charAt(pos) != search);

        if (in.size() == 1) {
            return in.get(0);
        }
        return findUncommon(in, pos + 1);
    }
}
