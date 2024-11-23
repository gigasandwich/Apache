package com.httpserver.server;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.HashMap;

public class SimpleHttpServer {

    public static void main(String[] args) {
        SimpleHttpServer server = new SimpleHttpServer();
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

    // 1- Mi-parse ny requete an'ny client
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
                while (in.ready()) {
                    body.append((char) in.read());
                }
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
    2. Avy mi-parse request, migenerer HTTP response MIFANARAKA amin'ilay request

    STEPS:
        -Set HTTP Status Line: HTTP/1.1 404 Not Found
        -Add Headers:
            Content-Type (e.g., text/html, application/json).
            Content-Length (length of the response body in bytes).
        -Add Body:
            HTML, JSON, text
    */

    private Response generateResponse(String method, String path, String body) {
        Response response = new Response();

        if (method.equalsIgnoreCase("GET")) {
            if (path.equals("/")) {
                String responseBody = "<html><body><h1>Welcome to Simple HTTP Server</h1></body></html>";
                response.setStatus(200, "OK");
                response.addHeader("Content-Type", "text/html");
                response.setBody(responseBody);
            } else {
                String responseBody = "Error 404: Resource not found";
                response.setStatus(404, "Not Found");
                response.addHeader("Content-Type", "text/plain");
                response.setBody(responseBody);
            }
        } else if (method.equalsIgnoreCase("POST")) {
            String responseBody = "Received POST data: " + body;
            response.setStatus(200, "OK");
            response.addHeader("Content-Type", "text/plain");
            response.setBody(responseBody);
        } else {
            String responseBody = "Error 405: Method not allowed";
            response.setStatus(405, "Method Not Allowed");
            response.addHeader("Content-Type", "text/plain");
            response.setBody(responseBody);
        }

        return response;
    }

}