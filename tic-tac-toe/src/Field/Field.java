package Field;

import javax.swing.*;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

class CellScanner {
    static <T extends Cell> boolean read(T el) {
        Scanner scanner = new Scanner(System.in);
        int x;
        int y;
        String str;
        if (!scanner.hasNextLine()) {
            return false;
        }
        str = scanner.nextLine();
        if (!Pattern.matches("\\([0-2], [0-2]\\)", str)) {
            return false;
        }
        x = str.charAt(1);
        y = str.charAt(3);
        Game.field.setCell(x, y, Game.playersMark);
        return true;
    }
}

class Cell {
    enum State {
        cross,
        zero,
        none
    }

    private int x;
    private int y;
    private ImageIcon imageIcon;
    private Character cellCharState;
    private State cellState;

    Cell() {
        cellCharState = ' ';
        cellState = State.none;
    }

    @Override
    public String toString() {
        return cellCharState.toString();
    }

    State getState() {
        return cellState;
    }

    void setState(Cell.State state) {
        this.cellState = state;
        switch (state) {
            case zero -> {
                cellCharState = 'o';
            }
            case cross -> {
                cellCharState = 'x';
            }
        }
    }
}

public class Field {
    private final int height;
    private final int width;
    private final Cell[][] field;

    Field(int height, int width) {
        this.height = height;
        this.width = width;
        field = new Cell[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                field[i][j] = new Cell();
            }
        }
    }

    void printField() {
        StringBuilder str = new StringBuilder(" ");
        for (int j = 0; j < width; j++) {
            str.append(" ").append(j);
        }

        System.out.println(str);
        for (int i = 0; i < height; i++) {
            System.out.printf("%d|", i);
            for (int j = 0; j < width; j++) {
                System.out.print(field[i][j] + "|");
            }
            System.out.println("");
        }
    }

    boolean isEmptyCell(int x, int y) {
        return field[x][y].getState() == Cell.State.none;
    }

    void setCell(int x, int y, Cell.State currentMark) {
        field[x][y].setState(currentMark);
    }

    boolean isFull() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (field[i][j].getState() == Cell.State.none) {
                    return false;
                }
            }
        }
        return true;
    }

    Cell getCell(int x, int y) {
        return field[x][y];
    }
}

class Game {
    enum Mode {
        easy,
        normal,
        hard
    }
    static Field field;
    static Random random = new Random();
    static int currentMove = random.nextInt(2);
    static Cell.State playersMark;
    static Cell.State computersMark;
    static String winnerName;
    static Mode gameMode;
    static private final Scanner scanner = new Scanner(System.in);

    static void randomMark() {
        if (random.nextInt(2) == 0) {
            playersMark = Cell.State.cross;
            computersMark = Cell.State.zero;
            return;
        }
        computersMark = Cell.State.cross;
        playersMark = Cell.State.zero;
    }

    static boolean checkCell(int x, int y) {
        return true;
    }

    static Cell.State somebodyWins() {
        Cell.State cell_00 = field.getCell(0, 0).getState();
        Cell.State cell_01 = field.getCell(0, 1).getState();
        Cell.State cell_02 = field.getCell(0, 2).getState();
        Cell.State cell_10 = field.getCell(1, 0).getState();
        Cell.State cell_11 = field.getCell(1, 1).getState();
        Cell.State cell_12 = field.getCell(1, 2).getState();
        Cell.State cell_20 = field.getCell(2, 0).getState();
        Cell.State cell_21 = field.getCell(2, 1).getState();
        Cell.State cell_22 = field.getCell(2, 2).getState();

        if (cell_00 == cell_01 && cell_01 == cell_02) {
            return cell_00;
        }
        if (cell_10 == cell_11 && cell_11 == cell_12) {
            return cell_10;
        }
        if (cell_20 == cell_21 && cell_21 == cell_22) {
            return cell_20;
        }
        if (cell_00 == cell_10 && cell_10 == cell_20) {
            return cell_00;
        }
        if (cell_01 == cell_11 && cell_11 == cell_21) {
            return cell_01;
        }
        if (cell_02 == cell_12 && cell_12 == cell_22) {
            return cell_02;
        }
        if (cell_00 == cell_11 && cell_11 == cell_22) {
            return cell_00;
        }
        if (cell_20 == cell_11 && cell_11 == cell_02) {
            return cell_20;
        }
        return Cell.State.none;
    }

    static boolean gameIsFinished() { // bad decision
        Cell.State state = somebodyWins();
        if (state == Cell.State.none) {
            winnerName = "nobody";
            return field.isFull();
        }

        if (state == computersMark) {
            winnerName = "super-bot";
        } else {
            winnerName = "player";
        }

        return true;
    }

    static void playNewRound() {
        if (currentMove % 2 == 0) {
            playersMove();
        } else {
            computerMove();
        }
        currentMove++;
    }

    static void playersMove() {
        System.out.print("Player turn: ");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        while (!field.isEmptyCell(x, y)) {
            System.out.println("This field is already in use, choose another one");
            x = scanner.nextInt();
            y = scanner.nextInt();
        }
        field.setCell(x, y, playersMark);
    }

    static void printGame() {
        field.printField();
    }

    static void easyMove() {
        int x = random.nextInt(3);
        int y = random.nextInt(3);
        while (!field.isEmptyCell(x, y)) {
            x = random.nextInt(3);
            y = random.nextInt(3);
        }
        field.setCell(x, y, computersMark);
        System.out.printf("[%d, %d]\n", x, y);
    }

    static void normalMove() {
        int easyMoveChance = 4;
        if (random.nextInt(easyMoveChance) == 0) {
            easyMove();
        }
        // normal move logic
    }

    static void hardMove() {
        int normalMoveChance = 4;
        if (random.nextInt(normalMoveChance) == 0) {
            normalMove();
        }
        // hard move logic
    }

    static void computerMove() {
        System.out.print("Bot turn: ");
        switch (gameMode) {
            case easy -> {
                easyMove();
            }
            case normal -> {
                normalMove();
            }
            case hard -> {
                hardMove();
            }
        }
    }

    public static void main(String[] args) {
        field = new Field(3, 3);
        System.out.println("Enter cell position: (x, y)\n" +
                "[where x - is vertical position and y - is horizontal]");
        Game.gameMode = Mode.easy;
        randomMark();
        System.out.println("Players mark is: " + playersMark);

        while (!gameIsFinished()) {
            printGame();
            playNewRound();
        }
        printGame();

        System.out.println("Game is over, " + winnerName + " wins");
    }
}
