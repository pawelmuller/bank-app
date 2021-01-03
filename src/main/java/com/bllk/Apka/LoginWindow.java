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

    public void performLogin() {
        String login = loginField.getText();
        String password = String.valueOf(passwordField.getPassword());
        String salt = null;
        Client client = null;
        Login log = null;

        if (login.isEmpty() || password.isEmpty()) {
            message.setText("No login or password given.");
            return;
        }

        try {
            message.setText("Checking...");
            String password_salt = connection.getSalt(login);
            String hashed_password = BCrypt.hashpw(password, password_salt);

            client = connection.getClient(login, hashed_password);
            log = new Login(client.getID(), login, hashed_password);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            message.setText("Invalid user...");
        }

        frame.setContentPane(new MainUserPage(frame, startingPanel, connection, client, log).menuPanel);
        loginField.setText("");
        passwordField.setText("");
        message.setText("");
    }

    public LoginWindow() {
        loginButton.addActionListener(e -> performLogin());

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        loginField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
    }
    public void pullDataFromServer(String login, String password) {

    }
}
