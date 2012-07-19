package hmfs.service;

import common.repository.hmfs.dao.*;
import common.repository.hmfs.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-12
 * Time: ÏÂÎç9:55
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppMngService {
    private static final Logger logger = LoggerFactory.getLogger(AppMngService.class);

    @Resource
    private HmSysCtlMapper hmSysCtlMapper;

    @Resource
    private TmpMsgOutMapper tmpMsgOutMapper;
    @Resource
    private TmpMsgInMapper tmpMsgInMapper;

    @Resource
    private HmMsgOutMapper hmMsgOutMapper;
    @Resource
    private HmMsgInMapper hmMsgInMapper;

    public HmSysCtl getAppSysStatus(){
         return hmSysCtlMapper.selectByPrimaryKey("1");
    }

    public List<TmpMsgOut>  selectTmpMsgoutList(String startDate, String endDate, String txnCode, String msgSn){
        TmpMsgOutExample example = new TmpMsgOutExample();
        example.createCriteria().andMsgProcDateBetween(startDate, endDate).andTxnCodeLike("%"+txnCode+"%").andMsgSnLike("%"+msgSn+"%");
        example.setOrderByClause("msg_sn, msg_sub_sn");
        return tmpMsgOutMapper.selectByExample(example);
    }

    public List<TmpMsgIn>  selectTmpMsginList(String startDate, String endDate, String txnCode, String msgSn){
        TmpMsgInExample example = new TmpMsgInExample();
        example.createCriteria().andMsgProcDateBetween(startDate, endDate).andTxnCodeLike("%"+txnCode+"%").andMsgSnLike("%"+msgSn+"%");
        example.setOrderByClause("msg_sn, msg_sub_sn");
        return tmpMsgInMapper.selectByExample(example);
    }

    public List<TmpMsgOut>  selectTmpMsgoutList(String txnCode, String msgSn){
        TmpMsgOutExample example = new TmpMsgOutExample();
        example.createCriteria().andTxnCodeLike("%"+txnCode+"%").andMsgSnLike("%"+msgSn+"%");
        example.setOrderByClause("msg_sn, msg_sub_sn");
        return tmpMsgOutMapper.selectByExample(example);
    }

    public List<TmpMsgIn>  selectTmpMsginList(String txnCode, String msgSn){
        TmpMsgInExample example = new TmpMsgInExample();
        example.createCriteria().andTxnCodeLike("%"+txnCode+"%").andMsgSnLike("%"+msgSn+"%");
        example.setOrderByClause("msg_sn, msg_sub_sn");
        return tmpMsgInMapper.selectByExample(example);
    }

    public List<HmMsgOut> selectHmMsgoutList(String startDate, String endDate, String txnCode, String msgSn){
        HmMsgOutExample example = new HmMsgOutExample();
        example.createCriteria().andMsgProcDateBetween(startDate, endDate).andTxnCodeLike("%" + txnCode + "%").andMsgSnLike("%"+msgSn+"%");
        example.setOrderByClause("msg_sn, msg_sub_sn");
        return hmMsgOutMapper.selectByExample(example);
    }

    public List<HmMsgIn> selectHmMsginList(String startDate, String endDate, String txnCode, String msgSn){
        HmMsgInExample example = new HmMsgInExample();
        example.createCriteria().andMsgProcDateBetween(startDate, endDate).andTxnCodeLike("%" + txnCode + "%").andMsgSnLike("%"+msgSn+"%");
        example.setOrderByClause("msg_sn, msg_sub_sn");
        return hmMsgInMapper.selectByExample(example);
    }


    public void updateTmpMsgoutRecord(TmpMsgOut tmpMsgOut){
       tmpMsgOutMapper.updateByPrimaryKey(tmpMsgOut);
    }
}
