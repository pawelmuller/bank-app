package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Account;
import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.*;
import java.util.List;

public class MainUserPage {
    ClientServerConnection connection;
    Client client;
    Login login;

    JFrame frame;
    JPanel previousPanel, menuPanel;
    private JLabel logoLabel, nameLabel;
    private JTextField amount;
    private JButton sendMoneyButton, logOutButton;
    private JLabel message;
    private JLabel currentBalance;
    private JLabel idLabel;
    private JPanel transactionPanel;
    private JTabbedPane tabbedPane;
    private JTextField titleTextField;
    private JComboBox<String> accountSelect;
    private JLabel payerBalance;
    private JLabel currencyLabel;
    private JScrollPane historyPanel;
    private JComboBox<String> currenciesComboBox;
    private JButton createAccountButton;
    private JLabel doubleAccountWarning;
    private JComboBox<String> contactBox;
    private JTextField accountNumber;
    private JButton addContactButton;
    private JPanel accountsSummary;
    private JPanel contactsSummary;

    List<Integer> user_currencies;
    Account active_payer_account = null;
    Map <String, String> currencies;
    Map <String, Integer> contacts;

    public MainUserPage(JFrame _frame, JPanel _previousPanel, ClientServerConnection _connection, Client _client, Login _login) {
        frame = _frame;
        previousPanel = _previousPanel;
        connection = _connection;
        client = _client;
        login = _login;
        nameLabel.setText("Witaj " + client.getName() + "!");
        idLabel.setText("Numer klienta: " + client.getID());
        currencies = connection.getCurrencies();
        user_currencies = new ArrayList<>();

        accountsSummary.setLayout(new BoxLayout(accountsSummary, BoxLayout.Y_AXIS));
        contactsSummary.setLayout(new BoxLayout(contactsSummary, BoxLayout.Y_AXIS));
        updateFonts();

        fillCurrenciesComboBox();
        updateAccounts();
        updateContacts();
        updateTransactionTable();
        updateAccountsSummary();
        updateContactsSummary();

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
        addContactButton.addActionListener(e -> {
            addContact();
            updateContactsSummary();
        });
        contactBox.addActionListener(e -> {
            String name = (String) contactBox.getSelectedItem();
            Integer accountid = contacts.get(name);
            if (accountid != null)
                accountNumber.setText("" + accountid);
        });
        contactBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusGained(e);
                updateContacts();
            }
        });
    }
    void addContact() {
        try {
            int accountid = Integer.parseInt(accountNumber.getText());
            String name = (String) contactBox.getSelectedItem();
            if (!name.equals("")) {
                if (!connection.checkAccount(Integer.parseInt(accountNumber.getText())))
                    throw new NumberFormatException();
                connection.createContact(login.getLogin(), login.getPasswordHash(), name, accountid);
                message.setText(String.format("Account %d: %s", accountid, name));
            }

        } catch(NumberFormatException ex) {
            message.setText("Nie można dodać kontaktu: błędny numer konta.");
            System.out.println(ex.getMessage());
        } catch (InputMismatchException ex) {
            message.setText("Nie można dodać kontaktu: błędna nazwa.");
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            message.setText("Nie można dodać kontaktu.");
            System.out.println(ex.getMessage());
        }
    }
    void makeTransaction() {
        try {
            if (active_payer_account == null) {
                message.setText("Błąd tranzakcji: Nie posiadasz żadnego konta.");
            }
            else {
                int payer_id = active_payer_account.getID();
                int target_id = Integer.parseInt(accountNumber.getText());
                int currency_id = active_payer_account.getCurrencyID();
                int money_value = (int) (Double.parseDouble(amount.getText()) * 100);
                String title = titleTextField.getText();

                if (active_payer_account.getID() == target_id) {
                    message.setText("Błąd tranzakcji: Konto docelowe jest takie samo jak początkowe.");
                } else if (money_value > active_payer_account.getValue() || money_value <= 0) {
                    message.setText("Błąd tranzakcji: Błędna kwota przelewu.");
                } else if (!connection.checkAccount(Integer.parseInt(accountNumber.getText()))) {
                    message.setText("Błąd tranzakcji: Konto docelowe nie istnieje.");
                } else if (titleTextField.getText().equals("")) {
                    message.setText("Błąd tranzakcji: Tutuł nie może być pusty.");
                } else {
                    if (connection.getBasicAccount(target_id).getCurrencyID() == currency_id || (connection.getBasicAccount(target_id).getCurrencyID() != currency_id && currencyChange())) {
                        message.setText(String.format("Przesłano %.2f %s na konto %d.", money_value / 100.0, currencies.get("" + active_payer_account.getCurrencyID()), target_id));
                        connection.makeTransfer(login.getLogin(), login.getPasswordHash(), payer_id, target_id, title, money_value, currency_id);
                        updateMoney();
                        updateTransactionTable();
                        updateAccountsSummary();
                    }
                }
            }
        }
        catch (Exception ex) {
            message.setText("Błąd tranzakcji: " + ex.getMessage());
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

    private void updateFonts() {
        logoLabel.setFont(StartWindow.fonts.radikal.deriveFont(48f));
        Font standard_font = StartWindow.fonts.adagio_slab.deriveFont(12f);
        Font header_font = StartWindow.fonts.adagio_slab.deriveFont(20f);

        tabbedPane.setFont(standard_font);
        nameLabel.setFont(header_font);


        String system_name = System.getProperty("os.name");
        if (!system_name.startsWith("Windows")) {
            tabbedPane.setForeground(Colors.getOrange());
        }
    }
    void updateContacts() {
        contacts = connection.getContacts(login.getLogin(), login.getPasswordHash());
        if (contactBox.getItemCount() == 0)
            contactBox.addItem("");
        contactBox.setSelectedIndex(0);
        int cnt = contactBox.getItemCount();
        while (cnt > 1) {
            contactBox.remove(1);
            cnt--;
        }
        for (Map.Entry<String, Integer> contact: contacts.entrySet())
            contactBox.addItem(contact.getKey());
    }
    void updateTransactionTable() {
        String[] columns = new String[] {"Od", "Do", "Data", "Tytuł", "Wartość", "Waluta"};
        Map<Integer, JSONObject> transactions = connection.getTransactions(login.getLogin(), login.getPasswordHash());
        List<String[]> values = new ArrayList<>();

        for (JSONObject transaction: transactions.values()) {
            values.add(new String[] {
                    transaction.getString("senderid"),
                    transaction.getString("receiverid"),
                    transaction.getString("date"),
                    transaction.getString("title"),
                    String.format("%.2f", transaction.getDouble("value") / 100.0),
                    currencies.get(transaction.getString("currencyid"))
            });
        }
        TableModel tableModel = new DefaultTableModel(values.toArray(new Object[][] {}), columns);
        JTable table = new JTable(tableModel);

        table.setBackground(Colors.getDarkGrey());
        table.setForeground(Colors.getBrightTextColor());
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(Colors.getOrange());
        table.setGridColor(Colors.getLightGrey());

        table.setDefaultEditor(Object.class, null);
        table.getTableHeader().setReorderingAllowed(false);
        historyPanel.getViewport().add(table);
    }
    void updateMoney() {
        if (accountSelect.getItemCount()>0) {
            active_payer_account = connection.getAccount(login.getLogin(), login.getPasswordHash(), Integer.parseInt((String) accountSelect.getSelectedItem()));
            String active_currency_shortcut = currencies.get("" + active_payer_account.getCurrencyID());
            int total_balance = connection.getTotalSavings(login.getLogin(), login.getPasswordHash(), active_payer_account.getCurrencyID());

            currentBalance.setText(String.format("%.2f %s", total_balance/100.0, active_currency_shortcut));
            payerBalance.setText(String.format("%.2f %s", active_payer_account.getValue() / 100.0, active_currency_shortcut));
            currencyLabel.setText(active_currency_shortcut);
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

        tabbedPane.setEnabledAt(2, accountSelect.getItemCount() != 0);
        updateMoney();
        updateAccountsSummary();
    }
    void fillCurrenciesComboBox() {
        Object[] currencies_sorted = currencies.values().toArray();
        Arrays.sort(currencies_sorted);
        for (Object currency: currencies_sorted)
            currenciesComboBox.addItem((String) currency);
    }
    void updateAccountsSummary() {
        accountsSummary.removeAll();
        Map<String, Object> accounts = connection.getUserAccounts(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<String, Object> account: accounts.entrySet()) {
            HashMap account_hash = (HashMap) account.getValue();
            String currency_id = (String) account_hash.get("currencyid");
            String currency_name = currencies.get(currency_id);
            Integer balance = Integer.parseInt((String) account_hash.get("value"));
            String formatted_balance = String.format("%.2f", balance/100.0);

            AccountPanel accountPanel = new AccountPanel(account.getKey(), formatted_balance, currency_name);
            accountsSummary.add(accountPanel);
        }
    }
    void updateContactsSummary() {
        contactsSummary.removeAll();
        Map<String, Integer> contacts = connection.getContacts(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<String, Integer> contact: contacts.entrySet()) {
            ContactPanel contactPanel = new ContactPanel(contactsSummary, this, contact.getValue(), contact.getKey());
            contactsSummary.add(contactPanel);
        }
    }
}
