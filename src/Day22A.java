import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day22A {
    public static final String EXAMPLE1 = "day22-1.txt";
    public static final String EXAMPLE2 = "day22-2.txt";
    public static final String REAL = "day22-3.txt";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(REAL));
        String input;
        int row = 0;
        // on x=10..12,y=10..12,z=10..12
        Pattern pattern = Pattern.compile("(on|off) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)");
        HashSet<Coord> on = new HashSet<>();
        while ((input = br.readLine()) != null) {
            Matcher matcher = pattern.matcher(input);
            if (!matcher.matches()) {
                System.out.println("no match");
                continue;
            }
            int pos = 1;
            String command = matcher.group(pos++);
            boolean turnOn = command.equals("on");
            int x1 = Integer.parseInt(matcher.group(pos++));
            int x2 = Integer.parseInt(matcher.group(pos++));
            int y1 = Integer.parseInt(matcher.group(pos++));
            int y2 = Integer.parseInt(matcher.group(pos++));
            int z1 = Integer.parseInt(matcher.group(pos++));
            int z2 = Integer.parseInt(matcher.group(pos++));

            if (x1 > 50 || x2 < -50 || y1 > 50 || y2 < -50 || z1 > 50 || z2 < -50) {
                continue;
            }

            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    for (int z = z1; z <= z2; z++) {
                        if (turnOn) {
                            on.add(new Coord(x, y, z));
                        } else {
                            on.remove(new Coord(x, y, z));
                        }
                    }
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");
        System.out.println(on.size());
    }

    private static class Coord {
        int x, y, z;

        public Coord(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Coord coord = (Coord) o;
            return x == coord.x && y == coord.y && z == coord.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }
}
