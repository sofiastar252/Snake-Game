import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SnakeGame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final int GRID_SIZE = 20;
    private static final int CELL_SIZE = 20;

    private int[][] grid;
    private int snakeLength;
    private Point[] snake;
    private Point fruit;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Direction currentDirection;

    public SnakeGame() {
        grid = new int[GRID_SIZE][GRID_SIZE];
        snakeLength = 3;
        snake = new Point[GRID_SIZE * GRID_SIZE];
        currentDirection = Direction.RIGHT;

        for (int i = 0; i < snakeLength; i++) {
            snake[i] = new Point(i, 0);
        }

        spawnFruit();

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (currentDirection != Direction.DOWN) {
                            currentDirection = Direction.UP;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (currentDirection != Direction.UP) {
                            currentDirection = Direction.DOWN;
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (currentDirection != Direction.RIGHT) {
                            currentDirection = Direction.LEFT;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (currentDirection != Direction.LEFT) {
                            currentDirection = Direction.RIGHT;
                        }
                        break;
                }
            }
        });

        setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        setLocationRelativeTo(null);

        add(new GamePanel());
    }

    private void spawnFruit() {
        int x, y;
        do {
            x = (int) (Math.random() * GRID_SIZE);
            y = (int) (Math.random() * GRID_SIZE);
        } while (grid[x][y] != 0);

        fruit = new Point(x, y);
        grid[x][y] = -1; // Fruit marker
    }

    private void move() {
        Point head = snake[0];
        Point newHead;

        switch (currentDirection) {
            case UP:
                newHead = new Point(head.x, (head.y - 1 + GRID_SIZE) % GRID_SIZE);
                break;
            case DOWN:
                newHead = new Point(head.x, (head.y + 1) % GRID_SIZE);
                break;
            case LEFT:
                newHead = new Point((head.x - 1 + GRID_SIZE) % GRID_SIZE, head.y);
                break;
            case RIGHT:
                newHead = new Point((head.x + 1) % GRID_SIZE, head.y);
                break;
            default:
                return;
        }

        if (grid[newHead.x][newHead.y] == -1) { // Eat fruit
            snakeLength++;
            spawnFruit();
        } else if (grid[newHead.x][newHead.y] > 0) { // Hit itself
            gameOver();
            return;
        }

        // Move the snake
        for (int i = snakeLength - 1; i > 0; i--) {
            snake[i] = snake[i - 1];
        }
        snake[0] = newHead;

        // Update the grid
        for (int i = 0; i < GRID_SIZE; i++) {
            Arrays.fill(grid[i], 0);
        }
        for (int i = 0; i < snakeLength; i++) {
            grid[snake[i].x][snake[i].y] = i + 1;
        }
        grid[fruit.x][fruit.y] = -1;
    }

    private void gameOver() {
        System.out.println("Game Over!");
        System.exit(0);
    }

    private class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.clearRect(0, 0, getWidth(), getHeight());

            // Draw the snake
            for (int i = 0; i < snakeLength; i++) {
                g.setColor(Color.GREEN);
                g.fillRect(snake[i].x * CELL_SIZE, snake[i].y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(snake[i].x * CELL_SIZE, snake[i].y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }

            // Draw the fruit
            g.setColor(Color.RED);
            g.fillOval(fruit.x * CELL_SIZE, fruit.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

            // Draw grid lines
            g.setColor(Color.BLACK);
            for (int i = 0; i <= GRID_SIZE; i++) {
                g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_SIZE * CELL_SIZE);
                g.drawLine(0, i * CELL_SIZE, GRID_SIZE * CELL_SIZE, i * CELL_SIZE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnakeGame snakeGame = new SnakeGame();
            snakeGame.setVisible(true);

            javax.swing.Timer timer = new javax.swing.Timer(150, e -> {
                snakeGame.move();
                snakeGame.repaint();
            });
            timer.start();
        });
    }
}
