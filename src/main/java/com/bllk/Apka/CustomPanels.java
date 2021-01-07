package com.bllk.Apka;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;
import java.io.IOException;
import java.io.InputStream;

class AccountPanel extends JPanel {
    public AccountPanel(String _account_name, String _account_number, String _balance, String _currency) {
        super();

        String _account_name = "Nazwa konta";
        if (_account_name.equals(_account_number))
            _account_name = "Konto";
        JLabel balanceLabel = new JLabel(_balance);
        JLabel currencyLabel = new JLabel(_currency);

        balanceLabel.setForeground(Colors.getBrightTextColor());
        currencyLabel.setForeground(Colors.getBrightTextColor());

        this.setLayout(new FlowLayout());

        this.add(balanceLabel);
        this.add(currencyLabel);

        this.setBackground(Colors.getLightGrey());
        this.setBorder(BorderFactory.createLineBorder(Colors.getOrange(), 3, true));
        this.setMaximumSize(new Dimension(200, 50));
        this.setPreferredSize(new Dimension(150, -1));

        Font font = StartWindow.fonts.adagio_slab;
        font = font.deriveFont(12f);

        this.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                _account_name + " (nr " + _account_number + ")",
                TitledBorder.CENTER,
                TitledBorder.DEFAULT_POSITION,
                font,
                Colors.getBrightTextColor()
                ));
    }
}

class Colors {
    private static Color bright_text_color, orange, blue, dark_grey, light_grey;
    public Colors() {
        bright_text_color = Color.decode("#EEEEEE");
        blue = Color.decode("#1891FF");
        orange = Color.decode("#FF7F00");
        dark_grey = Color.decode("#222222");
        light_grey = Color.decode("#333333");
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
    public static Color getLightGrey() {
        return light_grey;
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

class ContactPanel extends JPanel {
    public ContactPanel(JPanel parent, MainUserPage page, int target_id, String name) {
        super();

        this.setLayout(new FlowLayout(FlowLayout.LEADING));

        JLabel nameLabel = new JLabel(name);
        JLabel targetidLabel = new JLabel("" + target_id);
        JButton deleteButton = new JButton("Usuń");

        deleteButton.addActionListener(e -> {
            page.connection.removeContact(page.login.getLogin(), page.login.getPasswordHash(), target_id);
            page.updateContacts();
            parent.remove(this);
            parent.updateUI();
        });
        this.add(nameLabel);
        this.add(targetidLabel);
        this.add(deleteButton);
    }
}
