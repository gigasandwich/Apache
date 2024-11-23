package com.httpserver.server;

import java.io.*;
import java.util.*;

public class Response {
    private String version;
    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private StringBuilder body;

    // Default values 
    public Response() {
        this.version = "HTTP/1.1";
        this.statusCode = 200;
        this.statusMessage = "OK";
        this.headers = new HashMap<>();
        this.body = new StringBuilder();
    }

    // 404, page not found
    public void setStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    // host: fako.com, user-agent: opera/1.2
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    // Body: ilay tadiavin'ny client: page/sary/JSON object
    public void setBody(String bodyContent) {
        this.body.append(bodyContent);
        addHeader("Content-Length", String.valueOf(body.length()));
    }

    // Response ho an'ny client
    public String stringValue() {
        StringBuilder response = new StringBuilder();

        // Response line
        response.append(version)
                .append(" ")
                .append(statusCode)
                .append(" ")
                .append(statusMessage)
                .append("\r\n");

        // Headers
        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        // End of headers section
        response.append("\r\n");

        // Body
        response.append(body.toString());

        return response.toString();
    }
}
