package com.bllk.Apka;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Apka extends JFrame implements ActionListener {
    JPanel panel;
    JLabel user_label, password_label, message;
    JTextField userName_text;
    JPasswordField password_text;
    JButton submit;
    DatabaseConnection connection;

    Apka() {
        connection = new DatabaseConnection();

        user_label = new JLabel();
        user_label.setText("User Name :");
        userName_text = new JTextField();

        password_label = new JLabel();
        password_label.setText("Password :");
        password_text = new JPasswordField();

        submit = new JButton("Login");

        panel = new JPanel(new GridLayout(3, 1));

        panel.add(user_label);
        panel.add(userName_text);
        panel.add(password_label);
        panel.add(password_text);

        message = new JLabel();
        panel.add(message);
        panel.add(submit);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        submit.addActionListener(this);
        add(panel, BorderLayout.CENTER);
        setTitle("BLLK");
        setSize(400, 200);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Apka();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String login = userName_text.getText();
        String password = password_text.getText();
        try {
            Logins current_login = connection.check_login(login, password);
            message.setText("Hello");
            new Account(connection, current_login.getAccountid());
        }
        catch (Exception ex) {
            message.setText(" Invalid user.. ");
        }
    }
}