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
    private HmSctMapper hmSctMapper;

    @Resource
    private TmpMsgoutLogMapper tmpMsgoutLogMapper;
    @Resource
    private TmpMsginLogMapper tmpMsginLogMapper;

    @Resource
    private HisMsgoutLogMapper hisMsgoutLogMapper;
    @Resource
    private HisMsginLogMapper hisMsginLogMapper;

    public HmSct getAppSysStatus(){
         return hmSctMapper.selectByPrimaryKey("1");
    }

    public List<TmpMsgoutLog>  selectTmpMsgoutList(String txnCode, String msgSn){
        TmpMsgoutLogExample example = new TmpMsgoutLogExample();
        example.createCriteria().andTxnCodeLike("%"+txnCode+"%").andMsgSnLike("%"+msgSn+"%");
        example.setOrderByClause("msg_sn, msg_sub_sn");
        return tmpMsgoutLogMapper.selectByExample(example);
    }

    public List<TmpMsginLog>  selectTmpMsginList(String txnCode, String msgSn){
        TmpMsginLogExample example = new TmpMsginLogExample();
        example.createCriteria().andTxnCodeLike("%"+txnCode+"%").andMsgSnLike("%"+msgSn+"%");
        example.setOrderByClause("msg_sn, msg_sub_sn");
        return tmpMsginLogMapper.selectByExample(example);
    }

    public List<HisMsgoutLog>  selectHisMsgoutList(String txnCode, String msgSn){
        HisMsgoutLogExample example = new HisMsgoutLogExample();
        example.createCriteria().andTxnCodeLike("%"+txnCode+"%").andMsgSnLike("%"+msgSn+"%");
        example.setOrderByClause("msg_sn, msg_sub_sn");
        return hisMsgoutLogMapper.selectByExample(example);
    }

    public List<HisMsginLog>  selectHisMsginList(String txnCode, String msgSn){
        HisMsginLogExample example = new HisMsginLogExample();
        example.createCriteria().andTxnCodeLike("%"+txnCode+"%").andMsgSnLike("%"+msgSn+"%");
        example.setOrderByClause("msg_sn, msg_sub_sn");
        return hisMsginLogMapper.selectByExample(example);
    }


    public void updateTmpMsgoutRecord(TmpMsgoutLog tmpMsgoutLog){
       tmpMsgoutLogMapper.updateByPrimaryKey(tmpMsgoutLog);
    }
}
