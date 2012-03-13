package hmfs.service;

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

    public String call(String actionCode) throws IOException {
        if (actionCode == null || actionCode.length() != 3) {
            logger.error("动作码参数错误！");
            throw new IllegalArgumentException("动作码参数错误！");
        }
        IBlockingConnection connection = null;
        String response = "";
        try {
            connection = new BlockingConnection(depServerIP, depServerPort);
            connection.setConnectionTimeoutMillis(depServerTimeout);
            connection.setEncoding("GBK");
            connection.setAutoflush(true);

            connection.write("ACTION:" + actionCode + "\r\n");
            int bodyLength = 0;
            String line = null;
            do {
                line = connection.readStringByDelimiter("\r\n").trim();
                //header
                if (line.startsWith("Length:")) {
                    bodyLength = new Integer(line.substring("Length:".length(), line.length()).trim());
                }
                if (line.length() > 0) {
                    System.out.println(line);
                }
            } while (line.length() > 0);

            //body
            if (bodyLength > 0) {
                response = connection.readStringByLength(bodyLength);
                logger.info(response);
            }
            return response;
        } catch (Exception e) {
            logger.error("DEP 链接错误！");
            throw new RuntimeException("DEP 链接错误！");
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
