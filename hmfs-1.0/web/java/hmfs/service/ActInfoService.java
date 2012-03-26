package hmfs.service;

import common.repository.hmfs.dao.*;
import common.repository.hmfs.dao.hmfs.HmCmnMapper;
import common.repository.hmfs.dao.hmfs.HmWebTxnMapper;
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
 * Time: ����9:55
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
    private HmTxnFundMapper txnFundMapper;

    @Resource
    private HmTxnStlMapper txnStlMapper;

    @Resource
    protected HmMsgInMapper hmMsgInMapper;

    @Resource
    protected HmCmnMapper hmCmnMapper;

    @Resource
    protected HmWebTxnMapper hmWebTxnMapper;


    public HmSysCtl getAppSysStatus() {
        return hmSysCtlMapper.selectByPrimaryKey("1");
    }

    public String selectStlActno() {
        HmActStlExample example = new HmActStlExample();
        example.createCriteria();
        return actStlMapper.selectByExample(example).get(0).getCbsActno();
    }

    public List<HmActStl> selectStlActnoRecord(String actno) {
        HmActStlExample example = new HmActStlExample();
        example.createCriteria().andCbsActnoEqualTo(actno);
        List<HmActStl> actStlList = actStlMapper.selectByExample(example);
        return actStlList;
    }


    //ȫ�����㻧���
    public List<HmActFund> selectAllFundActBalList(ActinfoQryParam param) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria()
                .andActStsEqualTo(param.getActnoStatus());
        return actFundMapper.selectByExample(example);
    }

    //���㻧���
    public List<HmActFund> selectFundActBalList(ActinfoQryParam param) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria()
                .andFundActno1Between(param.getStartActno(), param.getEndActno())
                .andActStsEqualTo(param.getActnoStatus());
        return actFundMapper.selectByExample(example);
    }

    //ȫ�����㻧���
    public List<HmActStl> selectAllStlActBalList(ActinfoQryParam param) {
        HmActStlExample example = new HmActStlExample();
        example.createCriteria()
                .andActStsEqualTo(param.getActnoStatus());
        return actStlMapper.selectByExample(example);
    }

    //���㻧���
    public List<HmActStl> selectStlActBalList(ActinfoQryParam param) {
        HmActStlExample example = new HmActStlExample();
        example.createCriteria()
                .andCbsActnoEqualTo(param.getCbsActno())
                .andActStsEqualTo(param.getActnoStatus());
        return actStlMapper.selectByExample(example);
    }

    //���㻧������ϸ
    public List<HmTxnFund> selectFundTxnDetlList(ActinfoQryParam param) {
        HmTxnFundExample exampleHm = new HmTxnFundExample();
        exampleHm.createCriteria()
                .andFundActnoBetween(param.getStartActno(), param.getEndActno())
                .andTxnDateBetween(param.getStartDate(), param.getEndDate());
        return txnFundMapper.selectByExample(exampleHm);
    }

    //���㻧������ϸ
    public List<HmTxnStl> selectStlTxnDetl(ActinfoQryParam param) {
        HmTxnStlExample exampleHm = new HmTxnStlExample();
        exampleHm.createCriteria()
                .andCbsActnoEqualTo(param.getCbsActno())
                .andTxnDateBetween(param.getStartDate(), param.getEndDate());
        return txnStlMapper.selectByExample(exampleHm);
    }

    // ��ѯ���ܱ�����Ϣ  (���жϱ���״̬)
    public HmMsgIn selectSummaryMsg(String msgSn) {
        HmMsgInExample example = new HmMsgInExample();
        example.createCriteria().andMsgSnEqualTo(msgSn).andMsgTypeLike("00%");
        List<HmMsgIn> hmMsgInList = hmMsgInMapper.selectByExample(example);
        if (hmMsgInList.size() > 1) {
            throw new RuntimeException("���ܱ��Ķ���һ����");
        }else if (hmMsgInList.size() == 0) {
            throw new RuntimeException("���ܱ���δ�ҵ���");
        }
        return hmMsgInList.get(0);
    }

    // �������뵥�Ų�ѯ������ϸ  (���жϱ���״̬)
    public List<HmMsgIn> selectSubMsgList(String msgSn) {
        HmMsgInExample example = new HmMsgInExample();
        example.createCriteria().andMsgSnEqualTo(msgSn)
                .andMsgTypeLike("01%");
        example.setOrderByClause("MSG_SUB_SN");
        return hmMsgInMapper.selectByExample(example);
    }
    
    public List<HmMsgIn> selectSubMsgListByMsgSn(String msgSn){
        return hmWebTxnMapper.selectSubMsgListByMsgSn(msgSn);
    }

}
