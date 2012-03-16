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
 * 服务端数据处理类 监听中间业务平台
 * 6位报文长度
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
     * 连接的成功时的操作
     */
    @Override
    public boolean onConnect(INonBlockingConnection nbc) throws IOException,
            BufferUnderflowException {
        String remoteName = nbc.getRemoteAddress().getHostName();
        logger.info("【本地服务端】远程主机: " + remoteName + "与本地主机建立连接！");
        return true;
    }

    /**
     * 连接断开时的操作
     */
    @Override
    public boolean onDisconnect(INonBlockingConnection nbc) throws IOException {
        logger.info("【本地服务端】远程主机与本地主机断开连接！");
        return true;
    }

    public boolean onData(INonBlockingConnection connection) throws IOException {

        logger.info("【本地服务端】可接收报文长度：" + connection.available());
        // 报文长度
        int dataLength = Integer.parseInt(connection.readStringByLength(DATA_LENGTH_FIELD_LENGTH).trim()) - DATA_LENGTH_FIELD_LENGTH;
        logger.info("【本地服务端】需接收完整报文长度：" + dataLength);

        connection.setHandler(new CmbContentHandler(this, cmbMsgHandleService, dataLength));
        return false;
    }

    /**
     * 请求处理超时的处理事件
     */
    @Override
    public boolean onIdleTimeout(INonBlockingConnection connection) throws IOException {
        logger.error("【本地服务端】空闲超时。");
        return true;
    }

    /**
     * 连接超时处理事件
     */
    @Override
    public boolean onConnectionTimeout(INonBlockingConnection connection) throws IOException {
        logger.error("【本地客户端】与远程主机连接超时。");
        return true;
    }

    @Override
    public boolean onConnectException(INonBlockingConnection iNonBlockingConnection, IOException e) throws IOException {
        logger.error("【本地客户端】与远程主机连接发生异常。");
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
        // remaining：待接收报文长度，初始值为dataLength
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
            logger.info("【本地服务端】接收报文内容:" + new String(bytesDatagram));
            // 处理接收到的报文，并生成响应报文
            byte[] resBytesMsg = cmbMsgHandleService.handleMessage(bytesDatagram);
            logger.info("【本地服务端】发送报文内容:" + new String(resBytesMsg));
            logger.info("【本地服务端】发送报文长度:" + resBytesMsg.length);
            nbc.write(resBytesMsg);
            nbc.flush();
            //byteArrayOutStream.reset();
        }
        return false;
    }
}

