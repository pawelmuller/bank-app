package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

public class StartWindow {
    private static JFrame frame;
    private static ClientServerConnection connection;

    static JPanel startingPanel;
    private JPanel mainPanel, headerPanel, loginTab, registerTab;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton, forgotPasswordButton;
    private JLabel login_mainErrorLabel;
    private JTabbedPane mainTabbedPane;
    private JLabel logoLabel;
    private JScrollPane registerScrollPane;
    private JTextField register_login;
    private JPasswordField register_password, register_repeatPassword;
    private JTextField register_name, register_surname;
    private JComboBox<Integer> register_yearsComboBox, register_monthsComboBox, register_daysComboBox;
    private JTextField register_street, register_number, register_city, register_postcode;
    private JComboBox<String> register_countriesComboBox;
    private JButton register_button;
    private JRadioButton femaleRadioButton, maleRadioButton;
    private JLabel register_loginErrorLabel, register_passwordErrorLabel, register_repeatPasswordErrorLabel;
    private JLabel register_nameErrorLabel, register_surnameErrorLabel, register_genderErrorLabel;
    private JLabel register_address1ErrorLabel, register_address2ErrorLabel;
    private JLabel register_mainErrorLabel;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JLabel register_loginLabel, register_passwordLabel, register_repeatPasswordLabel;
    private JLabel register_nameLabel, register_surnameLabel, register_birthDateLabel, register_genderLabel;
    private JLabel register_yearLabel, register_monthLabel, register_dayLabel;
    private JLabel register_streetLabel, register_numberLabel, register_cityLabel, register_postcodeLabel, register_countryLabel;
    private JLabel register_loginHeaderLabel, register_personalHeaderLabel;

    private String login, password, repeatedPassword;
    private String name, surname, gender;
    private String street, buildingNumber, city, postcode, country;
    private Integer year, month, day;
    private boolean isDataValid;

    public static Fonts fonts = new Fonts();

    private static final Integer passwordMinimumLength = 8, passwordMaximumLength = 30;

