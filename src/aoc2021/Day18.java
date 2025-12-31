package aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import static aoc2021.Day18.Position.LEFT;
import static aoc2021.Day18.Position.RIGHT;

public class Day18 {
    private static final String EXAMPLE = "day18-1c.txt";
    private static final String REAL = "day18-2.txt";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(REAL));
        String input;
        List<SnailNumber> p1Numbers = new ArrayList<>();
        List<String> inputStrings = new ArrayList<>();
        while ((input = br.readLine()) != null) {
            inputStrings.add(input);
            p1Numbers.add(new Pair(input));
        }

        SnailNumber sum = new Pair(p1Numbers.get(0), p1Numbers.get(1));
        for (int i = 2; i < p1Numbers.size(); i++) {
            reduce(sum, false);
            sum = new Pair(sum, p1Numbers.get(i));
        }
        reduce(sum, false);
        System.out.println(sum);
        System.out.println("part1: " + sum.magnitude());
        System.out.println("Total time: " + (System.currentTimeMillis() - start) + " ms");

        long max = 0;
        for (int i = 0; i < p1Numbers.size(); i++) {
            for (int j = 0; j < p1Numbers.size(); j++) {
                if (i==j) {
                    continue;
                }
                sum = new Pair(new Pair(inputStrings.get(i)), new Pair(inputStrings.get(j)));
                reduce(sum, false);
                max = Math.max(max, sum.magnitude());
            }
        }
        System.out.println("part2: " + max);
        System.out.println("Total time: " + (System.currentTimeMillis() - start) + " ms");
    }

    private static void reduce(SnailNumber number, boolean print) {
        boolean finished = false;
        while (!finished) {
            boolean exploded;
            do {
                exploded = number.explodeIfNecessary(0);
                if (exploded && print) {
                    System.out.printf("%s exploded\n", number);
                }
            } while (exploded);

            boolean split = number.splitIfNecessary();
            if (split && print) {
                System.out.printf("%s split\n", number);
            }

            finished = !split;
        }
        if (print) {
            System.out.printf("%s\n\n", number);
        }
    }

    enum Position {
        LEFT,
        RIGHT
    }

    static abstract class SnailNumber {
        protected Pair parent;
        protected Position myPosition;

        public SnailNumber(Pair parent, Position pos) {
            this.parent = parent;
            this.myPosition = pos;
        }

        public abstract boolean explodeIfNecessary(int level);

        protected abstract void addToSingle(Single add, Position direction);

        public abstract boolean splitIfNecessary();

        public abstract long magnitude();
    }

    static class Single extends SnailNumber {
        private int value;

        public Single(StringCharacterIterator input, Pair parent, Position pos) {
            super(parent, pos);
            this.value = input.current() - '0';
            input.next();
        }

        public Single(int value, Pair parent, Position pos) {
            super(parent, pos);
            this.value = value;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }

        @Override
        public long magnitude() {
            return value;
        }

        @Override
        public boolean explodeIfNecessary(int level) {
            return false;
        }

        @Override
        protected void addToSingle(Single add, Position direction) {
            value += add.value;
        }

        @Override
        public boolean splitIfNecessary() {
            if (value > 9) {
                parent.split(myPosition, value);
                return true;
            }
            return false;
        }
    }

    static class Pair extends SnailNumber {
        private SnailNumber left;
        private SnailNumber right;

        public Pair(StringCharacterIterator input, Pair parent, Position pos) {
            super(parent, pos);
            if (input.current() == '[') {
                input.next();
                left = new Pair(input, this, LEFT);
            } else {
                left = new Single(input, this, LEFT);
            }
            if (input.current() != ',') {
                throw new IllegalArgumentException();
            }
            input.next();
            if (input.current() == '[') {
                input.next();
                right = new Pair(input, this, RIGHT);
            } else {
                right = new Single(input, this, RIGHT);
            }
            if (input.current() != ']') {
                throw new IllegalArgumentException();
            }
            input.next();
        }

        public Pair(int l, int r, Pair parent, Position pos) {
            super(parent, pos);
            left = new Single(l, this, LEFT);
            right = new Single(r, this, RIGHT);
        }

        public Pair(SnailNumber one, SnailNumber two) {
            super(null, null);
            left = one;
            right = two;
            left.parent = this;
            left.myPosition = LEFT;
            right.parent = this;
            right.myPosition = RIGHT;
        }

        public Pair(String input) {
            StringCharacterIterator it = new StringCharacterIterator(input);
            if (it.current() != '[') {  // first char is always [, skip it
                throw new IllegalArgumentException();
            }
            it.next();
            this(it, null, null);
        }

        @Override
        public String toString() {
            return "[" + left + " ; " + right + "]";
        }

        @Override
        public boolean explodeIfNecessary(int level) {
            if (level == 4) {
                parent.explode(myPosition, (Single) left, (Single) right);
                return true;
            }
            return left.explodeIfNecessary(level + 1) || right.explodeIfNecessary(level + 1);
        }

        private void explode(Position explosionPosition, Single one, Single two) {
            if (explosionPosition == LEFT) {
                left = new Single(0, this, LEFT);
                Pair cur = this;
                Pair up = this.parent;
                while (up != null && up.left == cur) {
                    cur = up;
                    up = cur.parent;
                }
                if (up != null) {
                    up.left.addToSingle(one, RIGHT);
                }
                right.addToSingle(two, LEFT);
            } else {
                right = new Single(0, this, RIGHT);
                Pair cur = this;
                Pair up = this.parent;
                while (up != null && up.right == cur) {
                    cur = up;
                    up = cur.parent;
                }
                if (up != null) {
                    up.right.addToSingle(two, LEFT);
                }
                left.addToSingle(one, RIGHT);
            }
        }

        @Override
        protected void addToSingle(Single add, Position direction) {
            if (direction == RIGHT) {
                right.addToSingle(add, RIGHT);
            } else {
                left.addToSingle(add, LEFT);
            }
        }

        @Override
        public boolean splitIfNecessary() {
            return left.splitIfNecessary() || right.splitIfNecessary();
        }

        public void split(Position splitPos, int value) {
            Pair splitPair = new Pair(value / 2, value / 2 + value % 2, this, splitPos);
            if (splitPos == LEFT) {
                left = splitPair;
            } else {
                right = splitPair;
            }
        }

        @Override
        public long magnitude() {
            return left.magnitude() * 3L + right.magnitude() * 2L;
        }
    }
}