package aoc2025;

import javax.swing.*;
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

        String s;
        List<JunctionBox> allBoxes = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            String[] coords = s.split(",");
            allBoxes.add(new JunctionBox(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]),
                    Integer.parseInt(coords[2])));
        }
        System.out.println("allBoxes: " + allBoxes.size());

        TreeSet<Distance> distances = new TreeSet<>();
        double cutoff = Double.MAX_VALUE;
        for (int i = 0; i < allBoxes.size(); i++) {
            JunctionBox box1 = allBoxes.get(i);
            for (int j = i + 1; j < allBoxes.size(); j++) {
                JunctionBox box2 = allBoxes.get(j);
                double dist = Math.sqrt(Math.pow(box1.x - box2.x, 2) + Math.pow(box1.y - box2.y, 2) +
                        Math.pow(box1.z - box2.z, 2));
                if (dist < cutoff) {
                    distances.add(new Distance(box1, box2, dist));
                }
                if (distances.size() == numConnections) {
                    cutoff = distances.last().distance;
                }
            }
        }
        System.out.println("Need " + numConnections + "; had to calculate " + distances.size());

        // Map the head marker boxes to the size of their network
        Map<JunctionBox, Integer> circuitHeads = new HashMap<>();
        int connectionsMade = 0;
        for (Distance dist : distances) {
//            System.out.printf("step %2d: connecting dist %4.1f %s and %s\n", connectionsMade, dist.distance, dist.one, dist.two);
            JunctionBox head1 = dist.one.findHead();
            Integer size1 = circuitHeads.get(head1);
            JunctionBox head2 = dist.two.findHead();
            Integer size2 = circuitHeads.get(head2);
//            System.out.printf("h1 %s s1 %d h2 %s s2 %d\n", head1, size1, head2, size2);

            if (size1 == null && size2 == null) {
                dist.two.head = dist.one;
                circuitHeads.put(dist.one, 2);
            } else if (size1 != null && size2 == null) {
                dist.two.head = head1;
                circuitHeads.put(head1, size1 + 1);
            } else if (size1 == null && size2 != null) {
                dist.one.head = head2;
                circuitHeads.put(head2, size2 + 1);
            } else if (head1 != head2) {
                head2.head = head1;
                circuitHeads.remove(head2);
                circuitHeads.put(head1, size1 + size2);
            }
            if (++connectionsMade == numConnections) {
                List<Integer> circuitSizes = new ArrayList<>(circuitHeads.values());
                circuitSizes.sort(Comparator.naturalOrder());
                int count = circuitSizes.size();
                int result1 = circuitSizes.get(count - 1) * circuitSizes.get(count - 2) * circuitSizes.get(count - 3);
                System.out.println("part1: " + result1);
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
            return "JunctionBox{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }

    record Distance(JunctionBox one, JunctionBox two, Double distance) implements Comparable<Distance> {
        @Override
        public int compareTo(Distance o) {
            return Double.compare(distance, o.distance);
        }
    }

    ;
}