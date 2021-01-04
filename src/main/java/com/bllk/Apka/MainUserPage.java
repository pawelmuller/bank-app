package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Account;
import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainUserPage {
    ClientServerConnection connection;
    Client client;
    Login login;

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
    private JPanel historyPanel2;
    private JTextField titleTextField;
    private JComboBox<String> accountSelect;
    private JLabel payerBalance;
    private JLabel currencyLabel;
    private JScrollPane historyPanel;

    Account active_payer_account = null;
    Map <String, String> currencies;

    public MainUserPage(JFrame _frame, JPanel _previousPanel, ClientServerConnection _connection, Client _client, Login _login) {
        frame = _frame;
        previousPanel = _previousPanel;
        connection = _connection;
        client = _client;
        login = _login;
        nameLabel.setText("Witaj " + client.getName() + " " + client.getSurname() + "!");
        idLabel.setText("Numer klienta: " + client.getID());
        currencies = connection.getCurrencies();

        updateAccounts();
        updateTransactionTable();
        sendMoneyButton.addActionListener(e -> {
            try {
                if (active_payer_account == null) {
                    message.setText("Transaction failed: You don't have any account.");
                }
                else {
                    int payer_id = active_payer_account.getID();
                    int target_id = Integer.parseInt(accountNumber.getText());
                    int currency_id = active_payer_account.getCurrencyID();
                    int money_value = (int) (Double.parseDouble(amount.getText()) * 100);
                    String title = titleTextField.getText();

                    if (active_payer_account.getID() == target_id) {
                        message.setText("Transaction failed: You can't send money to yourself.");
                    } else if (money_value > active_payer_account.getValue() || money_value <= 0) {
                        message.setText("Transaction failed: Invalid amount of money.");
                    } else if (!connection.checkAccount(Integer.parseInt(accountNumber.getText()))) {
                        message.setText("Transaction failed: Account don't exists.");
                    } else if (titleTextField.getText().equals("")) {
                        message.setText("Transaction failed: Title can't be null.");
                    } else {
                        message.setText(String.format("Sending %.2f %s to Account %d", money_value / 100.0, currencies.get("" + active_payer_account.getCurrencyID()), target_id));
                        connection.makeTransfer(login.getLogin(), login.getPasswordHash(), payer_id, target_id, title, money_value, currency_id);
                        updateMoney();
                        updateTransactionTable();
                    }
                }
            }
            catch (Exception ex) {
                message.setText("Transaction failed: " + ex.getMessage());
            }
        });
        logOutButton.addActionListener(e -> frame.setContentPane(previousPanel));
        accountSelect.addActionListener(e -> updateMoney());
    }
    void updateTransactionTable() {
        String[] columns = new String[] {
                "Od", "Do", "Tytuł", "Wartość", "Waluta"
        };
        Map<String, Object> transactions = connection.getTransactions(login.getLogin(), login.getPasswordHash());
        List<String[]> values = new ArrayList<String[]>();

        for (Object transaction: transactions.values()) {
            HashMap<String, String> transactionhash = (HashMap)transaction;
            values.add(new String[] {
                    transactionhash.get("senderid"),
                    transactionhash.get("receiverid"),
                    transactionhash.get("title"),
                    String.format("%.2f", Double.parseDouble(transactionhash.get("value")) / 100.0),
                    currencies.get(transactionhash.get("currencyid"))
            });
        }
        TableModel tableModel = new DefaultTableModel(values.toArray(new Object[][] {}), columns);
        JTable table = new JTable(tableModel);

        table.setDefaultEditor(Object.class, null);
        table.getTableHeader().setReorderingAllowed(false);
        historyPanel.getViewport().add(table);
    }
    void updateMoney() {
        if (accountSelect.getItemCount()>0) {
            active_payer_account = connection.getAccount(login.getLogin(), login.getPasswordHash(), Integer.parseInt((String) accountSelect.getSelectedItem()));
            payerBalance.setText(String.format("%.2f", active_payer_account.getValue() / 100.0));
            currencyLabel.setText(currencies.get("" + active_payer_account.getCurrencyID()));
        }
    }
    void updateAccounts() {
        for (Map.Entry<String,Integer> entry : connection.getUserAccounts(login.getLogin(), login.getPasswordHash()).entrySet())
            accountSelect.addItem(entry.getKey());
        updateMoney();
    }
}
