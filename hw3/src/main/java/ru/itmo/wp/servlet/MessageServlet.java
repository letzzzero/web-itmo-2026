package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MessageServlet extends HttpServlet {
    private static class Message {
        private transient long id;
        private String user;
        private String text;
        private Message(long id, String user, String text) {
            this.id = id;
            this.user = user;
            this.text = text;
        }
    }


    private List<Message> messages = new ArrayList<>();
    private long curId = 1; 


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getPathInfo();
        
        response.setContentType("application/json");
        if(uri.equals("/auth")) {
            getWriterJson(response, auth(request));
        } else if(uri.equals("/findAll")) {
            getWriterJson(response, findAll(request));
        } else if(uri.equals("/add")) {
            getWriterJson(response, add(request, response));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            getWriterJson(response, "");
        }    


    }

    private void getWriterJson(HttpServletResponse response, Object object) throws IOException{
        String json = new Gson().toJson(object);
        response.getWriter().print(json);
        response.getWriter().flush();
    }

    private String auth(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String user = request.getParameter("user");
        if(user != null) {
            session.setAttribute("user", user);
        }
        user = (String) session.getAttribute("user");
        return (user != null) ? user : "";  
    }
    private List<Message> findAll(HttpServletRequest request) {
        return new ArrayList<>(messages);  
    }
    private Message add(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("user");
        if(user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        String text = request.getParameter("text");
        Message message = new Message(curId++, user, text);
        messages.add(message);
        
        return message;  
    }
}
