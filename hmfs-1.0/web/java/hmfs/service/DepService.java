package hmfs.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xsocket.connection.BlockingConnection;
import org.xsocket.connection.IBlockingConnection;
import pub.platform.advance.utils.PropertyManager;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-13
 * Time: 下午6:00
 * To change this template use File | Settings | File Templates.
 */
@Service
public class DepService {
    private static final Logger logger = LoggerFactory.getLogger(DepService.class);

    private String depServerIP = PropertyManager.getProperty("xsocket_depserver_ip");
    private int depServerPort = PropertyManager.getIntProperty("xsocket_depserver_port");
    private int depServerTimeout = PropertyManager.getIntProperty("xsocket_depserver_timeout");


    public String process(String request) {
        if (request == null) {
            throw new RuntimeException("参数不能为空!");
        }
        int len = 0;
        try {
            len = request.getBytes("GBK").length + 0;
            String totalLength = StringUtils.rightPad(String.valueOf(len + 6), 6, " ");
            return call(totalLength + request);
        } catch (Exception e) {
            throw new RuntimeException("组包错误!" + e);
        }
    }

    /**
     * @param request 包括6位报文长度及内容
     * @return
     * @throws IOException
     */
    private String call(String request) throws IOException {
        if (request == null || "".equals(request)) {
            throw new IllegalArgumentException("参数错误！");
        }
        IBlockingConnection connection = null;
        String response = "";
        try {
            connection = new BlockingConnection(depServerIP, depServerPort);
            connection.setConnectionTimeoutMillis(depServerTimeout);
            connection.setEncoding("GBK");
            connection.setAutoflush(true);

            connection.write(request);
            int bodyLength = 0;
            String line = null;

            line = connection.readStringByLength(6);
            bodyLength = new Integer(line.trim()) - 6;

            if (bodyLength > 0) {
                response = connection.readStringByLength(bodyLength);
                logger.info(response);
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("DEP 链接错误！", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
