package com.bllk.Apka;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class ClientServerConnection {
    public BankClients get_login(String login, String password) {
        JSONObject jsonObject = new JSONObject(getData(String.format("client/%s/%s", login, password)));
        return new BankClients(jsonObject.getInt("id"), jsonObject.getString("name"), jsonObject.getString("surname"));
    }
    public double get_money(String login, String password) {
        JSONObject jsonObject = new JSONObject(getData(String.format("client/%s/%s/money", login, password)));
        return jsonObject.getDouble("moneyonaccount");
    }
    public String getData(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/" + url).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                    response.append("\n");
                }
                scanner.close();
                return response.toString();
            }
            else throw new Exception("Invalid response code");
        }
        catch (Exception ex) {
            return "";
        }
    }
}
