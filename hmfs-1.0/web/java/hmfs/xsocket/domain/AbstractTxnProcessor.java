package hmfs.xsocket.domain;

import hmfs.xsocket.XSocketBlockClient;
import hmfs.xsocket.domain.base.TOA;
import pub.platform.advance.utils.PropertyManager;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ÏÂÎç7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTxnProcessor {

    protected XSocketBlockClient socketBlockClient;
    protected String depServerIP = PropertyManager.getProperty("xsocket_depserver_ip");
    protected int depServerPort = PropertyManager.getIntProperty("xsocket_depserver_port");
    protected int depServerTimeout = PropertyManager.getIntProperty("xsocket_depserver_timeout");

    public abstract TOA process(byte[] bytes) throws Exception;
}
