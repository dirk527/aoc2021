package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day2 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("day2.txt"));
        String s;
        int points = 0;
        while ((s = br.readLine()) != null) {
            // part 1
            // Round round = new Round(fromChar(s.charAt(2)), fromChar(s.charAt(0)));
            // part 2
            Round round = calcRound(fromChar(s.charAt(0)), s.charAt(2));
            System.out.println(s + " -> " + round + ": " + round.points());
            points += round.points();
        }
        System.out.println(points);
    }

    private static Round calcRound(Choice opponent, char outcome) {
        Choice me;
        if (outcome == 'X') {
            me = switch (opponent) {
                case ROCK -> Choice.SCISSORS;
                case PAPER -> Choice.ROCK;
                case SCISSORS -> Choice.PAPER;
            };
        } else if (outcome == 'Y') {
            me = opponent;
        } else {
            me = switch (opponent) {
                case ROCK -> Choice.PAPER;
                case PAPER -> Choice.SCISSORS;
                case SCISSORS -> Choice.ROCK;
            };
        }
        return new Round(me, opponent);
    }

    static Choice fromChar(char c) {
        return switch(c) {
            case 'A', 'X' -> Choice.ROCK;
            case 'B', 'Y' -> Choice.PAPER;
            case 'C', 'Z' -> Choice.SCISSORS;
            default -> throw new IllegalArgumentException();
        };
    }

    private enum Choice {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);

        private final int points;

        Choice(int points) {
            this.points = points;
        }

        public int getPoints() {
            return points;
        }

        public float win(Choice other) {
            if (other == this) {
                return 0.5f;
            }
            return switch (this) {
                case ROCK -> other == SCISSORS ? 1 : 0;
                case PAPER -> other == ROCK ? 1 : 0;
                case SCISSORS -> other == PAPER ? 1 : 0;
            };
        }
    }

    record Round(Choice me, Choice opponent){
        public int points() {
            return me.getPoints() + ((int) (6 * me.win(opponent)));
        }

        @Override
        public String toString() {
            return me + " " + opponent;
        }
    }
}
