package com.bllk.Apka;

import javax.persistence.criteria.CriteriaBuilder;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Account implements ActionListener {
    DatabaseConnection connection;

    JFrame frame;
    JPanel panel;
    JTextArea textArea, header, yourmoney;
    Integer clientid;

    JLabel transaction, targetlabel, moneylabel;
    JTextField target, money, message;
    JButton submit;
    double yourmoney_value;

    Account(DatabaseConnection _connection, Integer _clientid) {
        clientid = _clientid;
        connection = _connection;

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

        header = new JTextArea("Welcome to BLLK");
        header.setEditable(false);
        panel.add(header);

        yourmoney = new JTextArea();
        yourmoney.setEditable(false);
        panel.add(yourmoney);
        UpdateMoney();

        transaction = new JLabel("Make transaction");
        targetlabel = new JLabel("Target client");
        target = new JTextField();
        moneylabel = new JLabel("Amount");
        money = new JTextField();
        submit = new JButton("Send");
        submit.addActionListener(this);
        message = new JTextField();
        message.setEditable(false);

//        panel.add(scroller);
//        panel.add(inputpanel);
        panel.add(transaction);
        panel.add(targetlabel);
        panel.add(target);
        panel.add(moneylabel);
        panel.add(money);
        panel.add(submit);
        panel.add(message);


        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(400, 200);
    }

    void UpdateMoney() {
        yourmoney_value = connection.get_money(clientid);
        yourmoney.setText("You have " + yourmoney_value + " PLN on your account.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int target_id = Integer.parseInt(target.getText());
            double money_value = Double.parseDouble(money.getText());
            if (target_id == clientid) {
                message.setText("Transaction failed: You can't send money to yourself.");
            }
            else if (money_value > yourmoney_value || money_value == 0) {
                message.setText("Transaction failed: Invalid amount of money.");
            }
            else if (!connection.check_client(Integer.parseInt(target.getText()))) {
                message.setText("Transaction failed: Account don't exists.");
            }
            else {
                message.setText("Sending " + money_value + " PLN to Account " + target_id);
                connection.make_transfer(clientid, target_id, money_value);
                UpdateMoney();
            }
        }
        catch (Exception ex) {
            message.setText("Transaction failed");
        }
    }
}
