package hmfs.service;

import common.enums.VouchStatus;
import common.repository.hmfs.dao.HmVchJrnlMapper;
import common.repository.hmfs.dao.HmVchStoreMapper;
import common.repository.hmfs.dao.hmfs.HmVoucherMapper;
import common.repository.hmfs.model.HmVchJrnl;
import common.repository.hmfs.model.HmVchJrnlExample;
import common.repository.hmfs.model.HmVchStore;
import common.repository.hmfs.model.HmVchStoreExample;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.platform.advance.utils.PropertyManager;
import pub.platform.form.config.SystemAttributeNames;
import pub.platform.security.OperatorManager;
import skyline.repository.dao.PtdeptMapper;
import skyline.repository.dao.PtoperMapper;
import skyline.repository.model.Ptdept;
import skyline.repository.model.PtdeptExample;
import skyline.repository.model.Ptoper;
import skyline.repository.model.PtoperExample;

import javax.annotation.Resource;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Ʊ�ݿ�����.
 * 1�����У�����У��Ŀ���뷿������ֱ�Ӷ�Ӧ�����У�����У���HeadOfficeInstitution ��дΪ��HoInst
 * 2�����У����У���Ʊ�ݳ�����Լ�������֮��Ĳ��벦��Ӧ����������Ӧ���ֲ𱾵����
 * <p/>
 * User: zhanrui
 * Date: 20150423
 */
@Service
public class VoucherService {
    private static final Logger logger = LoggerFactory.getLogger(VoucherService.class);

    @Resource
    private PtdeptMapper ptdeptMapper;
    @Resource
    private PtoperMapper ptoperMapper;
    @Resource
    private HmVchStoreMapper vchStoreMapper;
    @Resource
    private HmVchJrnlMapper vchJrnlMapper;
    @Resource
    private HmVoucherMapper voucherMapper;

    //��ȡ�뷿�����Ķ�Ӧ�����У�����У�������
    public String selectHoInstNo() {
        PtdeptExample ptdeptExample = new PtdeptExample();

        ptdeptExample.createCriteria().andParentdeptidEqualTo("0");
        List<Ptdept> ptdepts = ptdeptMapper.selectByExample(ptdeptExample);
        if (ptdepts.size() != 1) {
            throw new RuntimeException("�������ô���");
        }
        return ptdepts.get(0).getDeptid();
    }

    //�������鿴�����ϸ
    public List<HmVchStore> selectInstitutionVoucherStoreList(String instNo) {
        HmVchStoreExample example = new HmVchStoreExample();
        example.createCriteria().andBranchIdEqualTo(instNo);
        example.setOrderByClause(" vch_start_no");
        return vchStoreMapper.selectByExample(example);
        //return voucherMapper.selectInstVoucherStoreList(instNo);
    }

    //����Ʊ������
    @Transactional
    public void processHoInstVchInput(int vchCnt, String startNo, String endNo) {
        //��ȡ���п�����
        String instNo = selectHoInstNo();
        processInstVoucherInput(vchCnt, startNo, endNo, instNo);
        insertVoucherJournal(vchCnt, startNo, endNo, instNo, VouchStatus.RECEIVED, "����������");
    }

    //���нɻ����Ĵ���  ֻ�ܴ�ԭ������ſ�ʼ�������ɿ��������¼
    @Transactional
    public void processHoInstVchOutput(HmVchStore vchStore) {
        //
        int cnt = voucherMapper.deleteVoucherByPkid(vchStore.getPkid(), vchStore.getRecversion());
        if (cnt != 1) {
            throw new RuntimeException("������ͻ����¼�ѱ��޸ġ�");
        }
        insertVoucherJournal(vchStore.getVchCount(), vchStore.getVchStartNo(), vchStore.getVchEndNo(),
                selectHoInstNo(), VouchStatus.OUTSTORE, "�����Ľɻ�");
    }

