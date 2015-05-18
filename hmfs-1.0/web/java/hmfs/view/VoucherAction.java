package hmfs.view;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.enums.VouchStatus;
import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmTxnVch;
import common.repository.hmfs.model.HmVchJrnl;
import common.repository.hmfs.model.hmfs.HmVchTxnVO;
import hmfs.common.util.JxlsManager;
import hmfs.service.ActInfoService;
import hmfs.service.DepService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.advance.utils.PropertyManager;
import pub.platform.system.manage.dao.PtOperBean;
import skyline.common.utils.MessageUtil;
import skyline.service.PlatformService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ������Ʊ�ݲ�ѯ ״̬���´���.
 * User: zhanrui
 * Date: 12-4-22
 * Time: ����10:07
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
public class VoucherAction {
    private static final Logger logger = LoggerFactory.getLogger(VoucherAction.class);

    //    private HmMsgIn hmMsgIn;
    @ManagedProperty(value = "#{actInfoService}")
    private ActInfoService actInfoService;
    @ManagedProperty(value = "#{platformService}")
    private PlatformService platformService;
    @ManagedProperty(value = "#{depService}")
    private DepService depService;

    private String startDate = "";
    private String endDate = "";
    private String vchStatus = VouchStatus.CANCEL.getCode();
    private List<HmTxnVch> vchList = new ArrayList<HmTxnVch>();
    private HmTxnVch selectedTxnVch;
    private VouchStatus vouchStatus = VouchStatus.USED;

    private String msgSn;
    private String fundActno;
    private int totalCount;
    private List<HmMsgIn> subMsgList;
    private TxnCtlSts txnCtlSts = TxnCtlSts.SUCCESS;

    private String startno;
    private String endno;
    private String vchSendStatus;
    private List<SelectItem> vchStsList = new ArrayList<SelectItem>();

    //20150508 linyong Ʊ�����ò�ѯ
    private String vchNo;
    private List<HmVchJrnl> vchRecvList = new ArrayList<HmVchJrnl>();
    //Ʊ����ˮ��ѯ
    private List<HmTxnVch> vchTxnList = new ArrayList<HmTxnVch>();
    //Ʊ������ѯ
    private List<HmVchJrnl> vchInList = new ArrayList<HmVchJrnl>();
    //Ʊ����ˮ��ѯ,�����˺���Ϣ
    private List<HmVchTxnVO> vchTxnAcctList = new ArrayList<HmVchTxnVO>();

    @PostConstruct
    public void init() {
//        hmMsgIn = new HmMsgIn();
//        hmMsgIn.setBankName(" ");
        vchStsList.add(new SelectItem(VouchStatus.USED.getCode(), VouchStatus.USED.getTitle()));
        vchStsList.add(new SelectItem(VouchStatus.CANCEL.getCode(), VouchStatus.CANCEL.getTitle()));
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        startDate = date;
        endDate = date;
    }

    public String onQuery() {
        try {
            this.vchList = actInfoService.qryTxnVchByStsAndDate(startDate, endDate, vchStatus);
            if (vchList.isEmpty()) {
                MessageUtil.addWarn("���ݲ�����...");
            }
        } catch (Exception e) {
            logger.error("��������ʧ�ܡ�" + e.getMessage(), e);
            MessageUtil.addError("��������ʧ�ܡ�" + e.getMessage());
        }
        return null;
    }

    public String onExportExcelForVch() {
        onQuery();
        if (this.vchList.size() == 0) {
            MessageUtil.addWarn("��¼Ϊ��...");
            return null;
        } else {
            if (VouchStatus.CANCEL.getCode().equals(this.vchStatus)) {
                String excelFilename = "ά���ʽ�����Ʊ���嵥-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
                JxlsManager jxls = new JxlsManager();
                jxls.exportTxnvchList(excelFilename, this.vchList);
            }
            // ����״̬��Ʊ����Ҫ���ʱ���޸ĵ����ļ���
        }
        return null;
    }

    public String onQrySubMsgin() {
        try {
//            this.subMsgList = actInfoService.selectSubMsgList(msgSn);
            this.subMsgList = actInfoService.selectSubMsgActFundListByMsgSn(msgSn); //linyong
            this.totalCount = this.subMsgList.size();
            if (this.totalCount <= 0) {
                MessageUtil.addWarn("û�в�ѯ�����ݼ�¼��");
            }
        } catch (Exception e) {
            MessageUtil.addError("����ʧ�ܡ�" + e.getMessage());
        }
        return null;
    }

