//package main;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.HashMap;
//import java.util.LinkedHashSet;
//import java.util.Random;
//import java.util.Set;
//
//class Cell extends JButton {
//
//    enum FieldType {
//        BOMB,
//        NUMBER,
//        EMPTY
//    }
//
//    private int xCoordinate;
//    private int yCoordinate;
//    private boolean isHidden;
//    private ImageIcon icon;
//    private FieldType type;
//
//    Cell(int xCoordinate, int yCoordinate) {
//        this.xCoordinate = xCoordinate;
//        this.yCoordinate = yCoordinate;
//        this.isHidden = true;
//        this.icon = new ImageIcon("empty.jpeg");
//        this.type = FieldType.EMPTY;
//        this.setIcon(icon);
//    }
//
//    void setType(FieldType type) {
//       this.type = type;
//    }
//
//    FieldType getType() {
//        return type;
//    }
//}
//
//class GameField {
//
//    static final HashMap<String, ImageIcon> fieldImages = new HashMap<>() {{
//        put("emptyField", new ImageIcon("empty.jpeg"));
//        put("bombField", new ImageIcon("bomb.jpeg"));
//        put("flagField", new ImageIcon("flag.jpeg"));
//        // put("hiddenField", new ImageIcon("empty.jpeg"));
//    }};
//    static final HashMap<Integer, ImageIcon> numberImages = new HashMap<>() {{
//        put(1, new ImageIcon("numberOne.jpeg"));
//        put(2, new ImageIcon("numberTwo.jpeg"));
//        put(3, new ImageIcon("numberThree.jpeg"));
//        put(4, new ImageIcon("numberFour.jpeg"));
//        put(5, new ImageIcon("numberFive.jpeg"));
//        put(6, new ImageIcon("numberSix.jpeg"));
//        put(7, new ImageIcon("numberSeven.jpeg"));
//        put(8, new ImageIcon("numberEight.jpeg"));
//    }};
//
//    private int height;
//    private int width;
//    private final int bombsCount;
//    private final Cell [][]field = new Cell[height][width];
//
//    GameField(int height, int width, int bombsCount) {
//        this.height = height;
//        this.width = width;
//        this.bombsCount = bombsCount;
//        generateFields();
//    }
//
//    void generateFields() {
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                field[i][j] = new Cell(i, j);
//            }
//        }
//        generateBombs();
//        generateNumbers();
//    }
//
//    void generateBombs() {
//        Random randNum = new Random();
//        Set<Integer> bombsSet = new LinkedHashSet<Integer>();
//        while (bombsSet.size() < bombsCount) {
//            bombsSet.add(randNum.nextInt(height * width));
//        }
//
//        for (Integer index : bombsSet) {
//            int x = index / width;
//            int y = index % width;
//            field[x][y].setType(Cell.FieldType.BOMB);
//            field[x][y].setIcon(fieldImages.get("bomb.jpeg"));
//        }
//    }
//
//    void generateNumbers() {
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                if (field[i][j].getType() == Cell.FieldType.BOMB) {
//                    continue;
//                }
//                field[i][j].setType(Cell.FieldType.NUMBER);
//                field[i][j].setIcon(numberImages.get(getNearBombsCount(i, j)));
//            }
//        }
//    }
//
//    private int getNearBombsCount(int xCoordinates, int yCoordinates) {
//        int nearBombsCount = 0;
//
//        for (int i = Math.max(xCoordinates - 1, 0); i < Math.min(xCoordinates + 2, height); i++) {
//            for (int j = Math.max(yCoordinates - 1, 0); j < Math.min(yCoordinates + 2, width); j++) {
//                if (field[i][j].getType() != Cell.FieldType.BOMB) {
//                    continue;
//                }
//                nearBombsCount++;
//            }
//        }
//        return nearBombsCount;
//    }
//
//    Cell getCell(int xCoordinate, int yCoordinate) {
//        return field[xCoordinate][yCoordinate];
//    }
//}
//
//public class MinerGame_V1 extends JPanel {
//    static final JPanel gamePanel = new JPanel();
//
//    MinerGame_V1(int height, int width, int bombsCount) {
//        gamePanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
//        gamePanel.setLayout(new GridLayout(height, width));
//
//        GameField gameField = new GameField(height, width, bombsCount);
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                gamePanel.add(gameField.getCell(i, j));
//            }
//        }
//    }
//}
//
//class GameDemo {
//    public static void main(String[] args) {
//        JFrame demo = new JFrame("Miner game demo");
//        demo.setLayout(new BorderLayout());
//        demo.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        demo.setResizable(false);
//
//        MinerGame_V1 miner = new MinerGame_V1(15, 20, 60);
//
//        demo.add(miner);
//        demo.pack();
//        demo.setVisible(true);
//    }
//}
