package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day16 {

    public static void main(String[] args) throws IOException {
        // Initialize
        long begin = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("day16.txt"));

        Pattern pat = Pattern.compile("Valve ([A-Z][A-Z]) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]*)");
        String s;
        HashMap<String, Valve> valves = new HashMap<>();
        Set<Valve> usefulValves = new HashSet<>();
        while ((s = br.readLine()) != null) {
            Matcher m = pat.matcher(s);
            m.find();
            Valve v = new Valve(m.group(1), m.group(2), m.group(3).split(", "));
            valves.put(v.id, v);
            if (v.flow > 0) {
                usefulValves.add(v);
            }
        }
        for (Valve v : valves.values()) {
            v.findOthers(valves);
        }

        // Consolidate: for each useful (not-0-flow) Valve (and the start), store the length of the paths to every other useful one
        for (Valve v : valves.values()) {
            v.findPaths(usefulValves);
        }
        Valve start = valves.get("AA");
        start.findPaths(usefulValves);
//        System.out.println("AA: " + start.ways);
//        for (Valve v : usefulValves) {
//            System.out.println(v.id + ": " + v.ways);
//        }

        // part 1: find best way to spend 30 minutes, breadth-first-search
        List<StateP1> stack = new LinkedList<>();
        stack.add(new StateP1(start, 30, 0, new ArrayList<>()));
        int best = 0;
        while (!stack.isEmpty()) {
            StateP1 state = stack.remove(0);
            boolean terminal = true;
            for (var way : state.cur.ways.entrySet()) {
                Integer dist = way.getValue();
                Valve target = way.getKey();
                if (state.minutes - dist > 0 && !state.opened.contains(target)) {
                    ArrayList<Valve> opened = new ArrayList<>(state.opened);
                    opened.add(target);
                    int newlyReleased = (state.minutes - dist) * target.flow;
                    terminal = false;
//                    System.out.println("at " + state + " using way " + target + "/" + dist + " freshly releasing " + newlyReleased);
                    stack.add(new StateP1(target, state.minutes - dist, state.released + newlyReleased, opened));
                }
            }
            if (terminal) {
                if (state.released > best) {
//                    System.out.println("new best: " + state);
                    best = state.released;
                }
            }
        }
        System.out.println("part 1: " + best);
        long mid = System.currentTimeMillis();
        System.out.println((mid - begin) + " millis");

        // part 2: same but different, state is more complex
        Player startHuman = new Player(start, 26);
        Player startElephant = new Player(start, 26);
        List<StateP2> stack2 = new LinkedList<>();
        stack2.add(new StateP2(startHuman, startElephant, 0, new ArrayList<>()));
        best = 0;
        while (!stack2.isEmpty()) {
            StateP2 state = stack2.remove(0);

            // always move the player that is behind in time, or the human if tied
            boolean moveElephant = state.elephant.minutes > state.human.minutes;
            boolean terminal = addMoves(stack2, state, moveElephant);
            // if the mover cannot move, maybe the other Player still can
            if (terminal) {
                terminal = addMoves(stack2, state, !moveElephant);
            }

            if (terminal) {
                if (state.released > best) {
//                    System.out.println("new best: " + state);
                    best = state.released;
                }
            }
        }
        System.out.println("part 2: " + best);

        // finish
        long end = System.currentTimeMillis();
        System.out.println((end - mid) + " millis");
    }

    private static boolean addMoves(List<StateP2> stack2, StateP2 state, boolean moveElephant) {
        boolean terminal = true;
        Player mover = moveElephant ? state.elephant : state.human;
        for (var way : mover.cur.ways.entrySet()) {
            Integer dist = way.getValue();
            Valve target = way.getKey();
            if (mover.minutes - dist > 0 && !state.opened.contains(target)) {
                ArrayList<Valve> opened = new ArrayList<>(state.opened);
                opened.add(target);
                int newlyReleased = (mover.minutes - dist) * target.flow;
                terminal = false;
                Player newPlayer = new Player(target, mover.minutes - dist);
//                    System.out.println("at " + state + (moveElephant ? " elephant" : " human") + " using way " + target + "/" + dist + " freshly releasing " + newlyReleased);
                stack2.add(new StateP2(moveElephant ? state.human : newPlayer,
                        moveElephant ? newPlayer : state.elephant,
                        state.released + newlyReleased, opened));
            }
        }
        return terminal;
    }

    static class Valve {
        String[] out;
        String id;
        List<Valve> tunnels;
        HashMap<Valve, Integer> ways;
        int flow;

        public Valve(String id, String flow, String[] out) {
            this.id = id;
            this.flow = Integer.parseInt(flow);
            this.out = out;
        }

        public void findOthers(HashMap<String, Valve> valves) {
            tunnels = new ArrayList<>();
            for (String s : out) {
                tunnels.add(valves.get(s));
            }
        }

        public void findPaths(Set<Valve> targets) {
            ways = new HashMap<>();
            List<Valve> stack = new LinkedList<>();
            HashMap<Valve, Integer> distances = new HashMap<>();
            Set<Valve> visited = new HashSet<>();
            stack.add(this);
            distances.put(this, 0);
            while (!stack.isEmpty() && ways.size() < targets.size()) {
                Valve cur = stack.remove(0);
                visited.add(cur);
                Integer curDist = distances.get(cur);
                if (cur != this && targets.contains(cur)) {
                    ways.put(cur, curDist + 1); // the way takes curDist minutes; +1 minute for opening the valve
                }
                for (Valve v : cur.tunnels) {
                    if (!visited.contains(v)) {
                        stack.add(v);
                        distances.put(v, curDist + 1);
                    }
                }
            }
        }

        @Override
        public String toString() {
            return id;
        }
    }

    record StateP1(Valve cur, int minutes, int released, Collection<Valve> opened) {
    }

    record Player(Valve cur, int minutes){
        public String toString() {
            return cur + "_" + minutes ;
        }
    }

    record StateP2(Player human, Player elephant, int released, Collection<Valve> opened) {
    }
}