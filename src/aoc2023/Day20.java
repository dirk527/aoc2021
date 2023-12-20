package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static aoc2023.Day20.Pulse.HIGH;
import static aoc2023.Day20.Pulse.LOW;

public class Day20 {
    static boolean trace = false;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("20-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("20-sample.txt"));

        Pattern pat = Pattern.compile("([%&]?)([a-z]+) -> ([a-z, ]*)");
        String s;
        HashMap<String, String> connections = new HashMap<>();
        HashMap<String, Module> modules = new HashMap<>();
        while ((s = br.readLine()) != null) {
            Matcher mat = pat.matcher(s);
            if (mat.matches()) {
                String type = mat.group(1);
                String name = mat.group(2);
                String out = mat.group(3);

                connections.put(name, out);
                if (type.isEmpty()) {
                    modules.put(name, new Broadcaster(name));
                } else if (type.equals("%")) {
                    modules.put(name, new FlipFlop(name));
                } else if (type.equals("&")) {
                    modules.put(name, new Conjunction(name));
                } else {
                    throw new IllegalArgumentException(type);
                }
            } else {
                throw new IllegalArgumentException(s);
            }
        }
        for (var elem : connections.entrySet()) {
            for (String outModule : elem.getValue().split(", ")) {
                Module from = modules.get(elem.getKey());
                if (!modules.containsKey(outModule)) {
                    modules.put(outModule, new Broadcaster(outModule));
                }
                Module to = modules.get(outModule);
                from.addOutgoing(to);
                to.addIncoming(from);
            }
        }

        Counter c = new Counter();
        for (int i = 0; i < 1000; i++) {
            pushOnce(modules, c);
        }
        System.out.println(c.score());
        long p1Time = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n\n", (p1Time - startTime) / 1000f);

        for (Module m : modules.values()) {
            m.reset();
        }

        // Part 2 works for the specific input. There is a conjunction at the end that is fed by a few other
        // modules that are on repeating cycles.
        // First, find the repeating feeder modules. Throw an exception if the input does not fit the expectations.
        Module prev;
        if (modules.get("rx") instanceof Broadcaster finish) {
            prev = finish.incoming;
        } else {
            throw new IllegalStateException();
        }
        Set<String> repeaters;
        if (prev instanceof Conjunction conj) {
            repeaters = conj.states.keySet();
        } else {
            throw new IllegalStateException();
        }

        // Second, define a tracer that will save the cycles
        AtomicLong turn = new AtomicLong(1);
        HashMap<String, Long> cycles = new HashMap<>();
        Tracer t = wi -> {
            if (wi.from != null && repeaters.contains(wi.from.getName()) && wi.p() == HIGH) {
//                System.out.printf("%10d %s %s -> xm\n", turn.get(), wi.from.getName(), wi.p());
                if (!cycles.containsKey(wi.from.getName())) {
                    cycles.put(wi.from.getName(), turn.get());
                }
            }
        };
        while (cycles.size() != repeaters.size()) {
            pushOnce(modules, t);
            turn.addAndGet(1);
        }

        // Third, work out the least common multiple - the Java class library only has gcd and only
        // for BigInteger, but that is doable.
        BigInteger a = null;
        for (Long n : cycles.values()) {
            BigInteger b = BigInteger.valueOf(n);
            if (a != null) {
                b = a.multiply(b.divide(b.gcd(a)));
            }
            a = b;
        }

        System.out.println(a);
        long endTime = System.currentTimeMillis();
        System.out.printf("%5.3f sec\n", (endTime - p1Time) / 1000f);
    }

    private static void pushOnce(HashMap<String, Module> modules, Tracer tracer) {
        LinkedList<WorkItem> todo = new LinkedList<>();
        todo.addLast(new WorkItem(null, modules.get("broadcaster"), LOW));
        while (!todo.isEmpty()) {
            WorkItem wi = todo.removeFirst();
            tracer.trace(wi);
            wi.to().input(wi.from(), wi.p(), todo);
        }
    }

    enum Pulse {
        HIGH, LOW
    }

    abstract static class Module {
        protected List<Module> out = new ArrayList<>();
        private String name;

        public Module(String name) {
            this.name = name;
        }

        public void addIncoming(Module from) {
        }

        public void addOutgoing(Module m) {
            out.add(m);
        }

        protected final void send(Pulse pulse, List<WorkItem> todo) {
            for (Module m : out) {
                if (trace) {
                    System.out.printf("%s %s -> %s\n", name, pulse, m.name);
                }
                todo.add(new WorkItem(this, m, pulse));
            }
        }

        public abstract void input(Module src, Pulse pulse, List<WorkItem> todo);

        public String getName() {
            return name;
        }

        public void reset() {
        }
    }

    static class FlipFlop extends Module {
        boolean on = false;

        public FlipFlop(String name) {
            super(name);
        }

        @Override
        public void input(Module src, Pulse pulse, List<WorkItem> todo) {
            if (pulse == LOW) {
                on = !on;
                send(on ? HIGH : LOW, todo);
            }
        }

        @Override
        public void reset() {
            on = false;
        }
    }

    static class Conjunction extends Module {
        HashMap<String, Pulse> states = new HashMap<>();

        public Conjunction(String name) {
            super(name);
        }

        @Override
        public void addIncoming(Module from) {
            states.put(from.getName(), LOW);
        }

        @Override
        public void reset() {
            states.replaceAll((k, v) -> LOW);
        }

        @Override
        public void input(Module src, Pulse pulse, List<WorkItem> todo) {
            states.put(src.getName(), pulse);
            Pulse send = LOW;
            for (var elem : states.values()) {
                if (elem == LOW) {
                    send = HIGH;
                    break;
                }
            }
            send(send, todo);
        }
    }

    static class Broadcaster extends Module {
        Module incoming;

        public Broadcaster(String name) {
            super(name);
        }

        @Override
        public void addIncoming(Module from) {
            if (incoming != null) {
                throw new IllegalStateException();
            }
            incoming = from;
        }

        @Override
        public void input(Module src, Pulse pulse, List<WorkItem> todo) {
            send(pulse, todo);
        }
    }

    record WorkItem(Module from, Module to, Pulse p) {
    }

    @FunctionalInterface
    interface Tracer {
        void trace(WorkItem wi);
    }

    static class Counter implements Tracer {
        long low;
        long high;

        public long score() {
            return low * high;
        }

        public void trace(WorkItem wi) {
            if (wi.p() == LOW) {
                low++;
            } else {
                high++;
            }
        }
    }
}