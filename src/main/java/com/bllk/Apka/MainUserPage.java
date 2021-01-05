package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Account;
import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

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
    private JPanel transactionPanel;
    private JTabbedPane tabbedPane1;
    private JPanel historyPanel2;
    private JTextField titleTextField;
    private JComboBox<String> accountSelect;
    private JLabel payerBalance;
    private JLabel currencyLabel;
    private JScrollPane historyPanel;
    private JComboBox<String> currenciesComboBox;
    private JButton createAccountButton;
    private JLabel doubleAccountWarning;

    List<Integer> user_currencies;
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
        user_currencies = new ArrayList<>();

        fillCurrenciesComboBox();
        updateAccounts();
        updateTransactionTable();
        sendMoneyButton.addActionListener(e -> makeTransaction());
        logOutButton.addActionListener(e -> frame.setContentPane(previousPanel));
        accountSelect.addActionListener(e -> updateMoney());
        createAccountButton.addActionListener(e -> {
            for (Map.Entry<String, String> entry : currencies.entrySet()) {
                if (Objects.equals(currenciesComboBox.getSelectedItem(), entry.getValue())) {
                    int currency_id = Integer.parseInt(entry.getKey());
                    if (user_currencies.contains(currency_id)) {
                        doubleAccountWarning.setVisible(true);
                    } else {
                        doubleAccountWarning.setVisible(false);
                        connection.createAccount(login.getLogin(), login.getPasswordHash(), currency_id);
                    }
                }
            }
            updateAccounts();
        });
    }
    void makeTransaction() {
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
                    if (connection.getBasicAccount(target_id).getCurrencyID() != currency_id && currencyChange()) {
                        message.setText(String.format("Sending %.2f %s to Account %d", money_value / 100.0, currencies.get("" + active_payer_account.getCurrencyID()), target_id));
                        connection.makeTransfer(login.getLogin(), login.getPasswordHash(), payer_id, target_id, title, money_value, currency_id);
                        updateMoney();
                        updateTransactionTable();
                    }
                }
            }
        }
        catch (Exception ex) {
            message.setText("Transaction failed: " + ex.getMessage());
        }
    }
    boolean currencyChange() {
        int n = JOptionPane.showConfirmDialog(
                frame,
                "Konto, na które zamierzasz wysłać przelew, zawiera inną walutę niż wysyłana, czy chcesz przewalutować?",
                "Przewalutowanie",
                JOptionPane.YES_NO_OPTION);
        return n==0;
    }
    void updateTransactionTable() {
        String[] columns = new String[] {
                "Od", "Do", "Tytuł", "Wartość", "Waluta"
        };
        Map<Integer, JSONObject> transactions = connection.getTransactions(login.getLogin(), login.getPasswordHash());
        List<String[]> values = new ArrayList<>();

        for (JSONObject transaction: transactions.values()) {
            values.add(new String[] {
                    transaction.getString("senderid"),
                    transaction.getString("receiverid"),
                    transaction.getString("title"),
                    String.format("%.2f", transaction.getDouble("value") / 100.0),
                    currencies.get(transaction.getString("currencyid"))
            });
        }
        TableModel tableModel = new DefaultTableModel(values.toArray(new Object[][] {}), columns);
        JTable table = new JTable(tableModel);

        table.setBackground(Color.decode("#222222"));
        table.setForeground(Color.decode("#EEEEEE"));
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(Color.decode("#FF7F00"));
        table.setGridColor(Color.decode("#808080"));

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
        accountSelect.removeAllItems();
        Map<String, Object> accounts = connection.getUserAccounts(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<String, Object> account: accounts.entrySet()) {
            accountSelect.addItem(account.getKey());
            HashMap accounthash = (HashMap) account.getValue();
            user_currencies.add(Integer.parseInt((String) accounthash.get("currencyid")));
        }

        tabbedPane1.setEnabledAt(2, accountSelect.getItemCount() != 0);
        updateMoney();
    }
    void fillCurrenciesComboBox() {
        Object[] currencies_sorted = currencies.values().toArray();
        Arrays.sort(currencies_sorted);
        for (Object currency: currencies_sorted)
            currenciesComboBox.addItem((String) currency);
    }
}
