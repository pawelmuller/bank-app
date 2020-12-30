package com.bllk.Servlet;

import com.bllk.Apka.BankClients;
import com.bllk.Apka.Logins;
import com.bllk.Apka.Money;

import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

public class Server extends HttpServlet {
    private Database data;

    public void init(ServletConfig config) throws ServletException {
        data = new Database();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestUrl = request.getRequestURI();
        String[] atributes = requestUrl.substring("/client/".length()).split("/");
        Logins login;
        BankClients client;
        String json;

        switch (atributes.length) {
            case 2:
                System.out.println(atributes[0]);
                System.out.println(atributes[1]);

                login = data.get_login(atributes[0], atributes[1]);
                if(login != null){
                    client = data.get_client(login.getAccountid());
                    json = "{\n";
                    json += "\"id\": \"" + client.getID() + "\",\n";
                    json += "\"name\": \"" + client.getName() + "\",\n";
                    json += "\"surname\": \"" + client.getSurname() + "\"\n";
                    json += "}";
                    response.getOutputStream().println(json);
                }
                else{
                    response.getOutputStream().println("{}");
                }
                break;
            case 3:
                if (atributes[2].equals("money")) {
                    login = data.get_login(atributes[0], atributes[1]);
                    if (login != null) {
                        Money money = data.get_money(login.getAccountid());
                        String mjson = "{\n";
                        mjson += "\"id\": \"" + money.getID() + "\",\n";
                        mjson += "\"moneyonaccount\": \"" + money.getMoneyonaccount() + "\",\n";
                        mjson += "\"accountid\": \"" + money.getAccountid() + "\"\n";
                        mjson += "}";
                        System.out.println(mjson);
                        response.getOutputStream().println(mjson);
                    }
                    else{
                        response.getOutputStream().println("{}");
                    }
                }
                break;
        }
    }

//    @Override
//    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        String name = request.getParameter("id");
//        String about = request.getParameter("about");
//        int birthYear = Integer.parseInt(request.getParameter("birthYear"));
//        DataStore.getInstance().putPerson(new Person(name, about, birthYear, password));
//    }
}