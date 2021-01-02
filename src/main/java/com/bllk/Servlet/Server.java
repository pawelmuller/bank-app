package com.bllk.Servlet;

import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;
import com.bllk.Servlet.mapclasses.Account;
import com.bllk.Servlet.mapclasses.Country;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.util.List;

public class Server extends HttpServlet {
    private Database data;

    public void init(ServletConfig config) throws ServletException {
        data = new Database();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestUrl = request.getRequestURI();
        String[] atributes = requestUrl.substring(1).split("/");
        Login login;
        Client client;
        String json;
        List countries;
        Country country;

        switch (atributes.length) {
            case 1:
                if (atributes[0].equals("countries")) {
                    countries = data.get_countries();
                    if (countries != null) {
                        json = "{\n";
                        for (int i = 0; i < countries.size(); ++i) {
                            country = (Country) countries.get(i);
                            json += "\"" + country.getId() + "\": \"" + country.getName() + "\",\n";
                        }
                        json += "}";
                        response.getOutputStream().println(json);
                    } else
                        response.getOutputStream().println("{}");
                }
            case 2:
                if (atributes[0].equals("account")) {
                    boolean valid = data.check_client(Integer.parseInt(atributes[1]));
                    if (valid)
                        response.getOutputStream().println("{\n\"id\": \"" + atributes[1] + "\"\n}");
                    else
                        response.getOutputStream().println("{}");
                }
                break;
            case 3:
                if (atributes[0].equals("login")) {
                    login = data.get_login(atributes[1], atributes[2]);
                    if (login != null) {
                        client = data.get_client(login.getID());
                        json = "{\n";
                        json += "\"id\": \"" + client.getID() + "\",\n";
                        json += "\"name\": \"" + client.getName() + "\",\n";
                        json += "\"surname\": \"" + client.getSurname() + "\"\n";
                        json += "}";
                        response.getOutputStream().println(json);
                    } else {
                        response.getOutputStream().println("{}");
                    }
                }
                break;
            case 4:
                if (atributes[0].equals("login") && atributes[3].equals("money")) {
                    login = data.get_login(atributes[1], atributes[2]);
                    if (login != null) {
                        Account account = data.get_money(login.getID());
                        String mjson = "{\n";
                        mjson += "\"id\": \"" + account.getID() + "\",\n";
                        mjson += "\"moneyonaccount\": \"" + account.getValue() + "\",\n";
                        mjson += "\"accountid\": \"" + account.getValue() + "\"\n";
                        mjson += "}";
                        response.getOutputStream().println(mjson);
                    }
                    else{
                        response.getOutputStream().println("{}");
                    }
                }
                break;
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String requestUrl = request.getRequestURI();
        String[] atributes = requestUrl.substring(1).split("/");
        switch (atributes.length) {
            case 1:
                if (atributes[0].equals("createclient")) {
                    try {
                        String name = request.getParameter("name");
                        String surname = request.getParameter("surname");
                        String date = request.getParameter("date");
                        String street = request.getParameter("street");
                        String num = request.getParameter("num");
                        String city = request.getParameter("city");
                        String postal_code = request.getParameter("postal_code");
                        String country_name = request.getParameter("country");
                        String login = request.getParameter("login");
                        String password_hash = request.getParameter("passwordhash");

                        data.add_client(name, surname, date, street, num, city, postal_code, country_name, login, password_hash);
                    }
                    catch (Exception ignored) {}
                }
                break;
            case 4:
                if (atributes[0].equals("login") && atributes[3].equals("transaction")) {
                    Login login = data.get_login(atributes[1], atributes[2]);
                    if (login != null) {
                        int targetid = Integer.parseInt(request.getParameter("targetid"));
                        int amount = Integer.parseInt(request.getParameter("amount"));
                        data.make_transfer(login.getID(), targetid, amount);
                    }
                }
                break;
        }
    }
}