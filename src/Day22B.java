import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day22B {
    public static final String EXAMPLE1 = "day22-1.txt";
    public static final String EXAMPLE2 = "day22-2.txt";
    public static final String REAL = "day22-3.txt";
    public static final String EXAMPLE3 = "day22-4.txt";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(REAL));
        String input;
        int row = 0;
        // on x=10..12,y=10..12,z=10..12
        Pattern pattern = Pattern.compile("(on|off) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)");
        CuboidList cuboids = new CuboidList();
        while ((input = br.readLine()) != null) {
            Matcher matcher = pattern.matcher(input);
            if (!matcher.matches()) {
                System.out.println("no match");
                continue;
            }
            int pos = 1;
            boolean turnOn = matcher.group(pos++).equals("on");
            int x1 = Integer.parseInt(matcher.group(pos++));
            int x2 = Integer.parseInt(matcher.group(pos++));
            int y1 = Integer.parseInt(matcher.group(pos++));
            int y2 = Integer.parseInt(matcher.group(pos++));
            int z1 = Integer.parseInt(matcher.group(pos++));
            int z2 = Integer.parseInt(matcher.group(pos++));

// For part 1:
//            if (x1 > 50 || x2 < -50 || y1 > 50 || y2 < -50 || z1 > 50 || z2 < -50) {
//                continue;
//            }

            Cuboid c = new Cuboid(x1, x2, y1, y2, z1, z2, turnOn);
            System.out.println(row++ + " " + c + " / so far " + cuboids.count() + " cuboids");
            cuboids.add(c);
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms\n");
        System.out.println(cuboids.volume());
    }

    private static class CuboidList {
        private final List<Cuboid> cuboids = new ArrayList<>();

        public long volume() {
            AtomicLong sum = new AtomicLong();
            cuboids.forEach(c -> {
                if (c.on) {
                    sum.addAndGet(c.volume());
                }
            });
            return sum.get();
        }

        public void add(Cuboid c) {
            List<Cuboid> restOfExisting = null;
            List<Cuboid> restOfC = null;
            Cuboid existing = null;
            Cuboid intersection = null;
            for (Cuboid cuboid : cuboids) {
                existing = cuboid;
                intersection = existing.intersect(c);
                if (intersection != null) {
                    restOfExisting = existing.subtract(intersection);
                    restOfC = c.subtract(intersection);
                    break;
                }
            }
            if (intersection != null) {
                cuboids.remove(existing);
                cuboids.add(intersection);
                cuboids.addAll(restOfExisting);
                for (Cuboid rest : restOfC) {
                    add(rest);
                }
            } else {
                // success!
                cuboids.add(c);
            }
        }

        public int count() {
            return cuboids.size();
        }
    }

    private static class Cuboid {
        private final long x1;
        private final long x2;
        private final long y1;
        private final long y2;
        private final long z1;
        private final long z2;
        private final boolean on;

        public Cuboid(long x1, long x2, long y1, long y2, long z1, long z2, boolean on) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.z1 = z1;
            this.z2 = z2;
            this.on = on;
        }

        public Cuboid intersect(Cuboid cand) {
            boolean xIn = (within(cand.x1, x1, x2) || within(cand.x2, x1, x2));
            boolean yIn = (within(cand.y1, y1, y2) || within(cand.y2, y1, y2));
            boolean zIn = (within(cand.z1, z1, z2) || within(cand.z2, z1, z2));
            if (xIn || yIn || zIn) {
                long xx1 = Math.max(x1, cand.x1);
                long xx2 = Math.min(x2, cand.x2);
                long yy1 = Math.max(y1, cand.y1);
                long yy2 = Math.min(y2, cand.y2);
                long zz1 = Math.max(z1, cand.z1);
                long zz2 = Math.min(z2, cand.z2);
                if (xx1 <= xx2 && yy1 <= yy2 && zz1 <= zz2) {
                    return (new Cuboid(xx1, xx2, yy1, yy2, zz1, zz2, cand.on));
                }
            }
            return null;
        }

        public long volume() {
            return (x2 - x1 + 1) * (y2 - y1 + 1) * (z2 - z1 + 1);
        }

        public List<Cuboid> subtract(Cuboid remove) {
            List<Cuboid> ret = new ArrayList<>();
            long[][] xCand = new long[][]{
                    {x1, remove.x1 - 1},
                    {remove.x1, remove.x2},
                    {remove.x2 + 1, x2}
            };
            long[][] yCand = new long[][]{
                    {y1, remove.y1 - 1},
                    {remove.y1, remove.y2},
                    {remove.y2 + 1, y2}
            };
            long[][] zCand = new long[][]{
                    {z1, remove.z1 - 1},
                    {remove.z1, remove.z2},
                    {remove.z2 + 1, z2}
            };
            for (int x = 0; x < xCand.length; x++) {
                for (int y = 0; y < yCand.length; y++) {
                    for (int z = 0; z < zCand.length; z++) {
                        if (x != 1 || y != 1 || z != 1) {
                            addIfOk(ret, xCand[x][0], xCand[x][1], yCand[y][0], yCand[y][1], zCand[z][0], zCand[z][1]);
                        }
                    }
                }
            }

            return ret;
        }

        private void addIfOk(List<Cuboid> ret, long x1, long x2, long y1, long y2, long z1, long z2) {
            if (x1 <= x2 && y1 <= y2 && z1 <= z2) {
                ret.add(new Cuboid(x1, x2, y1, y2, z1, z2, on));
            }
        }

        @Override
        public String toString() {
            return (on ? "on ":"off ") +
                    x1 + ".." + x2 + " " +
                    y1 + ".." + y2 + " " +
                    z1 + ".." + z2;
        }
    }

    public static boolean within(long i, long lower, long upper) {
        return i >= lower && i <= upper;
    }
}
