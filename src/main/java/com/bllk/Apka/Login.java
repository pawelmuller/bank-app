package com.bllk.Apka;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    private static JFrame frame;
    private final DatabaseConnection connection;

    static JPanel loginPanel;
    JPanel mainPanel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton, forgotPasswordButton;
    private JLabel message;

    public static void main(String[] args) {
        frame = new JFrame("BLLK");
        loginPanel = new Login().mainPanel;
        frame.setContentPane(loginPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(400, 320));
    }

    public Login() {
        connection = new DatabaseConnection();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String password = passwordField.getText();
                if (login.isEmpty() || password.isEmpty()) {
                    message.setText("No login or password given.");
                    return;
                }
                try {
                    message.setText("Checking...");
                    Logins current_login = connection.get_login(login, password);
                    frame.setContentPane(new MainUserPage(frame, loginPanel, connection, current_login.getAccountid()).mainPanel);
                    loginField.setText("");
                    passwordField.setText("");
                    message.setText(" ");
                }
                catch (Exception ex) {
                    message.setText("Invalid user...");
                }
            }
        });
    }
}
