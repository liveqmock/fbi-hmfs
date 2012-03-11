package dep.gateway.xsocket.client.impl;

import dep.gateway.xsocket.client.ConnectClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.*;

import java.io.IOException;
import java.nio.BufferUnderflowException;

/**
 * 客户端接收服务端信息
 *
 * @author zxb
 */
public class XSocketBlockClient1 extends ConnectClient implements IConnectHandler {

    private static final Logger logger = LoggerFactory.getLogger(XSocketBlockClient1.class);

    private IBlockingConnection blockingConnection;
    private INonBlockingConnection nonBlockingConnection;

    public XSocketBlockClient1(String serverIP, int serverPort, long timeoutMills) throws IOException {
        super(serverIP, serverPort);
        nonBlockingConnection = new NonBlockingConnection(serverIP, serverPort, this);
        blockingConnection = new BlockingConnection(nonBlockingConnection);
        blockingConnection.setConnectionTimeoutMillis(timeoutMills);
        blockingConnection.setEncoding("GBK");
        blockingConnection.setAutoflush(true);  //  设置自动清空缓存
    }

    @Override
    public boolean onConnect(INonBlockingConnection nbc) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
        logger.info("【本地客户端】与远程主机:" + serverIP + "建立连接。");
        return true;
    }

    public String sendDataUntilRcv(String dataContent, int headLength) throws Exception {

        logger.info("【本地客户端】发送报文：" + dataContent);
        String datagram = null;
        if (sendData(dataContent)) {
            int garamTotalLength = Integer.parseInt(blockingConnection.readStringByLength(headLength).trim());
            logger.info("【本地客户端】接收报文总长度：" + garamTotalLength);
            datagram = new String(blockingConnection.readBytesByLength(garamTotalLength - headLength));
        }
        logger.info("【本地客户端】实际接收报文内容：" + datagram);
        logger.info("【本地客户端】实际接收报文内容长度：" + datagram.getBytes().length);
        return datagram;
    }

    private boolean sendData(String dataContent) throws IOException, InterruptedException {
        if (blockingConnection == null || !blockingConnection.isOpen()) {
            throw new RuntimeException("未建立连接！");
        } else {
            //blockingConnection.write(dataContent);
            int length = dataContent.length();
            blockingConnection.write(dataContent.substring(0,50));
            blockingConnection.flush();
            Thread.sleep(3000);
            blockingConnection.write(dataContent.substring(50));
            blockingConnection.flush();
        }
        return true;
    }

    /**
     * 关闭客户端链接
     *
     * @return
     * @throws java.io.IOException
     */
    public boolean close() throws IOException {
        if (blockingConnection != null && blockingConnection.isOpen()) {
            blockingConnection.close();
            blockingConnection = null;
        }
        if (nonBlockingConnection != null && nonBlockingConnection.isOpen()) {
            nonBlockingConnection.close();
            nonBlockingConnection = null;
        }
        return true;
    }

    public IBlockingConnection getBlockingConnection() {
        return blockingConnection;
    }

    public void setBlockingConnection(IBlockingConnection blockingConnection) {
        this.blockingConnection = blockingConnection;
    }

    public static void main(String[] args) {
        try {
            XSocketBlockClient1 socketBlockClient = new XSocketBlockClient1("127.0.0.1", 61601, 10000);
            String datagram = "1234567890      000080001   1123456789123456789890000.00       ";
            //socketBlockClient.sendDataUntilRcv(StringUtils.rightPad(datagram.getBytes().length + 6 +"", 6, " ") + datagram, 6);
            socketBlockClient.sendDataUntilRcv(StringUtils.rightPad(datagram.getBytes().length + 6 +"", 6, " ") + datagram, 6);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
