import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Main extends JFrame {

    GamePanel gamePanel;

    public Main() {
        setSize(1360, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel = new GamePanel();
        add(gamePanel);

        setVisible(true);
    }

    class GamePanel extends JPanel {
        int[] values = new int[80];
        Image picture;
        Image background;
        Image picture2;

        int X = 0;
        int Y = -100;
        Timer moveTimer;

        Tetromino currentTetromino;
        Random random = new Random();

        int COLS = 10;
        int ROWS = 8;
        int SIZE = 80;
        int GRID_START_X = 180;
        int GRID_START_Y = 100;
        int SPACING = 5;
        int ground = 700;

        int fallSpeed = 5;
        int normalSpeed = 5;
        int fastSpeed = 10;

        public GamePanel() {
            picture = Toolkit.getDefaultToolkit().createImage("C:/Users/turchaninavp.27/Downloads/Ресурс 1.png");
            picture2 = Toolkit.getDefaultToolkit().createImage("C:/Users/turchaninavp.27/Downloads/IMG_1255.JPG");
            background = Toolkit.getDefaultToolkit().createImage("/Users/varvaraturcanina/Downloads/image.jpg");

            for (int i = 0; i < values.length; i++) {
                values[i] = 0;
            }

            currentTetromino = createRandomTetromino();
            currentTetromino.setPosition(X, Y);

            moveTimer = new Timer(50, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int stoppingY = getStoppingYForCurrentPosition();

                    if (Y < stoppingY) {
                        Y += fallSpeed;
                        currentTetromino.setPosition(X, Y);
                    } else {
                        fixTetrominoToGrid();

                        Y = -100;
                        X = 0;
                        fallSpeed = normalSpeed;
                        currentTetromino = createRandomTetromino();
                        currentTetromino.setPosition(X, Y);
                    }
                    repaint();
                }
            });
            moveTimer.start();

            setFocusable(true);
            requestFocus();
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int keyCode = e.getKeyCode();

                    int oldX = X;

                    if (keyCode == KeyEvent.VK_LEFT && X - 75 > 100) {
                        X -= 75;
                        currentTetromino.setPosition(X, Y);
                    }

                    if (keyCode == KeyEvent.VK_RIGHT && X + 75 < 570) {
                        X += 75;
                        currentTetromino.setPosition(X, Y);
                    }

                    if (keyCode == KeyEvent.VK_DOWN) {
                        fallSpeed = fastSpeed;
                    }

                    if (keyCode == KeyEvent.VK_SPACE) {
                        currentTetromino.rotate();

                        if (!isValidPosition()) {
                            X -= 75;
                            currentTetromino.setPosition(X, Y);
                            if (!isValidPosition()) {
                                X += 75;
                                currentTetromino.rotateBack();
                                currentTetromino.setPosition(X, Y);
                            }
                        }
                    }

                    repaint();
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    int keyCode = e.getKeyCode();

                    if (keyCode == KeyEvent.VK_DOWN) {
                        fallSpeed = normalSpeed;
                    }
                }
            });
        }

        private boolean isValidPosition() {
            for (int row = 0; row < currentTetromino.


                    SIZE; row++) {
                for (int col = 0; col < currentTetromino.SIZE; col++) {
                    if (currentTetromino.getCell(row, col) == 1) {
                        int blockX = GRID_START_X + X + col * (SIZE - SPACING);
                        int blockY = GRID_START_Y + Y + row * (SIZE - SPACING);

                        if (blockX < GRID_START_X) {
                            return false;
                        }

                        if (blockX + SIZE > GRID_START_X + COLS * (SIZE - SPACING)) {
                            return false;
                        }

                        if (blockY + SIZE > ground) {
                            return false;
                        }

                        for (int gridRow = 0; gridRow < ROWS; gridRow++) {
                            for (int gridCol = 0; gridCol < COLS; gridCol++) {
                                int cellX = GRID_START_X + gridCol * (SIZE - SPACING);
                                int cellY = GRID_START_Y + gridRow * (SIZE - SPACING);

                                int blockCenterX = blockX + SIZE/2;
                                int blockCenterY = blockY + SIZE/2;

                                if (blockCenterX >= cellX && blockCenterX <= cellX + SIZE &&
                                        blockCenterY >= cellY && blockCenterY <= cellY + SIZE) {
                                    int index = gridRow * COLS + gridCol;
                                    if (values[index] == 1) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }

        private Tetromino createRandomTetromino() {
            int type = random.nextInt(6);

            switch(type) {
                case 0: return new TetrominoI();
                case 1: return new TetrominoO();
                case 2: return new TetrominoT();
                case 3: return new TetrominoL();
                case 4: return new TetrominoJ();
                case 5: return new TetrominoS();
                default: return new TetrominoI();
            }
        }

        private int getStoppingYForCurrentPosition() {
            int minStoppingY = ground - GRID_START_Y;

            for (int row = 0; row < currentTetromino.SIZE; row++) {
                for (int col = 0; col < currentTetromino.SIZE; col++) {
                    if (currentTetromino.getCell(row, col) == 1) {
                        int blockX = GRID_START_X + X + col * (SIZE - SPACING);

                        int gridCol = -1;
                        for (int c = 0; c < COLS; c++) {
                            int cellX = GRID_START_X + c * (SIZE - SPACING);
                            if (blockX + SIZE/2 >= cellX && blockX + SIZE/2 <= cellX + SIZE) {
                                gridCol = c;
                                break;
                            }
                        }

                        if (gridCol == -1) continue;

                        int highestBlockedY = -1;
                        for (int r = 0; r < ROWS; r++) {
                            int index = r * COLS + gridCol;
                            if (values[index] == 1) {
                                int cellY = GRID_START_Y + r * (SIZE - SPACING);
                                if (cellY > Y + row * (SIZE - SPACING)) {
                                    if (highestBlockedY == -1 || cellY < highestBlockedY) {
                                        highestBlockedY = cellY;
                                    }
                                }
                            }
                        }

                        int blockStopY;
                        if (highestBlockedY != -1) {
                            blockStopY = highestBlockedY - SIZE - (row * (SIZE - SPACING));
                        } else {
                            blockStopY = ground - SIZE - (row * (SIZE - SPACING));


                        }

                        int relativeStopY = blockStopY - GRID_START_Y;

                        if (relativeStopY < minStoppingY) {
                            minStoppingY = relativeStopY;
                        }
                    }
                }
            }

            return minStoppingY;
        }

        private void fixTetrominoToGrid() {
            for (int row = 0; row < currentTetromino.SIZE; row++) {
                for (int col = 0; col < currentTetromino.SIZE; col++) {
                    if (currentTetromino.getCell(row, col) == 1) {
                        int blockX = GRID_START_X + X + col * (SIZE - SPACING);
                        int blockY = GRID_START_Y + Y + row * (SIZE - SPACING);

                        for (int gridRow = 0; gridRow < ROWS; gridRow++) {
                            for (int gridCol = 0; gridCol < COLS; gridCol++) {
                                int cellX = GRID_START_X + gridCol * (SIZE - SPACING);
                                int cellY = GRID_START_Y + gridRow * (SIZE - SPACING);

                                if (Math.abs(blockX - cellX) < SIZE/2 && Math.abs(blockY - cellY) < SIZE/2) {
                                    int index = gridRow * COLS + gridCol;
                                    if (index >= 0 && index < values.length && values[index] == 0) {
                                        values[index] = 1;
                                        checkFullRows(gridRow);
                                        checkFullColumns(gridCol);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        private void checkFullRows(int rowToCheck) {
            boolean fullRow = true;

            for (int col = 0; col < COLS; col++) {
                int index = rowToCheck * COLS + col;
                if (values[index] == 0) {
                    fullRow = false;
                    break;
                }
            }

            if (fullRow) {
                for (int col = 0; col < COLS; col++) {
                    int index = rowToCheck * COLS + col;
                    values[index] = 0;
                }

                dropAllPicturesAboveRow(rowToCheck);
            }
        }

        private void dropAllPicturesAboveRow(int deletedRow) {
            for (int row = deletedRow - 1; row >= 0; row--) {
                for (int col = 0; col < COLS; col++) {
                    int currentIndex = row * COLS + col;
                    if (values[currentIndex] == 1) {
                        int dropDistance = 0;
                        for (int checkRow = row + 1; checkRow < ROWS; checkRow++) {
                            int checkIndex = checkRow * COLS + col;
                            if (values[checkIndex] == 0) {
                                dropDistance++;
                            } else {
                                break;
                            }
                        }
                        if (dropDistance > 0) {
                            values[currentIndex] = 0;
                            values[(row + dropDistance) * COLS + col] = 1;
                        }
                    }
                }
            }
        }

        private void checkFullColumns(int colToCheck) {
            boolean fullColumn = true;

            for (int row = 0; row < ROWS; row++) {
                int index = row * COLS + colToCheck;
                if (values[index] == 0) {
                    fullColumn = false;
                    break;
                }
            }

            if (fullColumn) {
                for (int row = 0; row < ROWS; row++) {
                    int index = row * COLS + colToCheck;
                    values[index] = 0;
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {

            if (background != null) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }

            int index = 0;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    int x = GRID_START_X + col * (SIZE - SPACING);
                    int y = GRID_START_Y + row * (SIZE - SPACING);

                    if (values[index] == 0) {
                        if (picture != null) {
                            g.drawImage(picture, x, y, SIZE, SIZE, this);
                        }
                    } else {
                        if (picture2 != null) {
                            g.drawImage(picture2, x, y, SIZE, SIZE, this);
                        }
                    }
                    index++;
                }
            }

            if (picture2 != null) {
                for (int row = 0; row < currentTetromino.SIZE; row++) {
                    for (int col = 0; col < currentTetromino.SIZE; col++) {
                        if (currentTetromino.getCell(row, col) == 1) {
                            int drawX = GRID_START_X + X + col * (SIZE - SPACING);
                            int drawY = GRID_START_Y + Y + row * (SIZE - SPACING);
                            g.drawImage(picture2, drawX, drawY, SIZE, SIZE, this);
                        }
                    }
                }
            }
        }
    }

    abstract class Tetromino {
        public static final int SIZE = 3;
        protected int[][] shape;
        protected int x, y;
        protected int[][] savedShape;

        public int getCell(int row, int col) {
            if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
                return shape[row][col];
            }
            return 0;
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }


        public void rotate() {
            savedShape = new int[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    savedShape[i][j] = shape[i][j];
                }
            }

            int[][] rotated = new int[SIZE][SIZE];

            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    rotated[j][SIZE - 1 - i] = shape[i][j];
                }
            }

            shape = rotated;
        }


        public void rotateBack() {
            if (savedShape != null) {
                shape = savedShape;
            }
        }
    }

    class TetrominoI extends Tetromino {
        public TetrominoI() {
            shape = new int[SIZE][SIZE];
            for (int col = 0; col < SIZE; col++) {
                shape[1][col] = 1;
            }
        }
    }

    class TetrominoO extends Tetromino {
        public TetrominoO() {
            shape = new int[SIZE][SIZE];
            shape[0][0] = 1;
            shape[0][1] = 1;
            shape[1][0] = 1;
            shape[1][1] = 1;
        }
    }

    class TetrominoT extends Tetromino {
        public TetrominoT() {
            shape = new int[SIZE][SIZE];
            shape[0][1] = 1;
            shape[1][0] = 1;
            shape[1][1] = 1;
            shape[1][2] = 1;
        }
    }

    class TetrominoL extends Tetromino {
        public TetrominoL() {
            shape = new int[SIZE][SIZE];
            shape[0][2] = 1;
            shape[1][2] = 1;
            shape[2][2] = 1;
            shape[2][1] = 1;
        }
    }

    class TetrominoJ extends Tetromino {
        public TetrominoJ() {
            shape = new int[SIZE][SIZE];
            shape[0][0] =


                    1;
            shape[1][0] = 1;
            shape[2][0] = 1;
            shape[2][1] = 1;
        }
    }

    class TetrominoS extends Tetromino {
        public TetrominoS() {
            shape = new int[SIZE][SIZE];
            shape[0][1] = 1;
            shape[0][2] = 1;
            shape[1][0] = 1;
            shape[1][1] = 1;
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}


