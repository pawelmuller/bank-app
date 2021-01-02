package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Client;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ClientServerConnection {
    public Client get_login(String login, String password) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/%s/%s", login, password)));
        return new Client(jsonObject.getInt("id"), jsonObject.getString("name"), jsonObject.getString("surname"));
    }
    public double get_money(String login, String password) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/%s/%s/money", login, password)));
        return jsonObject.getDouble("moneyonaccount");
    }
    public boolean check_client(int clientid) {
        JSONObject jsonObject = new JSONObject(getData(String.format("account/%d", clientid)));
        try {
            int test = jsonObject.getInt("id");
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    public void make_transfer(String login, String password, int targetid, double amount) throws IOException {
        HttpURLConnection httpconnection = (HttpURLConnection) new URL(String.format("http://localhost:8080/login/%s/%s/transaction", login, password)).openConnection();
        httpconnection.setRequestMethod("POST");

        String postData = "targetid=" + targetid;
        postData += "&amount=" + amount;

        httpconnection.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(httpconnection.getOutputStream());
        wr.write(postData);
        wr.flush();

        int responseCode = httpconnection.getResponseCode();
        if(responseCode == 200){
            System.out.println("POST was successful.");
        }
        else if(responseCode == 401){
            System.out.println("Wrong password.");
        }
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
