package dep.hmfs.online.processor.web;

import common.repository.hmfs.dao.HmChkActMapper;
import common.repository.hmfs.dao.HmChkTxnMapper;
import common.repository.hmfs.dao.hmfs.HmCmnMapper;
import common.repository.hmfs.model.HmChkActExample;
import common.repository.hmfs.model.HmChkTxnExample;
import dep.hmfs.online.service.hmb.HmbSysTxnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class WebAbstractHmbProductTxnProcessor extends  WebAbstractHmbTxnProcessor{
    protected static final Logger logger = LoggerFactory.getLogger(WebAbstractHmbProductTxnProcessor.class);

    @Resource
    protected HmbSysTxnService hmbSysTxnService;

    @Resource
    protected HmCmnMapper hmCmnMapper;

    @Resource
    protected HmChkActMapper hmChkActMapper;

    @Resource
    protected HmChkTxnMapper hmChkTxnMapper;

    @Override
    public String run(String request){
        //TODO 检查系统状态
         return process(request);
    }

    protected abstract String process(String request);


    //按日期清除余额对帐数据
    protected void deleteOldActChkDataByTxnDate(String txnDate, String sendSysId){
        HmChkActExample example = new HmChkActExample();
        example.createCriteria().andTxnDateEqualTo(txnDate).andSendSysIdEqualTo(sendSysId);
        hmChkActMapper.deleteByExample(example);
    }
    //按日期清除余额对帐数据
    protected void deleteOldTxnChkDataByTxnDate(String txnDate, String sendSysId){
        HmChkTxnExample example = new HmChkTxnExample();
        example.createCriteria().andTxnDateEqualTo(txnDate).andSendSysIdEqualTo(sendSysId);
        hmChkTxnMapper.deleteByExample(example);
    }
}
