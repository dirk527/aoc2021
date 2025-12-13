package aoc2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day12 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("12-in"));
        String s;
        List<Tile> tiles = new ArrayList<>();
        List<Tree> trees = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            if (s.length() == 2) {
                String[] lines = new String[3];
                lines[0] = br.readLine();
                lines[1] = br.readLine();
                lines[2] = br.readLine();
                br.readLine();
                tiles.add(new Tile(lines));
            } else {
                String[] split = s.split(": ");
                String[] wh = split[0].split("x");
                String[] countStr = split[1].split(" ");
                List<Integer> counts = Arrays.stream(countStr).map(Integer::parseInt).toList();
                trees.add(new Tree(Integer.parseInt(wh[0]), Integer.parseInt(wh[1]), counts));
            }
        }

        long dubious = 0;
        long noWay = 0;
        long sparseWay = 0;
        for (Tree tree : trees) {
            int neededSpace = 0;
            int nPresents = 0;
            for (int i = 0; i < tree.presents.size(); i++) {
                neededSpace += tree.presents.get(i) * tiles.get(i).nHashes;
                nPresents += tree.presents.get(i);
            }
            int availableSpace = tree.width * tree.height;
            int trivialPresents = tree.width / 3 * tree.height / 3;
            if (neededSpace > availableSpace) {
                noWay++;
            } else if (nPresents <= trivialPresents) {
                sparseWay++;
            } else {
                dubious++;
                System.out.printf("maybe way %4d < %4d: %s\n", neededSpace, availableSpace, tree);
            }
        }
        System.out.println("no way: " + noWay);
        System.out.println("trivial way: " + sparseWay);
        System.out.println("who knows: " + dubious);      // turns out we're done here 😂
    }

    private static class Tile {
        String[] lines;
        int nHashes;

        Tile(String[] lines) {
            this.lines = lines;
            nHashes = 0;
            for (String line : lines) {
                for (char c : line.toCharArray()) {
                    if (c == '#') {
                        nHashes++;
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "(\n" + lines[0] + "\n" + lines[1] + "\n" + lines[2] + "\n" + nHashes + ")";
        }
    }

    private record Tree(int width, int height, List<Integer> presents) {
    }
}