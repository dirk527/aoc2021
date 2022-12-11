package aoc2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day11 {
    private static final Pattern MONKEY = Pattern.compile("Monkey (\\d):\\n" +
            "  Starting items: ([0-9, ]*)\\n" +
            "  Operation: ([^\\n]*)\\n" +
            "  Test: divisible by (\\d+)\\n" +
            "    If true: throw to monkey (\\d+)\\n" +
            "    If false: throw to monkey (\\d+)", Pattern.MULTILINE);

    public static void main(String[] args) throws IOException {
        String filename = "day11.txt";
        String spec = Files.readString(Path.of(filename));

        Matcher matcher = MONKEY.matcher(spec);

        // part 1
        List<Monkey> monkeys = new ArrayList<>();
        while (matcher.find()) {
            monkeys.add(new Monkey(matcher, Part.ONE));
        }
        monkeys.forEach(m -> m.monkeyfy(monkeys));
        for (int round = 1; round <= 20; round++) {
            monkeys.forEach(Monkey::play);
        }
        printMonkeyBusiness(monkeys);

        // part 2 - same but diffrent mode
        matcher.reset();
        monkeys.clear();
        while (matcher.find()) {
            monkeys.add(new Monkey(matcher, Part.TWO));
        }
        monkeys.forEach(m -> m.monkeyfy(monkeys));
        for (int round = 1; round <= 10000; round++) {
            monkeys.forEach(Monkey::play);
        }
        printMonkeyBusiness(monkeys);
    }

    private static void printMonkeyBusiness(List<Monkey> monkeys) {
        monkeys.sort((m1, m2) -> m2.getInspectedCount() - m1.getInspectedCount());
        long monkeyBusiness = ((long) monkeys.get(0).getInspectedCount()) * ((long) monkeys.get(1).getInspectedCount());
        System.out.println("monkeyBusiness: " + monkeyBusiness);
    }

    private enum Part {
        ONE, TWO;
    }

    private static class Monkey {
        private Part mode;

        private int no;
        private final List<Item> items = new LinkedList<>();
        private int add;
        private int multiply;
        private int div;
        private Monkey falseMonkey;
        private Monkey trueMonkey;

        private int falseIdx;
        private int trueIdx;

        private int inspectedCount = 0;

        public Monkey(Matcher m, Part mode) {
            this.mode = mode;
            no = Integer.parseInt(m.group(1));
            String[] itemStr = m.group(2).split(", ");
            for (String i : itemStr) {
                int val = Integer.parseInt(i);
                items.add(mode == Part.ONE ? new IntItem(val) : new ModulusItem(val));
            }
            String operation = m.group(3);
            if ("new = old * old".equals(operation)) {
                add = 0;
                multiply = 0;
            } else if (operation.startsWith("new = old + ")) {
                multiply = 1;
                add = Integer.parseInt(operation.substring("new = old + ".length()));
            } else if (operation.startsWith("new = old * ")) {
                add = 0;
                multiply = Integer.parseInt(operation.substring("new = old * ".length()));
            } else {
                throw new IllegalArgumentException("unknown op");
            }
            div = Integer.parseInt(m.group(4));
            trueIdx = Integer.parseInt(m.group(5));
            falseIdx = Integer.parseInt(m.group(6));
        }

        public void monkeyfy(List<Monkey> herd) {
            trueMonkey = herd.get(trueIdx);
            falseMonkey = herd.get(falseIdx);
            if (mode == Part.TWO) {
                herd.forEach(m -> m.initialize(items));
            }
        }

        private void initialize(List<Item> items) {
            items.forEach(i -> i.addModulo(div));
        }

        public void play() {
            while (!items.isEmpty()) {
                inspectedCount++;
                Item inspect = items.remove(0);
                if (multiply == 0 && add == 0) {
                    inspect.square();
                } else {
                    inspect.operation(add, multiply);
                }
                if (mode == Part.ONE) {
                    inspect.divByThree();
                }
                if (inspect.isDivisibleBy(div)) {
                    trueMonkey.caught(inspect);
                } else {
                    falseMonkey.caught(inspect);
                }
            }
        }

        private void caught(Item item) {
            items.add(item);
        }

        public int getInspectedCount() {
            return inspectedCount;
        }

        @Override
        public String toString() {
            return "Monkey{" +
                    "no=" + no +
                    ", items=" + items +
                    ", add=" + add +
                    ", multiply=" + multiply +
                    ", div=" + div +
                    ", trueMonkey=" + trueMonkey.no +
                    ", falseMonkey=" + falseMonkey.no +
                    ", inspectedCount=" + inspectedCount +
                    '}';
        }
    }

    interface Item {
        void addModulo(int modulo);

        void square();

        void operation(int add, int mult);

        boolean isDivisibleBy(int div);

        void divByThree();
    }

    private static class ModulusItem implements Item {
        private final int startValue;
        private final HashMap<Integer, Integer> modulos = new HashMap<>();

        public ModulusItem(int startValue) {
            this.startValue = startValue;
        }

        @Override
        public void addModulo(int modulo) {
            modulos.put(modulo, startValue % modulo);
        }

        @Override
        public void square() {
            for (Map.Entry<Integer, Integer> entry : modulos.entrySet()) {
                Integer modulo = entry.getKey();
                int val = entry.getValue();
                modulos.put(modulo, (val * val) % modulo);
            }
        }

        @Override
        public void operation(int add, int mult) {
            for (Map.Entry<Integer, Integer> entry : modulos.entrySet()) {
                Integer modulo = entry.getKey();
                int val = entry.getValue();
                modulos.put(modulo, (val * mult + add) % modulo);
            }
        }

        @Override
        public boolean isDivisibleBy(int div) {
            return modulos.get(div) == 0;
        }

        @Override
        public void divByThree() {
            throw new UnsupportedOperationException();
        }
    }

    private static class IntItem implements Item {
        private int value;

        public IntItem(int value) {
            this.value = value;
        }

        @Override
        public void addModulo(int modulo) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void square() {
            value = value * value;
        }

        @Override
        public void operation(int add, int mult) {
            value = value * mult + add;
        }

        @Override
        public boolean isDivisibleBy(int div) {
            return value % div == 0;
        }

        @Override
        public void divByThree() {
            value = value / 3;
        }
    }
}