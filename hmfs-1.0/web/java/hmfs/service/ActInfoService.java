package hmfs.service;

import common.repository.hmfs.dao.*;
import common.repository.hmfs.model.*;
import hmfs.common.model.ActinfoQryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-12
 * Time: œ¬ŒÁ9:55
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ActInfoService {
    private static final Logger logger = LoggerFactory.getLogger(ActInfoService.class);

    @Resource
    private HmSctMapper hmSctMapper;

    @Resource
    private HmActinfoFundMapper actinfoFundMapper;

    @Resource
    private HmActinfoCbsMapper actinfoCbsMapper;

    @Resource
    private TxnFundLogMapper fundLogMapper;

    @Resource
    private TxnCbsLogMapper cbsLogMapper;


    public HmSct getAppSysStatus() {
        return hmSctMapper.selectByPrimaryKey("1");
    }

    public String selectCbsActno() {
        HmActinfoCbsExample example = new HmActinfoCbsExample();
        example.createCriteria();
        return actinfoCbsMapper.selectByExample(example).get(0).getCbsActno();
    }
    public HmActinfoCbs selectCbsActnoRecord(String actno) {
        HmActinfoCbsExample example = new HmActinfoCbsExample();
        example.createCriteria().andCbsActnoEqualTo(actno);
        return actinfoCbsMapper.selectByExample(example).get(0);
    }


    //∫ÀÀ„ªß”‡∂Ó
    public List<HmActinfoFund> selectAllFundActBal(ActinfoQryParam param) {
        HmActinfoFundExample example = new HmActinfoFundExample();
        example.createCriteria()
                .andActStsEqualTo(param.getActnoStatus());
        return actinfoFundMapper.selectByExample(example);
    }
    public List<HmActinfoFund> selectFundActBal(ActinfoQryParam param) {
        HmActinfoFundExample example = new HmActinfoFundExample();
        example.createCriteria()
                .andFundActno1Between(param.getStartActno(), param.getEndActno())
                .andActStsEqualTo(param.getActnoStatus());
        return actinfoFundMapper.selectByExample(example);
    }

    //Ω·À„ªß”‡∂Ó
    public List<HmActinfoCbs> selectAllCbsActBal(ActinfoQryParam param) {
        HmActinfoCbsExample example = new HmActinfoCbsExample();
        example.createCriteria()
                .andActStsEqualTo(param.getActnoStatus());
        return actinfoCbsMapper.selectByExample(example);
    }
    public List<HmActinfoCbs> selectCbsActBal(ActinfoQryParam param) {
        HmActinfoCbsExample example = new HmActinfoCbsExample();
        example.createCriteria()
                .andCbsActnoEqualTo(param.getStartActno())
                .andActStsEqualTo(param.getActnoStatus());
        return actinfoCbsMapper.selectByExample(example);
    }

    //∫ÀÀ„ªß√˜œ∏
    public List<TxnFundLog> selectFundActDetl(ActinfoQryParam param) {
        TxnFundLogExample example = new TxnFundLogExample();
        example.createCriteria()
                .andFundActnoBetween(param.getStartActno(), param.getEndActno())
                .andTxnDateBetween(param.getStartDate(),param.getEndDate());
        return fundLogMapper.selectByExample(example);
    }

    public List<TxnCbsLog> selectCbsActDetl(ActinfoQryParam param) {
        TxnCbsLogExample example = new TxnCbsLogExample();
        example.createCriteria()
                .andCbsAcctnoEqualTo(param.getStartActno())
                .andTxnDateBetween(param.getStartDate(),param.getEndDate());
        return cbsLogMapper.selectByExample(example);
    }
}
