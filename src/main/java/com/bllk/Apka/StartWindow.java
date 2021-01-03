package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.Map;

public class StartWindow {
    private static JFrame frame;
    private static ClientServerConnection connection;

    static JPanel startingPanel;
    private JPanel mainPanel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton, forgotPasswordButton;
    private JLabel message;
    private JPanel loginTab;
    private JTabbedPane mainTabbedPane;
    private JLabel logoLabel;
    private JPanel headerPanel;
    private JScrollPane registerTab;
    private JTextField register_loginField;
    private JPasswordField register_passwordField, register_repeatPasswordField;
    private JTextField register_name;
    private JTextField register_surname;
    private JComboBox<Integer> register_yearsComboBox;
    private JComboBox<Integer> register_monthsComboBox;
    private JComboBox<Integer> register_daysComboBox;
    private JTextField register_street;
    private JTextField register_number;
    private JTextField register_city;
    private JTextField register_postalcode;
    private JComboBox register_countriesComboBox;
    private JButton register_button;
    private JPanel registerPanel;

    private Integer year, month, day;

    public static void main(String[] args) {
        frame = new JFrame("BLLK");
        startingPanel = new StartWindow().mainPanel;
        connection = new ClientServerConnection();
        frame.setContentPane(startingPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(640, 480));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void performLogin() {
        String login = loginField.getText();
        String password = String.valueOf(passwordField.getPassword());
        String salt = null;
        Client client = null;
        Login log = null;

        if (login.isEmpty() || password.isEmpty()) {
            message.setText("No login or password given.");
            return;
        }

        try {
            message.setText("Checking...");
            String password_salt = connection.getSalt(login);
            String hashed_password = BCrypt.hashpw(password, password_salt);

            client = connection.getClient(login, hashed_password);
            log = new Login(client.getID(), login, hashed_password);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            message.setText("Invalid user...");
        }

        frame.setContentPane(new MainUserPage(frame, startingPanel, connection, client, log).menuPanel);
        loginField.setText("");
        passwordField.setText("");
        message.setText("");
    }

    public StartWindow() {
        loginButton.addActionListener(e -> performLogin());

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        loginField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        mainTabbedPane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (register_yearsComboBox.getItemCount() == 0) {
                    LocalDateTime now = LocalDateTime.now();
                    year = now.getYear();
                    month = now.getMonthValue();
                    day = now.getDayOfMonth();
                    register_yearsComboBox.removeAllItems();
                    fillYearComboBox(LocalDateTime.now());
                }
                if (register_countriesComboBox.getItemCount() == 0) {
                    fillCountriesComboBox();
                }
            }
        });
        register_yearsComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                year = (Integer) register_yearsComboBox.getSelectedItem();
                if (year == null)
                    year = LocalDateTime.now().getYear();
                register_monthsComboBox.removeAllItems();
                register_daysComboBox.removeAllItems();
                fillMonthComboBox(LocalDateTime.now(), year);
                register_monthsComboBox.setEnabled(true);
            }
        });
        register_monthsComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                month = (Integer) register_monthsComboBox.getSelectedItem();
                if (month == null)
                    month = 1;
                register_daysComboBox.removeAllItems();
                fillDaysComboBox(LocalDateTime.now(), year, month);
                register_daysComboBox.setEnabled(true);
            }
        });
        register_daysComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                day = (Integer) register_daysComboBox.getSelectedItem();
            }
        });
    }

    public void fillYearComboBox(LocalDateTime now) {
        int current_year = now.getYear();

        for (int i = current_year; i >= current_year - 120; --i)
            register_yearsComboBox.addItem(i);
    }

    public void fillMonthComboBox(LocalDateTime now, Integer chosen_year) {
        int month_count = 12;

        if (now.getYear() == chosen_year)
            month_count = now.getMonthValue();

        for (int i = 1; i <= month_count; ++i)
            register_monthsComboBox.addItem(i);
    }

    public void fillDaysComboBox(LocalDateTime now, Integer chosen_year, Integer chosen_month) {
        int day_count = 31;

        /* if (register_daysComboBox.getItemCount() != 0) {
            register_daysComboBox.removeAllItems();
        } */

        switch(chosen_month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                break;
            case 2:
                if ((chosen_year % 4 == 0 && chosen_year % 100 != 0) || chosen_year % 400 == 0)
                    day_count = 29;
                else
                    day_count = 28;
                break;
            default:
                day_count = 30;
        }

        if (now.getYear() == chosen_year && now.getMonthValue() == chosen_month)
            day_count = now.getDayOfMonth();

        for (int i = 1; i <= day_count; ++i)
            register_daysComboBox.addItem(i);
    }

    public void fillCountriesComboBox() {
        for (Map.Entry<String,Integer> entry : connection.getCountries().entrySet())
            register_countriesComboBox.addItem(entry.getKey());
    }

    public void pullDataFromServer(String login, String password) {

    }
}
