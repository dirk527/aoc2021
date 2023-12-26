package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day25 {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader("25-input.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("25-sample.txt"));

        HashMap<String, Vertex> vertices = new HashMap<>();
        List<Edge> edges = new ArrayList<>();
        String s;
        while ((s = br.readLine()) != null) {
            String[] parts = s.split(": ");
            Vertex nv = vertices.computeIfAbsent(parts[0], Vertex::new);
            String[] neighbours = parts[1].split(" ");
            for (String nb : neighbours) {
                Vertex nv2 = vertices.computeIfAbsent(nb, Vertex::new);
                Edge edge = new Edge(nv, nv2);
                edges.add(edge);
                nv.addNeighbour(edge);
                nv2.addNeighbour(edge);
            }
        }

        int tries = 3000;
        List<Vertex> vertexList = new ArrayList<>(vertices.values());
        Random r = new Random();
        for (int i = 0; i < tries; i++) {
            if (i % 1000 == 0) {
                System.out.println(i);
            }
            Vertex start = vertexList.get(r.nextInt(vertices.size()));
            Vertex end = vertexList.get(r.nextInt(vertices.size()));
            dijkstra(start, end);
        }

        edges.sort((o1, o2) -> o2.counter - o1.counter);
        for (Edge e : edges) {
            System.out.println(e);
        }

        Edge disconnect = edges.getFirst();
        Vertex start1 = disconnect.one;
        Vertex start2 = disconnect.two;

        HashSet<Edge> disconnected = new HashSet<>();
        disconnected.add(edges.get(0));
        disconnected.add(edges.get(1));
        disconnected.add(edges.get(2));
        int size1 = flood(start1, vertices, disconnected);
        int size2 = flood(start2, vertices, disconnected);
        System.out.printf("%d = %d + %d\n", vertexList.size(), size1, size2);
        System.out.println(size1 * size2);
    }

    private static int flood(Vertex start, HashMap<String, Vertex> vertices, HashSet<Edge> disconnected) {
        HashSet<Vertex> seen = new HashSet<>();
        LinkedList<Vertex> todo = new LinkedList<>();
        todo.add(start);
        while (!todo.isEmpty()) {
            Vertex cur = todo.removeFirst();
            if (seen.contains(cur)) {
                continue;
            }
            seen.add(cur);
            for (Edge e : cur.outgoing) {
                if (disconnected.contains(e)) {
                    continue;
                }
                todo.add(e.other(cur));
            }
        }
        return seen.size();
    }

    record WorkItem(Vertex v, int steps, Edge used, WorkItem prev){
        @Override
        public String toString() {
            return v.label + " " + steps;
        }
    }

    private static void dijkstra(Vertex start, Vertex end) {
        PriorityQueue<WorkItem> todo = new PriorityQueue<>(Comparator.comparingInt(o -> o.steps));
        HashMap<Vertex, Integer> shortest = new HashMap<>();
        todo.add(new WorkItem(start, 0, null, null));
        while (!todo.isEmpty()) {
            WorkItem wi = todo.remove();
            int way = shortest.computeIfAbsent(wi.v, v -> Integer.MAX_VALUE);
            if (way <= wi.steps) {
                continue;
            }
            if (end == wi.v) {
                WorkItem cur = wi;
                while (cur.used != null) {
                    cur.used.counter++;
                    cur = cur.prev;
                }
                return;
            }
            shortest.put(wi.v, wi.steps);
            for (Edge e : wi.v.outgoing) {
                Vertex cand = e.other(wi.v);
                int cur = shortest.computeIfAbsent(cand, v -> Integer.MAX_VALUE);
                if (cur > wi.steps + 1) {
                    todo.add(new WorkItem(cand, wi.steps +1, e, wi));
                }
            }
        }
    }

    private static void dfs(Vertex start, int n) {
        Set<Vertex> visited = new HashSet<>();
        LinkedList<Vertex> todo = new LinkedList<>();
        LinkedList<Edge> todo2 = new LinkedList<>();
        for (Edge e : start.outgoing) {
            todo2.addFirst(e);
            todo.addFirst(e.other(start));
        }
        while (visited.size() < n) {
            Vertex vertex = todo.removeLast();
            Edge edge = todo2.removeLast();
            if (!visited.contains(vertex)) {
                visited.add(vertex);
                edge.counter++;
                for (Edge e : vertex.outgoing) {
                    todo2.addFirst(e);
                    todo.addFirst(e.other(vertex));
                }
            }
        }
    }

    static class Vertex {
        List<Edge> outgoing = new ArrayList<>();
        String label;

        public Vertex(String label) {
            this.label = label;
        }

        public void addNeighbour(Edge edge) {
            outgoing.add(edge);
        }
    }

    static class Edge {
        Vertex one, two;
        int counter;

        public Edge(Vertex one, Vertex two) {
            this.one = one;
            this.two = two;
        }

        public Vertex other(Vertex v) {
            return v == one ? two : one;
        }

        @Override
        public String toString() {
            return String.format("%s -> %s : %d", one.label, two.label, counter);
        }
    }
}