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

public class InvestmentPanel extends JPanel {
    MainUserPage page;
    JSONObject inv;

    public InvestmentPanel(MainUserPage _page, int _id, JSONObject _inv) {
        super();
        page = _page;
        inv = _inv;

        JLabel nameLabel = new JLabel(inv.getString("name"));
        JLabel valueLabel = new JLabel("" + inv.getDouble("value") / 100);
        JLabel currencyLabel = new JLabel(MainUserPage.getCurrencies().get(inv.getString("currencyid")));
        JLabel profitLabel = new JLabel(inv.getString("profit"));
        JLabel yearProfitLabel = new JLabel(inv.getString("yearprofit"));
        JLabel capPeriodLabel = new JLabel(inv.getString("capperiod"));
        JLabel dateCreatedLabel = new JLabel(inv.getString("datecreated"));
        JButton closeInvestmentButton = new JButton("Zamknij lokatę");

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (JLabel jLabel : Arrays.asList(nameLabel, yearProfitLabel, capPeriodLabel, dateCreatedLabel,
                valueLabel, currencyLabel, profitLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
            jLabel.setFont(Fonts.getStandardFont());
        }
        closeInvestmentButton.setFont(Fonts.getStandardFont());
        dateCreatedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        p2.add(yearProfitLabel);
        p2.add(capPeriodLabel);
        this.add(p2);
        this.add(dateCreatedLabel);

        closeInvestmentButton.addActionListener(e -> {
            int accountid = removeInvestmentDialog();
            System.out.println(accountid);
            MainUserPage.getConnection().removeInvestment(MainUserPage.getLogin().getLogin(),
                    MainUserPage.getLogin().getPasswordHash(), _id, accountid);
            page.updateInvestmentsSummary();
            page.updateAccounts();
        });

        if (inv.has("dateended")) {
            JLabel dateendedLabel = new JLabel(inv.getString("dateended"));
            dateendedLabel.setForeground(Colors.getBrightTextColor());
            dateendedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.add(dateendedLabel);
        }
        closeInvestmentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(closeInvestmentButton);
    }

    int removeInvestmentDialog() {
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accounts_to_select = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account : MainUserPage.getAccounts().entrySet()) {
            if (account.getValue().getInt("currencyid") == inv.getInt("currencyid")) {
                accountBox.addItem(String.format("%s (%.2f %s)", page.getContactIfPossible(account.getKey()),
                        account.getValue().getDouble("value") / 100,
                        MainUserPage.getCurrencies().get(account.getValue().getString("currencyid"))));
                accounts_to_select.add(account.getKey());
            }
        }

        Object[] message = {
                "Na konto:", accountBox,
        };

        int option = JOptionPane.showConfirmDialog(null, message,
                "Nowa lokata",JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            return accounts_to_select.get(accountBox.getSelectedIndex());
        }
        return -1;
    }
}
