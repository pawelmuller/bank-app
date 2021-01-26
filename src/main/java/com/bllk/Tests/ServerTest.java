package com.bllk.Tests;

import static org.junit.Assert.*;
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.bllk.Servlet.Server;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class ServerTest {
    private final Server server = new Server();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    public void loginNull() throws ServletException, IOException {
        request.setRequestURI("/updatepassword");
        request.addParameter("login", "");
        request.addParameter("password", "abc");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newPasswordNull() throws ServletException, IOException {
        request.setRequestURI("/updatepassword");
        request.addParameter("login", "bilbo");
        request.addParameter("password", "");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
}