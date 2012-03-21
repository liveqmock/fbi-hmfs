package dep.hmfs.online.processor.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: обнГ7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class WebAbstractTxnProcessor {
    protected static final Logger logger = LoggerFactory.getLogger(WebAbstractTxnProcessor.class);

    public abstract String run(String request);

    protected  String getRequestTxnCode(String request){
        String[] fields = request.split("\\|");
        return fields[0];
    }
}
