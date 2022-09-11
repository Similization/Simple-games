import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Cell extends JButton {
    enum type {
        SHIP,
        DEFEATED_SHIP,
        SEA,
        DEFEATED_SEA
    }
    private final int x;
    private final int y;
    private ImageIcon icon;
    private ImageIcon iconDisabled;
    private Cell.type cellType;

    Cell(int width, int height, int xCoordinate, int yCoordinate) {
        this.setPreferredSize(new Dimension(width, height));
        this.x = xCoordinate;
        this.y = yCoordinate;
        icon = new ImageIcon("imageSrc//hidden.jpeg");
        iconDisabled = new ImageIcon("imageSrc//hidden_d.jpeg");
        redraw();
        cellType = type.SEA;
    }

    void redraw() {
        this.setIcon(icon);
        this.setDisabledIcon(iconDisabled);
    }
    void setImageIcon(String iconName) {
        this.icon = new ImageIcon("imageSrc//" + iconName + ".jpeg");
        this.iconDisabled = new ImageIcon("imageSrc//" + iconName + "_d.jpeg");
    }
    void setCellType(Cell.type type) {
        this.cellType = type;
        switch (type) {
            case SHIP -> this.setImageIcon("ship");
            case DEFEATED_SHIP -> this.setImageIcon("ship_defeated");
            case DEFEATED_SEA -> this.setImageIcon("shoot");
        }
    }
    Cell.type getCellType() {
        return this.cellType;
    }
    public int getXCoordinate() {
        return x;
    }
    public int getYCoordinate() {
        return y;
    }
}

class Field extends JPanel implements MouseListener {
    HashMap<Integer, Integer> ships = new HashMap<>(4) {{
        put(4, 1); // 4 cell ship, ships count:1
        put(3, 2); // 3 cell ship, ships count:2
        put(2, 3); // etc
        put(1, 4);
    }};
    static final int height = 10;
    static final int width = 10;
    final Cell[][] field = new Cell[height][width];

    Field(int width, int height) {
        this.setLayout(new GridLayout(Field.height, Field.width));
        initializeField();
        this.setPreferredSize(new Dimension(width, height));
    }

