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

    /*  public byte[] sendDataUntilRcvToDep(byte[] dataBytes) throws Exception {
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
    }*/

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
            XSocketBlockClient socketBlockClient = new XSocketBlockClient("127.0.0.1", 61601, 10000000);
//            XSocketBlockClient socketBlockClient = new XSocketBlockClient("48.135.44.51", 61601, 10000000);
            /*
            120426090967521000	3689.85
            120426090968521000	3080.91
            120426090969521000	3086.52
            120426090970521000	2962.08
            120426090971521000	3172.71
            120426090972521000	3589.89
            120426090973521000	1867.62
            120426090974521000	4568.07
            120426090975521000	3189.54
            120426090976521000	3362.94
            120426090977521000	3994.83
            120426090978521000	2126.70
            120426090979521000	4566.03
            120426090980521000	3080.91
            120426090981521000	3362.94
            120426090982521000	51.00
            120426090983521000	3197.70
            120426090984521000	3240.03
            120426090985521000	3190.05
            120426090986521000	3362.94
            120426090987521000	3987.69
             */
            //String datagram = "1234567890      00005001123456789123456789            10000.00        201203090000001|300|C\n21122000012|9090900|C\n000000455|300|D";
            // 1001 交款查询
//          String datagram = "123456789012345600001001120427091098521000";
            // 1002 交款  133716.39
//           String datagram = "123456789012345600001002" + "120427091098521000" + StringUtils.rightPad("2832.54", 16, ' ');
            // 2001 支取查询
            //String datagram = "123456789012345600002001120319004846531000";
            // 2002 支取
//             String datagram = "12345678901233340000200212031900484653100060              ";
            // 3001 退款查询
            //String datagram = "123456789012341100003001120319004845531000";
            // 3002 退款
//             String datagram = "1234567890123456000030021203160048385230001000.00         ";
            /*
            票据状态	1	    2:使用；3:作废
            打印票据起始编号	12	票据起始编号
            打印票据结束编号	12	票据结束编号（单张销号该字段为空）
            缴款通知书编号	18	非必填项，凭证使用时填写
             */
            //String datagram = "1234567890888891000040012000000010114000000010115120319004843521000";
//            String datagram = "123456789088889100005001100000000000003               268945.33       201204171234567890123333|60.00|C\n1234567890654321|133716.39|D";
            // 1011 划入查询
//            String datagram = "123456789012345600001011" + "120802106820630200";
            // 1012 划入确认
         String datagram = "123456789012345600001012" + "120802106820630200" + StringUtils.rightPad("51.00", 16, ' ');
            // 2011 划出查询
//              String datagram = "123456789012345500002011" + "120802106821630100";
            // 2012 划出确认 51.00
//            String datagram = "123456789012345100002012" + "120802106821630100" + StringUtils.rightPad("51.00", 16, ' ');

            socketBlockClient.sendDataUntilRcv(StringUtils.rightPad(datagram.getBytes().length + 6 + "", 6, " ") + datagram, 6);
            //socketBlockClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
