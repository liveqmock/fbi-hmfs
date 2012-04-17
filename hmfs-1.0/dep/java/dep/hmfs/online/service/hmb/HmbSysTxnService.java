package dep.hmfs.online.service.hmb;

import common.enums.FundActType;
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
    private HmActFundMapper hmActFundMapper;

    @Resource
    private HmActStlMapper hmActStlMapper;

    @Resource
    private HmChkActMapper hmChkActMapper;

    @Resource
    private HmChkTxnMapper hmChkTxnMapper;

    @Resource
    private HmTxnFundMapper hmTxnFundMapper;

    @Resource
    private HmTxnStlMapper hmTxnStlMapper;


    /**
     * 核算账户余额 (all)
     * @return
     */
    public List<HmActFund> selectAllFundActinfo(){
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActFundMapper.selectByExample(example);
    }

    /**
     * 项目核算账户余额
     * @return
     */
    public List<HmActFund> selectProjectFundActinfo(){
        HmActFundExample example = new HmActFundExample();
        example.createCriteria()
                .andFundActtype1EqualTo(FundActType.PROJECT.getCode())
                .andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActFundMapper.selectByExample(example);
    }
    /**
     * 分户核算账户余额
     * @return
     */
    public List<HmActFund> selectIndividFundActinfo(List<String> prjActnoList){
        HmActFundExample example = new HmActFundExample();
        example.createCriteria()
                .andFundActno2In(prjActnoList)
                .andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActFundMapper.selectByExample(example);
    }

    /**
     * 结算账户余额
     * @return
     */
    public List<HmActStl> selectStlActinfo(){
        HmActStlExample example = new HmActStlExample();
        example.createCriteria().andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActStlMapper.selectByExample(example);
    }

    /**
     * 查询未核对成功的核算户
     * @param txnDate
     * @return
     */
    public List<HmChkAct> selectChkFailedFundActList(String txnDate){
        HmChkActExample example = new HmChkActExample();
        //TODO 是否会查找到结算户？
        example.createCriteria().andChkstsNotEqualTo("0").andTxnDateEqualTo(txnDate);
        return hmChkActMapper.selectByExample(example);
    }

    /**
     * 核算账户流水
     * @return
     */
    public List<HmTxnFund> selectFundTxnDetl(String yyyymmdd){
        HmTxnFundExample exampleHm = new HmTxnFundExample();
        exampleHm.createCriteria().andTxnDateEqualTo(yyyymmdd);
        return hmTxnFundMapper.selectByExample(exampleHm);
    }

}
