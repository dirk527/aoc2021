package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day02 {
    private static Pattern linePat = Pattern.compile("Game (\\d+): (.*)");

    public static void main(String[] args) throws IOException {
//        BufferedReader br = new BufferedReader(new FileReader("02-sample.txt"));
        BufferedReader br = new BufferedReader(new FileReader("02-input.txt"));

        String s;
        List<Game> games = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            Matcher mat = linePat.matcher(s);
            if (mat.matches()) {
                Game cur = new Game(Integer.parseInt(mat.group(1)));
                games.add(cur);
                var gs = mat.group(2).split("; ");
                for (String g : gs) {
                    cur.addDraw(new Draw(g));
                }
            }
        }

        int sum = 0;
        for (Game g : games) {
            if (g.inConstraint(12, 13, 14)) {
                sum += g.getNo();
            }
        }
        System.out.println(sum);

        sum = 0;
        for (Game g : games) {
            sum += g.power();
        }
        System.out.println(sum);
    }

    private static class Game {
        private int no;
        private List<Draw> draws = new ArrayList<>();

        public Game(int no) {
            this.no = no;
        }

        public void addDraw(Draw d) {
            draws.add(d);
        }

        public int getNo() {
            return no;
        }

        public boolean inConstraint(int red, int green, int blue) {
            for (Draw d : draws) {
                if (!d.inConstraint(red, green, blue)) {
                    return false;
                }
            }
            return true;
        }

        public int power() {
            int red = 0;
            int green = 0;
            int blue = 0;
            for (Draw d : draws) {
                red = Math.max(red, d.red);
                green = Math.max(green, d.green);
                blue = Math.max(blue, d.blue);
            }
            return red * green * blue;
        }
    }

    private static Pattern bluePat = Pattern.compile("(\\d+) blue");
    private static Pattern redPat = Pattern.compile("(\\d+) red");
    private static Pattern greenPat = Pattern.compile("(\\d+) green");

    private static class Draw {
        int blue;
        int red;
        int green;

        Draw(String in) {
            // 2 blue, 1 red, 2 green
            var parts = in.split(", ");
            for (String s : parts) {
                Matcher mat = bluePat.matcher(s);
                if (mat.matches()) {
                    blue = Integer.parseInt(mat.group(1));
                }
                mat = redPat.matcher(s);
                if (mat.matches()) {
                    red = Integer.parseInt(mat.group(1));
                }
                mat = greenPat.matcher(s);
                if (mat.matches()) {
                    green = Integer.parseInt(mat.group(1));
                }
            }
        }

        public boolean inConstraint(int red, int green, int blue) {
            return red >= this.red && green >= this.green && blue >= this.blue;
        }
    }
}