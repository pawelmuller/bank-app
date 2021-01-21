package com.bllk.Apka.customComponents;

import com.bllk.Apka.MainUserPage;
import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CreditPanel extends JPanel {
    String creditName;
    MainUserPage page;
    JSONObject credit;

    public CreditPanel(MainUserPage _page, int _id, JSONObject _credit) {
        super();
        page = _page;
        credit = _credit;
        creditName = credit.getString("name");

        JLabel valueLabel = new JLabel("" + String.format("%.2f", credit.getDouble("value") / 100));
        JLabel currencyLabel1 = new JLabel(MainUserPage.getCurrencies().get(credit.getString("currencyid")));
        JLabel currencyLabel2 = new JLabel(MainUserPage.getCurrencies().get(credit.getString("currencyid")));
        JLabel currencyLabel3 = new JLabel(MainUserPage.getCurrencies().get(credit.getString("currencyid")));

        JLabel interestLabel = new JLabel(credit.getString("interest"));
        JLabel commissionLabel = new JLabel(credit.getString("commission"));
        JLabel RRSOLabel = new JLabel(credit.getString("rrso"));
        JLabel dateCreatedLabel = new JLabel(credit.getString("datecreated"));
        JLabel dateEndedLabel = new JLabel(credit.getString("dateended"));
        JLabel remainingLabel = new JLabel(credit.getString("remaining"));
        JLabel monthlyLabel = new JLabel(credit.getString("monthly"));
        JLabel monthsRemainingLabel = new JLabel(credit.getString("monthsremaining"));
        JButton payInstallmentButton = new JButton("Spłać ratę kredytu");

        for (JLabel jLabel : Arrays.asList(valueLabel, currencyLabel1, currencyLabel2, currencyLabel3, interestLabel, commissionLabel, RRSOLabel,
                dateCreatedLabel, dateEndedLabel, remainingLabel, monthlyLabel, monthsRemainingLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
            jLabel.setFont(Fonts.getStandardFont());
            jLabel.setPreferredSize(new Dimension(50, 25));
        }
        payInstallmentButton.setFont(Fonts.getStandardFont());

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        this.setBackground(Colors.getGrey());
        this.setPreferredSize(new Dimension(200, 200));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;

        c.gridy = 0;
        c.gridx = 0;
        c.insets = new Insets(0, 0, 0, 2);
        this.add(valueLabel, c);
        c.gridx = 1;
        c.insets = new Insets(0, 2, 0, 0);
        this.add(currencyLabel1, c);


        c.insets = new Insets(2, 2, 2, 2);

        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 1;
        this.add(interestLabel, c);

        c.gridy = 2;
        this.add(commissionLabel, c);

        c.gridy = 3;
        this.add(RRSOLabel, c);

        c.gridy = 4;
        this.add(dateCreatedLabel, c);

        c.gridy = 5;
        this.add(dateEndedLabel, c);

        c.gridy = 6;
        c.gridx = 0;
        this.add(remainingLabel, c);
        c.gridx = 1;
        this.add(currencyLabel2, c);

        c.gridy = 7;
        c.gridx = 0;
        this.add(monthlyLabel, c);
        c.gridx = 1;
        this.add(currencyLabel3, c);

        c.gridy = 8;
        c.gridx = 0;
        this.add(monthsRemainingLabel, c);

        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 9;
        this.add(payInstallmentButton, c);


        this.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                creditName,
                TitledBorder.CENTER,
                TitledBorder.DEFAULT_POSITION,
                Fonts.getStandardFont(),
                Colors.getBrightTextColor()
        ));
        payInstallmentButton.addActionListener(e -> {
            int accountID = payInstallmentDialog();
            if (accountID >= 0) {
                MainUserPage.getConnection().updateCredit(MainUserPage.getLogin().getLogin(), MainUserPage.getLogin().getPasswordHash(), _id, accountID);
                page.updateCreditsSummary();
                page.updateAccounts();
            }
        });
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Colors.getBlue(), 3, true),
                        creditName,
                        TitledBorder.CENTER,
                        TitledBorder.DEFAULT_POSITION,
                        Fonts.getStandardFont(),
                        Colors.getBrightTextColor()
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                        creditName,
                        TitledBorder.CENTER,
                        TitledBorder.DEFAULT_POSITION,
                        Fonts.getStandardFont(),
                        Colors.getBrightTextColor()
                ));
            }
        });
    }

    int payInstallmentDialog() {
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accounts_to_select = new ArrayList<>();
        List<Long> values_to_select = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account : MainUserPage.getAccounts().entrySet()) {
            if (account.getValue().getInt("currencyid") == credit.getInt("currencyid")) {
                accountBox.addItem(String.format("%s (%.2f %s)", page.getContactIfPossible(account.getKey()), account.getValue().getDouble("value") / 100, MainUserPage.getCurrencies().get(account.getValue().getString("currencyid"))));
                accounts_to_select.add(account.getKey());
                values_to_select.add(account.getValue().getLong("value"));
            }
        }

        Object[] message = {
                "Zapłać z konta:", accountBox,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Spłać ratę kredytu", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (values_to_select.get(accountBox.getSelectedIndex()) >= credit.getLong("monthly"))
                return accounts_to_select.get(accountBox.getSelectedIndex());
            else
                JOptionPane.showMessageDialog(null,"Nie posiadasz wystarczająco pieniędzy.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }
}