    void initializeField() {
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                field[i][j] = new Cell(this.getWidth()/width, this.getHeight()/height, i, j);
                field[i][j].addMouseListener(this);
                this.add(field[i][j]);
            }
        }
    }
    void printField() {
        System.out.println("0  1  2  3  4  5  6  7  8  9  ");
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if (field[i][j].getCellType()== Cell.type.SEA) System.out.print("_  ");
                if (field[i][j].getCellType()== Cell.type.SHIP) System.out.print("X  ");
            }
            System.out.println();
        }
        System.out.println();
    }
    void generateShips() {
        ArrayList<Integer> ableCells = new ArrayList<>(height*width);
        for (int i = 0; i < height*width; i++) {
            ableCells.add(i);
        }
        HashMap<Integer, Integer> ableShips = new HashMap<>(ships);
        int shipLength = ships.size(); // 4
        Random random = new Random();
        while (!ableShips.isEmpty()) {
            Collections.shuffle(ableCells);
            ArrayList<Character> directions = new ArrayList<>(4);
            int index = 0;
            int x;
            int y;

            do {
                int firstElement = ableCells.get(index++);
                x = firstElement / height;
                y = firstElement % width;

                if (x + shipLength - 1 < height && ableCells.contains((x + shipLength - 1) * height + y)) {
                    directions.add('d'); // can be continued to UP
                }
                if (x - shipLength + 1 >= 0 && ableCells.contains((x - shipLength + 1) * height + y)) {
                    directions.add('u'); // can be continued to DOWN
                }
                if (y + shipLength - 1 < width && ableCells.contains(x * height + (y + shipLength - 1))) {
                    directions.add('r'); // can be continued to RIGHT
                }
                if (y - shipLength + 1 >= 0 && ableCells.contains(x * height + (y  - shipLength + 1))) {
                    directions.add('l'); // can be continued to LEFT
                }
            }
            while (directions.isEmpty());

            int directionId = random.nextInt(directions.size());
            Character direction = directions.get(directionId);
            int stepX = 0;
            int stepY = 0;
            switch (direction) {
                case 'u' -> stepX = -1;
                case 'd' -> stepX = 1;
                case 'r' -> stepY = 1;
                case 'l' -> stepY = -1;
            }

            for (int i = 0; i < shipLength; i++) {

                field[x + i*stepX][y + i*stepY].setCellType(Cell.type.SHIP);
                // ableCells.remove(( (Object)(x * height + y) ));
            }

            // removeAllCells(startX, startY, endX, endY, shipLength);
            int i = x - (stepX != 0 ? stepX : 1);
            int iMax = x + (stepX != 0 ? stepX*(shipLength) : 1);
            int j = y - (stepY != 0 ? stepY : 1);
            int jMax = y + (stepY != 0 ? stepY*(shipLength) : 1);

            if (i < iMax) {
                i = Math.max(0, i);
                iMax = Math.min(9, iMax);
            } else {
                int k = i;
                i = Math.max(0, iMax);
                iMax = Math.min(9, k);
            }

            if (j < jMax) {
                j = Math.max(0, j);
                jMax = Math.min(9, jMax);
            } else {
                int k = j;
                j = Math.max(0, jMax);
                jMax = Math.min(9, k);
            }

            for (int t = i; t <= iMax; t++) {
                for (int k = j; k <= jMax; k++) {
                    ableCells.remove(( (Object)(t * height + k) ));
                }
            }

            // * need to add ship on the deck *
            ableShips.get(shipLength);
            ableShips.put(shipLength, ableShips.get(shipLength) - 1);
            printField();
            System.out.println(i + " " + iMax + " " + j + " " + jMax + " " + direction + " " + directions);
            if (ableShips.get(shipLength) == 0) {
                ableShips.remove(shipLength);
                shipLength--;
            }
        }
    }
    void setFieldEnabled(boolean isEnabled) {
            this.setEnabled(isEnabled);
            Component[] components = this.getComponents();

            for (Component component : components) {
                Cell.type type = ((Cell)component).getCellType();
                if (type == Cell.type.DEFEATED_SHIP || type == Cell.type.DEFEATED_SEA) {
                    continue;
                }
                component.setEnabled(isEnabled);
            }
            Game.isFirstPlayerTurn = !Game.isFirstPlayerTurn;
    }
    void redrawArea(Cell cell, int prevX, int prevY) {
        int x = cell.getXCoordinate();
        int y = cell.getYCoordinate();
        for (int i = Math.max(x - 1, 0); i <= Math.min(x + 1, 9); i++) {
            for (int j = Math.max(y - 1, 0); j <= Math.min(y + 1, 9); j++) {
                if ((x == i && j == y) || (i == prevX && j == prevY)) {
                    continue;
                }
                if (field[i][j].getCellType() == Cell.type.SEA) {
                    field[i][j].setCellType(Cell.type.DEFEATED_SEA);
                    field[i][j].redraw();
                    field[i][j].setEnabled(false);
                }
                else if (field[i][j].getCellType() == Cell.type.DEFEATED_SHIP) {
                    redrawArea(field[i][j], x, y);
                }
            }
        }
    }
    boolean isLast(Cell cell) {
        int x = cell.getXCoordinate();
        int y = cell.getYCoordinate();

        int stepX = 1;
        int stepY = 1;
        int endCountX = 0;
        int endCountY = 0;
        Cell.type type;

        while (endCountX < 2) {
            if (stepX > 0 && x - stepX >= 0) {
                type = field[x - stepX][y].getCellType();
                if (type == Cell.type.SHIP) {
                    return false;
                }
                if (type == Cell.type.DEFEATED_SHIP) {
                    x -= stepX;
                }
                else {
                    endCountX++;
                    stepX *= -1;
                    x = cell.getXCoordinate();
                }
            }
            else if (stepX < 0 && x - stepX <= 9) {
                type = field[x - stepX][y].getCellType();
                if (type == Cell.type.SHIP) {
                    return false;
                }
                if (type == Cell.type.DEFEATED_SHIP) {
                    x -= stepX;
                }
                else {
                    x = cell.getXCoordinate();
                    break;
                }
            }
            else {
                endCountX++;
                stepX *= -1;
                x = cell.getXCoordinate();
            }
        }
        while (endCountY < 2) {
            if (stepY > 0 && y - stepY >= 0) {
                type = field[x][y - stepY].getCellType();
                if (type == Cell.type.SHIP) {
                    return false;
                }
                if (type == Cell.type.DEFEATED_SHIP) {
                    y -= stepY;
                }
                else {
                    endCountY++;
                    stepY *= -1;
                    y = cell.getYCoordinate();
                }
            }
            else if (stepY < 0 && y - stepY <= 9) {
                type = field[x][y - stepY].getCellType();
                if (type == Cell.type.SHIP) {
                    return false;
                }
                if (type == Cell.type.DEFEATED_SHIP) {
                    y -= stepY;
                }
                else {
                    break;
                }
            }
            else {
                endCountY++;
                stepY *= -1;
                y = cell.getYCoordinate();
            }
        }

        return true;
    }
    boolean isGameOver() {
        for (Cell[] cells : field) {
            for (Cell cell : cells) {
                if (cell.getCellType() == Cell.type.SHIP) {
                    return false;
                }
            }
        }
        return true;
    }
    void openField() {
        for (Cell[] cells : field) {
            for (Cell cell : cells) {
                cell.redraw();
            }
        }
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        Object object = e.getSource();
        Cell cell = (Cell)object;
        if (!cell.isEnabled()) {
            return;
        }
        cell.setEnabled(false);
        cell.removeMouseListener(this);
        cell.redraw();
        try {
            Thread.sleep(250);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (cell.getCellType() == Cell.type.SHIP) {
            cell.setCellType(Cell.type.DEFEATED_SHIP);
            cell.redraw();
            if (isLast(cell)) {
                redrawArea(cell, -1, -1);
                if (isGameOver()) {
                    System.out.println("Game is over!!!");
                    Game.openFields();
                }
            }
        }
        else {
            cell.setCellType(Cell.type.DEFEATED_SEA);
            cell.redraw();
            Game.changeTurn();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}

class Game implements ActionListener {
    final JFrame gameFrame = new JFrame("Sea battle");
    final JPanel panelCenter = new JPanel(new BorderLayout());
    final JButton generate1 = new JButton();
    final JButton generate2 = new JButton();
    static private Field leftField;
    static private Field rightField;
    static boolean isFirstPlayerTurn = true;
    Game() {
        start();
        rightField.setFieldEnabled(false);
        gameFrame.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        generate1.addActionListener(this);
    }
    static void changeTurn() {
        leftField.setFieldEnabled(!leftField.isEnabled());
        rightField.setFieldEnabled(!rightField.isEnabled());
    }
    static void openFields() {
        leftField.openField();
        rightField.openField();
    }
    private void restart() {
        if (leftField != null) leftField.removeAll();
        if (rightField != null) rightField.removeAll();
        panelCenter.removeAll();

        leftField = new Field(300, 300);
        JPanel middle = new JPanel(new BorderLayout());
        middle.setPreferredSize(new Dimension(50, 300));
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon("imageSrc//turnArrow.jpeg"));
        middle.add(label);
        rightField = new Field(300, 300);

        leftField.setBackground(Color.orange);
        rightField.setBackground(Color.orange);

        panelCenter.add(leftField, BorderLayout.WEST);
        panelCenter.add(middle, BorderLayout.CENTER);
        panelCenter.add(rightField, BorderLayout.EAST);

        leftField.generateShips();
        rightField.generateShips();

        gameFrame.pack();
        gameFrame.setVisible(true);
    }
    private void start() {
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLayout(new BorderLayout());
        gameFrame.getContentPane().setBackground(Color.orange);
        gameFrame.setResizable(false);

        JPanel panelTop = new JPanel();
        JPanel panelLeft = new JPanel();
        JPanel panelRight = new JPanel();
        JPanel panelBottom = new JPanel();

        generate1.setPreferredSize(new Dimension(50, 50));
        generate1.setIcon(new ImageIcon("imageSrc//generate1.jpeg"));
        generate2.setPreferredSize(new Dimension(50, 50));
        generate2.setIcon(new ImageIcon("imageSrc//generate1.jpeg"));

        panelTop.setPreferredSize(new Dimension(50, 75));
        panelLeft.setPreferredSize(new Dimension(100, 50));
        panelCenter.setPreferredSize(new Dimension(650, 300));
        panelRight.setPreferredSize(new Dimension(100, 50));
        panelBottom.setPreferredSize(new Dimension(50, 75));

        panelTop.setBackground(Color.black);
        panelLeft.setBackground(Color.black);
        panelRight.setBackground(Color.black);
        panelBottom.setBackground(Color.black);
        panelLeft.add(generate1, BorderLayout.CENTER);
        panelRight.add(generate2, BorderLayout.CENTER);

        gameFrame.add(panelTop, BorderLayout.NORTH);
        gameFrame.add(panelLeft, BorderLayout.WEST);
        gameFrame.add(panelCenter, BorderLayout.CENTER);
        gameFrame.add(panelRight, BorderLayout.EAST);
        gameFrame.add(panelBottom, BorderLayout.SOUTH);

        restart();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generate1) {
            restart();
        }
    }
}

public class SeaBattle {
    public static void main(String[] args) {
        new Game();
    }
}