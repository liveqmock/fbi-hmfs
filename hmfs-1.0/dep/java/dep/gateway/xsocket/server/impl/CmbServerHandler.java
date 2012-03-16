package dep.gateway.xsocket.server.impl;

import dep.gateway.xsocket.server.ContentHandler;
import dep.gateway.xsocket.server.IServerHandler;
import dep.gateway.service.CmbMsgHandleService;
import dep.gateway.service.IMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xsocket.connection.INonBlockingConnection;

import java.io.IOException;
import java.nio.BufferUnderflowException;

/**
 * ��������ݴ����� �����м�ҵ��ƽ̨
 * 6λ���ĳ���
 *
 * @author zxb
 */
@Component
public class CmbServerHandler implements IServerHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebServerHandler.class);
    private static final int DATA_LENGTH_FIELD_LENGTH = 6;
    @Autowired
    private CmbMsgHandleService cmbMsgHandleService;

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
        // ���ĳ���
        int dataLength = Integer.parseInt(connection.readStringByLength(DATA_LENGTH_FIELD_LENGTH).trim()) - DATA_LENGTH_FIELD_LENGTH;
        logger.info("�����ط���ˡ�������������ĳ��ȣ�" + dataLength);

        connection.setHandler(new CmbContentHandler(this, cmbMsgHandleService, dataLength));
        return false;
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


class CmbContentHandler extends ContentHandler {

    private static Logger logger = LoggerFactory.getLogger(CmbContentHandler.class);
    private CmbMsgHandleService cmbMsgHandleService;

    CmbContentHandler(IServerHandler hdl, IMessageHandler msgHandleService, int dataLength) {
        super(hdl, msgHandleService, dataLength);
        cmbMsgHandleService = (CmbMsgHandleService) msgHandleService;
    }

    public boolean onData(INonBlockingConnection nbc) throws IOException {

        int available = nbc.available();
        // remaining�������ձ��ĳ��ȣ���ʼֵΪdataLength
        int lengthToRead = remaining;
        if (available < remaining) {
            lengthToRead = available;
        }

        byteArrayOutStream.write(nbc.readBytesByLength(lengthToRead));
        remaining -= lengthToRead;

        if (remaining == 0) {

            byteArrayOutStream.flush();
            //nbc.setAttachment(null);
            bytesDatagram = byteArrayOutStream.toByteArray();
            logger.info("�����ط���ˡ����ձ�������:" + new String(bytesDatagram));
            // ������յ��ı��ģ���������Ӧ����
            byte[] resBytesMsg = cmbMsgHandleService.handleMessage(bytesDatagram);
            logger.info("�����ط���ˡ����ͱ�������:" + new String(resBytesMsg));
            logger.info("�����ط���ˡ����ͱ��ĳ���:" + resBytesMsg.length);
            nbc.write(resBytesMsg);
            nbc.flush();
            //byteArrayOutStream.reset();
        }
        return false;
    }
}

