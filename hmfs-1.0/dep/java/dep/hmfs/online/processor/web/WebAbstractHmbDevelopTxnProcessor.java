package dep.hmfs.online.processor.web;

import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.util.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ÏÂÎç7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class WebAbstractHmbDevelopTxnProcessor extends WebAbstractHmbTxnProcessor{
    protected static final Logger logger = LoggerFactory.getLogger(WebAbstractHmbDevelopTxnProcessor.class);

    protected XSocketBlockClient socketBlockClient;
    protected String hmfsServerIP = PropertyManager.getProperty("socket_server_ip_hmfs");
    protected int hmfsServerPort = PropertyManager.getIntProperty("socket_server_port_hmfs");
    protected int hmfsServerTimeout = PropertyManager.getIntProperty("socket_server_timeout");

    @Override
    @Transactional
    public String run(String request){
        return process(request);
    }
    protected abstract String process(String request);
}
