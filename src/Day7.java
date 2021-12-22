import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Day7 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String input = br.readLine();
        String[] strings = input.split(",");
        int[] numbers = new int[strings.length];
        Arrays.sort(numbers);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < strings.length; i++) {
            int cur = Integer.parseInt(strings[i]);
            numbers[i] = cur;
            min = Math.min(min, cur);
            max = Math.max(max, cur);
        }

//        int median = numbers[numbers.length / 2];
//        System.out.println("Median " + median);
//        for (int i = Math.max(min, median - 150); i < Math.min(median + 15, max); i++) {
//            System.out.println(i + ": " + countDiff(numbers, i));
//        }

        int result = Integer.MAX_VALUE;
        for (int i = min; i <= max; i++) {
            int cand = countDiff(numbers, i);
            result = Math.min(cand, result);
        }
        System.out.println(result);
    }

    private static int countDiff(int[] numbers, int pos) {
        int fuel = 0;
        for (int i = 0; i < numbers.length; i++) {
            int diff = Math.abs(pos - numbers[i]);
            fuel += (1 + diff) * (diff / 2d);
        }
        return fuel;
    }
}
