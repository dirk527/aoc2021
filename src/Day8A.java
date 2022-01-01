import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day8A {
    public static final String EXAMPLE = "day8-1.txt";
    public static final String REAL = "day8-2.txt";

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(REAL));
        String input;
        int count = 0;
        while ((input = br.readLine()) != null) {
            String[] combinations = input.split(" ");
            // 10 input values, | 4 output values
            for (int i = 10; i < combinations.length; i++) {
                String s = combinations[i];
                int l = s.length();
                if (l == 2 || l == 3 || l == 4 || l == 7) {
                    System.out.println(s);
                    count++;
                }
            }
        }
        System.out.println(count);
    }
}
