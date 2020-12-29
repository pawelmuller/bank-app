package com.bllk.Servlet;

import com.bllk.Apka.BankClients;
import com.bllk.Apka.Logins;

import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class Server extends HttpServlet {
    private Database data;

    public void init(ServletConfig config) throws ServletException {
        data = new Database();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestUrl = request.getRequestURI();
        String[] logpass = requestUrl.substring("/client/".length()).split("/");
        System.out.println(logpass[0]);
        System.out.println(logpass[1]);

        Logins login = data.get_login(logpass[0], logpass[1]);
        if(login != null){
            BankClients client = data.get_client(login.getAccountid());
            String json = "{\n";
            json += "\"id\": " + client.getID() + ",\n";
            json += "\"name\": \"" + client.getName() + "\",\n";
            json += "\"surname\": \"" + client.getSurname() + "\"\n";
            json += "}";
            response.getOutputStream().println(json);
        }
        else{
            response.getOutputStream().println("{}");
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