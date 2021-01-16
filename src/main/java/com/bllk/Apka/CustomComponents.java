package com.bllk.Apka;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class Colors {
    private static Color bright_text_color, orange, blue, dark_grey, bright_grey, grey, light_grey;
    public Colors() {
        bright_text_color = Color.decode("#EEEEEE");
        blue = Color.decode("#1891FF");
        orange = Color.decode("#FF7F00");
        dark_grey = Color.decode("#222222");
        grey = Color.decode("#333333");
        light_grey = Color.decode("#444444");
        bright_grey = Color.decode("#808080");
    }
    public static Color getBrightTextColor() {
        return bright_text_color;
    }
    public static Color getOrange() {
        return orange;
    }
    public static Color getBlue() {
        return blue;
    }
    public static Color getDarkGrey() {
        return dark_grey;
    }
    public static Color getGrey() {
        return grey;
    }
    public static Color getLightGrey() {
        return  light_grey;
    }
    public static Color getBrightGrey() {
        return bright_grey;
    }
}

class Fonts {
    private static Font radikal, adagio_slab;

    public Fonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        radikal = loadFont("radikalwut-bold.otf");
        adagio_slab = loadFont("adagio_slab-regular.otf");
        ge.registerFont(radikal);
        ge.registerFont(adagio_slab);
    }
    private Font loadFont(String font_name) {
        String path = "/fonts/" + font_name;
        Font font = new Font("Veranda", Font.PLAIN, 14);
        try {
            InputStream stream = Fonts.class.getResourceAsStream(path);
            font = Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (IOException | FontFormatException e) {
            System.out.println("Have not found font file. Using default Veranda.");
        }
        return font;
    }
    public static Font getStandardFont() {
        return adagio_slab.deriveFont(14f);
    }
    public static Font getSmallHeaderFont() {
        return adagio_slab.deriveFont(16f);
    }
    public static Font getHeaderFont() {
        return adagio_slab.deriveFont(20f);
    }
    public static Font getLogoFont() {
        return radikal.deriveFont(48f);
    }
}






class AccountPanel extends JPanel {
    private String account_name;
    private final String account_number;
    MainUserPage page;

    public AccountPanel(String _account_name, String _account_number, String _balance, String _currency, MainUserPage _page) {
        super();
        page = _page;
        account_number = _account_number;
        account_name = _account_name;

        if (_account_name.equals(_account_number))
            account_name = "Konto";

        JLabel balanceLabel = new JLabel(_balance);
        JLabel currencyLabel = new JLabel(_currency);

        JButton deleteAccountButton = new JButton("Usuń konto");
        JButton renameAccountButton = new JButton("Zmień nazwę");

        balanceLabel.setHorizontalAlignment(JLabel.RIGHT);
        currencyLabel.setHorizontalAlignment(JLabel.LEFT);

        balanceLabel.setForeground(Colors.getBrightTextColor());
        currencyLabel.setForeground(Colors.getBrightTextColor());

        balanceLabel.setFont(Fonts.getStandardFont());
        currencyLabel.setFont(Fonts.getStandardFont());
        deleteAccountButton.setFont(Fonts.getStandardFont());
        renameAccountButton.setFont(Fonts.getStandardFont());

        deleteAccountButton.setEnabled(false);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        this.setBackground(Colors.getGrey());
        this.setPreferredSize(new Dimension(150, 100));
        this.setMinimumSize(new Dimension(150, 100));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;

        c.gridy = 0;
        c.gridx = 0;
        c.insets = new Insets(0, 0, 0, 2);
        this.add(balanceLabel, c);
        c.gridx = 1;
        c.insets = new Insets(0, 2, 0, 0);
        this.add(currencyLabel, c);


        c.insets = new Insets(0, 0, 0, 0);
        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 2;
        this.add(renameAccountButton, c);
        //c.gridx = 1;
        //this.add(deleteAccountButton, c);

        this.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                account_name + " (nr " + account_number + ")",
                TitledBorder.CENTER,
                TitledBorder.DEFAULT_POSITION,
                Fonts.getStandardFont(),
                Colors.getBrightTextColor()
                ));

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Colors.getBlue(), 3, true),
                        account_name + " (nr " + account_number + ")",
                        TitledBorder.CENTER,
                        TitledBorder.DEFAULT_POSITION,
                        Fonts.getStandardFont(),
                        Colors.getBrightTextColor()
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                        account_name + " (nr " + account_number + ")",
                        TitledBorder.CENTER,
                        TitledBorder.DEFAULT_POSITION,
                        Fonts.getStandardFont(),
                        Colors.getBrightTextColor()
                ));
            }
        });
        deleteAccountButton.addActionListener(e -> deleteAccount());
        renameAccountButton.addActionListener(e -> {
            renameAccount();
            page.updateContacts();
            page.updateAccounts();
            page.updateInvestmentsSummary();
            page.updateAccountsSummary();
            page.updateMoney();
        });
    }
    private void deleteAccount() {
        System.out.println("Not implemented yet.");
    }
    private void renameAccount() {
        JTextField new_name = new JTextField();
        String new_name_string;

        Object[] message = {
                "Chcesz zmienić nazwę konta '" + account_name + "'",
                "Nowa nazwa:", new_name
        };

        int option = JOptionPane.showConfirmDialog(null, message,
                "Zmiana nazwy konta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            new_name_string = new_name.getText();
            if (new_name_string.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Pole nazwy nie może być puste.",
                        "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            } else {
                MainUserPage.connection.createOrUpdateContact(page.login.getLogin(), page.login.getPasswordHash(), new_name_string, Integer.parseInt(account_number));
            }
        }
    }
}

