import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Window extends JFrame {
    private static final long serialVersionUID = 1L;
    boolean gameShouldStart = false;
    private SettingsWindow sw = new SettingsWindow();
    private JPanel contentPane;
    private GameWindow gameWindow;
    private PrintStream writer;
    private String username;

    public Window(Socket socket, boolean isServer) {
        try {
            this.writer = new PrintStream(socket.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }

        setTitle("PIXEL RUNNER GAME");
        setBackground(Color.ORANGE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 916, 534);
        setResizable(false);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(249, 240, 107));
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLocationRelativeTo(null);

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBackground(Color.ORANGE);
        panel.setBounds(26, 92, 858, 397);
        contentPane.add(panel);
        panel.setLayout(null);

        JButton startButton = new JButton("");
        startButton.setBackground(Color.ORANGE);
        startButton.setIcon(new ImageIcon("/home/alvrosgui/Escritorio/1º DAW/Programming/proyect_decoration/startButton.png"));
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writer.println("Play");
                username = sw.getUsername(); // Get the username from the settings window
                gameWindow = new GameWindow(socket, isServer, username);
                gameWindow.setVisible(true);
                gameWindow.startGame();
                setVisible(false); // Hide the main menu
            }
        });
        startButton.setBounds(32, 45, 135, 79);
        panel.add(startButton);

        JButton settingsButton = new JButton("");
        settingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sw.setVisible(true);
            }
        });
        settingsButton.setBackground(Color.ORANGE);
        settingsButton.setIcon(new ImageIcon("/home/alvrosgui/Escritorio/1º DAW/Programming/proyect_decoration/settingsButton.png"));
        settingsButton.setBounds(32, 171, 135, 79);
        panel.add(settingsButton);

        JButton quitButton = new JButton("");
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        quitButton.setBackground(Color.ORANGE);
        quitButton.setIcon(new ImageIcon("/home/alvrosgui/Escritorio/1º DAW/Programming/proyect_decoration/quitButton.png"));
        quitButton.setBounds(32, 289, 135, 79);
        panel.add(quitButton);

        JLabel labelCharacter = new JLabel("");
        labelCharacter.setHorizontalAlignment(SwingConstants.CENTER);
        labelCharacter.setBackground(Color.WHITE);
        labelCharacter.setBounds(674, 232, 186, 136);
        labelCharacter.setIcon(new ImageIcon("/home/alvrosgui/Escritorio/1º DAW/Programming/proyect_decoration/character_inicio.png"));
        panel.add(labelCharacter);

        JLabel labelCharacter2 = new JLabel("");
        labelCharacter2.setHorizontalAlignment(SwingConstants.CENTER);
        labelCharacter2.setBounds(185, 45, 186, 205);
        labelCharacter2.setIcon(new ImageIcon("/home/alvrosgui/Escritorio/1º DAW/Programming/proyect_decoration/character_inicio2.png"));
        panel.add(labelCharacter2);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.BLACK);
        panel_1.setBounds(126, 12, 633, 68);
        contentPane.add(panel_1);
        panel_1.setLayout(null);

        JLabel title = new JLabel("PIXEL RUNNER");
        title.setBounds(248, 22, 163, 23);
        title.setForeground(Color.ORANGE);
        title.setBackground(new Color(0, 0, 0));
        panel_1.add(title);
        title.setFont(new Font("Monocraft Nerd Font", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
    }

   
    public GameWindow returnGameWindow() {
        return gameWindow;
    }
}