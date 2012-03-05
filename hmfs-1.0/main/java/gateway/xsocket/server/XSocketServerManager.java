package gateway.xsocket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class XSocketServerManager {

    private static final Logger logger = LoggerFactory.getLogger(XSocketServerManager.class);
    private XSocketServer xSocketServer;

    public XSocketServerManager() {
    }

    // ��ʼ��
    public void init() {
        printLine();
        try {
            xSocketServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        printLine();
    }


    // ����
    public void destroy() {
        printLine();
        try {
            xSocketServer.stop();
            xSocketServer = null;
        } catch (Exception e) {
            throw new RuntimeException("Socket Server ����ʱ�����쳣 !");
        }
        printLine();
    }

    public XSocketServer getxSocketServer() {
        return xSocketServer;
    }

    public void setxSocketServer(XSocketServer xSocketServer) {
        this.xSocketServer = xSocketServer;
    }

    private static void printLine() {
        logger.info("//////////////////////////" + new Date() + "//////////////////////////");
    }

}


