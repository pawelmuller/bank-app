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
        JSONObject json_object = new JSONObject(getData(String.format("login?login=%s&password=%s", login, hashed_password)));
        return new Client(json_object.getInt("id"), json_object.getString("name"),
                json_object.getString("surname"), json_object.getString("birthdate"),
                json_object.getInt("addressid"), json_object.getInt("loginid"));
    }
    public String getSalt(String login) {
        JSONObject json_object = new JSONObject(getData(String.format("getsalt/%s", login)));
        return new String(json_object.getString("salt"));
    }
    public double get_money(String login, String hashed_password) {
        JSONObject json_object = new JSONObject(getData(String.format("login/%s/%s/money", login, hashed_password)));
        return json_object.getDouble("moneyonaccount");
    }
    public boolean check_client(int client_id) {
        JSONObject json_object = new JSONObject(getData(String.format("account/%d", client_id)));
        try {
            int test = json_object.getInt("id");
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    public void makeTransfer(String login, String hashed_password, int target_id, double amount) {
        try {
            HttpURLConnection http_connection = (HttpURLConnection) new URL(String.format("http://localhost:8080/login/%s/%s/transaction", login, hashed_password)).openConnection();
            http_connection.setRequestMethod("POST");

            String postData = "targetid=" + target_id;
            postData += "&amount=" + amount;

            http_connection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(http_connection.getOutputStream());
            wr.write(postData);
            wr.flush();

            int response_code = http_connection.getResponseCode();
            if (response_code == 200)
                System.out.println("POST was successful.");
            else if (response_code == 401)
                throw new Exception("Wrong password");
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void createClient(String name, String surname, String date, String street, String num, String city, String postal_code, String country, String login, String hashed_password) {
        try {
            HttpURLConnection http_connection = (HttpURLConnection) new URL("http://localhost:8080/createclient").openConnection();
            http_connection.setRequestMethod("POST");

            String post_data = "name=" + name;
            post_data += "&surname=" + surname;
            post_data += "&date=" + date;
            post_data += "&street=" + street;
            post_data += "&num=" + num;
            post_data += "&city=" + city;
            post_data += "&postal_code=" + postal_code;
            post_data += "&country=" + country;
            post_data += "&login=" + login;
            post_data += "&passwordhash=" + hashed_password;

            http_connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(http_connection.getOutputStream());
            writer.write(post_data);
            writer.flush();

            int responseCode = http_connection.getResponseCode();
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
        HttpURLConnection http_connection = (HttpURLConnection) new URL("http://localhost:8080/" + url).openConnection();
        System.out.println("http://localhost:8080/" + url);
        http_connection.setRequestMethod("GET");

        int response_code = http_connection.getResponseCode();
        if (response_code == 200) {
            StringBuilder response = new StringBuilder();
            Scanner scanner = new Scanner(http_connection.getInputStream());
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
