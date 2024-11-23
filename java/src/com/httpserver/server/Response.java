package com.httpserver.server;

import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.HashMap;

public class Response {
    private String version;
    private Map<String, String> headers;
    private StringBuilder body;
    private int statusCode;
    private String statusMessage;
    private static Map<Integer, String> status_messages = getStatus_Messages();

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

    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
        this.statusMessage = status_messages.getOrDefault(statusCode, "Unknown Status");
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

        // Response line: version + statusCode + statusMessage
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

    static Map<Integer, String> getStatus_Messages(){
        Map<Integer, String> answer = new HashMap<>();
        answer.put(200, "OK");
        answer.put(201, "Created");
        answer.put(202, "Accepted");
        answer.put(204, "No Content");
        answer.put(301, "Moved Permanently");
        answer.put(302, "Found");
        answer.put(304, "Not Modified");
        answer.put(400, "Bad Request");
        answer.put(401, "Unauthorized");
        answer.put(403, "Forbidden");
        answer.put(404, "Not Found");
        answer.put(405, "Method Not Allowed");
        answer.put(406, "Not Acceptable");
        answer.put(407, "Proxy Authentication Required");
        answer.put(408, "Request Timeout");
        answer.put(409, "Conflict");
        answer.put(410, "Gone");
        answer.put(415, "Unsupported Media Type");
        answer.put(429, "Too Many Requests");
        answer.put(500, "Internal Server Error");
        answer.put(501, "Not Implemented");
        answer.put(502, "Bad Gateway");
        answer.put(503, "Service Unavailable");
        answer.put(504, "Gateway Timeout");
        return answer;
    }
}
