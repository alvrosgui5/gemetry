import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWindow extends JFrame implements KeyListener {
    private Player player, player1;
    private List<Block> blocks;
    private int score;
    private boolean gameOver;
    private Timer timer;
    private int blockCount;
    private List<Color> backgroundColors;
    private int currentColorIndex;
    private boolean isServer;
    private Socket socket;
    private PrintStream writer;
    private GamePanel gamePanel;
    private String username;

    public GameWindow(Socket socket, boolean isServer, String username) {
        this.socket = socket;
        this.username = username;
        this.isServer = isServer;

        setupNetworking();
        setupUI();

        player = new Player(50, 880, "blue");
        player1 = new Player(150, 880, "green");

        if (!isServer) {
            Player aux = player;
            player = player1;
            player1 = aux;
        }

        blocks = new ArrayList<>();
        score = 0;
        gameOver = false;
        blockCount = 1000;
        currentColorIndex = 0;
        setupBackgroundColors();

        generateBlocks();
        startGame();
    }

    private void setupNetworking() {
        try {
            this.writer = new PrintStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Start a separate thread for handling incoming messages
            new Thread(() -> {
                try {
                    String request;
                    while ((request = reader.readLine()) != null) {
                        if (request.equals("JUMP!")) {
                            SwingUtilities.invokeLater(() -> player1.jump());
                        } else if (request.equals("DOWN!")) {
                            SwingUtilities.invokeLater(() -> player1.down());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupUI() {
        setTitle("Pixel Runner Game" + isServer);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel = new GamePanel();
        add(gamePanel);

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    private void setupBackgroundColors() {
        backgroundColors = new ArrayList<>();
        backgroundColors.add(Color.WHITE);
        backgroundColors.add(Color.BLACK);
        backgroundColors.add(Color.YELLOW);
        backgroundColors.add(Color.GREEN);
    }

    private void generateBlocks() {
        blocks.clear();
        Random random = new Random();
        for (int i = 0; i < blockCount; i++) {
            int x = 800 + i * Block.getBLOCK_GAP();
            int y = 880;
            int speed = 5;
            blocks.add(new Block(x, y, Color.RED, speed));
        }
    }

    public void startGame() {
        timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    player.update(false);
                    for (Block block : blocks) {
                        block.update();
                        if (block.getX() + block.getWidth() < 0) {
                            block.setX(block.getX() + Block.getBLOCK_GAP() * blocks.size());
                        }
                        if (player.getBounds().intersects(block.getBounds())) {
                            gameOver = true;
                        }
                        if (player.getX() > block.getX() && !block.isScored()) {
                            score++;
                            block.setScored(true);
                            checkScoreForColorChange();
                        }
                    }
                } else {
                    timer.stop();
                    showGameOverScreen();
                }
                repaint();
                Toolkit.getDefaultToolkit().sync();
            }
        });
        timer.start();
    }

    private void checkScoreForColorChange() {
        if (score % 10 == 0 && score / 10 <= backgroundColors.size()) {
            currentColorIndex = score / 10 - 1;
            gamePanel.setBackground(backgroundColors.get(currentColorIndex));
            increaseBlockSpeed();
        }
    }

    private void increaseBlockSpeed() {
        for (Block block : blocks) {
            block.setSpeed((int) (block.getSpeed() * 1.2));
        }
    }

    private void showGameOverScreen() {
        saveScoreToDatabase(username, score);

        JPanel gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new BoxLayout(gameOverPanel, BoxLayout.Y_AXIS));
        JLabel gameOverLabel = new JLabel("Game Over");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 50));
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton retryButton = new JButton("Retry");
        retryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                GameWindow newGameWindow = new GameWindow(socket, isServer, username);
                newGameWindow.setVisible(true);
                newGameWindow.startGame();
            }
        });

        JButton menuButton = new JButton("Menu");
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Window menuWindow = new Window(socket, isServer);
                menuWindow.setVisible(true);
            }
        });

        JLabel scoreLabel = new JLabel("You scored " + score + " points");
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setForeground(Color.MAGENTA);

        gameOverPanel.add(Box.createVerticalGlue());
        gameOverPanel.add(gameOverLabel);
        gameOverPanel.add(Box.createVerticalStrut(20));
        gameOverPanel.add(retryButton);
        gameOverPanel.add(Box.createVerticalStrut(10));
        gameOverPanel.add(menuButton);
        gameOverPanel.add(Box.createVerticalStrut(10));
        gameOverPanel.add(scoreLabel);
        gameOverPanel.add(Box.createVerticalGlue());

        setContentPane(gameOverPanel);
        revalidate();
        repaint();
        Toolkit.getDefaultToolkit().sync();
    }

    private void saveScoreToDatabase(String username, int score) {
        String jdbcUrl = "jdbc:mysql://db1.cflywlvymjfl.us-east-1.rds.amazonaws.com:3306/game_scores";
        String dbUser = "admin";
        String dbPassword = "alumnoalumno";

        String insertSQL = "INSERT INTO scores (username, score) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, score);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            player.draw(g);
            player1.draw(g);
            for (Block block : blocks) {
                block.draw(g);
            }

            Color backgroundColor = getBackground();
            Color textColor = (backgroundColor.getRed() + backgroundColor.getGreen() + backgroundColor.getBlue()) < 384 ? Color.WHITE : Color.BLACK;
            g.setColor(textColor);
            g.drawString("Score: " + score, 10, 20);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            player.jump();
            writer.println("JUMP!");
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            player.down();
            writer.println("DOWN!");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No action needed on key release for the current game logic
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No action needed on key typed for the current game logic
    }
}
