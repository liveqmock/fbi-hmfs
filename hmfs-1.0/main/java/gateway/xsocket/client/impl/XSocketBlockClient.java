package gateway.xsocket.client.impl;

import gateway.xsocket.client.ConnectClient;
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
public class XSocketBlockClient extends ConnectClient implements IConnectHandler {

    private static final Logger logger = LoggerFactory.getLogger(XSocketBlockClient.class);

    private IBlockingConnection blockingConnection;
    private INonBlockingConnection nonBlockingConnection;

    public XSocketBlockClient(String serverIP, int serverPort, long timeoutMills) throws IOException {
        super(serverIP, serverPort);
        nonBlockingConnection = new NonBlockingConnection(serverIP, serverPort, this);
        blockingConnection = new BlockingConnection(nonBlockingConnection);
        blockingConnection.setConnectionTimeoutMillis(timeoutMills);
        blockingConnection.setEncoding("GBK");
        blockingConnection.setAutoflush(true);  //  设置自动清空缓存
    }

    @Override
    public boolean onConnect(INonBlockingConnection nbc) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
        String remoteName = nbc.getRemoteAddress().getHostName();
        logger.info("【本地客户端】与远程主机:" + remoteName + "建立连接。");
        return true;
    }

    public String sendDataUntilRcv(String dataContent, int headLength) throws Exception {

        logger.info("【本地客户端】发送报文：" + dataContent);
        String datagram = null;
        if (sendData(dataContent)) {
            int garamLength = Integer.parseInt(blockingConnection.readStringByLength(headLength));
            logger.info("【本地客户端】接收报文内容长度：" + garamLength);
            datagram = blockingConnection.readStringByLength(garamLength);
        }
        logger.info("【本地客户端】接收报文内容：" + datagram);
        return datagram;
    }

    private boolean sendData(String dataContent) throws IOException {
        if (blockingConnection == null || !blockingConnection.isOpen()) {
            throw new RuntimeException("未建立连接！");
        } else {
            blockingConnection.write(dataContent);
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

}
