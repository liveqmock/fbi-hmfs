package dep.hmfs.online.processor.web;

import common.repository.hmfs.model.HmChkActExample;
import common.repository.hmfs.model.HmChkTxnExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class WebAbstractHmbProductTxnProcessor extends  WebAbstractHmbTxnProcessor{
    protected static final Logger logger = LoggerFactory.getLogger(WebAbstractHmbProductTxnProcessor.class);


    @Override
    @Transactional
    public String run(String request){
        //TODO ���ϵͳ״̬
         return process(request);
    }

    protected abstract String process(String request);


    //�������������������
    protected void deleteOldActChkDataByTxnDate(String txnDate, String sendSysId){
        HmChkActExample example = new HmChkActExample();
        example.createCriteria().andTxnDateEqualTo(txnDate).andSendSysIdEqualTo(sendSysId);
        hmChkActMapper.deleteByExample(example);
    }
    //�������������������
    protected void deleteOldTxnChkDataByTxnDate(String txnDate, String sendSysId){
        HmChkTxnExample example = new HmChkTxnExample();
        example.createCriteria().andTxnDateEqualTo(txnDate).andSendSysIdEqualTo(sendSysId);
        hmChkTxnMapper.deleteByExample(example);
    }
}
