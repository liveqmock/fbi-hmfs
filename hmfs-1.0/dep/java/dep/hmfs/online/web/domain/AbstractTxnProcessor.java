package dep.hmfs.online.web.domain;

import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.util.PropertyManager;

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

    public abstract TOA process(byte[] bytes) throws Exception;
}
