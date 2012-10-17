package dep.gateway.xsocket.server.impl;

import dep.gateway.service.HmbMsgHandleService;
import dep.gateway.service.IMessageHandler;
import dep.gateway.xsocket.server.ContentHandler;
import dep.gateway.xsocket.server.IServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xsocket.connection.INonBlockingConnection;

import java.io.IOException;
import java.nio.BufferUnderflowException;

/**
 * ��������ݴ����� �������ֿܾͻ���
 * 7λ���ĳ��� + 4λ������ + ��������
 * @author zxb
 */
@Component
public class HmbServerHandler implements IServerHandler {

    private static final Logger logger = LoggerFactory.getLogger(HmbServerHandler.class);
    private static final int DATA_LENGTH = 7;
    @Autowired
    private HmbMsgHandleService hmbMsgHandleService;
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

        connection.setHandler(new HmbContentHandler(this, hmbMsgHandleService, dataLength));

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

class HmbContentHandler extends ContentHandler {

    private static Logger logger = LoggerFactory.getLogger(HmbContentHandler.class);

    private HmbMsgHandleService hmbMsgHandleService;

    HmbContentHandler(IServerHandler hdl, IMessageHandler msgHandleService, int dataLength) {
        super(hdl, msgHandleService, dataLength);
        this.hmbMsgHandleService = (HmbMsgHandleService) msgHandleService;
    }

    public boolean onData(INonBlockingConnection nbc) throws IOException {
        int available = nbc.available();
        logger.info("�����ط����:onData()�����ձ��ĳ���:" + available);

        int lengthToRead = remaining;
        if (available < remaining) {
            lengthToRead = available;
        }
        byte[] newBytes = nbc.readBytesByLength(lengthToRead);
        logger.info("�����ط����:onData()�����ձ�������:" + new String(newBytes));

        byteArrayOutStream.write(newBytes);
        remaining -= lengthToRead;

        if (remaining == 0) {

            byteArrayOutStream.flush();
            // 2012-10-17 ��CbsServerHandlerһ�� ��ע�⡿�������
//            nbc.setAttachment(hdl);
            bytesDatagram = byteArrayOutStream.toByteArray();
            logger.info("�����ط���ˡ����ձ�������:" + new String(bytesDatagram));

            // ������յ��ı��ģ���������Ӧ����
            byte[] resBytesMsg = hmbMsgHandleService.handleMessage(bytesDatagram);
            logger.info("�����ط���ˡ����ͱ�������:" + new String(resBytesMsg));
            nbc.write(resBytesMsg);
            nbc.flush();
            byteArrayOutStream.reset();
        }
        // 2012-10-17 ��CbsServerHandlerһ��
        return false;
    }
}

