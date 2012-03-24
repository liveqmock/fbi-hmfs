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
public abstract class WebAbstractHmbProductTxnProcessor extends  WebAbstractHmbTxnProcessor{
    protected static final Logger logger = LoggerFactory.getLogger(WebAbstractHmbProductTxnProcessor.class);

    @Override
    public String run(String request){
         return process(request);
    }

    protected abstract String process(String request);

}
