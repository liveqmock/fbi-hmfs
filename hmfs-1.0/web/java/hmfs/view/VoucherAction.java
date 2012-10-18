package hmfs.view;

import common.enums.TxnCtlSts;
import common.enums.VouchStatus;
import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmTxnVch;
import hmfs.common.util.JxlsManager;
import hmfs.service.ActInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skyline.common.utils.MessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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

    @PostConstruct
    public void init() {
//        hmMsgIn = new HmMsgIn();
//        hmMsgIn.setBankName(" ");
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
            this.subMsgList = actInfoService.selectSubMsgList(msgSn);
            this.totalCount = this.subMsgList.size();
            if(this.totalCount <= 0) {
                MessageUtil.addWarn("û�в�ѯ�����ݼ�¼��");
            }
        } catch (Exception e) {
            MessageUtil.addError("����ʧ�ܡ�" + e.getMessage());
        }
        return null;
    }

    /* public HmMsgIn getHmMsgIn() {

            return hmMsgIn;
        }

        public void setHmMsgIn(HmMsgIn hmMsgIn) {
            this.hmMsgIn = hmMsgIn;
        }
        */

    // ----------------------------------------------------------

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
}
