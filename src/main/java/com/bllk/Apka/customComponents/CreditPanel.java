package com.bllk.Apka.customComponents;

import com.bllk.Apka.MainUserPage;
import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CreditPanel extends JPanel {
    MainUserPage page;
    JSONObject inv;

    public CreditPanel(MainUserPage _page, int _id, JSONObject _inv) {
        super();
        page = _page;
        inv = _inv;

        JLabel nameLabel = new JLabel(inv.getString("name"));
        JLabel valueLabel = new JLabel("" + inv.getDouble("value") / 100);
        JLabel currencyLabel = new JLabel(MainUserPage.getCurrencies().get(inv.getString("currencyid")));
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
            MainUserPage.getConnection().updateCredit(MainUserPage.getLogin().getLogin(), MainUserPage.getLogin().getPasswordHash(), _id, accountid);
            page.updateCreditsSummary();
            page.updateAccounts();
        });

        payInstallmentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(payInstallmentButton);
    }

    int payInstallmentDialog() {
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accounts_to_select = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account : MainUserPage.getAccounts().entrySet()) {
            if (account.getValue().getInt("currencyid") == inv.getInt("currencyid")) {
                accountBox.addItem(String.format("%s (%.2f %s)", page.getContactIfPossible(account.getKey()), account.getValue().getDouble("value") / 100, MainUserPage.getCurrencies().get(account.getValue().getString("currencyid"))));
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
