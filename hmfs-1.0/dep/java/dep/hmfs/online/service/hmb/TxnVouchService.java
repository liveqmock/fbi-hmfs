package dep.hmfs.online.service.hmb;

import common.enums.VouchStatus;
import common.repository.hmfs.dao.HmTxnVchMapper;
import common.repository.hmfs.dao.HmVchJrnlMapper;
import common.repository.hmfs.dao.HmVchStoreMapper;
import common.repository.hmfs.dao.hmfs.HmVoucherMapper;
import common.repository.hmfs.dao.hmfs.HmWebTxnMapper;
import common.repository.hmfs.model.*;
import common.service.SystemService;
import dep.util.PropertyManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-12
 * Time: ����10:12
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TxnVouchService {

    @Autowired
    private HmTxnVchMapper hmTxnVchMapper;
    @Autowired
    private HmWebTxnMapper hmWebTxnMapper;
    @Resource
    private HmVchStoreMapper vchStoreMapper;
    @Resource
    private HmVchJrnlMapper vchJrnlMapper;
    @Resource
    private HmVoucherMapper voucherMapper;
    @Transactional
    public long insertVouchsByNo(String msgSn, long startNo, long endNo, String serialNo,
                                 String deptCode, String operCode, String txnApplyNo, String vouchStatus) {

        //���ݽɿ��Ż�ȡ��ʹ��Ʊ�ݵ��������ˮ��
//        int maxSubsn = getMaxVchSubsn(tiaHeader.serialNo);
        int maxSubsn = getMaxVchSubsn(txnApplyNo);
        for (long i = startNo; i <= endNo; i++) {
            HmTxnVch hmTxnVch = new HmTxnVch();
            hmTxnVch.setPkid(UUID.randomUUID().toString());
            hmTxnVch.setTxnSn(msgSn);
            //Ĭ����ű����0��ʼ
            if (maxSubsn == 0) {
                hmTxnVch.setTxnSubSn(maxSubsn + (int) (i - startNo));
            }else{
                hmTxnVch.setTxnSubSn(maxSubsn + (int) (i - startNo) + 1);
            }
            hmTxnVch.setFundTxnSn(txnApplyNo);
            hmTxnVch.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            hmTxnVch.setTxnCode("4001");
            hmTxnVch.setVchSts(vouchStatus);
            hmTxnVch.setCbsTxnSn(serialNo);
            hmTxnVch.setVchNum(String.valueOf(i));
            hmTxnVch.setTxacBrid(deptCode);
            hmTxnVch.setOpr1No(operCode);
            hmTxnVch.setOpr2No(operCode);
            hmTxnVchMapper.insertSelective(hmTxnVch);
        }
        return endNo - startNo + 1;
    }

    //��ȡָ�����뵥������ʹ��Ʊ�ݵ��������ˮ��
    private int getMaxVchSubsn(String msgSn){
        HmTxnVchExample example = new HmTxnVchExample();
        example.createCriteria().andFundTxnSnEqualTo(msgSn);
        example.setOrderByClause(" txn_sub_sn desc");
        List<HmTxnVch> txnVchList =  hmTxnVchMapper.selectByExample(example);
        if (txnVchList.size() > 0) {
            return txnVchList.get(0).getTxnSubSn();
        }else{
            return 0;
        }
    }

    public boolean isExistMsgSnVch(String msgSn) {
        return qryUsedVchCntByMsgsn(msgSn) > 0;
    }

    public int qryUsedVchCntByMsgsn(String msgSn) {
        HmTxnVchExample example = new HmTxnVchExample();
        example.createCriteria().andFundTxnSnEqualTo(msgSn).andVchStsEqualTo(VouchStatus.USED.getCode());
        return hmTxnVchMapper.countByExample(example);
    }

    public boolean isUsedVchNo(String vchNo) {
        HmTxnVchExample example = new HmTxnVchExample();
        example.createCriteria().andVchNumEqualTo(vchNo);
        return hmTxnVchMapper.countByExample(example) > 0;
    }
    //2015-05-08 linyong �������ڲ�ѯ�����Ƿ����δ����Ʊ��ά���Ľɿ���
    public List<HmTxnStl> checkVoucherIsHandlerByDept(String prevDate,String deptCode){
        return hmWebTxnMapper.checkVoucherIsHandlerByDept(prevDate,deptCode);
    }
    //2015-05-08 linyong �������ڲ�ѯ��Ա�Ƿ����δ����Ʊ��ά���Ľɿ���
    public List<HmTxnStl> checkVoucherIsHandlerByOper(String currentDate,String operCode){
        return hmWebTxnMapper.checkVoucherIsHandlerByOper(currentDate, operCode);
    }

    //Ʊ��ʹ�ú�����
    @Transactional
    public void processVchUseOrCancel(final String instNo, final VouchStatus status, final String startNo, final String endNo,final String operCode) {
        //TODO ��� �м䲻���пպţ�����в����ڵ��м���룩

        List<HmVchStore> storeParamList = new ArrayList<HmVchStore>();
        doVchUseOrCancel(instNo, startNo, endNo, storeParamList);

        String remark = "";
        if (status == VouchStatus.USED) {
            remark = "Ʊ��ʹ��";
        } else if (status == VouchStatus.CANCEL) {
            remark = "Ʊ������";
        } else {
            throw new IllegalArgumentException("��������.");
        }

        for (final HmVchStore storeParam : storeParamList) {
            HmVchStore storeDb = selectVchStoreByStartNo(instNo, storeParam.getVchStartNo());
            long startNoDb = Long.parseLong(storeDb.getVchStartNo());
            long endNoDb = Long.parseLong(storeDb.getVchEndNo());
            long startNoParam = Long.parseLong(storeParam.getVchStartNo());
            long endNoParam = Long.parseLong(storeParam.getVchEndNo());

            vchStoreMapper.deleteByPrimaryKey(storeDb.getPkid());
            if (startNoDb != startNoParam || endNoDb != endNoParam) {//����������¼
                if (startNoParam != startNoDb) {
                    insertVoucherStore(startNoParam - startNoDb, storeDb.getVchStartNo(),
                            getStandLengthForVoucherString(startNoParam - 1), instNo, remark,operCode);
                }
                if (endNoParam != endNoDb) {
                    insertVoucherStore(endNoDb - endNoParam, getStandLengthForVoucherString(endNoParam + 1),
                            storeDb.getVchEndNo(), instNo, remark,operCode);
                }
            }
        }

        insertVoucherJournal(Long.parseLong(endNo) - Long.parseLong(startNo) + 1, startNo, endNo, instNo, status, remark,operCode);
        //�ֺܷ˶�
        if (!verifyVchStoreAndJrnl(instNo)) {
            throw new RuntimeException("����ֲܷ�����");
        }
    }
    private void doVchUseOrCancel(String instNo, String startNo, String endNo, List<HmVchStore> storeParamList) {
        HmVchStore storeDb = selectVchStoreByStartNo(instNo, startNo);
        HmVchStore storeParam = new HmVchStore();
        storeParam.setPkid(storeDb.getPkid());
        storeParam.setVchStartNo(startNo);
        if (storeDb.getVchEndNo().compareTo(endNo) < 0) {
            storeParam.setVchEndNo(storeDb.getVchEndNo());
            storeParam.setVchCount(Long.parseLong(storeDb.getVchEndNo()) - Long.parseLong(startNo) + 1);
            storeParamList.add(storeParam);
            String vchNo = getStandLengthForVoucherString(Long.parseLong(storeDb.getVchEndNo()) + 1);
            doVchUseOrCancel(instNo, vchNo, endNo, storeParamList);
        } else {
            storeParam.setVchEndNo(endNo);
            storeParam.setVchCount(Long.parseLong(endNo) - Long.parseLong(startNo) + 1);
            storeParamList.add(storeParam);
        }
    }

    //������Ų������ݿ��к��д˺���Ŀ���¼
    private HmVchStore selectVchStoreByStartNo(String instNo, String startNo) {
        HmVchStoreExample storeExample = new HmVchStoreExample();
        storeExample.createCriteria().andBranchIdEqualTo(instNo)
                .andVchStartNoLessThanOrEqualTo(startNo)
                .andVchEndNoGreaterThanOrEqualTo(startNo);
        List<HmVchStore> storesTmp = vchStoreMapper.selectByExample(storeExample);
        if (storesTmp.size() != 1) {
            throw new RuntimeException("δ�ҵ�����¼��");
        }
        return storesTmp.get(0);
    }

    private void insertVoucherStore(long vchCnt, String startNo, String endNo, String instNo, String remark,String operCode) {
        HmVchStore vchStore = new HmVchStore();
        vchStore.setVchCount(vchCnt);
        vchStore.setVchStartNo(startNo);
        vchStore.setVchEndNo(endNo);

        vchStore.setBranchId(instNo);
        vchStore.setOprDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        vchStore.setOprNo(operCode);
        vchStore.setRecversion(0);
        vchStore.setRemark(remark);

        String random = StringUtils.leftPad("" + (new SecureRandom().nextInt(99999) + 1), 5, "0");
        vchStore.setPkid(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-" + random + "-" + "001");
        vchStoreMapper.insert(vchStore);
    }

    //��Ʊ�ݺų���
    private String getStandLengthForVoucherString(long vchno) {
        String vchNo = "" + vchno;
        int vchnoLen = PropertyManager.getIntProperty("voucher_no_length");
        if (vchNo.length() != vchnoLen) { //���Ȳ��� ����
            vchNo = StringUtils.leftPad(vchNo, vchnoLen, "0");
        }
        return vchNo;
    }

    private void insertVoucherJournal(long vchCnt, String startNo, String endNo, String instNo,
                                      VouchStatus status, String remark,String operCode) {
        HmVchJrnl vchJrnl = new HmVchJrnl();
        vchJrnl.setVchCount(vchCnt);
        vchJrnl.setVchStartNo(startNo);
        vchJrnl.setVchEndNo(endNo);

        vchJrnl.setBranchId(instNo);
        vchJrnl.setOprDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        vchJrnl.setOprNo(operCode);
        vchJrnl.setRecversion(0);
        vchJrnl.setRemark(remark);

        vchJrnl.setVchState(status.getCode());

        String random = StringUtils.leftPad("" + (new SecureRandom().nextInt(99999) + 1), 5, "0");
        vchJrnl.setPkid(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-" + random + "-" + "001");
        vchJrnlMapper.insert(vchJrnl);
    }

    private boolean verifyVchStoreAndJrnl(String instNo) {
        int store = voucherMapper.selectVchStoreTotalNum(instNo);
        int jrnl = voucherMapper.selectVchJrnlTotalNum(instNo, VouchStatus.RECEIVED.getCode())
                - voucherMapper.selectVchJrnlTotalNum(instNo, VouchStatus.OUTSTORE.getCode())
                - voucherMapper.selectVchJrnlTotalNum(instNo, VouchStatus.USED.getCode())
                - voucherMapper.selectVchJrnlTotalNum(instNo, VouchStatus.CANCEL.getCode());
        return store == jrnl;
    }
}
