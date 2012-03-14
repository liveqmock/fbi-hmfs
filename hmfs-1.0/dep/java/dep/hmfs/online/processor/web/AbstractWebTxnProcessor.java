package dep.hmfs.online.processor.web;

import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.util.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ÏÂÎç7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractWebTxnProcessor {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractWebTxnProcessor.class);

    protected XSocketBlockClient socketBlockClient;
    protected String hmfsServerIP = PropertyManager.getProperty("socket_server_ip_hmfs");
    protected int hmfsServerPort = PropertyManager.getIntProperty("socket_server_port_hmfs");
    protected int hmfsServerTimeout = PropertyManager.getIntProperty("socket_server_timeout");

    public abstract String process(String request) throws Exception;
}
