package com.retzero.nio.simple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author JackJun
 * @date 2019/12/4 7:12 下午
 */
public class ClientHandler implements Runnable {

    private String host;
    private int port;
    private Selector selector;
    private SocketChannel clientChannel;
    private volatile boolean stop = false;

    public ClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            selector = Selector.open();
            clientChannel = SocketChannel.open();
            clientChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (!stop) {
            try {
                selector.select();
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
                        e.printStackTrace();
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable()) {
                if (sc.finishConnect()) {
                    sc.register(selector, SelectionKey.OP_READ);
                    doWrite(sc);
                } else {
                    System.exit(1);
                }
            }

            if (key.isReadable()) {
                ByteBuffer readBuf = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuf);
                if (readBytes > 0) {
                    readBuf.flip();
                    byte[] bytes = new byte[readBuf.remaining()];
                    readBuf.get(bytes);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("Now is :" + body);
                    this.stop = true;
                } else if (readBytes < 0) {
                    key.cancel();
                    sc.close();
                }
            }
        }
    }

    private void doConnect() throws IOException {
        if (clientChannel.connect(new InetSocketAddress(host, port))) {
            clientChannel.register(selector, SelectionKey.OP_READ);
            doWrite(clientChannel);
        } else {
            clientChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite (SocketChannel sc) throws IOException {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        sc.write(writeBuffer);
        if (!writeBuffer.hasRemaining()) {
            System.out.println("Send order to server succeed");
        }
    }
}
