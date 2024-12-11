package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Day11 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("11-in"));
        String s = br.readLine();
        String[] numbers = s.split(" ");

        int steps = 25;
        long result = 0;
        for (String number : numbers) {
            result += calculate(new Simulation(Long.parseLong(number), steps));
        }
        System.out.println(result);

        steps = 75;
        result = 0;
        for (String number : numbers) {
            result += calculate(new Simulation(Long.parseLong(number), steps));
        }
        System.out.println(result);
    }

    static HashMap<Simulation, Long> cache = new HashMap<>();

    private static long calculate(Simulation sim) {
        if (sim.steps == 0) {
            return 1;
        }

        Long cached = cache.get(sim);
        if (cached != null) {
            return cached;
        }

        long result;
        int nextSteps = sim.steps - 1;
        if (sim.number == 0) {
            result = calculate(new Simulation(1L, nextSteps));
        } else {
            int digits = (int) Math.log10(sim.number) + 1;
            if (digits % 2 == 0) {
                long divisor = (long) Math.pow(10, digits / 2);
                result = calculate(new Simulation(sim.number / divisor, nextSteps)) +
                        calculate(new Simulation(sim.number % divisor, nextSteps));
            } else {
                result = calculate(new Simulation(sim.number * 2024, nextSteps));
            }
        }
        cache.put(sim, result);
        return result;
    }

    record Simulation(long number, int steps) {
    }
}