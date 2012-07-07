package dep.hmfs.online.processor.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class WebAbstractHmbProductTxnProcessor extends  WebAbstractHmbTxnProcessor{
    protected static final Logger logger = LoggerFactory.getLogger(WebAbstractHmbProductTxnProcessor.class);


    @Override
    @Transactional
    public String run(String request){
        //TODO 检查系统状态
         return process(request);
    }

    protected abstract String process(String request);

}
