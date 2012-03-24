package dep.gateway.service;

import dep.ContainerManager;
import dep.hmfs.online.processor.web.WebAbstractTxnProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * 处理WEB APP发送的交易请求.
 * User: zhanrui
 * Date: 11-8-18
 */

@Service
public class WebMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebMsgHandleService.class);

    @Override
    public byte[] handleMessage(byte[] bytes) throws UnsupportedEncodingException {
        String response = "";
        String txnCode = "";
        try {
            String request = new String(bytes);
            logger.info("DEP接收到WEB请求：" + request);
            String[] fields = request.split("\\|");
            txnCode = fields[0];
            WebAbstractTxnProcessor abstractTxnProcessor = (WebAbstractTxnProcessor) ContainerManager.getBean("webTxn" + txnCode + "Processor");
            response = abstractTxnProcessor.run(request);
        } catch (Exception e) {
            logger.error("交易处理发生异常！", e);
            response = "9999|交易处理发生异常！" + e.getMessage();
        }

        int len;
        try {
            len = response.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            logger.error("组包错误!" + e);
            response = "9999";
            len = 4;
        }

        String totalLength = StringUtils.rightPad(String.valueOf(len + 6), 6, ' ');
        response = totalLength + response;
        logger.info("DEP发送到WEB响应：" + response);
        return response.getBytes("GBK");
    }
}
