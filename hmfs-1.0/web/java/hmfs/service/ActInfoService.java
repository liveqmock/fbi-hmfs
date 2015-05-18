package hmfs.service;

import common.enums.*;
import common.repository.hmfs.dao.*;
import common.repository.hmfs.dao.hmfs.HmCmnMapper;
import common.repository.hmfs.dao.hmfs.HmWebTxnMapper;
import common.repository.hmfs.model.*;
import common.repository.hmfs.model.hmfs.HmChkActVO;
import common.repository.hmfs.model.hmfs.HmChkTxnVO;
import common.repository.hmfs.model.hmfs.HmFundTxnVO;
import common.repository.hmfs.model.hmfs.HmVchTxnVO;
import common.service.SystemService;
import hmfs.common.model.ActinfoQryParam;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.platform.security.OperatorManager;
import skyline.service.PlatformService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    private HmTxnStlDblMapper txnStlDblMapper;

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
    private HmChkTxnMapper hmChkTxnMapper;

    @Resource
    private HmChkActMapper hmChkActMapper;

    @Resource
    private HmTxnVchMapper hmTxnVchMapper;

    @Resource
    private HmActFundMapper hmActFundMapper;

    @Resource
    private HmTxnStlMapper hmTxnStlMapper;

    @Resource
    private PlatformService platformService;

    @Resource
    private HmVchJrnlMapper hmVchJrnlMapper;

    public HmSysCtl getAppSysStatus() {
        return hmSysCtlMapper.selectByPrimaryKey("1");
    }

    public HmTxnStl selectTxnStlByPkid(String pkid) {
        return txnStlMapper.selectByPrimaryKey(pkid);
    }

    public String selectStlActno() {
        HmActStlExample example = new HmActStlExample();
        example.createCriteria();
        return actStlMapper.selectByExample(example).get(0).getCbsActno();
    }

    public HmActFund selectActFundByno(String fundActno) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andFundActno1EqualTo(fundActno).andActStsEqualTo(FundActnoStatus.NORMAL.getCode());
        List<HmActFund> actFundList = hmActFundMapper.selectByExample(example);
        return actFundList.size() > 0 ? actFundList.get(0) : null;
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

        if (!StringUtils.isEmpty(param.getMsgSn())) {
            criteria.andTxnSnEqualTo(param.getMsgSn());
        }
        if (!StringUtils.isEmpty(param.getCbsTxnSn())) {
            criteria.andCbsTxnSnEqualTo(param.getCbsTxnSn());
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

    // �ֻ�������ϸ
    public List<HmFundTxnVO> selectIndiviFundTxnDetlList(ActinfoQryParam param) {
        HmTxnFundExample example = new HmTxnFundExample();
        HmTxnFundExample.Criteria criteria = example.createCriteria();

        criteria.andTxnDateBetween(param.getStartDate(), param.getEndDate());
        criteria.andFundActtypeEqualTo(FundActType.INDIVID.getCode());

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

        return hmWebTxnMapper.selectIndiviFundTxnDetail(param.getStartDate(), param.getEndDate());
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

    //���㻧�ظ�������ϸ
    public List<HmTxnStlDbl> selectStlTxnDblDetl(ActinfoQryParam param) {
        HmTxnStlDblExample example = new HmTxnStlDblExample();
        example.createCriteria()
                .andCbsActnoEqualTo(param.getCbsActno())
                .andTxnDateBetween(param.getStartDate(), param.getEndDate());
        example.setOrderByClause("stl_actno, txn_date, txn_time");
        return txnStlDblMapper.selectByExample(example);
    }

    // ��ѯ���ܱ�����Ϣ  (���жϱ���״̬)
    public HmMsgIn selectSummaryMsg(String msgSn) {
        HmMsgInExample example = new HmMsgInExample();
        example.createCriteria().andMsgSnEqualTo(msgSn).andMsgTypeLike("00%");
        List<HmMsgIn> hmMsgInList = hmMsgInMapper.selectByExample(example);
        if (hmMsgInList.size() > 1) {
            throw new RuntimeException("���ܱ��Ķ���һ����");
        } else if (hmMsgInList.size() == 0) {
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

    // �������뵥�Ų�ѯ����������ϸ
    public List<HmMsgIn> selectCancelActSubMsgList(String msgSn) {
        HmMsgInExample example = new HmMsgInExample();
        example.createCriteria().andMsgSnEqualTo(msgSn)
                .andMsgTypeEqualTo("01051").andTxnCtlStsNotEqualTo(TxnCtlSts.CANCEL.getCode());
        example.setOrderByClause("MSG_SUB_SN");
        return hmMsgInMapper.selectByExample(example);
    }

    // �������뵥�Ų�ѯ���п�����ϸ
    public List<HmMsgIn> selectCreateActSubMsgList(String msgSn) {
        HmMsgInExample example = new HmMsgInExample();
        example.createCriteria().andMsgSnEqualTo(msgSn)
                .andMsgTypeEqualTo("01033").andTxnCtlStsNotEqualTo(TxnCtlSts.CANCEL.getCode());
        example.setOrderByClause("MSG_SUB_SN");
        return hmMsgInMapper.selectByExample(example);
    }

    //�ɿ�ף���ѯ�ӱ�����Ϣ
    public List<HmMsgIn> selectSubMsgListByMsgSn(String msgSn) {
        return hmWebTxnMapper.selectSubMsgListByMsgSn(msgSn);
    }

    //�����ʽ����ѯ
    public List<HmChkActVO> selectCbsChkActFailResult(String sendSysId, String txnDate) {
        return hmWebTxnMapper.selectCbsChkActFailResult(sendSysId, txnDate);
    }

    //�������������ʽ����ѯ(��ƽ������)
    public List<HmChkActVO> selectHmbChkActFailResult(String sendSysId, String txnDate) {
        return hmWebTxnMapper.selectHmbChkActFailResult(sendSysId, txnDate);
    }

    //�������������ʽ����ѯ(ƽ������)
    public List<HmChkActVO> selectHmbChkActSuccResult(String sendSysId, String txnDate) {
        return hmWebTxnMapper.selectHmbChkActSuccResult(sendSysId, txnDate);
    }

    //��ˮ���ʽ����ѯ
    public List<HmChkTxnVO> selectChkTxnFailResult(String sendSysId, String txnDate) {
        return hmWebTxnMapper.selectChkTxnFailResult(sendSysId, txnDate);
    }

    public List<HmChkTxnVO> selectChkTxnSuccResult(String sendSysId, String txnDate) {
        return hmWebTxnMapper.selectChkTxnSuccResult(sendSysId, txnDate);
    }

    //������ͳ�������ʱ�����
    public int countChkActRecordNumber(String txnDate) {
        HmChkActExample example = new HmChkActExample();
        example.createCriteria().andTxnDateEqualTo(txnDate);
        return hmChkActMapper.countByExample(example);
    }

    //������ͳ����ˮ���ʱ�����
    public int countChkTxnRecordNumber(String txnDate) {
        HmChkTxnExample example = new HmChkTxnExample();
        example.createCriteria().andTxnDateEqualTo(txnDate);
        return hmChkTxnMapper.countByExample(example);
    }

    public boolean checkVoucherPrintStatus(HmMsgIn msgIn) {
        return false;

    }

    public List<HmTxnVch> qryTxnVchByStsAndDate(String startDate, String endDate, String vouchStatus) {
        HmTxnVchExample example = new HmTxnVchExample();
        example.createCriteria().andVchStsEqualTo(vouchStatus).andTxnDateBetween(startDate, endDate);
        example.setOrderByClause(" txac_brid,opr1_no,txn_date ");
        return hmTxnVchMapper.selectByExample(example);
    }

    //����������ˮ�Ų�ѯ���뵥��(�ӽ����˻�������ϸ��)
    public List<HmTxnStl> selectHmTxnStlAccordingToMsgSn(String strMsgSn) {
        HmTxnStlExample example = new HmTxnStlExample();
        example.createCriteria().andCbsTxnSnEqualTo(strMsgSn);
        return hmTxnStlMapper.selectByExample(example);
    }

    public HmChkTxn selectHmChkTxnByPkid(String strPkid) {
        return hmChkTxnMapper.selectByPrimaryKey(strPkid);
    }

    public List<HmActStl> qryActStlsByCbsActno(String cbsActno) {
        HmActStlExample example = new HmActStlExample();
        example.createCriteria().andCbsActnoEqualTo(cbsActno);
        return actStlMapper.selectByExample(example);
    }

    // ��Ϣ
    @Transactional
    public void intAcctual(String cbsTxnSn, String cbsActno, String intDate, BigDecimal intamt) {
        OperatorManager om = platformService.getOperatorManager();
        HmActStl actStl = qryActStlsByCbsActno(cbsActno).get(0);
        // ���¼�Ϣ���
        stlActAddInt(actStl, intDate, intamt);
        // ��Ϣ��ϸ
        addTxnStl(cbsTxnSn, om.getOperator().getDeptid(), om.getOperatorId(), intDate, actStl, intamt);
    }

    private int stlActAddInt(HmActStl hmActStl, String intDate, BigDecimal intamt) {

        hmActStl.setIntAmt(intamt.add(hmActStl.getIntAmt()));
        hmActStl.setRemark("��Ϣ" + intDate);
        return updateHmActStl(hmActStl);
    }

    // ��Ϣ��ϸ
    private int addTxnStl(String cbsTxnSn, String deptCode, String operCode, String intDate,
                          HmActStl hmActStl, BigDecimal intamt) {
        // �������㻧�˻�������ϸ��¼
        HmTxnStl hmTxnStl = new HmTxnStl();
        hmTxnStl.setPkid(UUID.randomUUID().toString());
        hmTxnStl.setCbsTxnSn(cbsTxnSn);
        hmTxnStl.setTxnSn(hmTxnStl.getCbsTxnSn());   // TODO msgsn
        hmTxnStl.setStlActno(hmActStl.getSettleActno1());
        hmTxnStl.setTxnSubSn(1);
        hmTxnStl.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        hmTxnStl.setTxnTime(SystemService.formatTodayByPattern("HHmmss"));
        hmTxnStl.setTxnCode("0000");
        hmTxnStl.setCbsActno(hmActStl.getCbsActno());
        hmTxnStl.setOpacBrid(hmActStl.getBranchId());
        hmTxnStl.setTxnAmt(intamt);
        hmTxnStl.setDcFlag(DCFlagCode.DEPOSIT.getCode());
        hmTxnStl.setReverseFlag("0");
        hmTxnStl.setLastActBal(hmActStl.getLastActBal());
        hmTxnStl.setRemark(intDate + "��Ϣ");
        // ��������ź͹�Ա��
        hmTxnStl.setTxacBrid(deptCode);
        hmTxnStl.setOpr1No(operCode);
        hmTxnStl.setOpr2No(operCode);
        return hmTxnStlMapper.insertSelective(hmTxnStl);
    }

    //���㻧��Ϣ������ϸ
    public List<HmTxnStl> selectStlIntTxns(String cbsActno, String startDate, String endDate) {
        HmTxnStlExample example = new HmTxnStlExample();
        example.createCriteria()
                .andCbsActnoEqualTo(cbsActno).andTxnCodeEqualTo("0000")
                .andTxnDateBetween(startDate, endDate).andTxnStsIsNull();
        example.or().andCbsActnoEqualTo(cbsActno).andTxnCodeEqualTo("0000")
                .andTxnDateBetween(startDate, endDate).andTxnStsNotEqualTo("1");
        example.setOrderByClause("stl_actno, txn_date, txn_time");
        return txnStlMapper.selectByExample(example);
    }

    //  ��Ϣ�޸� ɾ���ɱ���ϸ�������±ʼ�Ϣ��ϸ
    @Transactional
    public int updateNewIntacc(HmTxnStl intTxnStl, BigDecimal intamt, String intDate) throws Exception {
        // �����¼�Ϣ��ϸ
        /*HmTxnStl newIntTxnStl = new HmTxnStl();
        BeanUtils.copyProperties(newIntTxnStl, intTxnStl);
        newIntTxnStl.setCbsTxnSn(SystemService.formatTodayByPattern("yyMMddHHMMSSsss"));
        newIntTxnStl.setPkid(UUID.randomUUID().toString());
        newIntTxnStl.setTxnAmt(intamt);
        newIntTxnStl.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        newIntTxnStl.setTxnTime(SystemService.formatTodayByPattern("HHmmss"));
        newIntTxnStl.setRemark(intDate + "��Ϣ");*/
       /* //  ɾ������ϸ
        intTxnStl.setTxnSts("1");
        txnStlMapper.updateByPrimaryKey(intTxnStl);*/

        // ���½��㻧��Ϣ���
        HmActStl actStl = qryActStlsByCbsActno(intTxnStl.getCbsActno()).get(0);
        actStl.setRemark("��Ϣ�޸�" + new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()));
        actStl.setIntAmt(actStl.getIntAmt().add(intamt).subtract(intTxnStl.getTxnAmt()));

        intTxnStl.setTxnAmt(intamt);
        intTxnStl.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        intTxnStl.setTxnTime(SystemService.formatTodayByPattern("HHmmss"));
        intTxnStl.setRemark(intDate + "�޸ļ�Ϣ");

//        return txnStlMapper.insertSelective(newIntTxnStl) + txnStlMapper.updateByPrimaryKey(intTxnStl) + updateHmActStl(actStl);
        return txnStlMapper.updateByPrimaryKey(intTxnStl) + updateHmActStl(actStl);
    }

    private int updateHmActStl(HmActStl actstl) {
        HmActStl originRecord = actStlMapper.selectByPrimaryKey(actstl.getPkid());
        if (!originRecord.getRecversion().equals(actstl.getRecversion())) {
            throw new RuntimeException("��¼�������³�ͻ�������ԣ�");
        } else {
            // ��ע�б������ʱ��
            if (StringUtils.isEmpty(actstl.getRemark())) {
                actstl.setRemark(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()));
            }
            actstl.setRecversion(actstl.getRecversion() + 1);
            return actStlMapper.updateByPrimaryKey(actstl);
        }
    }

    @Transactional
    public int updateHmActFund(HmActFund actFund) {
        HmActFund originRecord = actFundMapper.selectByPrimaryKey(actFund.getPkid());
        if (!originRecord.getRecversion().equals(actFund.getRecversion())) {
            throw new RuntimeException("��¼�������³�ͻ�������ԣ�");
        } else {
            // ��ע�б������ʱ��
            if (StringUtils.isEmpty(actFund.getRemark())) {
                actFund.setRemark(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()));
            }
            actFund.setRecversion(actFund.getRecversion() + 1);
            return actFundMapper.updateByPrimaryKey(actFund);
        }
    }

    //�ɿ�ף���ѯ�ӱ�����Ϣ linyong
    public List<HmMsgIn> selectSubMsgActFundListByMsgSn(String msgSn) {
        return hmWebTxnMapper.selectSubMsgActFundListByMsgSn(msgSn);
    }

    //20150508 linyong Ʊ�����ò�ѯ
    public List<HmVchJrnl> qryVchByStsAndVchNo(String vchNo, String vouchStatus) {
        HmVchJrnlExample example = new HmVchJrnlExample();
        example.createCriteria().andVchStateEqualTo(vouchStatus).andVchStartNoLessThanOrEqualTo(vchNo).andVchEndNoGreaterThanOrEqualTo(vchNo);
        example.setOrderByClause(" opr_date desc,pkid desc ");
        List<HmVchJrnl> vchJrnlList = hmVchJrnlMapper.selectByExample(example);
        return hmVchJrnlMapper.selectByExample(example);
    }

    //20150508 linyong Ʊ��ҵ������ˮ��ѯ
    public List<HmTxnVch> qryVchByVchNo(String startNo, String endNo) {
        HmTxnVchExample example = new HmTxnVchExample();
        example.createCriteria().andVchNumBetween(startNo,endNo);
        example.setOrderByClause(" txac_brid,opr1_no,txn_date ");
        return hmTxnVchMapper.selectByExample(example);
    }

    //20150508 linyong Ʊ������ѯ
    public List<HmVchJrnl> qryInVchByStsAndVchNo(String startNo,String endNo, String vouchStatus) {
        HmVchJrnlExample example = new HmVchJrnlExample();
        example.createCriteria().andVchStateEqualTo(vouchStatus).andVchStartNoLessThanOrEqualTo(startNo);
        example.or().andVchStateEqualTo(vouchStatus).andVchEndNoLessThanOrEqualTo(endNo);
        example.setOrderByClause(" branch_id,opr_date desc,pkid desc ");
        List<HmVchJrnl> vchJrnlList = hmVchJrnlMapper.selectByExample(example);
        return hmVchJrnlMapper.selectByExample(example);
    }

    //20150508 linyong Ʊ��ҵ������ˮ��ѯ�������˺ŵ���Ϣ
    public List<HmVchTxnVO> selectVchAccountTxn(String startNo, String endNo) {
        return hmWebTxnMapper.selectVchAccountTxn(startNo,endNo);
    }
}
