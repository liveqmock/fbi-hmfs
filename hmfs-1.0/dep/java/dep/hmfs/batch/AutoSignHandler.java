package dep.hmfs.batch;

import dep.ContainerManager;
import dep.hmfs.online.processor.web.WebAbstractTxnProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-4-18
 * Time: ����9:22
 * To change this template use File | Settings | File Templates.
 */
public class AutoSignHandler {
    private static final Logger logger = LoggerFactory.getLogger(AutoSignHandler.class);

    public void signon(){
        String txnCode = "1007000";
        String response = "";
        try {
            WebAbstractTxnProcessor abstractTxnProcessor = (WebAbstractTxnProcessor) ContainerManager.getBean("webTxn" + txnCode + "Processor");
            response = abstractTxnProcessor.run(txnCode);
        } catch (Exception e) {
            response = "[�������״������쳣��][�Զ�ǩ��]��" + e.getMessage();
            logger.error(response, e);
        }
        logger.info("[�������״�����]:" + response);
    }
    public void signout(){
        String txnCode = "1007001";
        String response = "";
        try {
            WebAbstractTxnProcessor abstractTxnProcessor = (WebAbstractTxnProcessor) ContainerManager.getBean("webTxn" + txnCode + "Processor");
            response = abstractTxnProcessor.run(txnCode);
        } catch (Exception e) {
            response = "[�������״������쳣��][�Զ�ǩ��]��" + e.getMessage();
            logger.error(response, e);
        }
        logger.info("[�������״�����]:" + response);
    }
}