class ContactPanel extends JPanel {
    public ContactPanel(JPanel parent, MainUserPage page, int target_id, String name) {
        super();

        this.setLayout(new FlowLayout());
        this.setBackground(Colors.getGrey());
        this.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                name + " (nr " + target_id + ")",
                TitledBorder.CENTER,
                TitledBorder.DEFAULT_POSITION,
                Fonts.getStandardFont(),
                Colors.getBrightTextColor()
        ));

        JLabel nameLabel = new JLabel(name);
        JLabel targetidLabel = new JLabel("" + target_id);
        JButton deleteButton = new JButton("Usuń");

        nameLabel.setForeground(Colors.getBrightTextColor());
        targetidLabel.setForeground(Colors.getBrightTextColor());

        nameLabel.setFont(Fonts.getStandardFont());
        targetidLabel.setFont(Fonts.getStandardFont());
        deleteButton.setFont(Fonts.getStandardFont());

        deleteButton.addActionListener(e -> {
            MainUserPage.connection.removeContact(page.login.getLogin(), page.login.getPasswordHash(), target_id);
            page.updateContacts();
            parent.remove(this);
            parent.updateUI();
        });
        this.add(nameLabel);
        this.add(targetidLabel);
        this.add(deleteButton);
    }
}

class InvestmentPanel extends JPanel {
    MainUserPage page;
    JSONObject inv;

    public InvestmentPanel(MainUserPage _page, int _id, JSONObject _inv) {
        super();
        page = _page;
        inv = _inv;

        JLabel nameLabel = new JLabel(inv.getString("name"));
        JLabel valueLabel = new JLabel("" + inv.getDouble("value") / 100);
        JLabel currencyLabel = new JLabel(page.currencies.get(inv.getString("currencyid")));
        JLabel profitLabel = new JLabel(inv.getString("profit"));
        JLabel yearprofit = new JLabel(inv.getString("yearprofit"));
        JLabel capperiod = new JLabel(inv.getString("capperiod"));
        JLabel datecreated = new JLabel(inv.getString("datecreated"));
        JButton endbutton = new JButton("Zamknij lokatę");

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (JLabel jLabel : Arrays.asList(nameLabel, yearprofit, capperiod, datecreated, valueLabel, currencyLabel, profitLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
            jLabel.setFont(Fonts.getStandardFont());
        }
        endbutton.setFont(Fonts.getStandardFont());
        datecreated.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.setBackground(Colors.getGrey());
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createLineBorder(Colors.getOrange(), 3, true));
        this.setMaximumSize(new Dimension(200, 200));
        this.setPreferredSize(new Dimension(150, -1));

        this.add(nameLabel);
        JPanel p1 = new JPanel();
        p1.setBackground(Colors.getGrey());
        p1.add(valueLabel);
        p1.add(currencyLabel);
        this.add(p1);
        JPanel p2 = new JPanel();
        p2.setBackground(Colors.getGrey());
        p2.add(profitLabel);
        p2.add(yearprofit);
        p2.add(capperiod);
        this.add(p2);
        this.add(datecreated);

        endbutton.addActionListener(e -> {
            int accountid = removeInvestmentDialog();
            System.out.println(accountid);
            MainUserPage.connection.removeInvestment(page.login.getLogin(), page.login.getPasswordHash(), _id, accountid);
            page.updateInvestmentsSummary();
            page.updateAccounts();
        });

        if (inv.has("dateended")) {
            JLabel dateendedLabel = new JLabel(inv.getString("dateended"));
            dateendedLabel.setForeground(Colors.getBrightTextColor());
            dateendedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.add(dateendedLabel);
        }
        endbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(endbutton);
    }
    int removeInvestmentDialog() {
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accounts_to_select = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account : page.accounts.entrySet()) {
            if (account.getValue().getInt("currencyid") == inv.getInt("currencyid")) {
                accountBox.addItem(String.format("%s (%.2f %s)", page.getContactIfPossible(account.getKey()), account.getValue().getDouble("value") / 100, page.currencies.get(account.getValue().getString("currencyid"))));
                accounts_to_select.add(account.getKey());
            }
        }

