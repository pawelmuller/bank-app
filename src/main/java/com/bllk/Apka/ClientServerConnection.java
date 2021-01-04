package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Account;
import com.bllk.Servlet.mapclasses.Client;
import org.json.JSONObject;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ClientServerConnection {
    public Map<String, String> getCurrencies() {
        JSONObject jsonObject = new JSONObject(getData("currencies"));
        Map<String, String> map = jsonObject.toMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (String)e.getValue()));
        return map;
    }
    public Map<String, Integer> getCountries() {
        JSONObject jsonObject = new JSONObject(getData("countries"));
        Map<String, Integer> map = jsonObject.toMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Integer.parseInt((String)e.getValue())));
        return new TreeMap<>(map);
    }
    public Map<String, Integer> getUserAccounts(String login, String hashed_password) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/accounts?login=%s&password=%s", login, hashed_password)));
        return jsonObject.toMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Integer.parseInt((String)e.getValue())));
    }
    public Client getClient(String login, String hashed_password) {
        JSONObject json_object = new JSONObject(getData(String.format("login?login=%s&password=%s", login, hashed_password)));
        return new Client(json_object.getInt("id"), json_object.getString("name"),
                json_object.getString("surname"), json_object.getString("birthdate"),
                json_object.getInt("addressid"), json_object.getInt("loginid"));
    }
    public String getSalt(String login) {
        JSONObject json_object = new JSONObject(getData(String.format("getsalt/%s", login)));
        return json_object.getString("salt");
    }
    public Account getAccount(String login, String hashed_password, int accountid) {
        JSONObject json_object = new JSONObject(getData(String.format("account/%s?login=%s&password=%s", accountid, login, hashed_password)));
        return new Account(json_object.getInt("id"), json_object.getInt("value"), json_object.getInt("currency"), json_object.getInt("ownerid"));
    }
    public double getTotalSavings(String login, String hashed_password, int currencyid) {
        JSONObject json_object = new JSONObject(getData(String.format("login/totalmoney?login=%s&password=%s&currency=%s", login, hashed_password, currencyid)));
        return json_object.getInt("value");
    }
    public boolean checkAccount(int accoundid) {
        JSONObject json_object = new JSONObject(getData(String.format("account/%d", accoundid)));
        return true;
    }
    public void makeTransfer(String login, String hashed_password, int payer_id, int target_id, String title, int amount, int currencyid) {
        try {
            HttpURLConnection http_connection = (HttpURLConnection) new URL("http://localhost:8080/login/transaction").openConnection();
            http_connection.setRequestMethod("POST");

            String postData = "login=" + login;
            postData += "&password=" + hashed_password;
            postData += "&payerid=" + payer_id;
            postData += "&targetid=" + target_id;
            postData += "&title=" + title;
            postData += "&amount=" + amount;
            postData += "&currencyid=" + currencyid;

            http_connection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(http_connection.getOutputStream());
            wr.write(postData);
            wr.flush();

            int response_code = http_connection.getResponseCode();
            if (response_code == 200)
                System.out.println("POST was successful.");
            else
                throw new Exception("Something went wrong: " + response_code);
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
