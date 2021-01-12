package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Account;
import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
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
    private JComboBox<String> accountBox;
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
    private JPanel creditPanel;
    private JLabel creditsBalance;
    private JPanel investmentsSummary;
    private JButton createInvestmentButton;
    private JTabbedPane financialProductsTabbedPane;
    private JLabel accountsSummaryLabel;

    List<Integer> user_currencies;
    List<Integer> accountBoxUnformatted;
    Account active_payer_account = null;
    Map <String, String> currencies;
    Map <String, Integer> contacts;
    Map <Integer, JSONObject> accounts;
    boolean lock_combobox = false;

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
        accountBoxUnformatted = new ArrayList<>();

        accountsSummary.setLayout(new GridBagLayout());
        contactsSummary.setLayout(new BoxLayout(contactsSummary, BoxLayout.Y_AXIS));
        investmentsSummary.setLayout(new BoxLayout(investmentsSummary, BoxLayout.Y_AXIS));
        updateFontsAndColors();

        updateContacts();
        fillCurrenciesComboBox();
        updateAccounts();
        updateTransactionTable();
        updateAccountsSummary();
        updateContactsSummary();
        updateCreditsBalance();
        updateInvestmentsSummary();

        sendMoneyButton.addActionListener(e -> makeTransaction());
        logOutButton.addActionListener(e -> frame.setContentPane(previousPanel));
        accountBox.addActionListener(e -> updateMoney());
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
            updateContacts();
            updateContactsSummary();
        });
        contactBox.addActionListener(e -> {
            if (!lock_combobox) {
                String name = (String) contactBox.getSelectedItem();
                Integer accountid = contacts.get(name);
                if (accountid != null)
                    accountNumber.setText("" + accountid);
            }
        });
        createInvestmentButton.addActionListener(e -> addInvestmentDialog());
    }
    void addInvestmentDialog() {
        JTextField name = new JTextField();
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accounts_to_select = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account: accounts.entrySet()) {
            accountBox.addItem(String.format("%s (%.2f %s)", getContactIfPossible(account.getKey()), account.getValue().getDouble("value") / 100, currencies.get(account.getValue().getString("currencyid"))));
            accounts_to_select.add(account.getKey());
        }
        JTextField value = new JTextField();
        JTextField profitrate = new JTextField();
        JTextField yearprofitrate = new JTextField();
        JTextField capperoid = new JTextField();

        Object[] message = {
                "Nazwa:", name,
                "Z konta:", accountBox,
                "Kwota początkowa", value,
                "Oprocentowanie", profitrate,
                "Oprocentowanie roczne", yearprofitrate,
                "Okres kapitalizacji", capperoid,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Nowa lokata", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            connection.createInvestment(
                    login.getLogin(),
                    login.getPasswordHash(),
                    name.getText(),
                    (int)(Double.parseDouble(value.getText()) * 100),
                    Double.parseDouble(profitrate.getText()),
                    Double.parseDouble(yearprofitrate.getText()),
                    Integer.parseInt(capperoid.getText()),
                    accounts_to_select.get(accountBox.getSelectedIndex())
            );
            updateInvestmentsSummary();
            updateAccounts();
        }
    }
    boolean currencyChangeWarning() {
        int n = JOptionPane.showConfirmDialog(
                frame,
                "Konto, na które zamierzasz wysłać przelew, zawiera inną walutę niż wysyłana, czy chcesz przewalutować?",
                "Przewalutowanie",
                JOptionPane.YES_NO_OPTION);
        return n==0;
    }

    void addContact() {
        try {
            int accountid = Integer.parseInt(accountNumber.getText());
            String name = (String) contactBox.getSelectedItem();
            if (!name.equals("")) {
                if (!connection.checkAccount(Integer.parseInt(accountNumber.getText())))
                    throw new NumberFormatException();
                connection.createContact(login.getLogin(), login.getPasswordHash(), name, accountid);
                message.setText(String.format("Konto %d: %s", accountid, name));
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
                message.setText("Błąd transakcji: Nie posiadasz żadnego konta.");
            }
            else {
                int payer_id = active_payer_account.getID();
                int target_id = Integer.parseInt(accountNumber.getText());
                int currency_id = active_payer_account.getCurrencyID();
                int money_value = (int) (Double.parseDouble(amount.getText()) * 100);
                String title = titleTextField.getText();

                if (active_payer_account.getID() == target_id) {
                    message.setText("Błąd transakcji: Konto docelowe jest takie samo jak początkowe.");
                } else if (money_value > active_payer_account.getValue() || money_value <= 0) {
                    message.setText("Błąd transakcji: Błędna kwota przelewu.");
                } else if (!connection.checkAccount(Integer.parseInt(accountNumber.getText()))) {
                    message.setText("Błąd transakcji: Konto docelowe nie istnieje.");
                } else if (titleTextField.getText().equals("")) {
                    message.setText("Błąd transakcji: Tytuł nie może być pusty.");
                } else {
                    if (connection.getBasicAccount(target_id).getCurrencyID() == currency_id || (connection.getBasicAccount(target_id).getCurrencyID() != currency_id && currencyChangeWarning())) {
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
            message.setText("Błąd transakcji: " + ex.getMessage());
        }
    }

    String getContactIfPossible(int value) {
        for (Map.Entry<String, Integer> contact:contacts.entrySet())
            if (contact.getValue() == value)
                return contact.getKey();
        return String.valueOf(value);
    }

    private void updateFontsAndColors() {
        Colors colors = new Colors();
        Fonts fonts = new Fonts();

        Font standard_font = Fonts.getStandardFont();
        Font header_font = Fonts.getHeaderFont();
        Font logo_font = Fonts.getLogoFont();

        logoLabel.setFont(logo_font);

        tabbedPane.setFont(standard_font);
        financialProductsTabbedPane.setFont(standard_font);
        nameLabel.setFont(header_font);

        logoLabel.setForeground(Colors.getOrange());

        idLabel.setForeground(Colors.getBrightTextColor());
        nameLabel.setForeground(Colors.getBrightTextColor());

        String system_name = System.getProperty("os.name");
        if (!system_name.startsWith("Windows")) {
            tabbedPane.setForeground(Colors.getOrange());
        }
    }
    public void updateContacts() {
        lock_combobox = true;
        contacts = connection.getContacts(login.getLogin(), login.getPasswordHash());
        String temp = (String) contactBox.getSelectedItem();
        contactBox.removeAllItems();
        contactBox.addItem("");
        for (Map.Entry<String, Integer> contact: contacts.entrySet())
            contactBox.addItem(contact.getKey());
        contactBox.setSelectedItem(temp);
        updateTransactionTable();
        lock_combobox = false;
    }
    private void updateTransactionTable() {
        String[] columns = new String[] {"Od", "Do", "Data", "Tytuł", "Wartość", "Waluta"};
        Map<Integer, JSONObject> transactions = connection.getTransactions(login.getLogin(), login.getPasswordHash());
        List<String[]> values = new ArrayList<>();

        for (JSONObject transaction: transactions.values()) {
            values.add(new String[] {
                    getContactIfPossible(transaction.getInt("senderid")),
                    getContactIfPossible(transaction.getInt("receiverid")),
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
    public void updateMoney() {
        if (accountBox.getItemCount()>0) {
            active_payer_account = connection.getAccount(login.getLogin(), login.getPasswordHash(), accountBoxUnformatted.get(accountBox.getSelectedIndex()));
            String active_currency_shortcut = currencies.get("" + active_payer_account.getCurrencyID());
            int total_balance = connection.getTotalSavings(login.getLogin(), login.getPasswordHash(), active_payer_account.getCurrencyID());

            currentBalance.setText(String.format("%.2f %s", total_balance/100.0, active_currency_shortcut));
            payerBalance.setText(String.format("%.2f %s", active_payer_account.getValue() / 100.0, active_currency_shortcut));
            currencyLabel.setText(active_currency_shortcut);
        }
    }
    public void updateAccounts() {
        accountBox.removeAllItems();
        accounts = connection.getUserAccounts(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<Integer, JSONObject> account: accounts.entrySet()) {
            accountBox.addItem(String.format("%s (%s)",
                    getContactIfPossible(account.getKey()),
                    currencies.get(account.getValue().getString("currencyid")))
            );
            accountBoxUnformatted.add(account.getKey());
            user_currencies.add(account.getValue().getInt("currencyid"));
        }

        tabbedPane.setEnabledAt(2, accountBox.getItemCount() != 0);
        updateMoney();
        updateAccountsSummary();
    }
    private void fillCurrenciesComboBox() {
        Object[] currencies_sorted = currencies.values().toArray();
        Arrays.sort(currencies_sorted);
        for (Object currency: currencies_sorted)
            currenciesComboBox.addItem((String) currency);
    }
    private void updateAccountsSummary() {
        accountsSummary.removeAll();
        int column = 0, row = 0, counter = 0, accounts_count = accounts.size();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;

        for (Map.Entry<Integer, JSONObject> account: accounts.entrySet()) {
            String currency_id = account.getValue().getString("currencyid");
            String currency_name = currencies.get(currency_id);
            int balance = account.getValue().getInt("value");
            String formatted_balance = String.format("%.2f", balance/100.0);

            AccountPanel accountPanel = new AccountPanel(getContactIfPossible(account.getKey()), "" + account.getKey(), formatted_balance, currency_name);

            c.gridx = column++;
            c.gridy = row;
            if (column % 3 == 0) {
                column = 0;
                row++;
            }
            if (accounts_count % 3 == 1 && ++counter == accounts_count) {
                c.gridx = 1;
            }

            accountsSummary.add(accountPanel, c);
        }
    }
    private void updateContactsSummary() {
        contactsSummary.removeAll();
        Map<String, Integer> contacts = connection.getContacts(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<String, Integer> contact: contacts.entrySet()) {
            ContactPanel contactPanel = new ContactPanel(contactsSummary, this, contact.getValue(), contact.getKey());
            contactsSummary.add(contactPanel);
            contactsSummary.add(new JSeparator());
        }
    }
    public void updateInvestmentsSummary() {
        investmentsSummary.removeAll();
        Map<Integer, JSONObject> investments = connection.getInvestments(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<Integer, JSONObject> investment: investments.entrySet()) {
            InvestmentPanel investmentPanel = new InvestmentPanel(investmentsSummary, this, investment.getKey(), investment.getValue());
            investmentsSummary.add(investmentPanel);
        }
        investmentsSummary.updateUI();
    }
    private void updateCreditsBalance() {
        if (accountBox.getItemCount() > 0) {
            active_payer_account = connection.getAccount(login.getLogin(), login.getPasswordHash(), accountBoxUnformatted.get(accountBox.getSelectedIndex()));
            String active_currency_shortcut = currencies.get("" + active_payer_account.getCurrencyID());
            int credits_total = connection.getTotalCredits(login.getLogin(), login.getPasswordHash(), active_payer_account.getCurrencyID());

            creditsBalance.setText(String.format("%.2f %s", credits_total / 100.0, active_currency_shortcut));
        }
    }
}