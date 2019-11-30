package com.retzero.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author JackJun
 * @date 2019/11/30 2:54 下午
 */
public class ServerExample {

    public static void main (String[] args) {
        // create socket server
        // default port
        int port = 8080;

        ServerSocket server;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port : " + port);
            Socket socket;
            while (true) {
                socket = server.accept();
                // wrong demo, should use ThreadPoolExecutor
                new Thread(new ServerHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
