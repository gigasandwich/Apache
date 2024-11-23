package com.httpserver.server;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.HashMap;

public class SimpleHttpServer {

    public static void main(String[] args) {
        SimpleHttpServer server = new SimpleHttpServer();
        // System.out.println("Current directory: " + new File("").getAbsolutePath());
        server.start(8080); 
    }

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            // Tokony multiple client connections
            while (true) {
                Socket clientSocket = serverSocket.accept(); 
                handleClient(clientSocket); 
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 
    Step 1: parse request
    Anaty Request:
        -Request line: GET /fako.html HTTP/1.1
        -Headers: {host: fako.com}
        -Body (post, put, patch ihany no manana): name=nathan&bobota=be
    */
    private void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            // Parse Request
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            System.out.println("Request Line: " + requestLine);
            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];

            // host: fako.com, user-agent: opera/1.2
            String line;
            Map<String, String> headers = new HashMap<>();
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                String[] headerParts = line.split(": ", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
            }

            // if(post/put/patch) dia asina body 
            StringBuilder body = new StringBuilder();
            if (method.equalsIgnoreCase("POST") ||
                method.equalsIgnoreCase("PUT") ||
                method.equalsIgnoreCase("PATCH")
            ) {
                int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
                char[] bodyChars = new char[contentLength];
                in.read(bodyChars, 0, contentLength);
                body.append(bodyChars);
            }

            // Generate Response
            Response response = generateResponse(method, path, body.toString());

            // Send response to client
            out.println(response.stringValue());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /* 
    Step2: generate response

    Anaty Response:
        -HTTP Status Line: HTTP/1.1 404 Not Found
        -Headers: {Content-Type, application/json)} {Content-Length, 42069}
        -Body: HTML, JSON, text
    */

    // Generate HTTP Response Based on Request Method and Path
    private Response generateResponse(String method, String path, String body) {
        Response response = new Response();

        // GET ho an' ny fichiers
        if (method.equalsIgnoreCase("GET")) {
            // localhost8080: blablabla/Apache/java
            String filePath = "../" + "www" + (path.equals("/") ? "/index.html" : path);
            File file = new File(filePath);

            if (file.exists() && !file.isDirectory()) {

                // Serve the requested file
                String contentType = getContentType(file);
                String responseBody = readFileContent(file);
                response.setStatus(200);
                response.addHeader("Content-Type", contentType);
                response.setBody(responseBody);

            } else if (file.exists() && file.isDirectory()) {

                // if(directory), serve index.html by default
                File defaultFile = new File(filePath + "/index.html");
                if (defaultFile.exists()) {
                    String responseBody = readFileContent(defaultFile);
                    response.setStatus(200);
                    response.setBody(responseBody);
                } else {
                    response.setStatus(404);
                    response.addHeader("Content-Type", "text/plain");
                    response.setBody("Error 404: Resource not found");
                }
                
            } else {

                // Resource not found
                response.setStatus(404);
                response.addHeader("Content-Type", "text/plain");
                response.setBody("Error 404: Resource not found");

            }

        } else if (
                    method.equalsIgnoreCase("POST") ||
                    method.equalsIgnoreCase("PUT") ||
                    method.equalsIgnoreCase("PATCH")
                ) {

            // Handle POST/PUT/PATCH request
            String responseBody = "Received Data: " + body;
            response.setStatus(200);
            response.addHeader("Content-Type", "text/plain");
            response.setBody(responseBody);

        } else {
            
            // Handle other HTTP methods (POST, PUT, PATCH)
            String responseBody = "Error 405: Method Not Allowed";
            response.setStatus(405);
            response.addHeader("Content-Type", "text/plain");
            response.setBody(responseBody);
            
        }

        return response;
    }

    // file to string 
    private String readFileContent(File file) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private String getContentType(File file) {
        String fileName = file.getName();
        if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".json")) {
            return "application/json";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else {
            return "application/octet-stream"; // Default binary type
        }
    }

}