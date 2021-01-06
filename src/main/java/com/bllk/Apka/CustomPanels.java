package com.bllk.Apka;

import javax.swing.*;

class AccountPanel extends JPanel {
    public AccountPanel(String _account_number, String _balance, String _currency) {
        super();

        String _account_name = "Nazwa konta";
        JLabel accountName = new JLabel(_account_name);
        JLabel accountNumber = new JLabel(_account_number);
        JLabel balanceLabel = new JLabel(_balance);
        JLabel currencyLabel = new JLabel(_currency);

        this.add(accountName);
        this.add(accountNumber);
        this.add(balanceLabel);
        this.add(currencyLabel);
    }
}
