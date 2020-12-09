package com.bllk.Apka;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class Account {
    JFrame frame;
    JPanel panel;
    JTextArea textArea;
    Integer clientid;

    Account(DatabaseConnection connection, Integer _clientid) {
        clientid = _clientid;

        frame = new JFrame("Account " + clientid);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        textArea = new JTextArea(15, 50);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(Font.getFont(Font.SANS_SERIF));
        JScrollPane scroller = new JScrollPane(textArea);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel inputpanel = new JPanel();
        inputpanel.setLayout(new FlowLayout());
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JTextArea header = new JTextArea("Welcome to BLLK");
        header.setEditable(false);
        panel.add(header);

        JTextArea money = new JTextArea();
        money.setEditable(false);
        money.setText("You have " + connection.get_money(clientid) + " PLN on your account.");
        panel.add(money);

        panel.add(scroller);
        panel.add(inputpanel);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