    public static void main(String[] args) {
        frame = new JFrame("BLLK");
        startingPanel = new StartWindow().mainPanel;
        connection = new ClientServerConnection();
        frame.setContentPane(startingPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(640, 520));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void performLogin() {
        String login = loginField.getText();
        String password = String.valueOf(passwordField.getPassword());
        Client client = null;
        Login log = null;

        if (login.isEmpty() || password.isEmpty()) {
            login_mainErrorLabel.setVisible(true);
            login_mainErrorLabel.setText("Pole loginu i hasła nie może być puste.");
            return;
        }

        try {
            login_mainErrorLabel.setVisible(true);
            login_mainErrorLabel.setText("Logowanie...");
            String password_salt = connection.getSalt(login);
            String hashed_password = BCrypt.hashpw(password, password_salt);

            client = connection.getClient(login, hashed_password);
            log = new Login(client.getID(), login, hashed_password);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            login_mainErrorLabel.setVisible(true);
            login_mainErrorLabel.setText("Błędny login lub hasło.");
        }

        frame.setContentPane(new MainUserPage(frame, startingPanel, connection, client, log).menuPanel);
        loginField.setText("");
        passwordField.setText("");
        login_mainErrorLabel.setText("");
        login_mainErrorLabel.setVisible(false);
    }

    public void performRegister() {
        if (areAllFieldsValid()) {
            register_mainErrorLabel.setVisible(false);
            String password_hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
            String date = year + "-" + month + "-" + day;
            connection.createClient(name, surname, date, gender, street, buildingNumber, city, postcode, country, login, password_hash);
            register_mainErrorLabel.setText("Konto zostało utworzone. Możesz się zalogować :)");
            register_mainErrorLabel.setForeground(Color.green);
        } else {
            register_mainErrorLabel.setForeground(Color.red);
            register_mainErrorLabel.setText("Formularz został błędnie uzupełniony.");
        }
        register_mainErrorLabel.setVisible(true);
    }

    public StartWindow() {
        updateFonts();
        makeErrorLabelsInvisible();

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
        register_yearsComboBox.addActionListener(e -> {
            year = (Integer) register_yearsComboBox.getSelectedItem();
            if (year == null)
                year = LocalDateTime.now().getYear();
            register_monthsComboBox.removeAllItems();
            register_daysComboBox.removeAllItems();
            fillMonthComboBox(LocalDateTime.now(), year);
            register_monthsComboBox.setEnabled(true);
        });
        register_monthsComboBox.addActionListener(e -> {
            month = (Integer) register_monthsComboBox.getSelectedItem();
            if (month == null)
                month = 1;
            register_daysComboBox.removeAllItems();
            fillDaysComboBox(LocalDateTime.now(), year, month);
            register_daysComboBox.setEnabled(true);
        });
        register_daysComboBox.addActionListener(e -> day = (Integer) register_daysComboBox.getSelectedItem());

        // Data validation
        register_login.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                login = getDataFromField(register_login, register_loginErrorLabel, 6, 30);
                validateLogin();
            }
        });
        register_password.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                password = getPasswordFromField(register_password, register_passwordErrorLabel, passwordMinimumLength, passwordMaximumLength);
            }
        });
        register_repeatPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                repeatedPassword = getPasswordFromField(register_repeatPassword, register_repeatPasswordErrorLabel, passwordMinimumLength, passwordMaximumLength);
                validatePasswords();
            }
        });
        register_name.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                name = getDataFromField(register_name, register_nameErrorLabel, 0, 60);
            }
        });
        register_surname.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                surname = getDataFromField(register_surname, register_surnameErrorLabel, 0, 60);
            }
        });
        register_street.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                street = getDataFromField(register_street, register_address1ErrorLabel, 0, 60);
            }
        });
        register_number.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                buildingNumber = getDataFromField(register_number, register_address1ErrorLabel, 0, 10);
            }
        });
        register_city.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                city = getDataFromField(register_city, register_address2ErrorLabel, 0, 60);
            }
        });
        register_postcode.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                postcode = getDataFromField(register_postcode, register_address2ErrorLabel, 0, 10);
            }
        });
        register_button.addActionListener(e -> performRegister());
    }
    private void updateFonts() {
        Colors colors = new Colors();
        Font standard_font = StartWindow.fonts.adagio_slab.deriveFont(14f);
        Font header_font = StartWindow.fonts.adagio_slab.deriveFont(20f);

        // Main elements
        logoLabel.setFont(StartWindow.fonts.radikal.deriveFont(48f));
        mainTabbedPane.setFont(standard_font);

        // Login page
        for (JLabel label : Arrays.asList(loginLabel, passwordLabel)) {
            label.setFont(standard_font);
            label.setForeground(Colors.getBrightTextColor());
        }
        for (JButton jButton : Arrays.asList(loginButton, forgotPasswordButton)) {
            jButton.setFont(standard_font);
        }

        //Register page
        for (JLabel jLabel : Arrays.asList(register_personalHeaderLabel, register_loginHeaderLabel)) {
            jLabel.setFont(header_font);
            jLabel.setForeground(Colors.getBrightTextColor());
        }
        for (JLabel jLabel : Arrays.asList(register_loginLabel, register_passwordLabel, register_repeatPasswordLabel, register_nameLabel, register_surnameLabel, register_genderLabel, register_birthDateLabel, register_yearLabel, register_monthLabel, register_dayLabel, register_streetLabel, register_numberLabel, register_cityLabel, register_postcodeLabel, register_countryLabel)) {
            jLabel.setFont(standard_font);
            jLabel.setForeground(Colors.getBrightTextColor());
        }
        for (JRadioButton jRadioButton : Arrays.asList(maleRadioButton, femaleRadioButton)) {
            jRadioButton.setFont(standard_font);
            jRadioButton.setForeground(Colors.getBrightTextColor());
        }
        for (JComboBox<Integer> integerJComboBox : Arrays.asList(register_yearsComboBox, register_monthsComboBox, register_daysComboBox)) {
            integerJComboBox.setFont(standard_font);
            integerJComboBox.setForeground(Colors.getOrange());
            integerJComboBox.setBackground(Colors.getLightGrey());
        }
        register_countriesComboBox.setFont(standard_font);
        register_button.setFont(standard_font);

        String system_name = System.getProperty("os.name");
        if (!system_name.startsWith("Windows")) {
            mainTabbedPane.setForeground(Color.decode("#FF7F00"));
        }
    }
    private void fillYearComboBox(LocalDateTime now) {
        int current_year = now.getYear();

        for (int i = current_year; i >= current_year - 120; --i)
            register_yearsComboBox.addItem(i);
    }
    private void fillMonthComboBox(LocalDateTime now, Integer chosen_year) {
        int month_count = 12;

        if (now.getYear() == chosen_year)
            month_count = now.getMonthValue();

        for (int i = 1; i <= month_count; ++i)
            register_monthsComboBox.addItem(i);
    }
    private void fillDaysComboBox(LocalDateTime now, Integer chosen_year, Integer chosen_month) {
        int day_count = 31;

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
    private void fillCountriesComboBox() {
        for (Map.Entry<String,Integer> entry : connection.getCountries().entrySet())
            register_countriesComboBox.addItem(entry.getKey());
    }
    private void makeErrorLabelsInvisible() {
        login_mainErrorLabel.setVisible(false);
        register_loginErrorLabel.setVisible(false);
        register_passwordErrorLabel.setVisible(false);
        register_repeatPasswordErrorLabel.setVisible(false);
        register_nameErrorLabel.setVisible(false);
        register_surnameErrorLabel.setVisible(false);
        register_genderErrorLabel.setVisible(false);
        register_address1ErrorLabel.setVisible(false);
        register_address2ErrorLabel.setVisible(false);
        register_mainErrorLabel.setVisible(false);
    }
    private boolean areAllFieldsValid() {
        login = getDataFromField(register_login, register_loginErrorLabel, 8, 30);
        password = getPasswordFromField(register_password, register_passwordErrorLabel, 8, 30);
        repeatedPassword = getPasswordFromField(register_repeatPassword, register_repeatPasswordErrorLabel, 8, 30);
        name = getDataFromField(register_name, register_nameErrorLabel, 0, 60);
        surname = getDataFromField(register_surname, register_surnameErrorLabel, 0, 60);
        street = getDataFromField(register_street, register_address1ErrorLabel, 0, 60);
        buildingNumber = getDataFromField(register_number, register_address1ErrorLabel, 0, 10);
        city = getDataFromField(register_city, register_address2ErrorLabel, 0, 60);
        postcode = getDataFromField(register_postcode, register_address2ErrorLabel, 0, 10);
        validatePasswords();
        validateLogin();
        year = (Integer) register_yearsComboBox.getSelectedItem();
        month = (Integer) register_monthsComboBox.getSelectedItem();
        day = (Integer) register_daysComboBox.getSelectedItem();
        country = (String) register_countriesComboBox.getSelectedItem();
        gender = determineGender();
        if (login == null || password == null || repeatedPassword == null || name == null || surname == null || street == null ||
            buildingNumber == null || city == null || postcode == null || gender == null)
            isDataValid = false;
        return isDataValid;
    }
    private String getDataFromField(JTextField field_to_check, JLabel label_to_modify, int min_length, int max_length) {
        String inputData = field_to_check.getText();
        int input_length = inputData.length();
        boolean is_valid;

        is_valid = validateDataFromField(label_to_modify, input_length, min_length, max_length, "Pole");

        if (is_valid) {
            isDataValid = true;
            return inputData;
        } else {
            isDataValid = false;
            return null;
        }
    }
    private String getPasswordFromField(JPasswordField password_field, JLabel label_to_modify, int min_length, int max_length) {
        String inputData = String.valueOf(password_field.getPassword());
        int input_length = inputData.length();
        boolean is_valid;

        is_valid = validateDataFromField(label_to_modify, input_length, min_length, max_length, "Hasło");

        if (is_valid) {
            isDataValid = true;
            return inputData;
        } else {
            isDataValid = false;
            return null;
        }
    }
    private boolean validateDataFromField(JLabel label_to_modify, int length, int min_length, int max_length, String data_name) {
        boolean is_valid;
        if (length == 0) {
            is_valid = false;
            label_to_modify.setText(data_name + " nie może być puste.");
            label_to_modify.setVisible(true);
        } else if (length < min_length || length > max_length) {
            is_valid = false;
            if (min_length == 0) {
                label_to_modify.setText(data_name + " powinno mieć długość do " + max_length + " znaków.");
            } else {
                label_to_modify.setText(data_name + " powinno mieć długość pomiędzy " + min_length + ", a " + max_length + " znaków.");
            }
            label_to_modify.setVisible(true);
        } else {
            is_valid = true;
            label_to_modify.setVisible(false);
        }
        return is_valid;
    }
    private void validateLogin() {
        if (login == null) {
            isDataValid = false;
            return;
        }
        if (connection.checkLogin(login)) {
            isDataValid = false;
            register_loginErrorLabel.setText("Podany login już istnieje w bazie.");
            register_loginErrorLabel.setVisible(true);
        } else {
            isDataValid = true;
            register_loginErrorLabel.setVisible(false);
        }
    }
    private void validatePasswords() {
        if (password == null || repeatedPassword == null) {
            isDataValid = false;
            return;
        }
        if (password.equals(repeatedPassword)) {
            register_repeatPasswordErrorLabel.setVisible(false);
            isDataValid = true;
        } else {
            register_repeatPasswordErrorLabel.setText("Hasła są różne.");
            register_repeatPasswordErrorLabel.setVisible(true);
            isDataValid = false;
        }
    }
    private String determineGender() {
        if (maleRadioButton.isSelected()) {
            register_genderErrorLabel.setVisible(false);
            return "M";
        } else if (femaleRadioButton.isSelected()) {
            register_genderErrorLabel.setVisible(false);
            return "F";
        } else {
            register_genderErrorLabel.setText("Należy wybrać jedną z opcji.");
            register_genderErrorLabel.setVisible(true);
            isDataValid = false;
            return null;
        }
    }
}
