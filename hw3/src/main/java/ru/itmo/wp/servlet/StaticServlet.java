package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class StaticServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();

        

        // File root = new File("C:/Users/leoni/web-itmo-2026/hw3/src/main/webapp/static").getCanonicalFile();
        File root = new File("./src/main/webapp/static").getCanonicalFile();
        // File file = new File(root, uri.substring(1)).getCanonicalFile();
        
        List<File> files = new ArrayList<>();
        for(String uri_part : uri.substring(1).split("\\+")) {  
            File file = getFile(uri_part, root);
            if(file == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            files.add(file);
        }

        response.setContentType(getServletContext().getMimeType(files.get(0).getName()));
        try (OutputStream outputStream = response.getOutputStream()) {
            for(File file : files) {
                Files.copy(file.toPath(), outputStream);
            }
        }


    }

    private File getFile(String uri, File root) throws IOException {
        File file = new File(root, uri).getCanonicalFile();
        if(file.toPath().startsWith(root.toPath())) {
            if(!file.isFile()) {
                root = new File(getServletContext().getRealPath("/static")).getCanonicalFile();
                file = new File(getServletContext().getRealPath("/static/" + uri)).getCanonicalFile();
                if(!file.toPath().startsWith(root.toPath())) {
                    return null;
                }
            }
            if (file.isFile()) {
                return file;
            } else {
                return null;
            }            
        } else {
            return null;
        }
    }
}
