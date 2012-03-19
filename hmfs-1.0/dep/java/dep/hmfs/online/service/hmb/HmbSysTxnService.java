package dep.hmfs.online.service.hmb;

import common.enums.FundActnoStatus;
import common.repository.hmfs.dao.*;
import common.repository.hmfs.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 基本服务：签到签退 对帐.
 * User: zhanrui
 * Date: 12-3-12
 * Time: 下午9:55
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbSysTxnService extends HmbBaseService {
    private static final Logger logger = LoggerFactory.getLogger(HmbSysTxnService.class);

    @Resource
    private HmActinfoFundMapper  hmActinfoFundMapper;

    @Resource
    private HmActinfoCbsMapper hmActinfoCbsMapper;


    @Resource
    private HmChkLogMapper hmChkLogMapper;
    @Resource
    private TxnFundLogMapper txnFundLogMapper;
    @Resource
    private TxnCbsLogMapper txnCbsLogMapper;


    /**
     * 核算账户余额
     * @return
     */
    public List<HmActinfoFund> selectFundActinfo(){
        HmActinfoFundExample example = new HmActinfoFundExample();
        example.createCriteria().andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActinfoFundMapper.selectByExample(example);
    }

    /**
     * 结算账户余额
     * @return
     */
    public List<HmActinfoCbs> selectCbsActinfo(){
        HmActinfoCbsExample example = new HmActinfoCbsExample();
        example.createCriteria().andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActinfoCbsMapper.selectByExample(example);
    }
    /**
     * 核算账户流水
     * @return
     */
    public List<TxnFundLog> selectFundTxnDetl(String yyyymmdd){
        TxnFundLogExample example = new TxnFundLogExample();
        example.createCriteria().andTxnDateEqualTo(yyyymmdd);
        return txnFundLogMapper.selectByExample(example);
    }

}
