package test;

import webserver.WebServer;

public class Main {
    public static void main(String[] args) {
        WebServer server = new WebServer();
        server.run(1500);
    }
}
