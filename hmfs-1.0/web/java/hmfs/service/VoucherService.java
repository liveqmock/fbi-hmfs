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
 * 票据库存管理.
 * 1、总行（或分行）的库存与房管中心直接对应，总行（或分行）：HeadOfficeInstitution 简写为：HoInst
 * 2、总行（分行）的票据出入库以及各网点之间的拨入拨出应按本管理，不应出现拆本的情况
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

    //获取与房管中心对应的总行（或分行）机构号
    public String selectHoInstNo() {
        PtdeptExample ptdeptExample = new PtdeptExample();

        ptdeptExample.createCriteria().andParentdeptidEqualTo("0");
        List<Ptdept> ptdepts = ptdeptMapper.selectByExample(ptdeptExample);
        if (ptdepts.size() != 1) {
            throw new RuntimeException("机构设置错误");
        }
        return ptdepts.get(0).getDeptid();
    }

    //按机构查看库存明细
    public List<HmVchStore> selectInstitutionVoucherStoreList(String instNo) {
        HmVchStoreExample example = new HmVchStoreExample();
        example.createCriteria().andBranchIdEqualTo(instNo);
        example.setOrderByClause(" vch_start_no");
        return vchStoreMapper.selectByExample(example);
        //return voucherMapper.selectInstVoucherStoreList(instNo);
    }

    //分行票据领用
    @Transactional
    public void processHoInstVchInput(int vchCnt, String startNo, String endNo) {
        //获取分行库存情况
        String instNo = selectHoInstNo();
        processInstVoucherInput(vchCnt, startNo, endNo, instNo);
        insertVoucherJournal(vchCnt, startNo, endNo, instNo, VouchStatus.RECEIVED, "自中心领用");
    }

    //分行缴回中心处理  只能从原来的起号开始，并不可跨多条库存记录
    @Transactional
    public void processHoInstVchOutput(HmVchStore vchStore) {
        //
        int cnt = voucherMapper.deleteVoucherByPkid(vchStore.getPkid(), vchStore.getRecversion());
        if (cnt != 1) {
            throw new RuntimeException("并发冲突，记录已被修改。");
        }
        insertVoucherJournal(vchStore.getVchCount(), vchStore.getVchStartNo(), vchStore.getVchEndNo(),
                selectHoInstNo(), VouchStatus.OUTSTORE, "向中心缴回");
    }

    //行内机构调拨处理
    //拨出时需按票号顺序出库,每次只处理一条库存记录，拨出的止号不可超出此条记录的止号
    @Transactional
    public synchronized void processVchTransfer(String fromInst, String toInst, HmVchStore vchStoreParam) {
        if (StringUtils.isEmpty(vchStoreParam.getPkid())) {
            throw new IllegalArgumentException("未选定拨出记录.");
        }

        HmVchStore vchStoreDB = vchStoreMapper.selectByPrimaryKey(vchStoreParam.getPkid());
        if (!vchStoreDB.getVchStartNo().equals(vchStoreParam.getVchStartNo())) {
            throw new RuntimeException("拨出的起号与库存不符.");
        }

        if (Long.parseLong(vchStoreParam.getVchEndNo()) > Long.parseLong(vchStoreDB.getVchEndNo())) {
            throw new RuntimeException("拨出的止号超出范围.");
        }

        Long nTransInStartNo = Long.parseLong(vchStoreParam.getVchEndNo()) + 1;
        String sTransInStartNo = nTransInStartNo.toString();

        int vchnoLen = PropertyManager.getIntProperty("voucher_no_length");
        if (sTransInStartNo.length() != vchnoLen) { //长度不足 左补零
            sTransInStartNo = StringUtils.leftPad(sTransInStartNo, vchnoLen, "0");
        }

        if (vchStoreParam.getRecversion().compareTo(vchStoreDB.getRecversion()) != 0) {
            throw new RuntimeException("并发冲突，记录已被修改。");
        }

        //调拨
        vchStoreDB.setOprDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        vchStoreDB.setOprNo(getOperatorManager().getOperatorId());
        vchStoreDB.setRecversion(vchStoreParam.getRecversion() + 1);
        if (vchStoreParam.getVchEndNo().equals(vchStoreDB.getVchEndNo())) { //整个记录拨出的情况
            //拨出日志
            insertVoucherJournal(vchStoreParam.getVchCount(), vchStoreParam.getVchStartNo(), vchStoreParam.getVchEndNo(),
                    fromInst, VouchStatus.OUTSTORE, "全部拨出");
            //直接修改原纪录的机构号
            vchStoreDB.setRemark("全部拨入");
            vchStoreDB.setBranchId(toInst);
            vchStoreMapper.updateByPrimaryKey(vchStoreDB);
            //拨入日志
            insertVoucherJournal(vchStoreDB.getVchCount(), vchStoreDB.getVchStartNo(), vchStoreDB.getVchEndNo(),
                    vchStoreDB.getBranchId(), VouchStatus.RECEIVED, "全部拨入");
        } else {//记录中部分票号拨出的情况
            //日志
            insertVoucherJournal(vchStoreParam.getVchCount(), vchStoreParam.getVchStartNo(), vchStoreParam.getVchEndNo(),
                    vchStoreDB.getBranchId(), VouchStatus.OUTSTORE, "部分拨出");

            //修改原纪录的起号
            vchStoreDB.setRemark("部分拨出");
            vchStoreDB.setVchStartNo(sTransInStartNo);
            vchStoreDB.setVchCount(Integer.parseInt(vchStoreDB.getVchEndNo()) - Integer.parseInt(vchStoreDB.getVchStartNo()) + 1);
            vchStoreMapper.updateByPrimaryKey(vchStoreDB);

            //增加新记录
            processInstVoucherInput(vchStoreParam.getVchCount(), vchStoreParam.getVchStartNo(), vchStoreParam.getVchEndNo(), toInst);
            //日志
            insertVoucherJournal(vchStoreParam.getVchCount(), vchStoreParam.getVchStartNo(), vchStoreParam.getVchEndNo(),
                    toInst, VouchStatus.RECEIVED, "部分拨入");
        }

        //总分核对
        if (!verifyVchStoreAndJrnl(fromInst) || !verifyVchStoreAndJrnl(toInst)) {
            throw new RuntimeException("库存总分不符！");
        }
    }

    //按机构查看近一年的记录
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

        if (startNo.compareTo(maxEndno) > 0) {//入库记录的起号比库存中的都大
            insertVoucherStore(vchCnt, startNo, endNo, instNo);
        } else {
            //检查是否存在入库记录的起号与止号之间的记录
            int recordNum = voucherMapper.selectStoreRecordnumBetweenStartnoAndEndno(startNo, endNo);
            if (recordNum > 0) {
                throw new RuntimeException("票号冲突。");
            }
            String minNearbyStartno = voucherMapper.selectStoreStartno_GreaterThanVchno(endNo);
            if (minNearbyStartno == null) {
                throw new RuntimeException("票号冲突。");
            } else {
                String maxNearbyEndno = voucherMapper.selectStoreEndno_LessThanVchno(startNo);
                if (maxNearbyEndno == null) {//有库存，但每条记录的起号都比入库记录的止号大
                    insertVoucherStore(vchCnt, startNo, endNo, instNo);
                } else {
                    int dbVchCnt = Integer.parseInt(minNearbyStartno) - Integer.parseInt(maxNearbyEndno) - 1;
                    if (vchCnt <= dbVchCnt) {
                        insertVoucherStore(vchCnt, startNo, endNo, instNo);
                    } else {
                        throw new RuntimeException("票号冲突。");
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
            throw new RuntimeException("用户未登录！");
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
        vchStore.setRemark("入库处理");

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
