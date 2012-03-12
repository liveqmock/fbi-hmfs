package dep.hmfs.online.cmb;

import dep.gateway.hmb8583.HmbMessageFactory;
import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.hmb.domain.HmbMsg;
import dep.util.PropertyManager;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ÏÂÎç7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTxnProcessor {

    protected XSocketBlockClient socketBlockClient;
    protected String hmfsServerIP = PropertyManager.getProperty("socket_server_ip_hmfs");
    protected int hmfsServerPort = PropertyManager.getIntProperty("socket_server_port_hmfs");
    protected int hmfsServerTimeout = PropertyManager.getIntProperty("socket_server_timeout");

    public Map<String, List<HmbMsg>> sendDataUntilRcv(byte[] bytes, HmbMessageFactory mf) throws Exception {
        if (socketBlockClient == null) {
            socketBlockClient = new XSocketBlockClient(hmfsServerIP, hmfsServerPort, hmfsServerTimeout);
        }
        byte[] hmfsDatagram = socketBlockClient.sendDataUntilRcv(bytes, 7);
        return mf.unmarshal(hmfsDatagram);
    }

    public abstract TOA process(String txnSerialNo, byte[] bytes) throws Exception;


}
