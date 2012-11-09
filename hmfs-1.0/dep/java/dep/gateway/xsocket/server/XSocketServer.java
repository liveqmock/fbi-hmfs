package dep.gateway.xsocket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.IConnection.FlushMode;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;

import java.io.IOException;
import java.net.InetAddress;

/**
 * �����
 *
 * @author zxb
 */
public class XSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(XSocketServer.class);

    private int port;
    private IServer server;
    private IServerHandler serverHandler;
    private Integer MIN_SIZE_WORKER_POOL = 2;
    private Integer SIZE_WORKER_POOL = 10;


    public XSocketServer() {
    }

    private void init() throws IOException {
        this.server = new Server(port, serverHandler, MIN_SIZE_WORKER_POOL, SIZE_WORKER_POOL);
        this.server.setFlushmode(FlushMode.ASYNC);   // �첽
    }

    public void start() throws IOException {
        String serverinfo = "��SocketServer�� " + InetAddress.getLocalHost().getHostAddress() + ":" + port;
        logger.info(serverinfo + "  ��ʼ����...");
        init();
        this.server.start();
        logger.info(serverinfo + "  �����ɹ�...");
    }

    public boolean stop() throws IOException {
        logger.info("��SocketServer��  " + server.getLocalAddress() + ":" + port + "  ��ʼ�ر�...");
        if (server != null) {
            server.close();
            server = null;
        }
        logger.info("��SocketServer���رս���...");

        return true;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public IServer getServer() {
        return server;
    }

    public void setServer(IServer server) {
        this.server = server;
    }

    public IServerHandler getServerHandler() {
        return serverHandler;
    }

    public void setServerHandler(IServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public int getMIN_SIZE_WORKER_POOL() {
        return MIN_SIZE_WORKER_POOL;
    }

    public void setMIN_SIZE_WORKER_POOL(int MIN_SIZE_WORKER_POOL) {
        this.MIN_SIZE_WORKER_POOL = MIN_SIZE_WORKER_POOL;
    }

    public int getSIZE_WORKER_POOL() {
        return SIZE_WORKER_POOL;
    }

    public void setSIZE_WORKER_POOL(int SIZE_WORKER_POOL) {
        this.SIZE_WORKER_POOL = SIZE_WORKER_POOL;
    }
}