        Object[] message = {
                "Na konto:", accountBox,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Nowa lokata", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            return accounts_to_select.get(accountBox.getSelectedIndex());
        }
        return -1;
    }
}

class CreditPanel extends JPanel {
    MainUserPage page;
    JSONObject inv;

    public CreditPanel(MainUserPage _page, int _id, JSONObject _inv) {
        super();
        page = _page;
        inv = _inv;

        JLabel nameLabel = new JLabel(inv.getString("name"));
        JLabel valueLabel = new JLabel("" + inv.getDouble("value") / 100);
        JLabel currencyLabel = new JLabel(page.currencies.get(inv.getString("currencyid")));
        JLabel interestLabel = new JLabel(inv.getString("interest"));
        JLabel commissionLabel = new JLabel(inv.getString("commission"));
        JLabel RRSOLabel = new JLabel(inv.getString("rrso"));
        JLabel datecreated = new JLabel(inv.getString("datecreated"));
        JLabel dateended = new JLabel(inv.getString("dateended"));
        JLabel remainingLabel = new JLabel(inv.getString("remaining"));
        JLabel monthlyLabel = new JLabel(inv.getString("monthly"));
        JLabel monthsRemainingLabel = new JLabel(inv.getString("monthsremaining"));
        JButton payInstallmentButton = new JButton("Spłać ratę kredytu");

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (JLabel jLabel : Arrays.asList(nameLabel, valueLabel, currencyLabel, interestLabel, commissionLabel, RRSOLabel, datecreated, dateended, remainingLabel, monthlyLabel, monthsRemainingLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
            jLabel.setFont(Fonts.getStandardFont());
        }
        payInstallmentButton.setFont(Fonts.getStandardFont());
        datecreated.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.setBackground(Colors.getGrey());
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createLineBorder(Colors.getOrange(), 3, true));
        this.setMaximumSize(new Dimension(200, 200));
        this.setPreferredSize(new Dimension(150, -1));

        this.add(nameLabel);
        JPanel p1 = new JPanel();
        p1.setBackground(Colors.getGrey());
        p1.add(valueLabel);
        p1.add(currencyLabel);
        this.add(p1);
        JPanel p2 = new JPanel();
        p2.setBackground(Colors.getGrey());
        p2.add(interestLabel);
        p2.add(commissionLabel);
        p2.add(RRSOLabel);
        this.add(p2);
        JPanel p3 = new JPanel();
        p3.setBackground(Colors.getGrey());
        p3.add(datecreated);
        p3.add(dateended);
        this.add(p3);
        JPanel p4 = new JPanel();
        p4.setBackground(Colors.getGrey());
        p4.add(remainingLabel);
        p4.add(currencyLabel);
        p4.add(monthlyLabel);
        p4.add(currencyLabel);
        p4.add(monthsRemainingLabel);
        this.add(p4);

        payInstallmentButton.addActionListener(e -> {
            int accountid = payInstallmentDialog();
            System.out.println(accountid);
            MainUserPage.connection.updateCredit(page.login.getLogin(), page.login.getPasswordHash(), _id, accountid);
            page.updateCreditsSummary();
            page.updateAccounts();
        });

        payInstallmentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(payInstallmentButton);
    }
    int payInstallmentDialog() {
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accounts_to_select = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account : page.accounts.entrySet()) {
            if (account.getValue().getInt("currencyid") == inv.getInt("currencyid")) {
                accountBox.addItem(String.format("%s (%.2f %s)", page.getContactIfPossible(account.getKey()), account.getValue().getDouble("value") / 100, page.currencies.get(account.getValue().getString("currencyid"))));
                accounts_to_select.add(account.getKey());
            }
        }

        Object[] message = {
                "Zapłać z konta:", accountBox,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Spłać ratę kredytu", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            return accounts_to_select.get(accountBox.getSelectedIndex());
        }
        return -1;
    }
}

