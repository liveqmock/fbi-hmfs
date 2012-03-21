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
 * �ͻ��˽��շ������Ϣ
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
        blockingConnection.setAutoflush(true);  //  �����Զ���ջ���
    }

    @Override
    public boolean onConnect(INonBlockingConnection nbc) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
        logger.info("�����ؿͻ��ˡ���Զ������:" + serverIP + "�������ӡ�");
        return true;
    }

    public String sendDataUntilRcv(String dataContent, int headLength) throws Exception {

        logger.info("�����ؿͻ��ˡ����ͱ��ģ�" + dataContent);
        String datagram = null;
        if (sendData(dataContent)) {
            int garamTotalLength = Integer.parseInt(blockingConnection.readStringByLength(headLength).trim());
            logger.info("�����ؿͻ��ˡ����ձ����ܳ��ȣ�" + garamTotalLength);
            datagram = new String(blockingConnection.readBytesByLength(garamTotalLength - headLength));
        }
        logger.info("�����ؿͻ��ˡ�ʵ�ʽ��ձ������ݣ�" + datagram);
        logger.info("�����ؿͻ��ˡ�ʵ�ʽ��ձ������ݳ��ȣ�" + datagram.getBytes().length);
        return datagram;
    }

    /**
     * �����ֿͻ���
     */
    public byte[] sendDataUntilRcvToHmb(byte[] dataBytes) throws Exception {
        logger.info("�����ؿͻ��ˡ����ͱ��ģ�" + new String(dataBytes));
        byte[] datagramBytes = null;
        if (sendData(dataBytes)) {
            int garamTotalLength = Integer.parseInt(blockingConnection.readStringByLength(7).trim());
            logger.info("�����ؿͻ��ˡ����ձ����ܳ��ȣ�" + garamTotalLength);
            datagramBytes = blockingConnection.readBytesByLength(garamTotalLength + 4);
        }
        logger.info("�����ؿͻ��ˡ�ʵ�ʽ��ձ������ݣ�" + new String(datagramBytes));
        logger.info("�����ؿͻ��ˡ�ʵ�ʽ��ձ������ݳ��ȣ�" + datagramBytes.length);
        return datagramBytes;
    }

    public byte[] sendDataUntilRcvToDep(byte[] dataBytes) throws Exception {
        logger.info("�����ؿͻ��ˡ����ͱ��ģ�" + new String(dataBytes));
        byte[] datagramBytes = null;
        if (sendData(dataBytes)) {
            int garamTotalLength = Integer.parseInt(blockingConnection.readStringByLength(6).trim());
            logger.info("�����ؿͻ��ˡ����ձ����ܳ��ȣ�" + garamTotalLength);
            datagramBytes = blockingConnection.readBytesByLength(garamTotalLength - 6);
        }
        logger.info("�����ؿͻ��ˡ�ʵ�ʽ��ձ������ݣ�" + new String(datagramBytes));
        logger.info("�����ؿͻ��ˡ�ʵ�ʽ��ձ������ݳ��ȣ�" + datagramBytes.length);
        return datagramBytes;
    }

    private boolean sendData(byte[] dataBytes) throws IOException {
        if (blockingConnection == null || !blockingConnection.isOpen()) {
            throw new RuntimeException("δ�������ӣ�");
        } else {
            blockingConnection.write(dataBytes);
            blockingConnection.flush();
        }
        return true;
    }

    private boolean sendData(String dataContent) throws IOException {
        if (blockingConnection == null || !blockingConnection.isOpen()) {
            throw new RuntimeException("δ�������ӣ�");
        } else {
            blockingConnection.write(dataContent);
            blockingConnection.flush();
        }
        return true;
    }

    /**
     * �رտͻ�������
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
            XSocketBlockClient socketBlockClient = new XSocketBlockClient("127.0.0.1", 61601, 60000);
            //String datagram = "1234567890      00005001123456789123456789            10000.00        201203090000001|300|C\n21122000012|9090900|C\n000000455|300|D";
            // 1001 �����ѯ
            //String datagram = "123456789012345600001001120319004843521000";
            // 1002 ����  133716.39
//            String datagram = "123456789065432100001002120319004843521000133716.39       ";
            // 2001 ֧ȡ��ѯ
            //String datagram = "123456789012345600002001120319004846531000";
            // 2002 ֧ȡ
//             String datagram = "12345678901233340000200212031900484653100060              ";
            // 3001 �˿��ѯ
            //String datagram = "123456789012341100003001120319004845531000";
            // 3002 �˿�
//             String datagram = "1234567890123456000030021203160048385230001000.00         ";
            /*
            Ʊ��״̬	1	    2:ʹ�ã�3:����
            ��ӡƱ����ʼ���	12	Ʊ����ʼ���
            ��ӡƱ�ݽ������	12	Ʊ�ݽ�����ţ��������Ÿ��ֶ�Ϊ�գ�
            �ɿ�֪ͨ����	18	�Ǳ����ƾ֤ʹ��ʱ��д
             */
            String datagram = "1234567890888891000040012000000010114000000010115120319004843521000";
           // String datagram = "123456789088889100005001100000000000003               268945.33       201203191234567890123333|60.00|C\n1234567890654321|133716.39|D";

            socketBlockClient.sendDataUntilRcv(StringUtils.rightPad(datagram.getBytes().length + 6 + "", 6, " ") + datagram, 6);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
