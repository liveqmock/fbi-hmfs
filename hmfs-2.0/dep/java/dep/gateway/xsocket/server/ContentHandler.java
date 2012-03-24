package dep.gateway.xsocket.server;

import dep.gateway.service.IMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class ContentHandler implements IDataHandler {

    private static Logger logger = LoggerFactory.getLogger(ContentHandler.class);

    protected ByteArrayOutputStream byteArrayOutStream;
    protected byte[] bytesDatagram;
    protected IMessageHandler msgHandleService;
    protected int remaining = 0;
    protected IServerHandler hdl = null;
    protected int destPos = 0;

    public ContentHandler(IServerHandler hdl, IMessageHandler msgHandleService, int dataLength) {
        this.hdl = hdl;
        remaining = dataLength;
        this.msgHandleService = msgHandleService;
        byteArrayOutStream = new ByteArrayOutputStream();
    }

    public abstract boolean onData(INonBlockingConnection nbc) throws IOException;

    public ByteArrayOutputStream getByteArrayOutStream() {
        return byteArrayOutStream;
    }

    public void setByteArrayOutStream(ByteArrayOutputStream byteArrayOutStream) {
        this.byteArrayOutStream = byteArrayOutStream;
    }

    public byte[] getBytesDatagram() {
        return bytesDatagram;
    }

    public void setBytesDatagram(byte[] bytesDatagram) {
        this.bytesDatagram = bytesDatagram;
    }

    public int getDestPos() {
        return destPos;
    }

    public void setDestPos(int destPos) {
        this.destPos = destPos;
    }

    public IServerHandler getHdl() {
        return hdl;
    }

    public void setHdl(IServerHandler hdl) {
        this.hdl = hdl;
    }

    public IMessageHandler getMsgHandleService() {
        return msgHandleService;
    }

    public void setMsgHandleService(IMessageHandler msgHandleService) {
        this.msgHandleService = msgHandleService;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }
}
