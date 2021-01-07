package com.bllk.Apka;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

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
        radikal = loadFont("radikalwut-bold.otf");
        adagio_slab = loadFont("adagio_slab-regular.otf");
        ge.registerFont(radikal);
        ge.registerFont(adagio_slab);
    }

    private Font loadFont(String font_name) {
        String path = "/fonts/" + font_name;
        Font font = new Font("Veranda", Font.PLAIN, 12);
        try {
            InputStream stream = Fonts.class.getResourceAsStream(path);
            font = Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (IOException | FontFormatException e) {
            System.out.println("Have not found font file. Using default Veranda.");
        }
        return font;
    }
}
