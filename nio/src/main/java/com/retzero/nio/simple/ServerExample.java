package com.retzero.nio.simple;

/**
 * @author JackJun
 * 2019/12/1 13:32
 * Life is not just about survival.
 */
public class ServerExample {

    public static void main (String[] args) {
        //default port
        final int port = 8080;

        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);

        new Thread(timeServer).start();
    }
}
