import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsWindow extends JFrame {
    JTextField usernameField;
    private JButton useButton;
    private String username;

    public SettingsWindow() {
        setTitle("Settings");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(20, 30, 80, 25);
        add(usernameLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 30, 160, 25);
        add(usernameField);

        useButton = new JButton("USE");
        useButton.setBounds(100, 80, 80, 25);
        useButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Username cannot be empty!");
                } else {
                    JOptionPane.showMessageDialog(null, "Username saved!");
                    dispose();
                }
            }
        });
        add(useButton);
    }

    public String getUsername() {
        return username;
    }
}
