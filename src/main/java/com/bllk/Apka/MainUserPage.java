package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Account;
import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MainUserPage {
    static JFrame frame = StartWindow.frame;
    static ClientServerConnection connection = StartWindow.connection;
    JPanel currentPanel;

    Client client;
    Login login;

    private JLabel logoLabel, nameLabel;
    private JTextField transfer_amount;
    private JButton transfer_sendMoneyButton, logOutButton;
    private JLabel transfer_message;
    private JLabel transfer_currentBalance;
    private JLabel idLabel;
    private JPanel transactionPanel;
    private JTabbedPane tabbedPane;
    private JTextField transfer_title;
    private JComboBox<String> transfer_accountSelectBox;
    private JLabel transfer_payerBalance;
    private JLabel transfer_currencyLabel;
    private JScrollPane historyPane;
    private JComboBox<String> currenciesComboBox;
    private JButton createAccountButton;
    private JLabel doubleAccountWarning;
    private JComboBox<String> transfer_contactBox;
    private JTextField transfer_accountNumber;
    private JButton transfer_addContactButton;
    private JPanel accountsSummary;
    private JPanel contactsSummary;
    private JLabel creditsBalance;
    private JPanel investmentsSummary;
    private JButton createInvestmentButton;
    private JButton createCreditButton;
    private JTabbedPane financialProductsTabbedPane;
    private JLabel accountsSummaryLabel;
    private JLabel transfer_currentBalanceLabel;
    private JLabel transfer_titleLabel;
    private JLabel transfer_fromLabel;
    private JLabel transfer_contactNameLabel;
    private JLabel transfer_toLabel;
    private JLabel transfer_valueLabel;
    private JPanel financialProductsPanel;
    private JPanel transactionHistoryPanel;
    private JPanel accountsPanel;
    private JPanel contactsPanel;
    private JPanel settingsPanel;
    private JPanel creditsSummary;
    private JPanel creditsBalancePanel;
    private JPanel investmentsPanel;
    private JPanel creditsPanel;
    private JButton changeLoginButton;
    private JButton changePasswordButton;
    private JTextField loginField;
    private JLabel loginpasswordLabel;
    private JLabel creditsBalanceLabel;

    List<Integer> user_currencies = new ArrayList<>();
    List<Integer> accountBoxUnformatted = new ArrayList<>();
    Account active_payer_account = null;
    Map <String, String> currencies;
    Map <String, Integer> contacts;
    Map <Integer, JSONObject> accounts;
    boolean lock_combobox = false;

    public MainUserPage(Client _client, Login _login) {
        client = _client;
        login = _login;
        nameLabel.setText("Witaj " + client.getName() + "!");
        idLabel.setText("Numer klienta: " + client.getID());
        loginField.setText(login.getLogin());
        currencies = connection.getCurrencies();

        accountsSummary.setLayout(new GridBagLayout());
        contactsSummary.setLayout(new BoxLayout(contactsSummary, BoxLayout.Y_AXIS));
        investmentsSummary.setLayout(new BoxLayout(investmentsSummary, BoxLayout.Y_AXIS));
        creditsSummary.setLayout(new BoxLayout(creditsSummary, BoxLayout.Y_AXIS));
        updateFontsAndColors();

        updateContacts();
        fillCurrenciesComboBox();
        updateAccounts();
        updateTransactionTable();
        updateAccountsSummary();
        updateContactsSummary();
        updateCreditsBalance();
        updateInvestmentsSummary();
        updateCreditsSummary();

        transfer_sendMoneyButton.addActionListener(e -> makeTransaction());
        logOutButton.addActionListener(e -> {
            StartWindow.startingPanel.setSize(currentPanel.getSize());
            frame.setContentPane(StartWindow.startingPanel);
        });
        transfer_accountSelectBox.addActionListener(e -> updateMoney());
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
        transfer_addContactButton.addActionListener(e -> {
            addContact();
            updateContacts();
            updateContactsSummary();
        });
        transfer_contactBox.addActionListener(e -> {
            if (!lock_combobox) {
                String name = (String) transfer_contactBox.getSelectedItem();
                Integer accountid = contacts.get(name);
                if (accountid != null)
                    transfer_accountNumber.setText("" + accountid);
            }
        });
        createInvestmentButton.addActionListener(e -> addInvestmentDialog());
        createCreditButton.addActionListener(e -> addCreditDialog());
        changeLoginButton.addActionListener(e -> changeLoginDialog());
        changePasswordButton.addActionListener(e -> changePasswordDialog());
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
        JTextField capperoid = new JTextField();

        JLabel profitrateValue = new JLabel("Oprocentowanie: 5,0%");
        JLabel yearprofitrateValue = new JLabel("Oprocentowanie roczne: 5,0%");
        JSlider profitrate = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        JSlider yearprofitrate = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);

        profitrate.addChangeListener(e -> profitrateValue.setText("Oprocentowanie: " + String.format("%.1f", profitrate.getValue() / 10f) + "%"));
        yearprofitrate.addChangeListener(e -> yearprofitrateValue.setText("Oprocentowanie roczne: " + String.format("%.1f", yearprofitrate.getValue() / 10f) + "%"));

        Hashtable<Integer, JLabel> slidersLabelTable = new Hashtable<>();
        slidersLabelTable.put(2, new JLabel("0%") );
        slidersLabelTable.put(22, new JLabel("2%") );
        slidersLabelTable.put(42, new JLabel("4%") );
        slidersLabelTable.put(62, new JLabel("6%") );
        slidersLabelTable.put(82, new JLabel("8%") );
        slidersLabelTable.put(100, new JLabel("10%") );

        profitrate.setMajorTickSpacing(10);
        profitrate.setMinorTickSpacing(5);
        profitrate.setPaintTicks(true);
        profitrate.setLabelTable(slidersLabelTable);
        profitrate.setPaintLabels(true);

        yearprofitrate.setMajorTickSpacing(10);
        yearprofitrate.setMinorTickSpacing(5);
        yearprofitrate.setPaintTicks(true);
        yearprofitrate.setLabelTable(slidersLabelTable);
        yearprofitrate.setPaintLabels(true);

        Object[] message = {
                "Nazwa:", name,
                "Z konta:", accountBox,
                "Kwota początkowa", value,
                profitrateValue, profitrate,
                yearprofitrateValue, yearprofitrate,
                "Okres kapitalizacji [mies.]", capperoid,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Nowa lokata", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (name.getText().isEmpty() || value.getText().isEmpty() || capperoid.getText().isEmpty())
                JOptionPane.showMessageDialog(null,"Pole nie może być puste.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else {
                try {
                    int valueint = (int) Double.parseDouble(value.getText().replace(",",".")) * 100;
                    int capperoidint = Integer.parseInt(capperoid.getText());

                    if (accounts.get(accounts_to_select.get(accountBox.getSelectedIndex())).getInt("value") < valueint)
                        JOptionPane.showMessageDialog(null,"Nie posiadasz tyle pieniędzy.", "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
                    else {
                        connection.createInvestment(login.getLogin(), login.getPasswordHash(), name.getText(),
                                valueint, profitrate.getValue() / 1000.0, yearprofitrate.getValue() / 1000.0,
                                capperoidint, accounts_to_select.get(accountBox.getSelectedIndex()));
                        updateInvestmentsSummary();
                        updateAccounts();
                        JOptionPane.showMessageDialog(null,"Operacja powiodła się.","Sukces", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,"Błędna wartość.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    void addCreditDialog() {
        JTextField name = new JTextField();
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accounts_to_select = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account : accounts.entrySet()) {
            accountBox.addItem(String.format("%s (%.2f %s)", getContactIfPossible(account.getKey()), account.getValue().getDouble("value") / 100, currencies.get(account.getValue().getString("currencyid"))));
            accounts_to_select.add(account.getKey());
        }
        JTextField value = new JTextField();
        value.setText("1000");
        JLabel interestValue = new JLabel("Oprocentowanie: 5,0%");
        JLabel commissionValue = new JLabel("Prowizja: 5,0%");

        JSlider interestrate = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        JSlider commission = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);

        ChangeListener interestChangeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                interestValue.setText("Oprocentowanie: " + String.format("%.1f", slider.getValue() / 10f) + "%");
                }
            };
        ChangeListener commissionChangeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                commissionValue.setText("Prowizja: " + String.format("%.1f", slider.getValue() / 10f) + "%");
            }
        };

        commission.addChangeListener(commissionChangeListener);
        commission.setMajorTickSpacing(10);
        commission.setMinorTickSpacing(5);
        commission.setPaintTicks(true);
        commission.setPaintLabels(true);

        interestrate.addChangeListener(interestChangeListener);
        interestrate.setMajorTickSpacing(10);
        interestrate.setMinorTickSpacing(5);
        interestrate.setPaintTicks(true);
        interestrate.setPaintLabels(true);

        //Create the label table
        Hashtable<Integer, JLabel> slidersLabelTable = new Hashtable<>();
        slidersLabelTable.put(2, new JLabel("0%") );
        slidersLabelTable.put(22, new JLabel("2%") );
        slidersLabelTable.put(42, new JLabel("4%") );
        slidersLabelTable.put(62, new JLabel("6%") );
        slidersLabelTable.put(82, new JLabel("8%") );
        slidersLabelTable.put(100, new JLabel("10%") );

        commission.setLabelTable( slidersLabelTable );
        commission.setPaintLabels(true);
        interestrate.setLabelTable( slidersLabelTable );
        interestrate.setPaintLabels(true);

        List<String> monthsList = new ArrayList<>();
        for (int i = 1; i <= 120; i++)
            monthsList.add(i + "");
        SpinnerListModel monthsModel = new SpinnerListModel(monthsList.toArray());
        JSpinner months = new JSpinner(monthsModel);
        months.setValue("24");

        Object[] message = {
                "Nazwa:", name,
                "Na konto:", accountBox,
                "Kwota początkowa", value,
                interestValue, interestrate,
                commissionValue, commission,
                "Całkowity okres spłaty [mies.]", months,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Nowy kredyt", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            connection.createCredit(
                    login.getLogin(),
                    login.getPasswordHash(),
                    name.getText(),
                    (int)(Double.parseDouble(value.getText()) * 100),
                    interestrate.getValue()/1000.0,
                    commission.getValue()/1000.0,
                    Integer.parseInt((String) months.getValue()),
                    accounts_to_select.get(accountBox.getSelectedIndex())
            );
            updateCreditsSummary();
            updateAccounts();
            JOptionPane.showMessageDialog(null,"Operacja powiodła się.","Sukces", JOptionPane.ERROR_MESSAGE);
        }
    }
    void changeLoginDialog() {
        JTextField new_name = new JTextField();

        Object[] message = {
                "Czy chcesz zmienić login '" + login.getLogin() + "'?",
                "Nowy login:", new_name
        };

        int option = JOptionPane.showConfirmDialog(null, message,
                "Zmiana loginu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String new_name_string = new_name.getText();
            if (new_name_string.isEmpty())
                JOptionPane.showMessageDialog(null,"Pole nie może być puste.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (connection.checkLogin(new_name_string))
                JOptionPane.showMessageDialog(null,"Login jest zajęty.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else {
                if (connection.updateLogin(login.getLogin(), login.getPasswordHash(), new_name_string)) {
                    JOptionPane.showMessageDialog(null,"Zmiana loginu powiodła się.","Sukces", JOptionPane.ERROR_MESSAGE);
                    loginField.setText(new_name_string);
                }
                else
                    JOptionPane.showMessageDialog(null,"Serwer odrzucił żądanie","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    void changePasswordDialog() {
        JTextField new_password = new JPasswordField();
        JTextField new_password_repeat = new JPasswordField();

        Object[] message = {
                "Czy chcesz zmienić twoje hasło?",
                "Nowe hasło:", new_password,
                "Powtórz hasło", new_password_repeat
        };

        int option = JOptionPane.showConfirmDialog(null, message,
                "Zmiana hasła", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String new_password_string = new_password.getText();
            String new_password_repeat_string = new_password_repeat.getText();
            int password_length = new_password_string.length();

            if (new_password_string.isEmpty())
                JOptionPane.showMessageDialog(null,"Pole nie może być puste.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (password_length < StartWindow.passwordMinimumLength || password_length > StartWindow.passwordMaximumLength)
                JOptionPane.showMessageDialog(null,"Hasło musi mieć od 8 do 16 znaków.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (!new_password_string.equals(new_password_repeat_string))
                JOptionPane.showMessageDialog(null,"Hasła nie są identyczne.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else {
                String hashed_password = BCrypt.hashpw(new_password_string, BCrypt.gensalt(12));
                connection.updatePassword(login.getLogin(), hashed_password);
                JOptionPane.showMessageDialog(null,"Zmiana hasła powiodła się.","Sukces", JOptionPane.ERROR_MESSAGE);
            }
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
            int accountid = Integer.parseInt(transfer_accountNumber.getText());
            String name = (String) transfer_contactBox.getSelectedItem();
            if (!name.equals("")) {
                if (!connection.checkAccount(Integer.parseInt(transfer_accountNumber.getText())))
                    throw new NumberFormatException();
                connection.createOrUpdateContact(login.getLogin(), login.getPasswordHash(), name, accountid);
                transfer_message.setText(String.format("Konto %d: %s", accountid, name));
            }

        } catch(NumberFormatException ex) {
            transfer_message.setText("Nie można dodać kontaktu: błędny numer konta.");
            System.out.println(ex.getMessage());
        } catch (InputMismatchException ex) {
            transfer_message.setText("Nie można dodać kontaktu: błędna nazwa.");
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            transfer_message.setText("Nie można dodać kontaktu.");
            System.out.println(ex.getMessage());
        }
    }
    void makeTransaction() {
        try {
            if (active_payer_account == null) {
                transfer_message.setText("Błąd transakcji: Nie posiadasz żadnego konta.");
            }
            else {
                int payer_id = active_payer_account.getID();
                int target_id = Integer.parseInt(transfer_accountNumber.getText());
                int currency_id = active_payer_account.getCurrencyID();
                int money_value = (int) (Double.parseDouble(transfer_amount.getText()) * 100);
                String title = transfer_title.getText();

                if (active_payer_account.getID() == target_id) {
                    transfer_message.setText("Błąd transakcji: Konto docelowe jest takie samo jak początkowe.");
                } else if (money_value > active_payer_account.getValue() || money_value <= 0) {
                    transfer_message.setText("Błąd transakcji: Błędna kwota przelewu.");
                } else if (!connection.checkAccount(Integer.parseInt(transfer_accountNumber.getText()))) {
                    transfer_message.setText("Błąd transakcji: Konto docelowe nie istnieje.");
                } else if (transfer_title.getText().equals("")) {
                    transfer_message.setText("Błąd transakcji: Tytuł nie może być pusty.");
                } else {
                    if (connection.getBasicAccount(target_id).getCurrencyID() == currency_id || (connection.getBasicAccount(target_id).getCurrencyID() != currency_id && currencyChangeWarning())) {
                        transfer_message.setText(String.format("Przesłano %.2f %s na konto %d.", money_value / 100.0, currencies.get("" + active_payer_account.getCurrencyID()), target_id));
                        connection.makeTransfer(login.getLogin(), login.getPasswordHash(), payer_id, target_id, title, money_value, currency_id);
                        updateMoney();
                        updateTransactionTable();
                        updateAccountsSummary();
                    }
                }
            }
        }
        catch (Exception ex) {
            transfer_message.setText("Błąd transakcji: " + ex.getMessage());
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


        // Main elements
        logoLabel.setFont(logo_font);
        logoLabel.setForeground(Colors.getOrange());

        nameLabel.setFont(header_font);

        logOutButton.setFont(standard_font);

        for (JLabel jLabel : Arrays.asList(idLabel, nameLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
        }

        for (JTabbedPane jTabbedPane : Arrays.asList(tabbedPane, financialProductsTabbedPane)) {
            jTabbedPane.setFont(standard_font);
            jTabbedPane.setForeground(Colors.getDarkGrey());
            jTabbedPane.setBackground(Colors.getBlue());
        }

        for (JPanel jPanel : Arrays.asList(accountsPanel, transactionPanel, transactionHistoryPanel,
                financialProductsPanel, contactsPanel, settingsPanel, investmentsPanel, creditsPanel)) {
            jPanel.setFont(standard_font);
            jPanel.setForeground(Colors.getBrightTextColor());
            jPanel.setBackground(Colors.getDarkGrey());
        }

        // Accounts
        accountsSummaryLabel.setFont(Fonts.getHeaderFont());

        accountsSummaryLabel.setForeground(Colors.getBrightTextColor());
        accountsSummary.setForeground(Colors.getBrightTextColor());

        accountsSummary.setBackground(Colors.getDarkGrey());

        // Transfers
        for (JLabel jLabel : Arrays.asList(transfer_contactNameLabel, transfer_currencyLabel, transfer_currentBalance,
                transfer_currentBalanceLabel, transfer_fromLabel, transfer_message, transfer_payerBalance,
                transfer_titleLabel, transfer_toLabel, transfer_valueLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
            jLabel.setFont(standard_font);
        }
        for (JTextField jTextField : Arrays.asList(transfer_accountNumber, transfer_amount, transfer_title)) {
            jTextField.setForeground(Colors.getBrightTextColor());
            jTextField.setBackground(Colors.getLightGrey());
            jTextField.setFont(standard_font);
        }
        for (JComboBox<String> jComboBox : Arrays.asList(transfer_contactBox, transfer_accountSelectBox)) {
            jComboBox.setForeground(Colors.getOrange());
            jComboBox.setBackground(Colors.getGrey());
            jComboBox.setFont(standard_font);
        }
        for (JButton jButton : Arrays.asList(transfer_addContactButton, transfer_sendMoneyButton)) {
            jButton.setFont(standard_font);
        }

        // Transfer history
        historyPane.setFont(standard_font);
        historyPane.getViewport().setBackground(Colors.getDarkGrey());

        // Settings
        loginpasswordLabel.setFont(Fonts.getHeaderFont());
        loginpasswordLabel.setForeground(Colors.getBrightTextColor());
        loginField.setFont(standard_font);

        // Credits
        creditsBalance.setFont(Fonts.getHeaderFont());
        creditsBalance.setForeground(Colors.getBrightTextColor());
        creditsBalanceLabel.setFont(Fonts.getHeaderFont());
        creditsBalanceLabel.setForeground(Colors.getBrightTextColor());

        String system_name = System.getProperty("os.name");
        if (!system_name.startsWith("Windows")) {
            tabbedPane.setForeground(Colors.getOrange());
            financialProductsTabbedPane.setForeground(Colors.getOrange());
        }
    }
    public void updateContacts() {
        lock_combobox = true;
        contacts = connection.getContacts(login.getLogin(), login.getPasswordHash());
        String temp = (String) transfer_contactBox.getSelectedItem();
        transfer_contactBox.removeAllItems();
        transfer_contactBox.addItem("");
        for (Map.Entry<String, Integer> contact: contacts.entrySet())
            transfer_contactBox.addItem(contact.getKey());
        transfer_contactBox.setSelectedItem(temp);
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

        table.setFont(Fonts.getStandardFont());
        table.setBackground(Colors.getDarkGrey());
        table.setForeground(Colors.getBrightTextColor());
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setFont(Fonts.getSmallHeaderFont());
        table.getTableHeader().setBackground(Colors.getOrange());
        table.setGridColor(Colors.getGrey());
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        table.setDefaultEditor(Object.class, null);
        table.getTableHeader().setReorderingAllowed(false);
        historyPane.getViewport().add(table);
    }
    public void updateMoney() {
        if (transfer_accountSelectBox.getItemCount() > 0) {
            active_payer_account = connection.getAccount(login.getLogin(), login.getPasswordHash(), accountBoxUnformatted.get(transfer_accountSelectBox.getSelectedIndex()));
            if (transfer_accountSelectBox.getItemCount() > 0) {
                active_payer_account = connection.getAccount(login.getLogin(), login.getPasswordHash(), accountBoxUnformatted.get(transfer_accountSelectBox.getSelectedIndex()));
                String active_currency_shortcut = currencies.get("" + active_payer_account.getCurrencyID());
                int total_balance = connection.getTotalSavings(login.getLogin(), login.getPasswordHash(), active_payer_account.getCurrencyID());

                transfer_currentBalance.setText(String.format("%.2f %s", total_balance / 100.0, active_currency_shortcut));
                transfer_payerBalance.setText(String.format("%.2f %s", active_payer_account.getValue() / 100.0, active_currency_shortcut));
                transfer_currencyLabel.setText(active_currency_shortcut);
            }
        }
    }
    public void updateAccounts() {
        transfer_accountSelectBox.removeAllItems();
        accounts = connection.getUserAccounts(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<Integer, JSONObject> account: accounts.entrySet()) {
            transfer_accountSelectBox.addItem(String.format("%s (%s)",
                    getContactIfPossible(account.getKey()),
                    currencies.get(account.getValue().getString("currencyid")))
            );
            accountBoxUnformatted.add(account.getKey());
            user_currencies.add(account.getValue().getInt("currencyid"));
        }

        tabbedPane.setEnabledAt(2, transfer_accountSelectBox.getItemCount() != 0);
        updateMoney();
        updateAccountsSummary();
    }
    private void fillCurrenciesComboBox() {
        Object[] currencies_sorted = currencies.values().toArray();
        Arrays.sort(currencies_sorted);
        for (Object currency: currencies_sorted)
            currenciesComboBox.addItem((String) currency);
    }
    public void updateAccountsSummary() {
        accountsSummary.removeAll();
        int column = 0, row = 1, counter = 1, accounts_count = accounts.size();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;

        c.gridy = 0;
        if (accounts_count != 0) {
            for (int i = 0; i < 6; i++) {
                JLabel nothing = new JLabel("");
                c.gridx = i;
                accountsSummary.add(nothing, c);
            }
        } else {
            c.gridx = 0;
            JLabel no_account_information = new JLabel("Nie masz żadnego konta. Możesz dodać je poniżej.");
            no_account_information.setFont(Fonts.getStandardFont());
            no_account_information.setForeground(Colors.getBrightTextColor());
            accountsSummary.add(no_account_information, c);
        }

        c.gridwidth = 2;
        for (Map.Entry<Integer, JSONObject> account: accounts.entrySet()) {
            String currency_id = account.getValue().getString("currencyid");
            String currency_name = currencies.get(currency_id);
            int balance = account.getValue().getInt("value");
            String formatted_balance = String.format("%.2f", balance/100.0);

            AccountPanel accountPanel = new AccountPanel(getContactIfPossible(account.getKey()),
                    "" + account.getKey(), formatted_balance, currency_name, this);

            if (counter == accounts_count - 1) {
                if (accounts_count % 3 == 2) column = 1;
            } else if (counter == accounts_count) {
                if (accounts_count % 3 == 1) column = 2;
            }

            counter++;
            c.gridx = column;
            c.gridy = row;
            column += 2;
            if (column % 6 == 0) {
                column = 0;
                row++;
            }
            accountsSummary.add(accountPanel, c);
        }
        refreshFrame();
    }
    private void updateContactsSummary() {
        contactsSummary.removeAll();
        Map<String, Integer> contacts = connection.getContacts(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<String, Integer> contact: contacts.entrySet()) {
            if (!accounts.containsKey(contact.getValue())) {
                ContactPanel contactPanel = new ContactPanel(contactsSummary, this, contact.getValue(), contact.getKey());
                contactsSummary.add(contactPanel);
                contactsSummary.add(new JSeparator());
            }
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
    public void updateCreditsSummary() {
        creditsSummary.removeAll();
        Map<Integer, JSONObject> credits = connection.getCredits(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<Integer, JSONObject> credit: credits.entrySet()) {
            CreditPanel creditPanel = new CreditPanel(creditsSummary, this, credit.getKey(), credit.getValue());
            creditsSummary.add(creditPanel);
        }
        creditsSummary.updateUI();
    }
    private void updateCreditsBalance() {
        if (transfer_accountSelectBox.getItemCount() > 0) {
            active_payer_account = connection.getAccount(login.getLogin(), login.getPasswordHash(), accountBoxUnformatted.get(transfer_accountSelectBox.getSelectedIndex()));
            String active_currency_shortcut = currencies.get("" + active_payer_account.getCurrencyID());
            int credits_total = connection.getTotalCredits(login.getLogin(), login.getPasswordHash(), active_payer_account.getCurrencyID());

            creditsBalance.setText(String.format("%.2f %s", credits_total / 100.0, active_currency_shortcut));
        }
    }
    private void refreshFrame() {
        Dimension dimension = frame.getSize();
        frame.setSize(dimension.width, dimension.height + 10);
        frame.setSize(dimension);
    }
}