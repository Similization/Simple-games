package Bot;

import Game.Game;

import java.util.ArrayList;

public class Bot {
    private static class Number {
        enum Status {
            UNDEFINED,
            UNUSED,
            USED
        }
        private final int number;
        private Status status;
        private final ArrayList<Integer> possiblePositions;
        Number(int number) {
            this.number = number;
            status = Status.UNDEFINED;
            int POSITION_COUNT = 4;
            possiblePositions = new ArrayList<>(POSITION_COUNT);
            for (int i = 0; i < POSITION_COUNT; ++i) {
                possiblePositions.add(i);
            }
        }
        public void setStatus(Status status) {
            this.status = status;
            if (status == Status.UNUSED) {
                possiblePositions.clear();
            }
        }
        public void deletePossiblePosition(Integer possiblePosition) {
            this.possiblePositions.remove(possiblePosition);
        }
        public int getNumber() {
            return number;
        }
        public ArrayList<Integer> getPossiblePositions() {
            return possiblePositions;
        }
    }
    private static class Multiplicity {
        int cowsCount;
        int bullsCount;
        ArrayList<Integer> numbers;
        Multiplicity(ArrayList<Integer> numbers, int cowsCount, int bullsCount) {
            this.cowsCount = cowsCount;
            this.bullsCount = bullsCount;
            this.numbers = numbers;
        }
    }
    private final Game game;
    private ArrayList<Multiplicity> multiplicities;
    private ArrayList<Number> numberPool;
    Bot(Game game) {
        this.game = game;
        multiplicities = new ArrayList<>();
        int NUMBER_COUNT = 10;
        numberPool = new ArrayList<>(NUMBER_COUNT);
        for (int i = 0; i < NUMBER_COUNT; ++i) {
            numberPool.add(new Number(i));
        }
    }
    ArrayList<Integer> getBestGuess() {
        int POSITION_COUNT = 4;
        ArrayList<Integer> bestGuess = new ArrayList<>(POSITION_COUNT);
        if (multiplicities.isEmpty()) {
            for (int i = 0; i < POSITION_COUNT; ++i) {
                bestGuess.add(i);
            }
            return bestGuess;
        }
        // else
        return bestGuess;
    }
    void makeGuess() {
        ArrayList<Integer> bestGuess = getBestGuess();
        String resultOfGuess = game.guess(bestGuess);
        updateInformation(resultOfGuess, bestGuess);
    }
    void updateInformation(String info, ArrayList<Integer> guess) {
        // other ways to get cows count and bulls count
        int cowsCount = Integer.parseInt(info.substring(1, 2));
        int bullsCount = Integer.parseInt(info.substring(3, 4));
        // insert multiplicity
        multiplicities.add(new Multiplicity(guess, cowsCount, bullsCount));
        // update information
        if (bullsCount != 0) {
        }
        else if (cowsCount != 0) {
            for (int i = 0; i < guess.size(); ++i) {
                numberPool.get(guess.get(i)).deletePossiblePosition(i);
            }
        }
        else {
            for (Integer number : guess) {
                numberPool.get(number).setStatus(Number.Status.UNUSED);
            }
        }
    }

}

class StartBot {
    public static void main(String[] args) {
        Game game = new Game();
        Bot bot = new Bot(game);
        while (!Game.isEnded) {
            bot.makeGuess();
        }
    }
}
