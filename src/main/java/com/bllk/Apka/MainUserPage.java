package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;

import javax.swing.*;

public class MainUserPage {
    ClientServerConnection connection;
    Client client;
    Login login;
    double your_money_value;

    JFrame frame;
    JPanel previousPanel, menuPanel;
    private JLabel logoLabel, nameLabel;
    private JTextField accountNumber, amount;
    private JButton sendMoneyButton, logOutButton;
    private JLabel message;
    private JLabel currentBalance;
    private JLabel idLabel;
    private JPanel contentPanel;
    private JTabbedPane tabbedPane1;
    private JPanel historyPanel;

    public MainUserPage(JFrame _frame, JPanel _previousPanel, ClientServerConnection _connection, Client _client, Login _login) {
        frame = _frame;
        previousPanel = _previousPanel;
        connection = _connection;
        client = _client;
        login = _login;
//        your_money_value = connection.get_money(login.getLogin(), login.getPasswordHash());
        nameLabel.setText("Witaj " + client.getName() + " " + client.getSurname() + "!");
        idLabel.setText("Numer klienta: " + client.getID());
//        UpdateMoney();

        sendMoneyButton.addActionListener(e -> {
            try {
                int target_id = Integer.parseInt(accountNumber.getText());
                double money_value = Double.parseDouble(amount.getText());
                if (target_id == client.getID()) {
                    message.setText("Transaction failed: You can't send money to yourself.");
                }
                else if (money_value > your_money_value || money_value <= 0) {
                    message.setText("Transaction failed: Invalid amount of money.");
                }
                else if (!connection.check_client(Integer.parseInt(accountNumber.getText()))) {
                    message.setText("Transaction failed: Account don't exists.");
                }
                else {
                    message.setText("Sending " + money_value + " PLN to Account " + target_id);
                    connection.makeTransfer(login.getLogin(), login.getPasswordHash(), target_id, money_value);
                    UpdateMoney();
                }
            }
            catch (Exception ex) {
                message.setText("Transaction failed: " + ex.getMessage());
            }
        });
        logOutButton.addActionListener(e -> frame.setContentPane(previousPanel));
    }

    void UpdateMoney() {
        your_money_value = connection.get_money(login.getLogin(), login.getPasswordHash());
        currentBalance.setText(your_money_value + " PLN");
    }
}
