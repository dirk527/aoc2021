public class Day21 {

    public static void main(String[] args) {
        // we number from 0 to 9, that's easier
        int player1Pos = 3;
        int player2Pos = 6;
        int player1Score = 0;
        int player2Score = 0;
        boolean player1Turn = true;
        DeterministicDie die = new DeterministicDie();
        while (player1Score < 1000 && player2Score < 1000) {
            if (player1Turn) {
                player1Pos += die.roll() + die.roll() + die.roll();
                player1Score += player1Pos % 10 + 1;
                System.out.println("p1 score " + player1Score);
            } else {
                player2Pos += die.roll() + die.roll() + die.roll();
                player2Score += player2Pos % 10 + 1;
                System.out.println("p2 score " + player2Score);
            }
            player1Turn = !player1Turn;
        }

        System.out.println(Math.min(player1Score, player2Score) * (die.cur - 1));
    }

    private static class DeterministicDie {
        private int cur = 1;

        public int roll() {
            return cur++;
        }
    }
}