    //���ڻ�����������
    //����ʱ�谴Ʊ��˳�����,ÿ��ֻ����һ������¼��������ֹ�Ų��ɳ���������¼��ֹ��
    @Transactional
    public synchronized void processVchTransfer(String fromInst, String toInst, HmVchStore vchStoreParam) {
        if (StringUtils.isEmpty(vchStoreParam.getPkid())) {
            throw new IllegalArgumentException("δѡ��������¼.");
        }

        HmVchStore vchStoreDB = vchStoreMapper.selectByPrimaryKey(vchStoreParam.getPkid());
        if (!vchStoreDB.getVchStartNo().equals(vchStoreParam.getVchStartNo())) {
            throw new RuntimeException("������������治��.");
        }

        if (Long.parseLong(vchStoreParam.getVchEndNo()) > Long.parseLong(vchStoreDB.getVchEndNo())) {
            throw new RuntimeException("������ֹ�ų�����Χ.");
        }

        Long nTransInStartNo = Long.parseLong(vchStoreParam.getVchEndNo()) + 1;
        String sTransInStartNo = nTransInStartNo.toString();

        int vchnoLen = PropertyManager.getIntProperty("voucher_no_length");
        if (sTransInStartNo.length() != vchnoLen) { //���Ȳ��� ����
            sTransInStartNo = StringUtils.leftPad(sTransInStartNo, vchnoLen, "0");
        }

        if (vchStoreParam.getRecversion().compareTo(vchStoreDB.getRecversion()) != 0) {
            throw new RuntimeException("������ͻ����¼�ѱ��޸ġ�");
        }

        //����
        vchStoreDB.setOprDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        vchStoreDB.setOprNo(getOperatorManager().getOperatorId());
        vchStoreDB.setRecversion(vchStoreParam.getRecversion() + 1);
        if (vchStoreParam.getVchEndNo().equals(vchStoreDB.getVchEndNo())) { //������¼���������
            //������־
            insertVoucherJournal(vchStoreParam.getVchCount(), vchStoreParam.getVchStartNo(), vchStoreParam.getVchEndNo(),
                    fromInst, VouchStatus.OUTSTORE, "ȫ������");
            //ֱ���޸�ԭ��¼�Ļ�����
            vchStoreDB.setRemark("ȫ������");
            vchStoreDB.setBranchId(toInst);
            vchStoreMapper.updateByPrimaryKey(vchStoreDB);
            //������־
            insertVoucherJournal(vchStoreDB.getVchCount(), vchStoreDB.getVchStartNo(), vchStoreDB.getVchEndNo(),
                    vchStoreDB.getBranchId(), VouchStatus.RECEIVED, "ȫ������");
        } else {//��¼�в���Ʊ�Ų��������
            //��־
            insertVoucherJournal(vchStoreParam.getVchCount(), vchStoreParam.getVchStartNo(), vchStoreParam.getVchEndNo(),
                    vchStoreDB.getBranchId(), VouchStatus.OUTSTORE, "���ֲ���");

            //�޸�ԭ��¼�����
            vchStoreDB.setRemark("���ֲ���");
            vchStoreDB.setVchStartNo(sTransInStartNo);
            vchStoreDB.setVchCount(Integer.parseInt(vchStoreDB.getVchEndNo()) - Integer.parseInt(vchStoreDB.getVchStartNo()) + 1);
            vchStoreMapper.updateByPrimaryKey(vchStoreDB);

            //�����¼�¼
            processInstVoucherInput(vchStoreParam.getVchCount(), vchStoreParam.getVchStartNo(), vchStoreParam.getVchEndNo(), toInst);
            //��־
            insertVoucherJournal(vchStoreParam.getVchCount(), vchStoreParam.getVchStartNo(), vchStoreParam.getVchEndNo(),
                    toInst, VouchStatus.RECEIVED, "���ֲ���");
        }

        //�ֺܷ˶�
        if (!verifyVchStoreAndJrnl(fromInst) || !verifyVchStoreAndJrnl(toInst)) {
            throw new RuntimeException("����ֲܷ�����");
        }
    }

    //�������鿴��һ��ļ�¼
    public List<HmVchJrnl> selectVchJrnl(String instNo) {
        HmVchJrnlExample example = new HmVchJrnlExample();
        DateTime dt = new DateTime();
        dt = dt.minusYears(1);
        example.createCriteria().andBranchIdEqualTo(instNo).andOprDateGreaterThan(dt.toString("yyyyMMdd"));

        return vchJrnlMapper.selectByExample(example);
    }

