package com.retzero.netty;

/**
 * @author JackJun
 * @date 2019/12/4 7:10 下午
 */
public class ClientExample {

    public static void main (String[] args) throws InterruptedException {
        //default port
        final int port = 8080;

        new TimeClient().connect("127.0.0.1", port);
    }
}
