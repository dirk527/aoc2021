import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day1B {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String s;
        int[] m = new int[4];
        int count = 0;
        int inc = 0;
        int prev = Integer.MAX_VALUE;
        while ((s = br.readLine()) != null) {
            m[count % 4] = Integer.valueOf(s);
            if (count > 1) {
                int sum = 0;
                int offset = (count - 2) % 4;
                for (int i = 0; i < 3; i++) {
                    sum += m[offset];
                    offset = (offset + 1) % 4;
                }

                if (sum > prev) {
                    inc++;
                }
                prev = sum;
            }
            count++;
        }
        System.out.println(inc);
    }
}
