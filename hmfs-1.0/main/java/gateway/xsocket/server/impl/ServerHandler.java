package gateway.xsocket.server.impl;

import gateway.xsocket.server.IServerHandler;
import gateway.xsocket.service.SoktServerMsgService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;

/**
 * ��������ݴ�����
 * 7λ���ĳ��� + 4λ������ + ��������
 * @author zxb
 */
public class ServerHandler implements IServerHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    private static final int DATA_LENGTH = 7;
    private SoktServerMsgService soktServerMsgService;
    // = ConfigParser.createFromClasspathConfig("/j8583-config.xml")
    /**
     * ���ӵĳɹ�ʱ�Ĳ���
     */
    @Override
    public boolean onConnect(INonBlockingConnection nbc) throws IOException,
            BufferUnderflowException {
        String remoteName = nbc.getRemoteAddress().getHostName();
        logger.info("�����ط���ˡ�Զ������: " + remoteName + "�뱾�������������ӣ�");
        return true;
    }

    /**
     * ���ӶϿ�ʱ�Ĳ���
     */
    @Override
    public boolean onDisconnect(INonBlockingConnection nbc) throws IOException {
        logger.info("�����ط���ˡ�Զ�������뱾�������Ͽ����ӣ�");
        return true;
    }

    public boolean onData(INonBlockingConnection connection) throws IOException {

        logger.info("�����ط���ˡ��ɽ��ձ��ĳ��ȣ�" + connection.available());

        int dataLength = 0;

        // ���ĳ��� + 4λ���ױ�ų���
        dataLength = Integer.parseInt(connection.readStringByLength(DATA_LENGTH)) + 4;
        logger.info("�����ط���ˡ�������������ĳ��ȣ�" + dataLength);

        connection.setHandler(new ContentHandler(this, soktServerMsgService, dataLength));

        return true;
    }

    /**
     * ������ʱ�Ĵ����¼�
     */
    @Override
    public boolean onIdleTimeout(INonBlockingConnection connection) throws IOException {
        logger.error("�����ط���ˡ����г�ʱ��");
        return true;
    }

    /**
     * ���ӳ�ʱ�����¼�
     */
    @Override
    public boolean onConnectionTimeout(INonBlockingConnection connection) throws IOException {
        logger.error("�����ؿͻ��ˡ���Զ���������ӳ�ʱ��");
        return true;
    }

    @Override
    public boolean onConnectException(INonBlockingConnection iNonBlockingConnection, IOException e) throws IOException {
        logger.error("�����ؿͻ��ˡ���Զ���������ӷ����쳣��");
        return true;
    }

    public SoktServerMsgService getSoktServerMsgService() {
        return soktServerMsgService;
    }

    public void setSoktServerMsgService(SoktServerMsgService soktServerMsgService) {
        this.soktServerMsgService = soktServerMsgService;
    }
}


class ContentHandler implements IDataHandler {

    private static Logger logger = LoggerFactory.getLogger(ContentHandler.class);

    //private StringBuilder strBuilder = new StringBuilder();
    private ByteArrayOutputStream byteArrayOutStream;
    private byte[] bytesDatagram;
    private SoktServerMsgService soktServerMsgService;
    private int remaining = 0;
    private ServerHandler hdl = null;
    private int destPos = 0;

    public ContentHandler(ServerHandler hdl, SoktServerMsgService soktServerMsgService, int dataLength) {
        this.hdl = hdl;
        remaining = dataLength;
        this.soktServerMsgService = soktServerMsgService;
        byteArrayOutStream = new ByteArrayOutputStream();
        // bytesDatagram = new byte[dataLength];
    }

    public boolean onData(INonBlockingConnection nbc) throws IOException {
        int available = nbc.available();

        int lengthToRead = remaining;
        if (available < remaining) {
            lengthToRead = available;
        }

        //String buffers = nbc.readStringByLength(lengthToRead);
        //strBuilder.append(buffers);
        byteArrayOutStream.write(nbc.readBytesByLength(lengthToRead));
        remaining -= lengthToRead;

        if (remaining == 0) {

            byteArrayOutStream.flush();
            nbc.setAttachment(hdl);
           // String datagram = null; //  strBuilder.toString();
            bytesDatagram = byteArrayOutStream.toByteArray();
            logger.info("�����ط���ˡ����ձ�������:" + new String(bytesDatagram));

            // ������յ��ı��ģ���������Ӧ����
            byte[] resBytesMsg = soktServerMsgService.handleMessage(bytesDatagram);
            String dataLength = StringUtils.leftPad(String.valueOf(resBytesMsg.length - 4), 7, '0');

            logger.info("�����ط���ˡ����ͱ��ĳ���:" + dataLength);
            logger.info("�����ط���ˡ����ͱ�������:" + new String(resBytesMsg));
            nbc.write(dataLength);
            nbc.write(resBytesMsg);
            nbc.flush();
        }
        return true;
    }
}
