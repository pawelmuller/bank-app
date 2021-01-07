package com.bllk.Apka;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;

class AccountPanel extends JPanel {
    public AccountPanel(String _account_number, String _balance, String _currency) {
        super();

        String _account_name = "Nazwa konta";
        JLabel accountName = new JLabel(_account_name);
        JLabel accountNumber = new JLabel(_account_number);
        JLabel balanceLabel = new JLabel(_balance);
        JLabel currencyLabel = new JLabel(_currency);

        accountName.setForeground(Color.decode("#EEEEEE"));
        accountNumber.setForeground(Color.decode("#EEEEEE"));
        balanceLabel.setForeground(Color.decode("#EEEEEE"));
        currencyLabel.setForeground(Color.decode("#EEEEEE"));

        this.setLayout(new FlowLayout());

        this.add(accountNumber);
        this.add(balanceLabel);
        this.add(currencyLabel);

        this.setBackground(Color.decode("#222222"));
        this.setBorder(BorderFactory.createLineBorder(Color.decode("#FF7F00"), 3, true));
        this.setMaximumSize(new Dimension(200, 50));
        this.setPreferredSize(new Dimension(150, -1));

        Font font = StartWindow.fonts.adagio_slab;
        font = font.deriveFont(12f);

        this.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.decode("#FF7F00"), 3, true),
                _account_name + " (nr " + _account_number + ")",
                TitledBorder.CENTER,
                TitledBorder.DEFAULT_POSITION,
                font,
                Color.decode("#EEEEEE")
                ));
    }
}

class Fonts {
    public Font radikal;
    public Font adagio_slab;

    public Fonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            radikal = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/fonts/radikalwut-bold.otf"));
            adagio_slab = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/fonts/adagio_slab-regular.otf"));
            ge.registerFont(radikal);
            ge.registerFont(adagio_slab);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
