package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;

import javax.swing.*;
import java.awt.*;import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginWindow {
    private static JFrame frame;
    private static ClientServerConnection connection;

    static JPanel startingPanel;
    private JPanel mainPanel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton, forgotPasswordButton;
    private JLabel message;
    private JPanel loginTabPanel;
    private JTabbedPane mainTabbedPane;
    private JLabel logoLabel;
    private JPanel headerPanel;
    private JPanel registerTabPanel;

    public static void main(String[] args) {
        frame = new JFrame("BLLK");
        startingPanel = new LoginWindow().mainPanel;
        connection = new ClientServerConnection();
        frame.setContentPane(startingPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(640, 480));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void Submit() {
        String login = loginField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (login.isEmpty() || password.isEmpty()) {
            message.setText("No login or password given.");
            return;
        }
        try {
            message.setText("Checking...");
            String password_salt = BCrypt.gensalt(12);
            String hashed_password = BCrypt.hashpw(password, password_salt);
            System.out.println(hashed_password);

            connection.create_client("Bilbo", "Baggins", "2000-01-12", "bilbo", "shire");

            Client client = connection.get_login(login, hashed_password);
            Login log = new Login(client.getID(), login, hashed_password);

            frame.setContentPane(new MainUserPage(frame, startingPanel, connection, client, log).menuPanel);
            loginField.setText("");
            passwordField.setText("");
            message.setText("");
        }
        catch (Exception ex) {
            message.setText("Invalid user...");
        }
    }

    public LoginWindow() {
        loginButton.addActionListener(e -> Submit());

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Submit();
                }
            }
        });
        loginField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Submit();
                }
            }
        });
    }
    public void pullDataFromServer(String login, String password) {

    }
}
