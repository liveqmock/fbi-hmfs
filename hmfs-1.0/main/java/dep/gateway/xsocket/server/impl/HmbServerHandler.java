package dep.gateway.xsocket.server.impl;

import dep.gateway.xsocket.server.ContentHandler;
import dep.gateway.xsocket.server.IServerHandler;
import dep.gateway.service.HmbMsgHandleService;
import dep.gateway.service.IMessageHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xsocket.connection.INonBlockingConnection;

import java.io.IOException;
import java.nio.BufferUnderflowException;

/**
 * 服务端数据处理类
 * 7位报文长度 + 4位交易码 + 报文正文
 * @author zxb
 */
@Component
public class HmbServerHandler implements IServerHandler {

    private static final Logger logger = LoggerFactory.getLogger(HmbServerHandler.class);
    private static final int DATA_LENGTH = 7;
    @Autowired
    private HmbMsgHandleService hmbMsgHandleService;
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

        int dataLength = 0;

        // 正文长度 + 4位交易编号长度
        dataLength = Integer.parseInt(connection.readStringByLength(DATA_LENGTH)) + 4;
        logger.info("【本地服务端】需接收完整报文长度：" + dataLength);

        connection.setHandler(new HmbContentHandler(this, hmbMsgHandleService, dataLength));

        return true;
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

class HmbContentHandler extends ContentHandler {

    private static Logger logger = LoggerFactory.getLogger(HmbContentHandler.class);

    private HmbMsgHandleService hmbMsgHandleService;

    HmbContentHandler(IServerHandler hdl, IMessageHandler msgHandleService, int dataLength) {
        super(hdl, msgHandleService, dataLength);
        this.hmbMsgHandleService = (HmbMsgHandleService) msgHandleService;
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
            logger.info("【本地服务端】接收报文内容:" + new String(bytesDatagram));

            // 处理接收到的报文，并生成响应报文
            byte[] resBytesMsg = hmbMsgHandleService.handleMessage(bytesDatagram);
            String dataLength = StringUtils.leftPad(String.valueOf(resBytesMsg.length - 4), 7, '0');

            logger.info("【本地服务端】发送报文长度:" + dataLength);
            logger.info("【本地服务端】发送报文内容:" + new String(resBytesMsg));
            nbc.write(dataLength);
            nbc.write(resBytesMsg);
            nbc.flush();
        }
        return true;
    }
}

