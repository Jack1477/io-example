package com.retzero.nio.simple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author JackJun
 * 2019/12/1 13:33
 * Life is not just about survival.
 */
public class MultiplexerTimeServer implements Runnable {
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private volatile boolean stop = false;

    public MultiplexerTimeServer(int port) {
        //bind port

        try {
            // init selector and serverChannel.
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port: " + port);
        } catch (IOException e) {
            //print exception.
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (!stop) {
            try {
                selector.select();
                // 获取进入IO状态的管道，进行多路复用
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        // close.
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                // close selector.
                if (selector != null) {
                    try {
                        selector.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        // 多路复用器关闭后，注册在其上的资源都会自动被关闭。
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleInput (SelectionKey key) throws IOException {
        if (key.isValid()) {
            // key is valid
            if (key.isAcceptable()) {
                //处理新接入的消息
                ServerSocketChannel newChannel = (ServerSocketChannel) key.channel();
                SocketChannel sc = newChannel.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }

            if (key.isReadable()) {
                // Read the data.
                SocketChannel channel = (SocketChannel) key.channel();
                ByteBuffer readBuf = ByteBuffer.allocate(1024);
                int readBytes = channel.read(readBuf);
                if (readBytes > 0) {
                    // 读取到数据，
                    readBuf.flip();
                    byte[] bytes = new byte[readBuf.remaining()];
                    readBuf.get(bytes);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("The time server receive order : " + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
                    doWrite(channel, currentTime);
                } else if (readBytes < 0) {
                    key.cancel();
                    channel.close();
                }
            }
        }
    }

    private void doWrite (SocketChannel channel, String response) throws IOException {
        byte[] bytes = response.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        channel.write(writeBuffer);
    }
}
