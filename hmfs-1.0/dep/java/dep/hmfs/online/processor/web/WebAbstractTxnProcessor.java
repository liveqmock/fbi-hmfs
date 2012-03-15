package dep.hmfs.online.processor.web;

import dep.gateway.hmb8583.HmbMessageFactory;
import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.util.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 下午7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class WebAbstractTxnProcessor {
    protected static final Logger logger = LoggerFactory.getLogger(WebAbstractTxnProcessor.class);

    @Resource
    protected HmbMessageFactory messageFactory;

    protected XSocketBlockClient socketBlockClient;
    protected String hmfsServerIP = PropertyManager.getProperty("socket_server_ip_hmfs");
    protected int hmfsServerPort = PropertyManager.getIntProperty("socket_server_port_hmfs");
    protected int hmfsServerTimeout = PropertyManager.getIntProperty("socket_server_timeout");

    public abstract String process(String request) throws Exception;

    protected Map<String, List<HmbMsg>> sendDataUntilRcv(byte[] bytes) {
        byte[] hmfsDatagram;
        try {
            socketBlockClient = new XSocketBlockClient(hmfsServerIP, hmfsServerPort, hmfsServerTimeout);
            hmfsDatagram = socketBlockClient.sendDataUntilRcvToHmb(bytes);
            return messageFactory.unmarshal(hmfsDatagram);
        } catch (Exception e) {
            throw new RuntimeException("通讯或解包错误.", e);
        } finally {
            try {
                socketBlockClient.close();
            } catch (IOException e) {
                //
            }
        }
    }

}
