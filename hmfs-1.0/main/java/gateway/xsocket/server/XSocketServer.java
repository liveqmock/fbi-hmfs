package gateway.xsocket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.IConnection.FlushMode;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;

import java.io.IOException;

/**
 * 服务端
 * @author zxb
 */
public class XSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(XSocketServer.class);

    private int port;
    private IServer server;
    private IServerHandler serverHandler;

    public XSocketServer() {
    }
    private void init() throws IOException {
        this.server = new Server(port, serverHandler);
        this.server.setFlushmode(FlushMode.ASYNC);   // 异步
    }

    public void start() throws IOException {
        init();
        logger.info("【SocketServer】 " + server.getLocalAddress() + ":" + port + "  开始启动...");
        server.start();
        logger.info("【SocketServer】  " + server.getLocalAddress() + ":" + port + "  启动成功...");
    }

    public boolean stop() throws IOException {
        logger.info("【SocketServer】  " + server.getLocalAddress() + ":" + port + "  开始关闭...");
        if (server != null) {
            server.close();
            server = null;
        }
        logger.info("【SocketServer】关闭结束...");

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
}
