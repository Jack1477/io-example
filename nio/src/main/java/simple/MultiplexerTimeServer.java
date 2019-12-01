package simple;

/**
 * @author JackJun
 * 2019/12/1 13:33
 * Life is not just about survival.
 */
public class MultiplexerTimeServer implements Runnable {
    private int port;

    public MultiplexerTimeServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void run() {

    }
}
