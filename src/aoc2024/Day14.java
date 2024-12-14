package aoc2024;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14 {

//    private static final int W = 11;
//    private static final int H = 7;
//    private static final String FILE = "14-ex";

    private static final int W = 101;
    private static final int H = 103;
    private static final String FILE = "14-in";

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(FILE));

        String s;
        Pattern pat = Pattern.compile("p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)");
        List<Bot> bots = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            Matcher mat = pat.matcher(s);
            if (mat.matches()) {
                bots.add(new Bot(new Pos(Integer.parseInt(mat.group(1)), Integer.parseInt(mat.group(2))),
                        new Pos(Integer.parseInt(mat.group(3)), Integer.parseInt(mat.group(4)))));
            } else {
                System.out.println(s);
                System.exit(1);
            }
        }

        int lu = 0, ru = 0, ll = 0, rl = 0;
        for (Bot bot : bots) {
            Pos pos = bot.calcPos(100);
            if (pos.row < H / 2) {
                if (pos.col < W / 2) {
                    lu++;
                }
                if (pos.col > W / 2) {
                    ru++;
                }
            }
            if (pos.row > H / 2) {
                if (pos.col < W / 2) {
                    ll++;
                }
                if (pos.col > W / 2) {
                    rl++;
                }
            }
        }
        System.out.printf("%d %d %d %d%np1 = %d", lu, ru, ll, rl, lu * ru * ll * rl);

        // LOL part 2 we'll use human intelligence then, look at the pictures in Finder or Explorer or whatever
        File outputDir = new File("day14-pix");
        if (!outputDir.exists() && outputDir.isDirectory()) {
            System.out.println("please create dir " + outputDir);
            System.exit(1);
        }
        BufferedImage bi = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bi.getGraphics();
        for (int steps = 0; steps < 10000; steps++) {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
            graphics.setColor(Color.BLACK);

            for (Bot bot : bots) {
                Pos pos = bot.calcPos(steps);
                graphics.drawRect((int) pos.col(), (int) pos.row(), 1, 1);
            }

            ImageIO.write(bi, "png", new File(outputDir, "%04d.png".formatted(steps)));
        }
    }

    record Bot(Pos start, Pos velocity) {
        Pos calcPos(long steps) {
            return start.step(velocity, steps);
        }
    }

    record Pos(long col, long row) {
        Pos step(Pos v, long steps) {
            long c = (col + steps * v.col) % W;
            if (c < 0) {
                c += W;
            }
            long r = (row + steps * v.row) % H;
            if (r < 0) {
                r += H;
            }
            return new Pos(c, r);
        }
    }
}