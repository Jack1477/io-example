package com.retzero.nio.simple;

/**
 * @author JackJun
 * @date 2019/12/4 7:10 下午
 */
public class ClientExample {

    public static void main (String[] args) {
        //default port
        final int port = 8080;

        ClientHandler client = new ClientHandler("127.0.0.1", port);

        new Thread(client).start();
    }
}
