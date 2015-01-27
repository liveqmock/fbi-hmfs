package dep.hmfs.online.service.hmb;

import common.enums.FundActType;
import common.enums.FundActnoStatus;
import common.enums.SysCtlSts;
import common.repository.hmfs.dao.*;
import common.repository.hmfs.model.*;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg002;
import dep.hmfs.online.processor.hmb.domain.Msg094;
import dep.hmfs.online.processor.hmb.domain.Msg098;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * ��������ǩ��ǩ�� ����.
 * User: zhanrui
 * Date: 12-3-12
 * Time: ����9:55
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
     * �����˻���� (all)
     * @return
     */
    public List<HmActFund> selectAllFundActinfo(){
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActFundMapper.selectByExample(example);
    }

    /**
     * ��Ŀ�����˻����
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
     * �ֻ������˻����
     * @return
     */
    public List<HmActFund> selectIndividFundActinfo(List<String> prjActnoList) {
        if (prjActnoList == null || prjActnoList.isEmpty()) {
            throw new RuntimeException("��������");
        }
        for (String actno : prjActnoList) {
            logger.info("����ʧ�ܵ��ʻ�:" + actno);
        }
        HmActFundExample example = new HmActFundExample();
        example.createCriteria()
                .andFundActno2In(prjActnoList)
                .andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActFundMapper.selectByExample(example);
    }

    /**
     * �����˻����
     * @return
     */
    public List<HmActStl> selectStlActinfo(){
        HmActStlExample example = new HmActStlExample();
        example.createCriteria().andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActStlMapper.selectByExample(example);
    }

    /**
     * ��ѯδ�˶Գɹ��ĺ��㻧
     * @param txnDate
     * @return
     */
    public List<HmChkAct> selectChkFailedFundActList(String txnDate){
        HmChkActExample example = new HmChkActExample();
        //TODO �Ƿ����ҵ����㻧��
        example.createCriteria().andChkstsNotEqualTo("0").andTxnDateEqualTo(txnDate).andSendSysIdEqualTo("99");
        return hmChkActMapper.selectByExample(example);
    }

    /**
     * �����˻���ˮ
     * @return
     */
    public List<HmTxnFund> selectFundTxnDetl(String yyyymmdd){
        HmTxnFundExample exampleHm = new HmTxnFundExample();
        exampleHm.createCriteria().andTxnDateEqualTo(yyyymmdd);
        return hmTxnFundMapper.selectByExample(exampleHm);
    }


    //========================�����ֶ���ר�� 20120707 zhanrui ��ʱ�޸�============
    //�������������������
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void deleteOldActChkDataByTxnDate(String txnDate, String sendSysId){
        HmChkActExample example = new HmChkActExample();
        example.createCriteria().andTxnDateEqualTo(txnDate).andSendSysIdEqualTo(sendSysId);
        hmChkActMapper.deleteByExample(example);
    }
    //�������������������
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void deleteOldTxnChkDataByTxnDate(String txnDate, String sendSysId){
        HmChkTxnExample example = new HmChkTxnExample();
        example.createCriteria().andTxnDateEqualTo(txnDate).andSendSysIdEqualTo(sendSysId);
        hmChkTxnMapper.deleteByExample(example);
    }

    //���淢����ʵ����ݵ��������ݿ�
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void insertChkFundRecord(List<HmActFund> actFundList, String txnDate){
        for (HmActFund hmActFund : actFundList) {
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setActno(hmActFund.getFundActno1());
            hmChkAct.setTxnDate(txnDate);
            //hmChkAct.setSendSysId(SEND_SYS_ID);
            hmChkAct.setSendSysId("99");
            hmChkAct.setActbal(hmActFund.getActBal());
            hmChkAct.setActtype(hmActFund.getFundActtype1());
            hmChkAct.setInfoId1(hmActFund.getInfoId1());
            hmChkAct.setInfoIdType1(hmActFund.getInfoIdType1());
            hmChkAct.setCellNum(hmActFund.getCellNum());
            hmChkAct.setBuilderArea(hmActFund.getBuilderArea());
            hmChkActMapper.insert(hmChkAct);
        }
    }
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void insertChkStlRecord(List<HmActStl> actStlList, String txnDate){
        for (HmActStl stl : actStlList) {
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setActno(stl.getSettleActno1());
            hmChkAct.setTxnDate(txnDate);
            //hmChkAct.setSendSysId(SEND_SYS_ID);
            hmChkAct.setSendSysId("99");
            hmChkAct.setActbal(stl.getActBal());
            hmChkAct.setActtype(stl.getSettleActtype1());  //�ʻ����� 210
            hmChkAct.setInfoId1(stl.getOrgId());    //��λID  100
            hmChkAct.setInfoIdType1(stl.getOrgType());  //��λ����  80
            hmChkAct.setCellNum("0");
            hmChkAct.setBuilderArea("0");
            hmChkActMapper.insert(hmChkAct);
        }
    }

    /**
     * ��������ַ��ص���������Ϣ
     */
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void processChkBalResponse(List<HmbMsg> msgList, String txnDate) {
        Msg002 msg002 = (Msg002) msgList.get(0);
        //TODO
        // String txnDate = msg002.msgDt.substring(0,8);
        for (HmbMsg hmbMsg : msgList.subList(1, msgList.size())) {
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setTxnDate(txnDate);
            hmChkAct.setSendSysId("00");
            if (hmbMsg instanceof Msg098) {
                hmChkAct.setActno(((Msg098) hmbMsg).fundActno1);
                hmChkAct.setActbal(((Msg098) hmbMsg).getActBal());
                hmChkAct.setActtype(((Msg098) hmbMsg).getFundActtype1());
                hmChkAct.setInfoId1(((Msg098) hmbMsg).getInfoId1());
                hmChkAct.setInfoIdType1(((Msg098) hmbMsg).getInfoIdType1());
                hmChkAct.setCellNum(((Msg098) hmbMsg).getCellNum());
                hmChkAct.setBuilderArea(((Msg098) hmbMsg).getBuilderArea());
            } else {
                hmChkAct.setActno(((Msg094) hmbMsg).settleActno1);
                hmChkAct.setActbal(((Msg094) hmbMsg).getActBal());
                hmChkAct.setActtype(((Msg094) hmbMsg).getSettleActtype1());
                hmChkAct.setInfoId1(((Msg094) hmbMsg).getOrgId());
                hmChkAct.setInfoIdType1(((Msg094) hmbMsg).getOrgType());
                hmChkAct.setCellNum("0");
                hmChkAct.setBuilderArea("0");
            }
            hmChkActMapper.insert(hmChkAct);
        }
    }

    /**
     * У������������
     *
     * @return
     */
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public boolean verifyHmbActBalData(String txnDate, String actType) {
        int successNumber = 0;
        int failNumber = 0;
        successNumber = hmCmnMapper.verifyChkActResult_0(txnDate, "99", "00", "210");
        successNumber = successNumber + hmCmnMapper.verifyChkActResult_0(txnDate, "99", "00", actType);
        logger.info(txnDate + "���ʳɹ�������" + successNumber);

        failNumber = hmCmnMapper.verifyChkActResult_11(txnDate, "99", "00");
        failNumber += hmCmnMapper.verifyChkActResult_12(txnDate, "99", "00");
        failNumber += hmCmnMapper.verifyChkActResult_2(txnDate, "99", "00", "210");
        failNumber += hmCmnMapper.verifyChkActResult_2(txnDate, "99", "00", actType);
        logger.info(txnDate + "����ʧ�ܱ�����" + failNumber);

        return failNumber == 0;
    }

    //========================��������ר�� 20120719 zhanrui ��ʱ�޸�============
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int deleteCbsChkActByDate(String date8, String cbsActno, String sendSysId) {
        HmChkActExample example = new HmChkActExample();
        example.createCriteria().andTxnDateEqualTo(date8).andActnoEqualTo(cbsActno).andSendSysIdEqualTo(sendSysId);
        return hmChkActMapper.deleteByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int deleteCbsChkTxnByDate(String date8, String cbsActno, String sendSysId) {
        HmChkTxnExample example = new HmChkTxnExample();
        example.createCriteria().andTxnDateEqualTo(date8).andActnoEqualTo(cbsActno).andSendSysIdEqualTo(sendSysId);
        return hmChkTxnMapper.deleteByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int insertChkActWithNewTx(HmChkAct hmChkAct) {
        return hmChkActMapper.insert(hmChkAct);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int insertChkTxnWithNewTx(HmChkTxn hmChkTxn) {
        return hmChkTxnMapper.insert(hmChkTxn);
    }

    //У����ˮ��������
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public boolean verifyChkTxnData(String txnDate, String bankId) {
        int successNumber = hmCmnMapper.verifyChkTxnResult_0(txnDate, "99", bankId);
        logger.info(txnDate + "���ʳɹ�������" + successNumber);

        int failNumber = hmCmnMapper.verifyChkTxnResult_11(txnDate, "99", bankId);
        failNumber += hmCmnMapper.verifyChkTxnResult_12(txnDate, "99", bankId);
        failNumber += hmCmnMapper.verifyChkTxnResult_2(txnDate, "99", bankId);
        logger.info(txnDate + "����ʧ�ܱ�����" + failNumber);

        return failNumber == 0;
    }


    //======================20120724 zhanrui ǩ��ʱ���������кŴ���==========
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public HmSysCtl checkDateAndUpdateTxnSeqForSignon(){
        HmSysCtl hmSysCtl = hmSysCtlMapper.selectByPrimaryKey("1");
        String date8 = new SimpleDateFormat("yyyyMMdd").format(new Date());
        if (!date8.equals(hmSysCtl.getTxnDate())) {
            hmSysCtl.setTxnDate(date8);
            hmSysCtl.setTxnseq(1);
        }
        hmSysCtlMapper.updateByPrimaryKey(hmSysCtl);
        return hmSysCtl;
    }

    //======================20150116 zhanrui �����������ϵͳ���Ʊ���Ϣ ����ǩ��==========
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void updateSysCTlForSignout(){
        HmSysCtl hmSysCtl = hmSysCtlMapper.selectByPrimaryKey("1");
        hmSysCtl.setSysSts(SysCtlSts.SIGNOUT.getCode());
        hmSysCtl.setSignoutDt(new Date());
        hmSysCtlMapper.updateByPrimaryKey(hmSysCtl);
    }

}
