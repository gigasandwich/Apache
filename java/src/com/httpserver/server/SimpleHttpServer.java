package com.httpserver.server;

import java.io.*;
import java.net.*;

public class SimpleHttpServer {

    public static void main(String[] args) {
        SimpleHttpServer server = new SimpleHttpServer();
        server.start(8080); // Start server on port 8080
    }

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            // For accepting multiple client connections
            while (true) {
                Socket clientSocket = serverSocket.accept(); 
                handleClient(clientSocket); 
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            // PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            String line;
            StringBuilder request = new StringBuilder();

            // Read HTTP request from the client
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                request.append(line).append("\n");
            }

            System.out.println("Received request:\n" + request);

            // tokony HTTP response: 
            // header
            out.write("HTTP/1.1 200 OK"); out.newLine();
            out.write("Content-Type: text/plain"); out.newLine();
            out.write("Content-Length: 13"); out.newLine();

            out.newLine(); // Manavaka ny header sy ny body

            // body
            out.write("Hello, World!");

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
}
