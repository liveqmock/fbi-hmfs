package hmfs.view;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.enums.VouchStatus;
import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmTxnVch;
import hmfs.common.util.JxlsManager;
import hmfs.service.ActInfoService;
import hmfs.service.DepService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * 基本的票据查询 状态更新处理.
 * User: zhanrui
 * Date: 12-4-22
 * Time: 上午10:07
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

    private String msgSn = " ";
    private String fundActno;
    private int totalCount;
    private List<HmMsgIn> subMsgList;
    private TxnCtlSts txnCtlSts = TxnCtlSts.SUCCESS;

    private String startno;
    private String endno;
    private String vchSendStatus;
    private List<SelectItem> vchStsList = new ArrayList<SelectItem>();

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
                MessageUtil.addWarn("数据不存在...");
            }
        } catch (Exception e) {
            logger.error("操作处理失败。" + e.getMessage(), e);
            MessageUtil.addError("操作处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onExportExcelForVch() {
        onQuery();
        if (this.vchList.size() == 0) {
            MessageUtil.addWarn("记录为空...");
            return null;
        } else {
            if (VouchStatus.CANCEL.getCode().equals(this.vchStatus)) {
                String excelFilename = "维修资金作废票据清单-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
                JxlsManager jxls = new JxlsManager();
                jxls.exportTxnvchList(excelFilename, this.vchList);
            }
            // 其他状态的票据需要添加时再修改导出文件名
        }
        return null;
    }

    public String onQrySubMsgin() {
        try {
            this.subMsgList = actInfoService.selectSubMsgList(msgSn);
            this.totalCount = this.subMsgList.size();
            if (this.totalCount <= 0) {
                MessageUtil.addWarn("没有查询到数据记录。");
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    // 票据发送
    public String onSendVoucher() {
        long lstartNo = 0;
        long lendNo = 0;
        endno = StringUtils.isEmpty(endno) ? startno : endno;
        try {
            lstartNo = Long.parseLong(startno);
            lendNo = Long.parseLong(endno);
        } catch (Exception e) {
            MessageUtil.addError("票据起止号码错误。");
        }
        try {
            if (lstartNo > lendNo) {
                throw new RuntimeException("票据起止号码输入错误！");
            }
            onQrySubMsgin();
            PtOperBean oper = platformService.getOperatorManager().getOperator();
            String response = depService.process("1005610|" + this.msgSn + "|"
                    + oper.getDeptid() + "|" + oper.getOperid() + "|" + vchSendStatus
                    + "|" + startno + "|" + endno);
            if (response.startsWith("0000")) { //成功
                MessageUtil.addInfo("票据发送成功。");
            } else {
                MessageUtil.addError("处理失败。" + response);
            }
        } catch (Exception e) {
            MessageUtil.addError(e.getMessage());
        }
        return null;
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
}
