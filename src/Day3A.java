import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day3A {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        int[] bits = null;
        String s;
        int count = 0;
        while ((s = br.readLine()) != null) {
            if (bits == null) {
                bits = new int[s.length()];
            }
            for (int i = 0; i < bits.length; i++) {
                if (s.charAt(i) == '1') {
                    bits[i]++;
                }
            }
            count++;
        }

        int gamma = 0;
        int epsilon = 0;

        for (int bit : bits) {
            gamma *= 2;
            epsilon *= 2;
            if (bit > count / 2) {
                gamma++;
            } else {
                epsilon++;
            }
        }
//            System.out.println("horiz = " + horiz + "; depth = " + depth);
        System.out.println(gamma * epsilon);
    }
}
