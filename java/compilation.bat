@REM on windows
javac -d bin/ src/server/*.java
@REM javac -d bin/ src/client/*.java 

@REM server execution
java -cp bin/ server.SimpleHttpServer.java

