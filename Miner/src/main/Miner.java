package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

class Field extends JButton {
    static final HashMap<String, ImageIcon> fieldImages = new HashMap<>() {{
        put("emptyField", new ImageIcon("empty.jpeg"));
        put("bombField", new ImageIcon("bomb.jpeg"));
        put("flagField", new ImageIcon("flag.jpeg"));
        put("hiddenField", new ImageIcon("empty.jpeg"));
    }};
    static final HashMap<Integer, ImageIcon> numberImages = new HashMap<>() {{
        put(1, new ImageIcon("numberOne.jpeg"));
        put(2, new ImageIcon("numberTwo.jpeg"));
        put(3, new ImageIcon("numberThree.jpeg"));
        put(4, new ImageIcon("numberFour.jpeg"));
        put(5, new ImageIcon("numberFive.jpeg"));
        put(6, new ImageIcon("numberSix.jpeg"));
        put(7, new ImageIcon("numberSeven.jpeg"));
        put(8, new ImageIcon("numberEight.jpeg"));
    }};
    enum FieldType {
        BOMB,
        NUMBER,
        EMPTY
    }
    private FieldType type;
    private final int xCoordinate;
    private final int yCoordinate;
    private ImageIcon fieldImage;
    private boolean isHidden;
    private long startTime;

    public Field(int x, int y) {
        xCoordinate = x;
        yCoordinate = y;
        type = FieldType.EMPTY;
        isHidden = true;

        this.setPreferredSize(new Dimension(50, 50));
        this.setIcon(fieldImages.get("hiddenField"));
        this.setBorder(BorderFactory.createEmptyBorder());

//        this.addActionListener(e -> {
//        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startTime = System.currentTimeMillis();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (System.currentTimeMillis() - startTime > 1000) {
                    setIcon(fieldImages.get("flagField.jpeg"));
                    System.out.println("here");
                } else {
                    setIcon(fieldImage);
                    setEnabled(false);
                    setDisabledIcon(fieldImage);
                    isHidden = false;

                    if (type == FieldType.BOMB) {
                        System.out.println("You loose");
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        MinerGame.loosesCount++;
                        MinerGame.restart();
                    }
                    if (MinerGame.isGameFinished()) {
                        System.out.println("You win");
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        MinerGame.winsCount++;
                        MinerGame.restart();
                    }
                }
            }
        });
    }

    public void setType(FieldType type, int nearBombsCount) {
        this.type = type;
        switch (type) {
            case EMPTY -> { }
            case BOMB -> this.fieldImage = fieldImages.get("bombField");
            case NUMBER -> this.fieldImage = numberImages.get(nearBombsCount);
            default -> this.fieldImage = fieldImages.get("emptyField");
        }
    }
    public void setType(FieldType type) {
        setType(type, 0);
    }

    public void changeColor() {
        this.setBackground(Color.orange);
    }

    public FieldType getType() {
        return type;
    }
    public int getXCoordinate() {
        return xCoordinate;
    }
    public int getYCoordinate() {
        return yCoordinate;
    }
    boolean isHidden() {
        return isHidden;
    }
}

public class Miner extends JPanel {
    static int bombsCount = 36;
    private final int width;
    private final int height;
    private final Field[][] fields;

    Miner() {
        this.setBackground(Color.lightGray);
        width = 16;
        height = 12;
        fields = new Field[height][width];

        this.setLayout(new GridLayout(height, width, 0, 0));
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                fields[i][j] = new Field(i, j);
                this.add(fields[i][j]);
            }
        }
        generateBombs();
        generateNumbers();
    }

    public int getNearBombsCount(Field field) {
        int nearBombsCount = 0;
        int x = field.getXCoordinate();
        int y = field.getYCoordinate();

        for (int i = Math.max(x - 1, 0); i < Math.min(x + 2, height); i++) {
            for (int j = Math.max(y - 1, 0); j < Math.min(y + 2, width); j++) {
                if (fields[i][j].getType() != Field.FieldType.BOMB) {
                    continue;
                }
                nearBombsCount++;
            }
        }
        return nearBombsCount;
    }
    private void generateBombs() {
        for (int i = 0; i < bombsCount; i++) {
            int x;
            int y;
            do {
                x = (int) (Math.random() * height);
                y = (int) (Math.random() * width);
            } while (fields[x][y].getType() == Field.FieldType.BOMB);
            fields[x][y].changeColor();
            fields[x][y].setType(Field.FieldType.BOMB);
        }
    }

    private void generateNumbers() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (fields[i][j].getType() == Field.FieldType.BOMB) {
                    continue;
                }
                fields[i][j].setType(Field.FieldType.NUMBER, getNearBombsCount(fields[i][j]));
            }
        }
    }

    public Field[][] getFields() {
        return fields;
    }

    //    public void increaseBombsCount(int bombsCount) {
//        if (bombsCount > this.width * this.height - Miner.bombsCount) {
//            System.out.printf("Wrong bombs count. Game add %d by default and you tried to add %d." +
//                              "Summary bombs count is %d, which is bigger than fields count: %d",
//                              Miner.bombsCount, bombsCount, Miner.bombsCount + bombsCount, width*height);
//        }
//        Miner.bombsCount += bombsCount;
//    }
//    public void setHeight(int height) {
//        this.height = height;
//    }
//    public void setWidth(int width) {
//        this.width = width;
//    }
}

class MinerGame {
    static int winsCount = 0;
    static int loosesCount = 0;

    static Miner panelCenter = new Miner();

    static boolean isGameFinished() {
        for (Field[] cells : panelCenter.getFields()) {
            for (Field cell : cells) {
                if (cell.isHidden() && cell.getType() != Field.FieldType.BOMB) {
                    return false;
                }
            }
        }
        return true;
    }

    static void restart() {
        System.out.printf(
                "Your statistics:\nWins: %d\nLosses: %d\nRatio of wins to losses: %f",
                winsCount,
                loosesCount,
                (double) winsCount / loosesCount
                );
        JFrame mainWindow = new JFrame("Miner game");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setLayout(new BorderLayout());
        mainWindow.getContentPane().setBackground(Color.orange);
        mainWindow.setResizable(false);

        JPanel panelTop = new JPanel();
        JPanel panelLeft = new JPanel();
        panelCenter = new Miner();
        JPanel panelRight = new JPanel();
        JPanel panelBottom = new JPanel();

        panelTop.setPreferredSize(new Dimension(50, 75));
        panelLeft.setPreferredSize(new Dimension(100, 50));
        panelRight.setPreferredSize(new Dimension(100, 50));
        panelBottom.setPreferredSize(new Dimension(50, 75));

        panelTop.setBackground(Color.black);
        panelLeft.setBackground(Color.black);
        panelRight.setBackground(Color.black);
        panelBottom.setBackground(Color.black);

        mainWindow.add(panelTop, BorderLayout.NORTH);
        mainWindow.add(panelLeft, BorderLayout.WEST);
        mainWindow.add(panelCenter, BorderLayout.CENTER);
        mainWindow.add(panelRight, BorderLayout.EAST);
        mainWindow.add(panelBottom, BorderLayout.SOUTH);

        mainWindow.pack();
        mainWindow.setVisible(true);
    }
    public static void main(String[] args) {
        restart();
    }
}
