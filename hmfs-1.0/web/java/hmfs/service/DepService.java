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
 * Time: ����6:00
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
            throw new RuntimeException("��������Ϊ��!");
        }
        int len = 0;
        try {
            len = request.getBytes("GBK").length + 0;
            String totalLength = StringUtils.rightPad(String.valueOf(len + 6), 6, " ");
            return call(totalLength + request);
        } catch (Exception e) {
            throw new RuntimeException("�������!" + e);
        }
    }

    /**
     * @param request ����6λ���ĳ��ȼ�����
     * @return
     * @throws IOException
     */
    private String call(String request) throws IOException {
        if (request == null || "".equals(request)) {
            throw new IllegalArgumentException("��������");
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
            throw new RuntimeException("DEP ���Ӵ���", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
