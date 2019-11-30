package com.retzero.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * @author JackJun
 * @date 2019/11/30 3:03 下午
 */
public class ServerHandler implements Runnable {
    private Socket socket;

    @Override
    public void run() {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream())) ;
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String currentTime;
            String body;
            while (true) {
                body = in.readLine();
                if (body == null) {
                    break;
                }
                System.out.println("The time server receive order : " + body);
                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
                out.println(currentTime);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }
}
