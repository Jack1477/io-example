package com.retzero.netty;

/**
 * @author JackJun
 * 2019/12/1 13:32
 * Life is not just about survival.
 */
public class ServerExample {

    public static void main (String[] args) throws InterruptedException {
        //default port
        final int port = 8080;

        new TimeServer().bind(port);

    }
}