    // Ʊ�ݷ���
    public String onSendVoucher() {
        long lstartNo = 0;
        long lendNo = 0;
        endno = StringUtils.isEmpty(endno) ? startno : endno;
        try {
            lstartNo = Long.parseLong(startno);
            lendNo = Long.parseLong(endno);
        } catch (Exception e) {
            MessageUtil.addError("Ʊ����ֹ�������");
        }
        try {
            if (lstartNo > lendNo) {
                throw new RuntimeException("Ʊ����ֹ�����������");
            }
            onQrySubMsgin();
            PtOperBean oper = platformService.getOperatorManager().getOperator();
            String response = depService.process("1005610|" + this.msgSn + "|"
                    + oper.getDeptid() + "|" + oper.getOperid() + "|" + vchSendStatus
                    + "|" + startno + "|" + endno);
            if (response.startsWith("0000")) { //�ɹ�
                MessageUtil.addInfo("Ʊ�ݷ��ͳɹ���");
            } else {
                MessageUtil.addError("����ʧ�ܡ�" + response);
            }
        } catch (Exception e) {
            MessageUtil.addError(e.getMessage());
        }
        return null;
    }

    //20150508 linyong Ʊ�����ò�ѯ
    public void onReceiveQry(){
        try {
            int vchnoLen = PropertyManager.getIntProperty("voucher_no_length");
            if (vchNo.length() != vchnoLen) {
                MessageUtil.addError("Ʊ�ų��ȴ���");
                return;
            }
            this.vchRecvList = actInfoService.qryVchByStsAndVchNo(vchNo, "1");
            if (vchRecvList.isEmpty()) {
                MessageUtil.addWarn("���ݲ�����...");
            }
        } catch (Exception e) {
            logger.error("��������ʧ�ܡ�" + e.getMessage(), e);
            MessageUtil.addError("��������ʧ�ܡ�" + e.getMessage());
        }
    }

    //20150508 linyong Ʊ������excel����
    public void onReceiveForExcel() {
        onReceiveQry();
        if (this.vchRecvList.size() == 0) {
            MessageUtil.addWarn("��¼Ϊ��...");
            return;
        } else {
            String excelFilename = "ά���ʽ�Ʊ�������嵥-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportVchRecvList(excelFilename, this.vchRecvList,vchNo);
        }
    }

    //20150508 linyong Ʊ����ˮ��ѯ
    public void onTxnQuery(){
        try {
            int vchnoLen = PropertyManager.getIntProperty("voucher_no_length");
            if (startno.length() != vchnoLen || endno.length() != vchnoLen) {
                MessageUtil.addError("Ʊ�ų��ȴ���");
                return;
            }
            this.vchTxnList = actInfoService.qryVchByVchNo(startno, endno);
            if (vchTxnList.isEmpty()) {
                MessageUtil.addWarn("���ݲ�����...");
            }
        } catch (Exception e) {
            logger.error("��������ʧ�ܡ�" + e.getMessage(), e);
            MessageUtil.addError("��������ʧ�ܡ�" + e.getMessage());
        }
    }

    //20150508 linyong Ʊ����ˮexcel����
    public void onTxnQueryForExcel() {
        onTxnQuery();
        if (this.vchTxnList.size() == 0) {
            MessageUtil.addWarn("��¼Ϊ��...");
            return;
        } else {
            String excelFilename = "ά���ʽ�Ʊ����ˮ�嵥-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportTxnvchList(excelFilename, this.vchTxnList);
        }
    }

    //20150508 linyong Ʊ������ѯ
    public void onInQuery(){
        try {
            int vchnoLen = PropertyManager.getIntProperty("voucher_no_length");
            if (startno.length() != vchnoLen || endno.length() != vchnoLen) {
                MessageUtil.addError("Ʊ�ų��ȴ���");
                return;
            }
            this.vchInList = actInfoService.qryInVchByStsAndVchNo(startno, endno,"1");
            if (vchInList.isEmpty()) {
                MessageUtil.addWarn("���ݲ�����...");
            }
        } catch (Exception e) {
            logger.error("��������ʧ�ܡ�" + e.getMessage(), e);
            MessageUtil.addError("��������ʧ�ܡ�" + e.getMessage());
        }
    }

    //20150508 linyong Ʊ�����excel����
    public void onInQueryForExcel() {
        onInQuery();
        if (this.vchInList.size() == 0) {
            MessageUtil.addWarn("��¼Ϊ��...");
            return;
        } else {
            String excelFilename = "ά���ʽ�Ʊ����ˮ�嵥-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportVchInList(excelFilename, this.vchInList);
        }
    }

