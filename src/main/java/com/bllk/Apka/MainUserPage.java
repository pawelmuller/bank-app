package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Account;
import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Currency;
import com.bllk.Servlet.mapclasses.Login;

import javax.swing.*;
import java.util.Map;

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
    private JComboBox<String> currencySelect;
    private JTextField title;
    private JComboBox<String> accountSelect;
    private JLabel payerBalance;

    Currency active_currency;
    Account active_payer_account = null;
    Map <String, Integer> currencies;

    public MainUserPage(JFrame _frame, JPanel _previousPanel, ClientServerConnection _connection, Client _client, Login _login) {
        frame = _frame;
        previousPanel = _previousPanel;
        connection = _connection;
        client = _client;
        login = _login;
        nameLabel.setText("Witaj " + client.getName() + " " + client.getSurname() + "!");
        idLabel.setText("Numer klienta: " + client.getID());

        updateAccounts();
        updateCurrencies();

        sendMoneyButton.addActionListener(e -> {
            try {
                int target_id = Integer.parseInt(accountNumber.getText());
                double money_value = Double.parseDouble(amount.getText());
                if (active_payer_account.getID() == target_id) {
                    message.setText("Transaction failed: You can't send money to yourself.");
                }
                else if (money_value*100 > active_payer_account.getValue() || money_value <= 0) {
                    message.setText("Transaction failed: Invalid amount of money.");
                }
                else if (!connection.checkAccount(Integer.parseInt(accountNumber.getText()))) {
                    message.setText("Transaction failed: Account don't exists.");
                }
                else if (active_payer_account == null) {
                    message.setText("Transaction failed: Account don't selected.");
                }
                else {
                    System.out.println(currencySelect.getSelectedItem());
                    message.setText("Sending " + money_value + " PLN to Account " + target_id);
                    connection.makeTransfer(login.getLogin(), login.getPasswordHash(), active_payer_account.getID(), target_id, money_value, 0);
                    updateMoney();
                }
            }
            catch (Exception ex) {
                message.setText("Transaction failed: " + ex.getMessage());
            }
        });
        logOutButton.addActionListener(e -> frame.setContentPane(previousPanel));
        currencySelect.addActionListener(e -> {});
        accountSelect.addActionListener(e -> updateMoney());
    }
    void updateMoney() {
        if (accountSelect.getItemCount()>=0) {
            active_payer_account = connection.getAccount(login.getLogin(), login.getPasswordHash(), Integer.parseInt((String) accountSelect.getSelectedItem()));
            payerBalance.setText(String.format("%.2f", active_payer_account.getValue() / 100.0));
        }
    }
    void updateAccounts() {
        for (Map.Entry<String,Integer> entry : connection.getUserAccounts(login.getLogin(), login.getPasswordHash()).entrySet())
            accountSelect.addItem(entry.getKey());
        updateMoney();
    }
    void updateCurrencies() {
        currencies = connection.getCurrencies();
        for (Map.Entry<String,Integer> entry : currencies.entrySet())
            currencySelect.addItem(entry.getKey());
    }
}
