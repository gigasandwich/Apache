#!/bin/bash

javac -d "bin"/ src/server/*.java
# javac -d "bin"/ src/client/*.java

# server execution
java -cp "bin" server.SimpleHttpServer
