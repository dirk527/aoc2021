package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day08 {
    public static void main(String[] args) throws IOException {
        boolean example = false;
        int numConnections;
        BufferedReader br;
        if (example) {
            br = new BufferedReader(new FileReader("08-ex"));
            numConnections = 10;
        } else {
            br = new BufferedReader(new FileReader("08-in"));
            numConnections = 1000;
        }

        // Read and parse the input
        String s;
        List<JunctionBox> allBoxes = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            String[] coords = s.split(",");
            allBoxes.add(new JunctionBox(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]),
                    Integer.parseInt(coords[2])));
        }
        long startTime = System.currentTimeMillis();

        // Calculate all pairwise distances - input has 1000 rows, so that's only about half a million
        ArrayList<Distance> distances = new ArrayList<>();
        for (int i = 0; i < allBoxes.size(); i++) {
            JunctionBox box1 = allBoxes.get(i);
            for (int j = i + 1; j < allBoxes.size(); j++) {
                JunctionBox box2 = allBoxes.get(j);
                double dist = Math.pow(box1.x - box2.x, 2) + Math.pow(box1.y - box2.y, 2) +
                        Math.pow(box1.z - box2.z, 2);
                distances.add(new Distance(box1, box2, dist));
            }
        }
        distances.sort(Distance::compareTo);
        long curTime = System.currentTimeMillis();
        System.out.printf("calculate distances: %d millis \n", curTime - startTime);

        // Union-Find data structure: every circuit is represented by one of its boxes called the head
        // Every other box that is a member of that group has a pointer to either the head or another memeber of the
        // group, no loops
        // Map the head marker boxes to the size of their network
        Map<JunctionBox, Integer> circuitHeads = new HashMap<>();
        int connectionsMade = 0;
        for (Distance dist : distances) {
            JunctionBox head1 = dist.one.findHead();
            Integer size1 = circuitHeads.get(head1);
            JunctionBox head2 = dist.two.findHead();
            Integer size2 = circuitHeads.get(head2);

            if (size1 == null && size2 == null) {        // new circuit
                dist.two.head = dist.one;
                circuitHeads.put(dist.one, 2);
            } else if (size1 != null && size2 == null) { // add two to one's circuit
                dist.two.head = head1;
                circuitHeads.put(head1, size1 + 1);
            } else if (size1 == null && size2 != null) { // add one to two's circuit
                dist.one.head = head2;
                circuitHeads.put(head2, size2 + 1);
            } else if (head1 != head2) {                 // merge the circuits
                head2.head = head1;
                circuitHeads.remove(head2);
                circuitHeads.put(head1, size1 + size2);
            }                                            // last case head1 == head2 -> no action
            if (++connectionsMade == numConnections) {
                // part 1 must be calculated after numConnection steps
                List<Integer> circuitSizes = new ArrayList<>(circuitHeads.values());
                circuitSizes.sort(Comparator.reverseOrder());
                int result1 = circuitSizes.get(0) * circuitSizes.get(1) * circuitSizes.get(2);
                curTime = System.currentTimeMillis();
                System.out.printf("part1: %15d after %d millis\n", result1, curTime - startTime);
            }
            if (circuitHeads.size() == 1 && circuitHeads.containsValue(allBoxes.size())) {
                // part 2 must be calculated the first time all boxes are in one circuit
                long result2 = (long) dist.one.x * (long) dist.two.x;
                curTime = System.currentTimeMillis();
                System.out.printf("part1: %15d after %d millis total\n", result2, curTime - startTime);
                break;
            }
        }
    }

    static class JunctionBox {
        public final int x;
        public final int y;
        public final int z;
        public JunctionBox head;

        public JunctionBox(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public JunctionBox findHead() {
            JunctionBox cand = this;
            while (cand.head != null) {
                cand = cand.head;
            }
            return cand;
        }

        @Override
        public final boolean equals(Object o) {
            if (!(o instanceof JunctionBox that)) {
                return false;
            }

            return x == that.x && y == that.y && z == that.z;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }

        @Override
        public String toString() {
            return "JunctionBox{x=" + x + ", y=" + y + ", z=" + z + '}';
        }
    }

    record Distance(JunctionBox one, JunctionBox two, Double distance) implements Comparable<Distance> {
        @Override
        public int compareTo(Distance o) {
            return Double.compare(distance, o.distance);
        }
    }
}