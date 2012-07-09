package hmfs.service;

import common.repository.hmfs.dao.*;
import common.repository.hmfs.dao.hmfs.HmCmnMapper;
import common.repository.hmfs.dao.hmfs.HmWebTxnMapper;
import common.repository.hmfs.model.*;
import common.repository.hmfs.model.hmfs.HmChkActVO;
import common.repository.hmfs.model.hmfs.HmChkTxnVO;
import hmfs.common.model.ActinfoQryParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import skyline.service.PlatformService;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    private HmMsgInMapper hmMsgInMapper;

    @Resource
    private HmCmnMapper hmCmnMapper;

    @Resource
    private HmWebTxnMapper hmWebTxnMapper;

    @Resource
    private  HmChkTxnMapper hmChkTxnMapper;

    @Resource
    private  HmChkActMapper hmChkActMapper;

    @Resource
    private  PlatformService platformService;

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
        example.setOrderByClause("fund_actno1");
        return actFundMapper.selectByExample(example);
    }

    //���㻧���
    public List<HmActFund> selectFundActBalList(ActinfoQryParam param) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria()
                .andFundActno1Between(param.getStartActno(), param.getEndActno())
                .andActStsEqualTo(param.getActnoStatus());
        example.setOrderByClause("fund_actno1");
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
        HmTxnFundExample example = new HmTxnFundExample();
        HmTxnFundExample.Criteria criteria = example.createCriteria();

        criteria.andTxnDateBetween(param.getStartDate(), param.getEndDate());

        String startActno = param.getStartActno();
        String endActno = param.getEndActno();
        if (!StringUtils.isEmpty(startActno) || !StringUtils.isEmpty(endActno)) {
            criteria.andFundActnoBetween(startActno, endActno);
        }

        String fundActType = param.getFundActType();
        if (!StringUtils.isEmpty(fundActType)) {
            criteria.andFundActtypeEqualTo(fundActType);
        }

        BigDecimal txnAmt = param.getTxnAmt();
        if (txnAmt.compareTo(new BigDecimal(0)) != 0) {
            criteria.andTxnAmtEqualTo(txnAmt);
        }

        int count = txnFundMapper.countByExample(example);

        int max_query_count = 0;
        try {
            max_query_count = Integer.parseInt(platformService.selectEnuExpandValue("SYSTEMPARAM", "MAXQRYNUM"));
        } catch (NumberFormatException e) {
            logger.error("ϵͳ����������ѯ������������󡣲���Ĭ��ֵ 10000");
            max_query_count = 10000;
        }

        if (count > max_query_count) {
            throw new RuntimeException("��ѯ�������������" + max_query_count + "�ʣ���ı��ѯ��������С��ѯ��Χ��");
        }

        example.setOrderByClause("fund_actno, txn_date, txn_time");
        return txnFundMapper.selectByExample(example);
    }

    //���㻧������ϸ
    public List<HmTxnStl> selectStlTxnDetl(ActinfoQryParam param) {
        HmTxnStlExample example = new HmTxnStlExample();
        example.createCriteria()
                .andCbsActnoEqualTo(param.getCbsActno())
                .andTxnDateBetween(param.getStartDate(), param.getEndDate());
        example.setOrderByClause("stl_actno, txn_date, txn_time");
        return txnStlMapper.selectByExample(example);
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

    //�ɿ�ף���ѯ�ӱ�����Ϣ
    public List<HmMsgIn> selectSubMsgListByMsgSn(String msgSn){
        return hmWebTxnMapper.selectSubMsgListByMsgSn(msgSn);
    }

    //�����ʽ����ѯ
    public List<HmChkActVO> selectChkActResult(String sendSysId, String txnDate){
        return  hmWebTxnMapper.selectChkActResult(sendSysId, txnDate);
    }
    //��ˮ���ʽ����ѯ
    public List<HmChkTxnVO> selectChkTxnResult(String sendSysId, String txnDate){
        return  hmWebTxnMapper.selectChkTxnResult(sendSysId, txnDate);
    }

    //������ͳ�������ʱ�����
    public int countChkActRecordNumber(String txnDate){
        HmChkActExample example = new HmChkActExample();
        example.createCriteria().andTxnDateEqualTo(txnDate);
        return  hmChkActMapper.countByExample(example);
    }
    //������ͳ����ˮ���ʱ�����
    public int countChkTxnRecordNumber(String txnDate){
        HmChkTxnExample example = new HmChkTxnExample();
        example.createCriteria().andTxnDateEqualTo(txnDate);
        return  hmChkTxnMapper.countByExample(example);
    }

    public boolean checkVoucherPrintStatus(HmMsgIn msgIn){
        return false;

    }
}
