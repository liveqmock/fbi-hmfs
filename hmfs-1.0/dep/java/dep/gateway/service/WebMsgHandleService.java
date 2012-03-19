package dep.gateway.service;

import dep.ContainerManager;
import dep.hmfs.online.processor.web.WebAbstractTxnProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: 上午2:27
 * To change this template use File | Settings | File Templates.
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
            String[] fields = request.split("\\|");
            txnCode = fields[0];
            WebAbstractTxnProcessor abstractTxnProcessor = (WebAbstractTxnProcessor) ContainerManager.getBean("webTxn" + txnCode + "Processor");
            response = abstractTxnProcessor.run(request);
        } catch (Exception e) {
            logger.error("交易处理发生异常！", e);
            response = "9999|交易处理发生异常！" + e.getMessage();
        }

        response = txnCode + "|" + response;
        int len = 0;
        try {
            len = response.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            logger.error("组包错误!" + e);
            response = "9999";
            len = 4;
        }

        String totalLength = StringUtils.rightPad(String.valueOf(len + 6), 6, ' ');
        return (totalLength + response).getBytes("GBK");
    }
}
