package Game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;

class Statistic {
    private int winsCount;
    private int lossesCount;
    private int currentWinStreak;
    private int maxWinStreak;
    private int bestAttemptsCount;
    private int countOfBestAttempts;
    private final ArrayList<Double> timePasses;
    private final Timeline timeline;
    private int minutes = 0, seconds = 0, millis = 0;
    private String minutesStr = "", secondsStr = "", millisStr = "";

    Statistic() {
        winsCount = 0;
        lossesCount = 0;
        maxWinStreak = 0;
        bestAttemptsCount = 0;
        countOfBestAttempts = 0;
        timePasses = new ArrayList<>();
        timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> change()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);
    }

    void change() {
        if (millis == 1000) {
            seconds++;
            millis = 0;
        }
        if (seconds == 60) {
            minutes++;
            seconds = 0;
        }
        millis++;
        minutesStr = String.format("%02d", minutes);
        secondsStr = String.format("%02d", seconds);
        millisStr = String.format("%03d", millis);
    }

    void increaseWinsCount() {
        winsCount++;
        this.currentWinStreak++;
        this.maxWinStreak = Math.max(maxWinStreak, currentWinStreak);
    }

    void increaseLossesCount() {
        lossesCount++;
        this.currentWinStreak = 0;
    }

    void updateBestAttemptsCount(int bestAttemptsCount) {
        if (this.bestAttemptsCount > bestAttemptsCount || this.bestAttemptsCount == 0) {
            this.bestAttemptsCount = bestAttemptsCount;
            this.countOfBestAttempts = 1;
        } else if (this.bestAttemptsCount == bestAttemptsCount) {
            this.countOfBestAttempts++;
        }
    }

    void setGameStarted() {
        timeline.play();
    }

    void setGameEnded() {
        timeline.stop();
        timePasses.add(getGameTimePassed());
        resetTime();
    }

    private void resetTime() {
        minutes = 0;
        seconds = 0;
        millis = 0;
    }

    public double getGameTimePassed() {
        System.out.printf("time passed: %02.6f%n", (double) ((minutes * 60 + seconds) * 1000 + millis) / 60000);
        return (double) ((minutes * 60 + seconds) * 1000 + millis) / 60000;
    }

    String calculateAverageTime() {
        double allTimePassed = 0;
        for (Double time : timePasses) {
            allTimePassed += time;
        }
        double averageTime = allTimePassed / timePasses.size();
        long averageMillis = (long) (averageTime * 60000);
        int minutes = (int) (averageMillis / 60000);
        int seconds = (int) (averageMillis / 1000 % 60);
        int millis = (int) (averageMillis % 1000);
        System.out.printf("%02d:%02d:%03d\n", minutes, seconds, millis);
        return String.format("%02d:%02d:%03d", minutes, seconds, millis);
    }

    String getTimePassed() {
        return minutesStr + ":" + secondsStr + ":" + millisStr;
    }

    String getAllResults() {
        String result = "";
        result += "Wins: " + winsCount + "\n";
        result += "Losses: " + lossesCount + "\n";
        result += "Current win streak: " + currentWinStreak + "\n";
        result += "Max win streak: " + maxWinStreak + "\n";
        result += "Best attempts count: " + bestAttemptsCount + "\n";
        result += "Count of best attempts count: " + countOfBestAttempts + "\n";
        result += "Average time: " + calculateAverageTime() + " (min)" + "\n";
        return result;
    }
}

public class Game {
    public static final int DEFAULT_CAPACITY = 4;
    private final ArrayList<Integer> generatedNumber;
    private int countOfTries;
    public static boolean isEnded;
    public static boolean isStarted;
    private final Statistic statistic;

    public Game() {
        generatedNumber = new ArrayList<>(DEFAULT_CAPACITY);
        countOfTries = 0;
        generateNumbers();
        statistic = new Statistic();
    }

    public void restart() {
        countOfTries = 0;
        isEnded = false;
        generatedNumber.clear();
        generateNumbers();
    }

    public String getAnswer() {
        int numeric = 0;
        for (Integer number : generatedNumber) {
            numeric = numeric * 10 + number;
        }
        return String.format("%04d", numeric);
    }

    public void generateNumbers() {
        ArrayList<Integer> possibleNumbers = new ArrayList<>(10);
        for (int i = 0; i < 10; ++i) {
            possibleNumbers.add(i);
        }

        Collections.shuffle(possibleNumbers);
        for (int i = 0; i < DEFAULT_CAPACITY; ++i) {
            generatedNumber.add(possibleNumbers.get(i));
        }
    }

    public boolean isGuessCorrect(String guess) {
        if (!isStarted) {
            statistic.setGameStarted();
            isStarted = false;
        }

        if (guess.length() != 4) {
            return false;
        }

        String uniqueChars = guess;
        for (int i = 0; i < 4; i++) {
            uniqueChars = uniqueChars.replaceAll(String.valueOf(guess.charAt(i)), "");
            if (uniqueChars.equals("") && i != 3) {
                return false;
            }

            int num = Integer.parseInt(String.valueOf(guess.charAt(i)));
            if (num < 0 || num > 9) {
                return false;
            }
        }
        return true;
    }

    public static ArrayList<Integer> stringToArrayListOfIntegers(String string) {
        ArrayList<Integer> integerArrayList = new ArrayList<>(DEFAULT_CAPACITY);
        for (char ch : string.toCharArray()) {
            int num = Integer.parseInt(String.valueOf(ch));
            integerArrayList.add(num);
        }
        return integerArrayList;
    }

    public String guess(ArrayList<Integer> guessedNumber) {
        ++countOfTries;
        String guessResult;
        int bullsCount = 0;
        int cowsCount = 0;

        for (int i = 0; i < guessedNumber.size(); ++i) {
            if (Objects.equals(guessedNumber.get(i), generatedNumber.get(i))) {
                ++bullsCount;
            } else if (generatedNumber.contains(guessedNumber.get(i))) {
                ++cowsCount;
            }
        }

        guessResult = "C" + cowsCount + "B" + bullsCount + "\n";
        if (bullsCount == 4) {
            statistic.setGameEnded();
            statistic.increaseWinsCount();
            statistic.updateBestAttemptsCount(countOfTries);
            isEnded = true;
        }

        return guessResult;
    }

    public int getCountOfTries() {
        return countOfTries;
    }

    public String getTimePassed() {
        return statistic.getTimePassed();
    }

    public String getGameResults() {
        return statistic.getAllResults();
    }

    public static void main(String[] args) {
        Game game = new Game();
        Scanner scanner = new Scanner(System.in);
        while (!Game.isEnded) {
            String guess = scanner.nextLine();

            if (game.isGuessCorrect(guess)) {
                game.guess(stringToArrayListOfIntegers(guess));
            } else {
                System.out.println("Expected number like: '____', got: " + guess);
            }

        }
    }
}
