#!/bin/bash

javac -d bin/ src/com/httpserver/server/*.java
# javac -d bin/ src/com/httpserver/client/*.java

# server execution
java -cp bin com.httpserver.server.SimpleHttpServer
