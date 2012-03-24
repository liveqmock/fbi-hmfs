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
    private HmSysCtlMapper hmSysCtlMapper;

    @Resource
    private HmActFundMapper actFundMapper;

    @Resource
    private HmActStlMapper actStlMapper;

    @Resource
    private HmTxnFundMapper fundMapperHm;

    @Resource
    private HmTxnStlMapper stlMapperHm;


    public HmSysCtl getAppSysStatus() {
        return hmSysCtlMapper.selectByPrimaryKey("1");
    }

    public String selectCbsActno() {
        HmActStlExample example = new HmActStlExample();
        example.createCriteria();
        return actStlMapper.selectByExample(example).get(0).getCbsActno();
    }
    public List<HmActStl> selectCbsActnoRecord(String actno) {
        HmActStlExample example = new HmActStlExample();
        example.createCriteria().andCbsActnoEqualTo(actno);
        List<HmActStl> actStlList = actStlMapper.selectByExample(example);
        return actStlList;
    }


    //∫ÀÀ„ªß”‡∂Ó
    public List<HmActFund> selectAllFundActBal(ActinfoQryParam param) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria()
                .andActStsEqualTo(param.getActnoStatus());
        return actFundMapper.selectByExample(example);
    }
    public List<HmActFund> selectFundActBal(ActinfoQryParam param) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria()
                .andFundActno1Between(param.getStartActno(), param.getEndActno())
                .andActStsEqualTo(param.getActnoStatus());
        return actFundMapper.selectByExample(example);
    }

    //Ω·À„ªß”‡∂Ó
    public List<HmActStl> selectAllCbsActBal(ActinfoQryParam param) {
        HmActStlExample example = new HmActStlExample();
        example.createCriteria()
                .andActStsEqualTo(param.getActnoStatus());
        return actStlMapper.selectByExample(example);
    }
    public List<HmActStl> selectCbsActBal(ActinfoQryParam param) {
        HmActStlExample example = new HmActStlExample();
        example.createCriteria()
                .andCbsActnoEqualTo(param.getCbsActno())
                .andActStsEqualTo(param.getActnoStatus());
        return actStlMapper.selectByExample(example);
    }

    //∫ÀÀ„ªß√˜œ∏
    public List<HmTxnFund> selectFundActDetl(ActinfoQryParam param) {
        HmTxnFundExample exampleHm = new HmTxnFundExample();
        exampleHm.createCriteria()
                .andFundActnoBetween(param.getStartActno(), param.getEndActno())
                .andTxnDateBetween(param.getStartDate(),param.getEndDate());
        return fundMapperHm.selectByExample(exampleHm);
    }

    public List<HmTxnStl> selectCbsActDetl(ActinfoQryParam param) {
        HmTxnStlExample exampleHm = new HmTxnStlExample();
        exampleHm.createCriteria()
                .andCbsAcctnoEqualTo(param.getCbsActno())
                .andTxnDateBetween(param.getStartDate(),param.getEndDate());
        return stlMapperHm.selectByExample(exampleHm);
    }
}
