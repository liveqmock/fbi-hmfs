package dep.gateway.xsocket.server.impl;

import dep.gateway.xsocket.server.ContentHandler;
import dep.gateway.xsocket.server.IServerHandler;
import dep.gateway.service.CbsMsgHandleService;
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
public class CbsServerHandler implements IServerHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebServerHandler.class);
    private static final int DATA_LENGTH_FIELD_LENGTH = 6;
    @Autowired
    private CbsMsgHandleService cbsMsgHandleService;

    /**
     * ���ӵĳɹ�ʱ�Ĳ���
     */
    @Override
    public boolean onConnect(INonBlockingConnection nbc) throws IOException,
            BufferUnderflowException {
        String remoteName = nbc.getRemoteAddress().getHostAddress();
        logger.info("��CBS���ط���ˡ�Զ������: " + remoteName + "�뱾�������������ӣ�");
        return true;
    }

    /**
     * ���ӶϿ�ʱ�Ĳ���
     */
    @Override
    public boolean onDisconnect(INonBlockingConnection nbc) throws IOException {
        logger.info("��CBS���ط���ˡ�Զ�������뱾�������Ͽ����ӣ�");
        return true;
    }

    public boolean onData(INonBlockingConnection connection) throws IOException {

        logger.info("��CBS���ط���ˡ����οɽ��ձ��ĳ��ȣ�" + connection.available());
        // ���ĳ���
        int dataLength = Integer.parseInt(connection.readStringByLength(DATA_LENGTH_FIELD_LENGTH).trim()) - DATA_LENGTH_FIELD_LENGTH;
        logger.info("��CBS���ط���ˡ�������������ĳ��ȣ�" + dataLength);
        connection.setHandler(new CbsContentHandler(this, cbsMsgHandleService, dataLength));
        return true;
    }

    /**
     * ������ʱ�Ĵ����¼�
     */
    @Override
    public boolean onIdleTimeout(INonBlockingConnection connection) throws IOException {
        logger.error("��CBS���ط���ˡ����г�ʱ��");
        return true;
    }

    /**
     * ���ӳ�ʱ�����¼�
     */
    @Override
    public boolean onConnectionTimeout(INonBlockingConnection connection) throws IOException {
        logger.error("��CBS���ط���ˡ���Զ���������ӳ�ʱ��");
        return true;
    }

    @Override
    public boolean onConnectException(INonBlockingConnection iNonBlockingConnection, IOException e) throws IOException {
        logger.error("��CBS���ط���ˡ���Զ���������ӷ����쳣��");
        return true;
    }

}


class CbsContentHandler extends ContentHandler {

    private static Logger logger = LoggerFactory.getLogger(CbsContentHandler.class);
    private CbsMsgHandleService cbsMsgHandleService;

    CbsContentHandler(IServerHandler hdl, IMessageHandler msgHandleService, int dataLength) {
        super(hdl, msgHandleService, dataLength);
        cbsMsgHandleService = (CbsMsgHandleService) msgHandleService;
    }

    public boolean onData(INonBlockingConnection nbc) throws IOException {

        int available = nbc.available();
        logger.info("��CBS���ط����:onData()�����οɽ��ձ��ĳ���:" + available);

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
            logger.info("��CBS���ط���ˡ��ѽ���������������:" + new String(bytesDatagram));
            // ������յ��ı��ģ���������Ӧ����
            byte[] resBytesMsg = cbsMsgHandleService.handleMessage(bytesDatagram);
            logger.info("��CBS���ط���ˡ���Ӧ��������:" + new String(resBytesMsg));
            nbc.write(resBytesMsg);
            nbc.flush();
            byteArrayOutStream.reset();
            // ҵ��������󣬷���true
            return true;
        }
        return false;
    }
}