class TransactionPanel extends JPanel {
    private final String sender;
    private final String receiver;
    private final String date;
    private final String title;
    private String amount;
    private final String currency;
    private final String sign;
    char type;
    Color color;

    //"Od", "Do", "Data", "Tytuł", "Wartość", "Waluta"
    public TransactionPanel(String _sender, String _receiver, String _date, String _title, Double _unformatted_amount,
                            String _currency, char _type) {
        super();
        sender = _sender;
        receiver = _receiver;
        date = _date;
        title = _title;
        currency = _currency;
        type = _type;

        switch (type) {
            case 0:
                sign = "-";
                color = Color.red;
                break;
            case 1:
                sign = "+";
                color = Color.green;
                break;
            case 2:
                sign = ">";
                color = Color.magenta;
                break;
            default:
                sign = "?";
        }
        amount = String.format("%.2f", _unformatted_amount / 100.0);

        if (type == 0 || type == 1) {
            amount = sign + amount;
        }

        addSubcomponents();
        addListeners();
    }

    private void addSubcomponents() {
        JLabel signLabel = new JLabel(sign);
        JLabel senderLabel = new JLabel(sender);
        JLabel receiverLabel = new JLabel(receiver);
        JLabel dateLabel = new JLabel(date);
        JLabel titleLabel = new JLabel(title);
        JLabel amountLabel = new JLabel(amount);
        JLabel currencyLabel = new JLabel(currency);
        JLabel arrowLabel = new JLabel("->");

        // Colors and fonts
        for (JLabel label : Arrays.asList(signLabel, senderLabel, receiverLabel, dateLabel, titleLabel, amountLabel, arrowLabel)) {
            label.setFont(Fonts.getStandardFont());
            label.setForeground(Colors.getBrightTextColor());
            label.setPreferredSize(new Dimension(10, 25));
        }

        // Layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        this.setBackground(Colors.getGrey());
        this.setPreferredSize(new Dimension(600, 50));
        this.setMinimumSize(new Dimension(150, 50));
        this.setMaximumSize(new Dimension(5000, 50));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        c.gridwidth = 1;

        // Adding subcomponents
        signLabel.setForeground(color);
        signLabel.setFont(Fonts.getHeaderFont().deriveFont(40f));
        signLabel.setHorizontalAlignment(JLabel.CENTER);
        signLabel.setPreferredSize(new Dimension(80, 50));
        c.gridy = 0;
        c.gridx = 0;
        c.gridheight = 2;
        c.weightx = 0;
        this.add(signLabel, c);

        titleLabel.setForeground(Colors.getOrange());
        titleLabel.setFont(Fonts.getStandardFont().deriveFont(Font.BOLD));
        c.weightx = 1;
        c.gridx = 1;
        c.gridheight = 1;
        c.gridwidth = 3;
        this.add(titleLabel, c);

        //dateLabel.setPreferredSize(new Dimension(100, 50));
        c.weightx = 1;
        c.gridx = 4;
        c.gridwidth = 2;
        this.add(dateLabel, c);

        amountLabel.setForeground(color);
        amountLabel.setFont(Fonts.getHeaderFont().deriveFont(26f));
        amountLabel.setHorizontalAlignment(JLabel.RIGHT);
        amountLabel.setPreferredSize(new Dimension(120, 50));
        c.insets = new Insets(0, 0, 0, 2);
        c.weightx = 0;
        c.gridx = 6;
        c.gridwidth = 1;
        c.gridheight = 2;
        this.add(amountLabel, c);

        currencyLabel.setForeground(color);
        currencyLabel.setFont(Fonts.getHeaderFont().deriveFont(18f));
        currencyLabel.setHorizontalAlignment(JLabel.LEFT);
        currencyLabel.setPreferredSize(new Dimension(45, 50));
        c.insets = new Insets(0, 2, 0, 0);
        c.gridx = 7;
        c.gridwidth = 1;
        this.add(currencyLabel, c);

        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1;
        c.gridy = 1;
        c.gridx = 1;
        c.gridheight = 1;
        c.gridwidth = 2;
        this.add(senderLabel, c);

        arrowLabel.setHorizontalAlignment(JLabel.CENTER);
        c.weightx = 0.1;
        c.gridx = 3;
        c.gridwidth = 1;
        this.add(arrowLabel, c);

        c.weightx = 1;
        c.gridx = 4;
        c.gridwidth = 2;
        this.add(receiverLabel, c);
    }

    private void addListeners() {
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(Colors.getLightGrey());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(Colors.getGrey());
            }
        });
    }
}