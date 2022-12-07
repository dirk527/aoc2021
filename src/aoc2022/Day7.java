package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day7 {
    public static void main(String[] args) throws IOException {
        String filename = "day7.txt";

        Pattern cd = Pattern.compile("^\\$ cd (.*)$");
        Pattern ls = Pattern.compile("^\\$ ls$");
        Pattern fileSpec = Pattern.compile("^(\\d+) (.*)$");
        Pattern dirSpec = Pattern.compile("^dir (.*)$");

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String s = br.readLine();
        assert s.equals("cd /");
        Dir root = new Dir("/");
        Dir cur = root;
        while ((s = br.readLine()) != null) {
            if (cur == null) {
                throw new IllegalStateException("cur is null");
            }
            Matcher m = cd.matcher(s);
            if (m.find()) {
                cur = cur.getSubdir(m.group(1));
                continue;
            }
            m = ls.matcher(s);
            if (m.find()) {
                continue;
            }
            m = fileSpec.matcher(s);
            if (m.find()) {
                cur.addChild(new File(m.group(2), Integer.parseInt(m.group(1))));
                continue;
            }
            m = dirSpec.matcher(s);
            if (m.find()) {
                cur.addChild(new Dir(m.group(1)));
                continue;
            }
            System.out.println("unknown line: " + s);
        }
        br.close();

        AtomicInteger p1 = new AtomicInteger();
        Consumer<Dir> part1 = dir -> p1.addAndGet(dir.size() < 100000 ? dir.size() : 0);
        root.receive(part1);
        System.out.println("part 1: " + p1);

        int totalSize = 70000000;
        int necessary = 30000000;
        int toDelete = necessary - (totalSize - root.size());
        final int[] p2 = {totalSize};
        Consumer<Dir> part2 = dir -> {
            int size = dir.size();
            if (size >= toDelete) {
                if (size < p2[0]) {
                    p2[0] = size;
                }
            }
        };
        root.receive(part2);
        System.out.println("part 2: " + p2[0]);
    }

    private static abstract class Entry {
        private final String name;

        public Entry(String name) {
            this.name = name;
        }

        public abstract int size();

        public String getName() {
            return name;
        }
    }

    private static class File extends Entry {
        private final int size;

        public File(String name, int size) {
            super(name);
            this.size = size;
        }

        @Override
        public int size() {
            return size;
        }
    }

    private static class Dir extends Entry {
        private final HashMap<String, Entry> children = new HashMap<>();

        public Dir(String name) {
            super(name);
        }

        @Override
        public int size() {
            AtomicInteger size = new AtomicInteger();
            children.forEach((s, c) -> size.addAndGet(s.equals("..") ? 0 : c.size()));
            return size.get();
        }

        public void addChild(Entry entry) {
            children.put(entry.getName(), entry);
            if (entry instanceof Dir dir) {
                dir.children.put("..", this);
            }
        }

        public Dir getSubdir(String s) {
            Entry e = children.get(s);
            if (e instanceof Dir) {
                return (Dir) e;
            }
            return null;
        }

        public void receive(Consumer<Dir> cons) {
            cons.accept(this);
            children.forEach((s, c) -> {
                if (!s.equals("..") && c instanceof Dir dir) {
                    ((Dir) c).receive(cons);
                }
            });
        }
    }
}