    //20150508 linyong Ʊ����ˮ��ѯ�������˺ŵ���Ϣ
    public void onTxnAccountQuery(){
        try {
            int vchnoLen = PropertyManager.getIntProperty("voucher_no_length");
            if (startno.length() != vchnoLen || endno.length() != vchnoLen) {
                MessageUtil.addError("Ʊ�ų��ȴ���");
                return;
            }
            this.vchTxnAcctList = actInfoService.selectVchAccountTxn(startno, endno);
            if (vchTxnAcctList.isEmpty()) {
                MessageUtil.addWarn("���ݲ�����...");
            }
        } catch (Exception e) {
            logger.error("��������ʧ�ܡ�" + e.getMessage(), e);
            MessageUtil.addError("��������ʧ�ܡ�" + e.getMessage());
        }
    }

    //20150508 linyong Ʊ����ˮexcel����
    public void onTxnAccountQueryForExcel() {
        onTxnAccountQuery();
        if (this.vchTxnAcctList.size() == 0) {
            MessageUtil.addWarn("��¼Ϊ��...");
            return;
        } else {
            String excelFilename = "ά���ʽ�Ʊ����ˮ�嵥-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportTxnvchAcctList(excelFilename, this.vchTxnAcctList);
        }
    }

    // ----------------------------------------------------------

    public List<SelectItem> getVchStsList() {
        return vchStsList;
    }

    public void setVchStsList(List<SelectItem> vchStsList) {
        this.vchStsList = vchStsList;
    }

    public PlatformService getPlatformService() {
        return platformService;
    }

    public void setPlatformService(PlatformService platformService) {
        this.platformService = platformService;
    }

    public DepService getDepService() {
        return depService;
    }

    public void setDepService(DepService depService) {
        this.depService = depService;
    }

    public String getVchSendStatus() {
        return vchSendStatus;
    }

    public void setVchSendStatus(String vchSendStatus) {
        this.vchSendStatus = vchSendStatus;
    }

    public String getStartno() {
        return startno;
    }

    public void setStartno(String startno) {
        this.startno = startno;
    }

    public String getEndno() {
        return endno;
    }

    public void setEndno(String endno) {
        this.endno = endno;
    }

    public TxnCtlSts getTxnCtlSts() {
        return txnCtlSts;
    }

    public void setTxnCtlSts(TxnCtlSts txnCtlSts) {
        this.txnCtlSts = txnCtlSts;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getMsgSn() {
        return msgSn;
    }

    public void setMsgSn(String msgSn) {
        this.msgSn = msgSn;
    }

    public String getFundActno() {
        return fundActno;
    }

    public void setFundActno(String fundActno) {
        this.fundActno = fundActno;
    }

    public List<HmMsgIn> getSubMsgList() {
        return subMsgList;
    }

    public void setSubMsgList(List<HmMsgIn> subMsgList) {
        this.subMsgList = subMsgList;
    }

    public String getVchStatus() {
        return vchStatus;
    }

    public void setVchStatus(String vchStatus) {
        this.vchStatus = vchStatus;
    }

    public VouchStatus getVouchStatus() {
        return vouchStatus;
    }

    public void setVouchStatus(VouchStatus vouchStatus) {
        this.vouchStatus = vouchStatus;
    }

    public HmTxnVch getSelectedTxnVch() {
        return selectedTxnVch;
    }

    public void setSelectedTxnVch(HmTxnVch selectedTxnVch) {
        this.selectedTxnVch = selectedTxnVch;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public List<HmTxnVch> getVchList() {
        return vchList;
    }

    public void setVchList(List<HmTxnVch> vchList) {
        this.vchList = vchList;
    }

    public ActInfoService getActInfoService() {
        return actInfoService;
    }

    public void setActInfoService(ActInfoService actInfoService) {
        this.actInfoService = actInfoService;
    }

    public List<HmVchJrnl> getVchRecvList() {
        return vchRecvList;
    }

    public void setVchRecvList(List<HmVchJrnl> vchRecvList) {
        this.vchRecvList = vchRecvList;
    }

    public String getVchNo() {
        return vchNo;
    }

    public void setVchNo(String vchNo) {
        this.vchNo = vchNo;
    }

    public List<HmTxnVch> getVchTxnList() {
        return vchTxnList;
    }

    public void setVchTxnList(List<HmTxnVch> vchTxnList) {
        this.vchTxnList = vchTxnList;
    }

    public List<HmVchJrnl> getVchInList() {
        return vchInList;
    }

    public void setVchInList(List<HmVchJrnl> vchInList) {
        this.vchInList = vchInList;
    }

    public List<HmVchTxnVO> getVchTxnAcctList() {
        return vchTxnAcctList;
    }

    public void setVchTxnAcctList(List<HmVchTxnVO> vchTxnAcctList) {
        this.vchTxnAcctList = vchTxnAcctList;
    }
}
