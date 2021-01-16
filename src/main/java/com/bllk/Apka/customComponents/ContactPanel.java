package com.bllk.Apka.customComponents;

import com.bllk.Apka.MainUserPage;
import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ContactPanel extends JPanel {
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
            MainUserPage.getConnection().removeContact(MainUserPage.getLogin().getLogin(), MainUserPage.getLogin().getPasswordHash(), target_id);
            page.updateContacts();
            parent.remove(this);
            parent.updateUI();
        });
        this.add(nameLabel);
        this.add(targetidLabel);
        this.add(deleteButton);
    }
}
