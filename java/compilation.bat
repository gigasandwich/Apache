@REM on windows
javac -d bin/ src/com/httpserver/server/*.java
@REM javac -d bin/ src/com/httpserver/client/*.java 

@REM server execution
javac -cp bin/ com.httpserver.server.SimpleHttpServer.java