    public Map<String, String> selectPtoperMap(){
        PtoperExample example = new PtoperExample();
        List<Ptoper> ptopers = ptoperMapper.selectByExample(example);
        Map<String, String> operMap = new HashMap<String, String>();
        for (Ptoper ptoper : ptopers) {
            operMap.put(ptoper.getOperid(), ptoper.getOpername());
        }
        return  operMap;
    }
    public Map<String, String> selectPtdeptMap(){
        PtdeptExample example = new PtdeptExample();
        List<Ptdept> ptdepts = ptdeptMapper.selectByExample(example);
        Map<String, String> map = new HashMap<String, String>();
        for (Ptdept dept : ptdepts) {
            map.put(dept.getDeptid(), dept.getDeptname());
        }
        return  map;
    }

    //=====================================
    private void processInstVoucherInput(int vchCnt, String startNo, String endNo, String instNo) {
        String maxEndno = voucherMapper.selectInstVchMaxEndNo(instNo);
        if (maxEndno == null) {
            maxEndno = "0";
        }

        if (startNo.compareTo(maxEndno) > 0) {//����¼����űȿ���еĶ���
            insertVoucherStore(vchCnt, startNo, endNo, instNo);
        } else {
            //����Ƿ��������¼�������ֹ��֮��ļ�¼
            int recordNum = voucherMapper.selectStoreRecordnumBetweenStartnoAndEndno(startNo, endNo);
            if (recordNum > 0) {
                throw new RuntimeException("Ʊ�ų�ͻ��");
            }
            String minNearbyStartno = voucherMapper.selectStoreStartno_GreaterThanVchno(endNo);
            if (minNearbyStartno == null) {
                throw new RuntimeException("Ʊ�ų�ͻ��");
            } else {
                String maxNearbyEndno = voucherMapper.selectStoreEndno_LessThanVchno(startNo);
                if (maxNearbyEndno == null) {//�п�棬��ÿ����¼����Ŷ�������¼��ֹ�Ŵ�
                    insertVoucherStore(vchCnt, startNo, endNo, instNo);
                } else {
                    int dbVchCnt = Integer.parseInt(minNearbyStartno) - Integer.parseInt(maxNearbyEndno) - 1;
                    if (vchCnt <= dbVchCnt) {
                        insertVoucherStore(vchCnt, startNo, endNo, instNo);
                    } else {
                        throw new RuntimeException("Ʊ�ų�ͻ��");
                    }
                }
            }
        }
    }

    //========================
    private static OperatorManager getOperatorManager() {
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        ExternalContext extContext = currentInstance.getExternalContext();
        HttpSession session = (HttpSession) extContext.getSession(true);
        OperatorManager om = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
        if (om == null) {
            throw new RuntimeException("�û�δ��¼��");
        }
        return om;
    }


    private void insertVoucherStore(int vchCnt, String startNo, String endNo, String instNo) {
        HmVchStore vchStore = new HmVchStore();
        vchStore.setVchCount(vchCnt);
        vchStore.setVchStartNo(startNo);
        vchStore.setVchEndNo(endNo);

        vchStore.setBranchId(instNo);
        vchStore.setOprDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        vchStore.setOprNo(getOperatorManager().getOperatorId());
        vchStore.setRecversion(0);
        vchStore.setRemark("��⴦��");

        String random = StringUtils.leftPad("" + (new SecureRandom().nextInt(99999) + 1), 5, "0");
        vchStore.setPkid(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-" + random + "-" + "001");
        vchStoreMapper.insert(vchStore);
    }

    private void insertVoucherJournal(int vchCnt, String startNo, String endNo, String instNo,
                                      VouchStatus status, String remark) {
        HmVchJrnl vchJrnl = new HmVchJrnl();
        vchJrnl.setVchCount(vchCnt);
        vchJrnl.setVchStartNo(startNo);
        vchJrnl.setVchEndNo(endNo);

        vchJrnl.setBranchId(instNo);
        vchJrnl.setOprDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        vchJrnl.setOprNo(getOperatorManager().getOperatorId());
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
