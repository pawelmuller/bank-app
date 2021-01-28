package com.bllk.Apka;

import com.bllk.Apka.customComponents.*;
import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;
import com.bllk.Mapclasses.Account;
import com.bllk.Mapclasses.Client;
import com.bllk.Mapclasses.Login;
import com.google.common.base.CharMatcher;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class MainUserPage {
    static JFrame frame = StartWindow.frame;
    static ClientServerConnection connection = StartWindow.connection;
    JPanel currentPanel;

    Client client;
    static Login login;

    private JLabel logoLabel, nameLabel;
    private JTextField transfer_amount;
    private JButton transfer_sendMoneyButton, logOutButton;
    private JLabel transfer_currentBalance;
    private JLabel idLabel;
    private JPanel transactionPanel;
    private JTabbedPane tabbedPane;
    private JTextField transfer_title;
    private JComboBox<String> transfer_accountSelectBox;
    private JLabel transfer_payerBalance;
    private JLabel transfer_currencyLabel;
    private JScrollPane historyPane;
    private JButton createAccountButton;
    private JComboBox<String> transfer_contactBox;
    private JTextField transfer_accountNumber;
    private JButton transfer_addContactButton;
    private JPanel accountsSummaryPanel;
    private JPanel contactsSummary;
    private JLabel creditsBalance;
    private JPanel investmentsSummaryPanel;
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
    private JPanel creditsSummaryPanel;
    private JPanel creditsBalancePanel;
    private JPanel investmentsPanel;
    private JPanel creditsPanel;
    private JButton settings_changeLoginButton;
    private JButton settings_changePasswordButton;
    private JTextField settings_newLoginField;
    private JLabel settings_loginPasswordLabel;
    private JLabel creditsBalanceLabel;
    private JPanel historyPanel;
    private JScrollPane accountsSummaryPane;
    private JScrollPane creditsSummaryPane;
    private JScrollPane investmentsSummaryPane;

    List<Integer> userCurrencies = new ArrayList<>();
    List<Integer> accountBoxUnformatted = new ArrayList<>();
    Account activePayerAccount = null;
    Map <String, Integer> contacts;
    private static Map <Integer, JSONObject> accounts;
    private static Map <String, String> currencies;
    boolean lockComboBox = false;

    public MainUserPage(Client _client, Login _login) {
        client = _client;
        login = _login;
        nameLabel.setText("Witaj " + client.getName() + "!");
        idLabel.setText("Numer klienta: " + client.getID());
        settings_newLoginField.setText(login.getLogin());
        currencies = connection.getCurrencies();
        accounts = connection.getUserAccounts(login.getLogin(), login.getPasswordHash());

        transfer_contactBox.setEditor(new ColorableComboBoxEditor(Colors.getBrightGrey()));

        accountsSummaryPanel.setLayout(new GridBagLayout());
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        contactsSummary.setLayout(new BoxLayout(contactsSummary, BoxLayout.Y_AXIS));
        investmentsSummaryPanel.setLayout(new GridBagLayout());
        creditsSummaryPanel.setLayout(new GridBagLayout());

        updateFontsAndColors();
        updateAll();

        transfer_sendMoneyButton.addActionListener(e -> makeTransaction());
        logOutButton.addActionListener(e -> {
            StartWindow.startingPanel.setSize(currentPanel.getSize());
            frame.setContentPane(StartWindow.startingPanel);
        });
        transfer_accountSelectBox.addActionListener(e -> updateMoney());
        transfer_addContactButton.addActionListener(e -> {
            addContact();
            updateContacts();
            updateContactsSummary();
        });
        transfer_contactBox.addActionListener(e -> {
            if (!lockComboBox) {
                String name = (String) transfer_contactBox.getSelectedItem();
                Integer accountID = contacts.get(name);
                if (accountID != null)
                    transfer_accountNumber.setText("" + accountID);
            }
        });
        createAccountButton.addActionListener(e -> createAccountDialog());
        createInvestmentButton.addActionListener(e -> addInvestmentDialog());
        createCreditButton.addActionListener(e -> addCreditDialog());
        settings_changeLoginButton.addActionListener(e -> changeLoginDialog());
        settings_changePasswordButton.addActionListener(e -> changePasswordDialog());
        logoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Thread(() -> updateAll()).start();
            }
        });
    }

    void createAccountDialog() {
        JComboBox<String> currenciesComboBox = new JComboBox<>();

        for (Map.Entry<String, String> currency: currencies.entrySet()) {
            if (!userCurrencies.contains(Integer.parseInt(currency.getKey()))) {
                currenciesComboBox.addItem(currency.getValue());
            }
        }

        currenciesComboBox.setFont(Fonts.getStandardFont());
        currenciesComboBox.setBackground(Colors.getGrey());
        currenciesComboBox.setForeground(Colors.getOrange());

        Object[] message = {
                "Wybierz walutę", currenciesComboBox
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Dodawanie nowego konta", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            for (Map.Entry<String, String> entry : currencies.entrySet()) {
                if (Objects.equals(currenciesComboBox.getSelectedItem(), entry.getValue())) {
                    int currencyID = Integer.parseInt(entry.getKey());
                    connection.createAccount(login.getLogin(), login.getPasswordHash(), currencyID);
                }
            }
        }
        new Thread(this::updateAccounts).start();
    }
    void addInvestmentDialog() {
        JTextField name = new JTextField();
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accountsToSelect = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account: accounts.entrySet()) {
            accountBox.addItem(String.format("%s (%.2f %s)", getContactIfPossible(account.getKey()),
                    account.getValue().getDouble("value") / 100,
                    currencies.get(account.getValue().getString("currencyid"))));
            accountsToSelect.add(account.getKey());
        }
        JTextField value = new JTextField();
        value.setText("1000");

        JLabel profitRateValue = new JLabel("Oprocentowanie: 5,0%");
        JLabel yearProfitRateValue = new JLabel("Oprocentowanie roczne: 5,0%");
        JSlider profitRate = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        JSlider yearProfitRate = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);

        accountBox.setFont(Fonts.getStandardFont());
        accountBox.setBackground(Colors.getGrey());
        accountBox.setForeground(Colors.getOrange());
        value.setBackground(Colors.getBrightGrey());
        name.setBackground(Colors.getBrightGrey());
        profitRateValue.setFont(Fonts.getStandardFont());
        profitRateValue.setForeground(Colors.getBrightTextColor());
        yearProfitRateValue.setFont(Fonts.getStandardFont());
        yearProfitRateValue.setForeground(Colors.getBrightTextColor());

        profitRate.addChangeListener(e -> profitRateValue.setText("Oprocentowanie: " + String.format("%.1f", profitRate.getValue() / 10f) + "%"));
        yearProfitRate.addChangeListener(e -> yearProfitRateValue.setText("Oprocentowanie roczne: " + String.format("%.1f", yearProfitRate.getValue() / 10f) + "%"));

        Hashtable<Integer, JLabel> slidersLabelTable = new Hashtable<>();

        slidersLabelTable.put(2, new BetterJLabel("0%", Colors.getBrightTextColor(), Fonts.getStandardFont()) );
        slidersLabelTable.put(22, new BetterJLabel("2%", Colors.getBrightTextColor(), Fonts.getStandardFont()) );
        slidersLabelTable.put(42, new BetterJLabel("4%", Colors.getBrightTextColor(), Fonts.getStandardFont()) );
        slidersLabelTable.put(62, new BetterJLabel("6%", Colors.getBrightTextColor(), Fonts.getStandardFont()) );
        slidersLabelTable.put(82, new BetterJLabel("8%", Colors.getBrightTextColor(), Fonts.getStandardFont()) );
        slidersLabelTable.put(100, new BetterJLabel("10%", Colors.getBrightTextColor(), Fonts.getStandardFont()) );

        profitRate.setMajorTickSpacing(10);
        profitRate.setMinorTickSpacing(5);
        profitRate.setPaintTicks(true);
        profitRate.setLabelTable(slidersLabelTable);
        profitRate.setPaintLabels(true);
        profitRate.setBackground(Colors.getDarkGrey());

        yearProfitRate.setMajorTickSpacing(10);
        yearProfitRate.setMinorTickSpacing(5);
        yearProfitRate.setPaintTicks(true);
        yearProfitRate.setLabelTable(slidersLabelTable);
        yearProfitRate.setPaintLabels(true);
        yearProfitRate.setBackground(Colors.getDarkGrey());

        List<String> monthsList = new ArrayList<>();
        for (int i = 1; i <= 120; i++)
            monthsList.add(i + "");
        SpinnerListModel monthsModel = new SpinnerListModel(monthsList.toArray());
        JSpinner months = new JSpinner(monthsModel);
        ((JSpinner.ListEditor) months.getEditor()).getTextField().setBackground(Colors.getBrightGrey());
        months.setValue("24");

        Object[] message = {
                "Nazwa:", name,
                "Z konta:", accountBox,
                "Kwota początkowa", value,
                profitRateValue, profitRate,
                yearProfitRateValue, yearProfitRate,
                "Okres kapitalizacji [mies.]", months,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Nowa lokata", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            class WrongAmountException extends Exception
            { public WrongAmountException() {} }
            if (name.getText().isEmpty() || value.getText().isEmpty())
                JOptionPane.showMessageDialog(null,"Pole nie może być puste.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else {
                try {
                    long amount = Math.round(Double.parseDouble(value.getText().replace(",",".")) * 100);
                    if (amount < 10000 || amount > 7000000000L)
                        throw new WrongAmountException();

                    if (accounts.get(accountsToSelect.get(accountBox.getSelectedIndex())).getLong("value") < amount)
                        JOptionPane.showMessageDialog(null,"Nie posiadasz tyle pieniędzy.", "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
                    else {
                        connection.createInvestment(
                                login.getLogin(),
                                login.getPasswordHash(),
                                name.getText().substring(0, Math.min(name.getText().length(), 100)),
                                amount,
                                profitRate.getValue() / 1000.0,
                                yearProfitRate.getValue() / 1000.0,
                                Integer.parseInt((String) months.getValue()),
                                accountsToSelect.get(accountBox.getSelectedIndex()));
                        new Thread(() -> {
                            updateInvestmentsSummary();
                            updateAccounts();
                        }).start();
                        JOptionPane.showMessageDialog(null,"Operacja powiodła się.","Sukces", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (WrongAmountException e) {
                    JOptionPane.showMessageDialog(null,"Błędna kwota inwestycji.\nKwota powinna zawierać się w przedziale od 100 do 70 000 000.","Błędna kwota inwestycji", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,"Wprowadzona wartość inwestycji nie jest liczbą.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    void addCreditDialog() {
        JTextField name = new JTextField();
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accountsToSelect = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account : accounts.entrySet()) {
            accountBox.addItem(String.format("%s (%.2f %s)", getContactIfPossible(account.getKey()), account.getValue().getDouble("value") / 100, currencies.get(account.getValue().getString("currencyid"))));
            accountsToSelect.add(account.getKey());
        }
        JTextField value = new JTextField();
        value.setText("1000");
        JLabel interestValue = new JLabel("Oprocentowanie: 5,0%");
        JLabel commissionValue = new JLabel("Prowizja: 5,0%");

        JSlider interestRate = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        JSlider commission = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);

        accountBox.setFont(Fonts.getStandardFont());
        accountBox.setBackground(Colors.getGrey());
        accountBox.setForeground(Colors.getOrange());
        value.setBackground(Colors.getBrightGrey());
        name.setBackground(Colors.getBrightGrey());
        interestValue.setFont(Fonts.getStandardFont());
        interestValue.setForeground(Colors.getBrightTextColor());
        commissionValue.setFont(Fonts.getStandardFont());
        commissionValue.setForeground(Colors.getBrightTextColor());

        ChangeListener interestChangeListener = e -> {
            JSlider slider = (JSlider)e.getSource();
            interestValue.setText("Oprocentowanie: " + String.format("%.1f", slider.getValue() / 10f) + "%");
            };
        ChangeListener commissionChangeListener = e -> {
            JSlider slider = (JSlider)e.getSource();
            commissionValue.setText("Prowizja: " + String.format("%.1f", slider.getValue() / 10f) + "%");
        };

        commission.addChangeListener(commissionChangeListener);
        commission.setMajorTickSpacing(10);
        commission.setMinorTickSpacing(5);
        commission.setPaintTicks(true);
        commission.setPaintLabels(true);
        commission.setBackground(Colors.getDarkGrey());

        interestRate.addChangeListener(interestChangeListener);
        interestRate.setMajorTickSpacing(10);
        interestRate.setMinorTickSpacing(5);
        interestRate.setPaintTicks(true);
        interestRate.setPaintLabels(true);
        interestRate.setBackground(Colors.getDarkGrey());

        //Create the label table
        Hashtable<Integer, JLabel> slidersLabelTable = new Hashtable<>();
        slidersLabelTable.put(2, new BetterJLabel("0%", Colors.getBrightTextColor(), Fonts.getStandardFont()) );
        slidersLabelTable.put(22, new BetterJLabel("2%", Colors.getBrightTextColor(), Fonts.getStandardFont()) );
        slidersLabelTable.put(42, new BetterJLabel("4%", Colors.getBrightTextColor(), Fonts.getStandardFont()) );
        slidersLabelTable.put(62, new BetterJLabel("6%", Colors.getBrightTextColor(), Fonts.getStandardFont()) );
        slidersLabelTable.put(82, new BetterJLabel("8%", Colors.getBrightTextColor(), Fonts.getStandardFont()) );
        slidersLabelTable.put(100, new BetterJLabel("10%", Colors.getBrightTextColor(), Fonts.getStandardFont()) );

        commission.setLabelTable( slidersLabelTable );
        commission.setPaintLabels(true);
        interestRate.setLabelTable( slidersLabelTable );
        interestRate.setPaintLabels(true);

        List<String> monthsList = new ArrayList<>();
        for (int i = 1; i <= 120; i++)
            monthsList.add(i + "");
        SpinnerListModel monthsModel = new SpinnerListModel(monthsList.toArray());
        JSpinner months = new JSpinner(monthsModel);
        ((JSpinner.ListEditor) months.getEditor()).getTextField().setBackground(Colors.getBrightGrey());
        months.setValue("24");

        Object[] message = {
                "Nazwa:", name,
                "Na konto:", accountBox,
                "Kwota początkowa", value,
                interestValue, interestRate,
                commissionValue, commission,
                "Całkowity okres spłaty [mies.]", months,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Nowy kredyt", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            class WrongCreditNameException extends Exception
            { public WrongCreditNameException() {} }
            class WrongAmountException extends Exception
            { public WrongAmountException() {} }

            try {
                if (name.getText().isEmpty())
                    throw new WrongCreditNameException();
                long amount = Math.round(Double.parseDouble(value.getText().replace(",", ".")) * 100);
                if (amount < 10000 || amount > 7000000000L)
                    throw new WrongAmountException();
                connection.createCredit(
                        login.getLogin(),
                        login.getPasswordHash(),
                        name.getText().substring(0, Math.min(name.getText().length(), 100)),
                        amount,
                        interestRate.getValue() / 1000.0,
                        commission.getValue() / 1000.0,
                        Integer.parseInt((String) months.getValue()),
                        accountsToSelect.get(accountBox.getSelectedIndex())
                );
                new Thread(() -> {
                    updateCreditsSummary();
                    updateAccounts();
                }).start();
                JOptionPane.showMessageDialog(null, "Operacja powiodła się.", "Sukces", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,"Błędna wartość liczbowa jednego z parametrów.\nSprawdź, czy poprawnie wprowadziłeś kwotę kredytu.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            } catch (WrongCreditNameException e) {
                JOptionPane.showMessageDialog(null,"Błąd w nazwie kredytu.\nNazwa nie powinna pozostawać pusta.","Pusta nazwa kredytu", JOptionPane.ERROR_MESSAGE);
            } catch (WrongAmountException e) {
                JOptionPane.showMessageDialog(null,"Błąd kwoty kredytu.\nKwota kredytu powinna zawierać się od 100 do 70 000 000.","Błąd kwoty kredytu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    void changeLoginDialog() {
        JTextField newName = new JTextField();

        newName.setBackground(Colors.getBrightGrey());

        Object[] message = {
                "Czy chcesz zmienić login '" + login.getLogin() + "'?",
                "Nowy login:", newName
        };

        int option = JOptionPane.showConfirmDialog(null, message,
                "Zmiana loginu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String newNameString = newName.getText();
            if (newNameString.isEmpty())
                JOptionPane.showMessageDialog(null,"Pole nie może być puste.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (!CharMatcher.ascii().matchesAllOf(newNameString))
                JOptionPane.showMessageDialog(null,"Login może składać się tylko ze znaków ASCII.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (connection.checkLogin(newNameString))
                JOptionPane.showMessageDialog(null,"Login jest zajęty.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else {
                if (connection.updateLogin(login.getLogin(), login.getPasswordHash(), newNameString)) {
                    JOptionPane.showMessageDialog(null,"Zmiana loginu powiodła się.","Sukces", JOptionPane.INFORMATION_MESSAGE);
                    settings_newLoginField.setText(newNameString);
                }
                else
                    JOptionPane.showMessageDialog(null,"Serwer odrzucił żądanie","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    void changePasswordDialog() {
        JTextField newPassword = new JPasswordField();
        JTextField newPasswordRepeat = new JPasswordField();

        newPassword.setBackground(Colors.getBrightGrey());
        newPasswordRepeat.setBackground(Colors.getBrightGrey());

        Object[] message = {
                "Czy chcesz zmienić twoje hasło?",
                "Nowe hasło:", newPassword,
                "Powtórz hasło", newPasswordRepeat
        };

        int option = JOptionPane.showConfirmDialog(null, message,
                "Zmiana hasła", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String newPasswordString = newPassword.getText();
            String newPasswordRepeatString = newPasswordRepeat.getText();
            int passwordLength = newPasswordString.length();

            if (newPasswordString.isEmpty())
                JOptionPane.showMessageDialog(null,"Pole nie może być puste.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (passwordLength < StartWindow.passwordMinimumLength || passwordLength > StartWindow.passwordMaximumLength)
                JOptionPane.showMessageDialog(null,"Hasło musi mieć od 8 do 16 znaków.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (!newPasswordString.equals(newPasswordRepeatString))
                JOptionPane.showMessageDialog(null,"Hasła nie są identyczne.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else {
                String hashedPassword = BCrypt.hashpw(newPasswordString, BCrypt.gensalt(12));
                connection.updatePassword(login.getLogin(), hashedPassword);
                JOptionPane.showMessageDialog(null,"Zmiana hasła powiodła się.","Sukces", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    boolean currencyChangeWarning() {
        int n = JOptionPane.showConfirmDialog(
                frame,
                "Konto, na które zamierzasz wysłać przelew,\nzawiera inną walutę niż wysyłana. \n\nCzy chcesz przewalutować?",
                "Przewalutowanie",
                JOptionPane.YES_NO_OPTION);
        return n==0;
    }

    void addContact() {
        try {
            int accountID = Integer.parseInt(transfer_accountNumber.getText());
            String name = (String) transfer_contactBox.getSelectedItem();

            if (name != null && !name.equals("")) {
                if (!connection.checkAccountExistence(Integer.parseInt(transfer_accountNumber.getText())))
                    throw new NumberFormatException();
                connection.createOrUpdateContact(login.getLogin(), login.getPasswordHash(), name, accountID);
                JOptionPane.showMessageDialog(null,
                        String.format("Konto %d: %s", accountID, name),
                        "Utworzono kontakt",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch(NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    "Podano błędny numer konta.",
                    "Błąd dodawania kontaktu",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(ex.getMessage());
        } catch (InputMismatchException ex) {
            JOptionPane.showMessageDialog(null,
                    "Podano błędną nazwę.",
                    "Błąd dodawania kontaktu",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Błąd dodawania kontaktu",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(ex.getMessage());
        }
    }
    void makeTransaction() {
        try {
            if (activePayerAccount == null) {
                JOptionPane.showMessageDialog(null,
                        "Nie posiadasz żadnego konta.",
                        "Błąd transakcji",
                        JOptionPane.ERROR_MESSAGE);
            }
            else {
                int payerID = activePayerAccount.getID();
                String stringTargetID = transfer_accountNumber.getText();
                String stringAmount = transfer_amount.getText();

                if (stringTargetID.equals("")) {
                    JOptionPane.showMessageDialog(null,
                            "Nie podano konta docelowego.",
                            "Błąd transakcji",
                            JOptionPane.ERROR_MESSAGE);
                } else if (stringAmount.equals("")) {
                    JOptionPane.showMessageDialog(null,
                            "Nie podano kwoty przelewu.",
                            "Błąd transakcji",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    int targetID = Integer.parseInt(stringTargetID);
                    int currencyID = activePayerAccount.getCurrencyID();
                    long moneyValue = Math.round(Double.parseDouble(stringAmount.replace(",", ".")) * 100.0);
                    String title = transfer_title.getText();

                    if (activePayerAccount.getID() == targetID) {
                        JOptionPane.showMessageDialog(null,
                                "Konto docelowe jest takie samo jak początkowe.",
                                "Błąd transakcji",
                                JOptionPane.ERROR_MESSAGE);
                    } else if (moneyValue > activePayerAccount.getValue() || moneyValue <= 0) {
                        JOptionPane.showMessageDialog(null,
                                "Błędna kwota przelewu.",
                                "Błąd transakcji",
                                JOptionPane.ERROR_MESSAGE);
                    } else if (!connection.checkAccountExistence(targetID)) {
                        JOptionPane.showMessageDialog(null,
                                "Konto docelowe nie istnieje.",
                                "Błąd transakcji",
                                JOptionPane.ERROR_MESSAGE);
                    } else if (transfer_title.getText().equals("")) {
                        JOptionPane.showMessageDialog(null,
                                "Tytuł nie może być pusty.",
                                "Błąd transakcji",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (connection.getBasicAccount(targetID).getCurrencyID() == currencyID || (connection.getBasicAccount(targetID).getCurrencyID() != currencyID && currencyChangeWarning())) {
                            JOptionPane.showMessageDialog(null,
                                    String.format("Przesłano %.2f %s na konto %d.", moneyValue / 100.0, currencies.get("" + activePayerAccount.getCurrencyID()), targetID),
                                    "Wykonano przelew",
                                    JOptionPane.INFORMATION_MESSAGE);
                            new Thread(() -> {
                                connection.makeTransfer(login.getLogin(), login.getPasswordHash(), payerID, targetID, title, moneyValue, currencyID);
                                updateMoney();
                                updateTransactionTable();
                                updateAccountsSummary();
                            }).start();
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Błąd transakcji (exception)",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getContactIfPossible(int value) {
        for (Map.Entry<String, Integer> contact:contacts.entrySet())
            if (contact.getValue() == value)
                return contact.getKey();
        return String.valueOf(value);
    }
    public String getContactIfPossible(String value) {
        try {
            int value_parsed = Integer.parseInt(value);
            return getContactIfPossible(value_parsed);
        }
        catch (NumberFormatException ex) {
            return value;
        }
    }

    /** Updates everything. */
    private void updateAll() {
        updateContacts();
        updateAccounts();
        updateContactsSummary();
        updateCreditsBalance();
        updateInvestmentsSummary();
        updateCreditsSummary();
    }
    /** Updates fonts of labels and sets backgrounds of fields. */
    private void updateFontsAndColors() {
        Font standardFont = Fonts.getStandardFont();
        Font headerFont = Fonts.getHeaderFont();
        Font logoFont = Fonts.getLogoFont();


        // Main elements
        logoLabel.setFont(logoFont);
        logoLabel.setForeground(Colors.getOrange());

        nameLabel.setFont(headerFont);

        logOutButton.setFont(standardFont);

        creditsSummaryPane.setBackground(Colors.getDarkGrey());
        investmentsSummaryPane.setBackground(Colors.getDarkGrey());

        for (JLabel jLabel : Arrays.asList(idLabel, nameLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
        }

        for (JTabbedPane jTabbedPane : Arrays.asList(tabbedPane, financialProductsTabbedPane)) {
            jTabbedPane.setFont(standardFont);
            jTabbedPane.setForeground(Colors.getDarkGrey());
            jTabbedPane.setBackground(Colors.getBlue());
        }

        for (JPanel jPanel : Arrays.asList(accountsPanel, transactionPanel, transactionHistoryPanel,
                financialProductsPanel, contactsPanel, settingsPanel, investmentsPanel, creditsPanel, historyPanel,
                creditsBalancePanel)) {
            jPanel.setFont(standardFont);
            jPanel.setForeground(Colors.getBrightTextColor());
            jPanel.setBackground(Colors.getDarkGrey());
        }

        // Accounts
        createAccountButton.setFont(standardFont);
        accountsSummaryLabel.setFont(Fonts.getHeaderFont());
        accountsSummaryLabel.setForeground(Colors.getBrightTextColor());
        accountsSummaryPanel.setForeground(Colors.getBrightTextColor());
        accountsSummaryPanel.setBackground(Colors.getDarkGrey());
        accountsSummaryPane.setBackground(Colors.getDarkGrey());

        // Transfers
        for (JLabel jLabel : Arrays.asList(transfer_contactNameLabel, transfer_currencyLabel, transfer_currentBalance,
                transfer_currentBalanceLabel, transfer_fromLabel, transfer_payerBalance,
                transfer_titleLabel, transfer_toLabel, transfer_valueLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
            jLabel.setFont(standardFont);
        }
        for (JTextField jTextField : Arrays.asList(transfer_accountNumber, transfer_amount, transfer_title)) {
            jTextField.setForeground(Colors.getBrightTextColor());
            jTextField.setBackground(Colors.getBrightGrey());
            jTextField.setFont(standardFont);
        }
        for (JComboBox<String> jComboBox : Arrays.asList(transfer_contactBox, transfer_accountSelectBox)) {
            jComboBox.setForeground(Colors.getOrange());
            jComboBox.setBackground(Colors.getGrey());
            jComboBox.setFont(standardFont);
        }
        for (JButton jButton : Arrays.asList(transfer_addContactButton, transfer_sendMoneyButton)) {
            jButton.setFont(standardFont);
        }

        // Transfer history
        historyPane.setFont(standardFont);
        historyPane.setBackground(Colors.getDarkGrey());
        historyPane.getViewport().setBackground(Colors.getDarkGrey());

        // Settings
        settings_loginPasswordLabel.setFont(Fonts.getHeaderFont());
        settings_loginPasswordLabel.setForeground(Colors.getBrightTextColor());
        settings_newLoginField.setFont(standardFont);
        settings_changeLoginButton.setFont(standardFont);
        settings_changePasswordButton.setFont(standardFont);

        // Investments
        createInvestmentButton.setFont(standardFont);

        // Credits
        createCreditButton.setFont(standardFont);
        creditsBalance.setFont(Fonts.getHeaderFont());
        creditsBalance.setForeground(Colors.getBrightTextColor());
        creditsBalanceLabel.setFont(Fonts.getHeaderFont());
        creditsBalanceLabel.setForeground(Colors.getBrightTextColor());

        UIManager.put("OptionPane.background", Colors.getDarkGrey());
        UIManager.put("Panel.background", Colors.getDarkGrey());
        UIManager.put("OptionPane.messageForeground", Colors.getBrightTextColor());
        UIManager.put("OptionPane.messageFont", standardFont);

        String systemName = System.getProperty("os.name");
        if (!systemName.startsWith("Windows")) {
            tabbedPane.setForeground(Colors.getOrange());
            financialProductsTabbedPane.setForeground(Colors.getOrange());
        }
    }
    /** Updates contacts in transactions combobox.
     *  Runs updateTransactionTable()
     */
    public void updateContacts() {
        lockComboBox = true;
        contacts = connection.getContacts(login.getLogin(), login.getPasswordHash());
        contacts.put("(Konto usunięte)", -1);
        String temp = (String) transfer_contactBox.getSelectedItem();
        transfer_contactBox.removeAllItems();
        transfer_contactBox.addItem("");
        for (Map.Entry<String, Integer> contact: contacts.entrySet()) {
            if (contact.getValue() != -1)
                transfer_contactBox.addItem(contact.getKey());
        }
        transfer_contactBox.setSelectedItem(temp);
        updateTransactionTable();
        lockComboBox = false;
    }
    /** Updates transaction table. */
    private void updateTransactionTable() {
        historyPanel.removeAll();
        Map<Integer, JSONObject> transactions = connection.getTransactions(login.getLogin(), login.getPasswordHash());
        char type = 0;

        for (JSONObject transaction: transactions.values()) {
            String senderID_string = transaction.getString("senderid");
            String receiverID_string = transaction.getString("receiverid");
            int senderID, receiverID;

            if (senderID_string.equals("null"))
                senderID = -1;
            else
                senderID = Integer.parseInt(senderID_string);

            if (receiverID_string.equals("null"))
                receiverID = -1;
            else
                receiverID = Integer.parseInt(receiverID_string);

            if (accounts.containsKey(senderID) && !accounts.containsKey(receiverID)) {
                type = 0; // outgoing
            } else if (accounts.containsKey(receiverID) && !accounts.containsKey(senderID)) {
                type = 1; // incoming
            } else if (accounts.containsKey(senderID) && accounts.containsKey(receiverID)) {
                type = 2; // between own accounts
            } else {
                System.out.println("Something went terribly wrong.");
            }

            historyPanel.add(new TransactionPanel(getContactIfPossible(senderID),
                            getContactIfPossible(receiverID),
                            transaction.getString("date"),
                            transaction.getString("title"),
                            transaction.getLong("value"),
                            currencies.get(transaction.getString("currencyid")),
                            type
                            ));
            historyPanel.add(new Box.Filler(new Dimension(1, 1), new Dimension(100, 1), new Dimension(600, 1)));
        }
    }
    /** Updates money header in transaction screen. */
    public void updateMoney() {
        if (transfer_accountSelectBox.getItemCount() > 0 && accountBoxUnformatted.size() > 0) {
            activePayerAccount = connection.getAccount(login.getLogin(), login.getPasswordHash(), accountBoxUnformatted.get(transfer_accountSelectBox.getSelectedIndex()));
            if (transfer_accountSelectBox.getItemCount() > 0) {
                activePayerAccount = connection.getAccount(login.getLogin(), login.getPasswordHash(), accountBoxUnformatted.get(transfer_accountSelectBox.getSelectedIndex()));
                String activeCurrencyShortcut = currencies.get("" + activePayerAccount.getCurrencyID());
                long totalBalance = connection.getTotalSavings(login.getLogin(), login.getPasswordHash(), activePayerAccount.getCurrencyID());

                transfer_currentBalance.setText(String.format("%.2f %s", totalBalance / 100.0, activeCurrencyShortcut));
                transfer_payerBalance.setText(String.format("%.2f %s", activePayerAccount.getValue() / 100.0, activeCurrencyShortcut));
                transfer_currencyLabel.setText(activeCurrencyShortcut);
            }
        }
    }
    /** Updates accounts in transaction combobox.
     *  Runs updateMoney() and updateAccountsSummary()
     */
    public void updateAccounts() {
        transfer_accountSelectBox.removeAllItems();
        userCurrencies.clear();
        accounts = connection.getUserAccounts(login.getLogin(), login.getPasswordHash());
        for (Map.Entry<Integer, JSONObject> account: accounts.entrySet()) {
            transfer_accountSelectBox.addItem(String.format("%s (%s)",
                    getContactIfPossible(account.getKey()),
                    currencies.get(account.getValue().getString("currencyid")))
            );
            accountBoxUnformatted.add(account.getKey());
            userCurrencies.add(account.getValue().getInt("currencyid"));
        }

        tabbedPane.setEnabledAt(1, transfer_accountSelectBox.getItemCount() != 0);
        tabbedPane.setEnabledAt(2, transfer_accountSelectBox.getItemCount() != 0);
        tabbedPane.setEnabledAt(3, transfer_accountSelectBox.getItemCount() != 0);
        tabbedPane.setEnabledAt(4, transfer_accountSelectBox.getItemCount() != 0);
        updateMoney();
        updateAccountsSummary();
    }
    public void updateAccountsSummary() {
        accountsSummaryPanel.removeAll();
        int column = 0, row = 1, counter = 1, accountsCount = accounts.size();
        boolean canBeDeleted;

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.NONE;
        c.weightx = 1;
        c.weighty = 0.1;
        c.gridwidth = 1;

        c.gridy = 0;
        if (accountsCount != 0) {
            for (int i = 0; i < 6; i++) {
                JLabel nothing = new JLabel("");
                nothing.setMaximumSize(new Dimension(1, 1));
                c.gridx = i;
                accountsSummaryPanel.add(nothing, c);
            }
        } else {
            c.gridx = 0;
            JLabel noAccountInformation = new JLabel("Nie masz żadnego konta. Możesz dodać je poniżej.");
            noAccountInformation.setFont(Fonts.getStandardFont());
            noAccountInformation.setForeground(Colors.getBrightTextColor());
            accountsSummaryPanel.add(noAccountInformation, c);
        }

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 1;


        Map<Integer, JSONObject> credits = connection.getCredits(login.getLogin(), login.getPasswordHash());
        Map<Integer, JSONObject> investments = connection.getInvestments(login.getLogin(), login.getPasswordHash());
        Map<Integer, JSONObject> accounts = connection.getUserAccounts(login.getLogin(), login.getPasswordHash());
        List<String> currenciesUsed = new ArrayList<>();

        for (Map.Entry<Integer, JSONObject> credit : credits.entrySet()) {
            JSONObject values = credit.getValue();
            currenciesUsed.add(values.getString("currencyid"));
        }
        for (Map.Entry<Integer, JSONObject> investment : investments.entrySet()) {
            JSONObject values = investment.getValue();
            currenciesUsed.add(values.getString("currencyid"));
        }

        c.gridwidth = 2;
        for (Map.Entry<Integer, JSONObject> account: accounts.entrySet()) {
            String currencyID = account.getValue().getString("currencyid");
            String currencyName = currencies.get(currencyID);
            long balance = account.getValue().getLong("value");
            String formattedBalance = String.format("%.2f", balance/100.0);

            canBeDeleted = !currenciesUsed.contains(currencyID);

            AccountPanel accountPanel = new AccountPanel(getContactIfPossible(account.getKey()),
                    "" + account.getKey(), formattedBalance, currencyName, this, canBeDeleted);

            if (counter == accountsCount - 1) {
                if (accountsCount % 3 == 2) column = 1;
            } else if (counter == accountsCount) {
                if (accountsCount % 3 == 1) column = 2;
            }

            counter++;
            c.gridx = column;
            c.gridy = row;
            column += 2;
            if (column % 6 == 0) {
                column = 0;
                row++;
            }
            accountsSummaryPanel.add(accountPanel, c);
        }
        accountsSummaryPanel.updateUI();
    }
    public void updateContactsSummary() {
        contactsSummary.removeAll();
        Map<String, Integer> contacts = connection.getContacts(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<String, Integer> contact: contacts.entrySet()) {
            if (!accounts.containsKey(contact.getValue()) && contact.getValue() != -1) {
                ContactPanel contactPanel = new ContactPanel(contactsSummary, this, contact.getValue(), contact.getKey());
                contactsSummary.add(contactPanel);
                contactsSummary.add(new Box.Filler(new Dimension(1, 5), new Dimension(100, 5), new Dimension(600, 5)));

            }
        }
    }
    public void updateInvestmentsSummary() {
        Map<Integer, JSONObject> investments = connection.getInvestments(login.getLogin(), login.getPasswordHash());
        updateFinancialProductSummary(investmentsSummaryPanel, investments, (char) 0);
    }
    public void updateCreditsSummary() {
        Map<Integer, JSONObject> credits = connection.getCredits(login.getLogin(), login.getPasswordHash());

        updateFinancialProductSummary(creditsSummaryPanel, credits, (char) 1);
        updateCreditsBalance();
    }
    private void updateFinancialProductSummary(JPanel panelToUpdate, Map<Integer, JSONObject> mapOfProducts, char productType) {
        int column = 0, row = 1, counter = 1, productsCount = mapOfProducts.size();
        panelToUpdate.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0.1;
        c.gridwidth = 1;

        c.gridy = 0;

        if (productsCount != 0) {
            for (int i = 0; i < 4; i++) {
                JLabel nothing = new JLabel("");
                nothing.setMaximumSize(new Dimension(1, 1));
                c.gridx = i;
                panelToUpdate.add(nothing, c);
            }
        } else {
            JLabel noAccountInformation;
            c.gridx = 0;
            if (productType == 0) {
                noAccountInformation = new JLabel("Nie masz żadnej lokaty. Możesz dodać ją poniżej.");
            } else if (productType == 1) {
                noAccountInformation = new JLabel("Nie masz żadnego kredytu. Możesz dodać go poniżej.");
            } else {
                noAccountInformation = new JLabel("Nie masz żadnego produktu. Możesz dodać go poniżej.");
            }
            noAccountInformation.setFont(Fonts.getStandardFont());
            noAccountInformation.setForeground(Colors.getBrightTextColor());
            panelToUpdate.add(noAccountInformation, c);
        }

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        c.gridwidth = 2;


        for (Map.Entry<Integer, JSONObject> product: mapOfProducts.entrySet()) {
            c.gridx = column % 2;
            c.gridy = row;

            JPanel productPanel;

            if (productType == 0) {
                productPanel = new InvestmentPanel(this, product.getKey(), product.getValue());
            } else if (productType == 1) {
                productPanel = new CreditPanel(this, product.getKey(), product.getValue());
            } else {
                productPanel = new JPanel();
                productPanel.add(new JLabel("Błąd."));
            }

            if (counter == productsCount) {
                if (productsCount % 2 == 1) column = 1;
            }

            counter++;
            c.gridx = column;
            c.gridy = row;
            column += 2;
            if (column % 4 == 0) {
                column = 0;
                row++;
            }
            panelToUpdate.add(productPanel, c);
        }
        panelToUpdate.updateUI();
    }
    private void updateCreditsBalance() {
        if (transfer_accountSelectBox.getItemCount() > 0) {
            activePayerAccount = connection.getAccount(login.getLogin(), login.getPasswordHash(), accountBoxUnformatted.get(transfer_accountSelectBox.getSelectedIndex()));
            String activeCurrencyShortcut = currencies.get("" + activePayerAccount.getCurrencyID());
            long creditsTotal = connection.getTotalCredits(login.getLogin(), login.getPasswordHash(), activePayerAccount.getCurrencyID());

            creditsBalance.setText(String.format("%.2f %s", creditsTotal / 100.0, activeCurrencyShortcut));
        }
    }

    public static ClientServerConnection getConnection() {
        return connection;
    }
    public static Login getLogin() {
        return login;
    }
    public static Map <String, String> getCurrencies() {
        return currencies;
    }
    public static Map <Integer, JSONObject> getAccounts() {
        return accounts;
    }
}