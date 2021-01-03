package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Client;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

public class ClientServerConnection {
    public Client getClient(String login, String hashed_password) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login?login=%s&password=%s", login, hashed_password)));
        return new Client(jsonObject.getInt("id"), jsonObject.getString("name"),
                jsonObject.getString("surname"), jsonObject.getString("birthdate"),
                jsonObject.getInt("addressid"), jsonObject.getInt("loginid"));
    }
    public String get_salt(String login) {
        JSONObject jsonObject = new JSONObject(getData(String.format("getsalt/%s", login)));
        return new String(jsonObject.getString("salt"));
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
    public void make_transfer(String login, String password, int targetid, double amount) {
        try {
            HttpURLConnection httpconnection = (HttpURLConnection) new URL(String.format("http://localhost:8080/login/%s/%s/transaction", login, password)).openConnection();
            httpconnection.setRequestMethod("POST");

            String postData = "targetid=" + targetid;
            postData += "&amount=" + amount;

            httpconnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(httpconnection.getOutputStream());
            wr.write(postData);
            wr.flush();

            int responseCode = httpconnection.getResponseCode();
            if (responseCode == 200)
                System.out.println("POST was successful.");
            else if (responseCode == 401)
                throw new Exception("Wrong password");
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void create_client(String name, String surname, String date, String street, String num, String city, String postal_code, String country, String login, String passwordhash) {
        try {
            HttpURLConnection httpconnection = (HttpURLConnection) new URL("http://localhost:8080/createclient").openConnection();
            httpconnection.setRequestMethod("POST");

            String postData = "name=" + name;
            postData += "&surname=" + surname;
            postData += "&date=" + date;
            postData += "&street=" + street;
            postData += "&num=" + num;
            postData += "&city=" + city;
            postData += "&postal_code=" + postal_code;
            postData += "&country=" + country;
            postData += "&login=" + login;
            postData += "&passwordhash=" + passwordhash;

            httpconnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(httpconnection.getOutputStream());
            wr.write(postData);
            wr.flush();

            int responseCode = httpconnection.getResponseCode();
            if (responseCode == 200)
                System.out.println("POST was successful.");
            else if (responseCode == 401)
                throw new Exception("Wrong password");
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String getData(String url) {
    try {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/" + url).openConnection();
        System.out.println("http://localhost:8080/" + url);
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
        System.out.println(ex.getMessage());
        return "";
    }
}
}
