package dep.mocktool.cbs;

import dep.gateway.xsocket.client.ConnectClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.*;

import java.io.IOException;
import java.nio.BufferUnderflowException;

/**
 * 主机客户端模拟
 *
 * @author zxb
 */
public class MockXSocketBlockClient extends ConnectClient implements IConnectHandler {

    private static final Logger logger = LoggerFactory.getLogger(MockXSocketBlockClient.class);

    private IBlockingConnection blockingConnection;
    private INonBlockingConnection nonBlockingConnection;

    public MockXSocketBlockClient(String serverIP, int serverPort, long timeoutMills) throws IOException {
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

    /**
     * 国土局客户端
     */
    public byte[] sendDataUntilRcvToHmb(byte[] dataBytes) throws Exception {
        logger.info("【本地客户端】发送报文：" + new String(dataBytes));
        byte[] datagramBytes = null;
        if (sendData(dataBytes)) {
            int garamTotalLength = Integer.parseInt(blockingConnection.readStringByLength(7).trim());
            logger.info("【本地客户端】接收报文总长度：" + garamTotalLength);
            datagramBytes = blockingConnection.readBytesByLength(garamTotalLength + 4);
        }
        logger.info("【本地客户端】实际接收报文内容：" + new String(datagramBytes));
        logger.info("【本地客户端】实际接收报文内容长度：" + datagramBytes.length);
        return datagramBytes;
    }

    public byte[] sendDataUntilRcvToDep(byte[] dataBytes) throws Exception {
        logger.info("【本地客户端】发送报文：" + new String(dataBytes));
        byte[] datagramBytes = null;
        if (sendData(dataBytes)) {
            int garamTotalLength = Integer.parseInt(blockingConnection.readStringByLength(6).trim());
            logger.info("【本地客户端】接收报文总长度：" + garamTotalLength);
            datagramBytes = blockingConnection.readBytesByLength(garamTotalLength - 6);
        }
        logger.info("【本地客户端】实际接收报文内容：" + new String(datagramBytes));
        logger.info("【本地客户端】实际接收报文内容长度：" + datagramBytes.length);
        return datagramBytes;
    }

    private boolean sendData(byte[] dataBytes) throws IOException {
        if (blockingConnection == null || !blockingConnection.isOpen()) {
            throw new RuntimeException("未建立连接！");
        } else {
            blockingConnection.write(dataBytes);
            blockingConnection.flush();
        }
        return true;
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

    public static void main(String[] args) {
        try {
            MockXSocketBlockClient socketBlockClient = new MockXSocketBlockClient("127.0.0.1", 61601, 60000);
            //String datagram = "1234567890      00005001123456789123456789            10000.00        201203090000001|300|C\n21122000012|9090900|C\n000000455|300|D";
            // 1001 交款查询
//            String datagram = "123456789012345600001001120316004833521000";
            // 1002 交款
            //String datagram = "1234567890123456000010021203160048335210009692.55         ";
            // 2001 支取查询
            //String datagram = "123456789012345600002001120316004838523000";
            // 2002 支取
            // String datagram = "1234567890123456000020021234567891234567892000000.00      ";
            // 3001 退款查询
//            String datagram = "123456789012345600003001120316004838523000";
            // 3002 退款
             String datagram = "1234567890123489000030021203160048385230001000.00         ";
            /*
            票据状态	1	1:领用；2:使用；3:作废
            打印票据起始编号	12	票据起始编号
            打印票据结束编号	12	票据结束编号（单张销号该字段为空）
            缴款通知书编号	18	非必填项，凭证使用时填写
             */
            //String datagram = "1234567890123456000040012123456789123123456789122";
            socketBlockClient.sendDataUntilRcv(StringUtils.rightPad(datagram.getBytes().length + 6 + "", 6, " ") + datagram, 6);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
