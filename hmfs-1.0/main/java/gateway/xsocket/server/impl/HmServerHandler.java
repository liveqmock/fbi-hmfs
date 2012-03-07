package gateway.xsocket.server.impl;

import gateway.xsocket.server.ContentHandler;
import gateway.xsocket.server.IServerHandler;
import gateway.xsocket.service.HmMsgHandleService;
import gateway.xsocket.service.IMessageHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xsocket.connection.INonBlockingConnection;

import java.io.IOException;
import java.nio.BufferUnderflowException;

/**
 * ��������ݴ�����
 * 7λ���ĳ��� + 4λ������ + ��������
 * @author zxb
 */
@Component
public class HmServerHandler implements IServerHandler {

    private static final Logger logger = LoggerFactory.getLogger(HmServerHandler.class);
    private static final int DATA_LENGTH = 7;
    @Autowired
    private HmMsgHandleService hmMsgHandleService;
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

        connection.setHandler(new HmContentHandler(this, hmMsgHandleService, dataLength));

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
}

class HmContentHandler extends ContentHandler {

    private static Logger logger = LoggerFactory.getLogger(HmContentHandler.class);

    private HmMsgHandleService hmMsgHandleService;

    HmContentHandler(IServerHandler hdl, IMessageHandler msgHandleService, int dataLength) {
        super(hdl, msgHandleService, dataLength);
        this.hmMsgHandleService = (HmMsgHandleService) msgHandleService;
    }

    public boolean onData(INonBlockingConnection nbc) throws IOException {
        int available = nbc.available();

        int lengthToRead = remaining;
        if (available < remaining) {
            lengthToRead = available;
        }

        byteArrayOutStream.write(nbc.readBytesByLength(lengthToRead));
        remaining -= lengthToRead;

        if (remaining == 0) {

            byteArrayOutStream.flush();
            nbc.setAttachment(hdl);
           // String datagram = null; //  strBuilder.toString();
            bytesDatagram = byteArrayOutStream.toByteArray();
            logger.info("�����ط���ˡ����ձ�������:" + new String(bytesDatagram));

            // ������յ��ı��ģ���������Ӧ����
            byte[] resBytesMsg = hmMsgHandleService.handleMessage(bytesDatagram);
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

