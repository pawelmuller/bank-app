package com.bllk.Tests;

import static org.junit.Assert.*;
import java.io.*;
import javax.servlet.http.HttpServletResponse;

import com.bllk.Servlet.Server;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ServerTest {
    private final Server server = new Server();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    /* ---------------------------------------------- /updatepassword ----------------------------------------------- */
    @Test
    public void newPasswordHashNull() throws IOException {
        request.setRequestURI("/updatepassword");
        request.addParameter("login", "bilbo");
        request.addParameter("passwordhash", "");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newPasswordAssert() throws IOException {
        request.setRequestURI("/updatepassword");
        request.addParameter("login", "bilbo");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
    /* --------------------------------------------- /login/updatelogin --------------------------------------------- */
    @Test
    public void newLoginNotASCII() throws IOException {
        request.setRequestURI("/login/updatelogin");
        request.addParameter("login", "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("newlogin", "zażółć_gęślą_jaźń");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newLoginNull() throws IOException {
        request.setRequestURI("/login/updatelogin");
        request.addParameter("login", "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("newlogin", "");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newLoginAssert() throws IOException {
        request.setRequestURI("/login/updatelogin");
        request.addParameter("login", "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("newlogin", "szokobons");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
    /* --------------------------------------------- /login/transaction --------------------------------------------- */
    @Test
    public void newTransactionNameNull() throws IOException {
        request.setRequestURI("/login/transaction");
        request.addParameter("login", "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("payerid", "0");
        request.addParameter("targetid", "1");
        request.addParameter("title", "");
        request.addParameter("amount", "1");
        request.addParameter("currencyid", "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newTransactionNameTooLong() throws IOException {
        request.setRequestURI("/login/transaction");
        request.addParameter("login", "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("payerid", "0");
        request.addParameter("targetid", "1");
        request.addParameter("title", "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxyz"); // 101
        request.addParameter("amount", "1");
        request.addParameter("currencyid", "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newTransactionAssert() throws IOException {
        request.setRequestURI("/login/transaction");
        request.addParameter("login", "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("payerid", "0");
        request.addParameter("targetid", "1");
        request.addParameter("title", "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxy"); // 100
        request.addParameter("amount", "1");
        request.addParameter("currencyid", "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
}