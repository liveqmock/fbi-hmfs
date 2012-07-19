package hmfs.view;

import common.enums.SysCtlSts;
import common.repository.hmfs.model.*;
import hmfs.service.AppMngService;
import hmfs.service.DepService;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skyline.common.utils.MessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 通讯报文管理
 */
@ManagedBean
@ViewScoped
public class HmbMockAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(HmbMockAction.class);

    private String sysDate;
    private String sysTime;
    private String txnDate;
    private String sysSts;

    private String txnCode;
    private String msgSn;


    private List<TmpMsgOut> tmpMsgoutList;
    private List<TmpMsgIn> tmpMsginList;
    private TmpMsgIn[] selectedTmpMsginRecords;
    private TmpMsgOut[] selectedTmpMsgoutRecords;

    @ManagedProperty(value = "#{appMngService}")
    private AppMngService appMngService;
    @ManagedProperty(value = "#{depService}")
    private DepService depService;


    @PostConstruct
    public void init() {
        this.sysDate = new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
        this.txnDate = this.sysDate;
        this.sysTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());

        HmSysCtl hmSysCtl = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSysCtl.getSysSts());
        this.sysSts = sysCtlSts.getTitle();

        //initList();
    }

    private void initList() {
//        this.tmpMsginList = appMngService.selectTmpMsginList(txnCode, msgSn);
//        this.tmpMsgoutList = appMngService.selectTmpMsgoutList(txnCode, msgSn);
    }


    public String onQueryTmpMsgout() {
        try {
            this.tmpMsgoutList = appMngService.selectTmpMsgoutList(txnCode, msgSn);
            this.tmpMsginList = appMngService.selectTmpMsginList(txnCode, msgSn);
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    /**
     * 编辑模拟国土局发送报文
     *
     * @param event
     * @return
     */
    public String onSaveTmpMsgoutData(RowEditEvent event) {
        TmpMsgOut tmpMsgOut = (TmpMsgOut) event.getObject();
        try {
            appMngService.updateTmpMsgoutRecord(tmpMsgOut);
            MessageUtil.addInfo("记录处理成功");
        } catch (Exception e) {
            logger.error("修改记录时出现错误", e);
            MessageUtil.addError("修改记录时出现错误,可能帐号重复。");
        }
        return null;
    }

    public String onSendMockMsg() {
        if (StringUtils.isEmpty(txnCode)||StringUtils.isEmpty(msgSn)) {
            MessageUtil.addError("需输入交易码和报文编号。");
            return  null;
        }
        try {
            String response = depService.process("9001001|" + txnCode + "|" + msgSn);
            if (response.startsWith("0000")) { //成功
                MessageUtil.addInfo("发送成功, 国土局已受理, 稍等请查询返回信息...");
                this.tmpMsginList = appMngService.selectTmpMsginList(txnCode, msgSn);
                this.tmpMsgoutList = appMngService.selectTmpMsgoutList(txnCode, msgSn);
            } else {
                MessageUtil.addError("处理失败。" + response);
            }
        } catch (Exception e) {
            logger.error("处理失败。", e);
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    //=============================


    public String getSysDate() {
        return sysDate;
    }

    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }

    public String getSysTime() {
        return sysTime;
    }

    public void setSysTime(String sysTime) {
        this.sysTime = sysTime;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

    public String getSysSts() {
        return sysSts;
    }

    public void setSysSts(String sysSts) {
        this.sysSts = sysSts;
    }

    public String getTxnCode() {
        return txnCode;
    }

    public void setTxnCode(String txnCode) {
        this.txnCode = txnCode;
    }

    public String getMsgSn() {
        return msgSn;
    }

    public void setMsgSn(String msgSn) {
        this.msgSn = msgSn;
    }

    public List<TmpMsgOut> getTmpMsgoutList() {
        return tmpMsgoutList;
    }

    public void setTmpMsgoutList(List<TmpMsgOut> tmpMsgoutList) {
        this.tmpMsgoutList = tmpMsgoutList;
    }

    public List<TmpMsgIn> getTmpMsginList() {
        return tmpMsginList;
    }

    public void setTmpMsginList(List<TmpMsgIn> tmpMsginList) {
        this.tmpMsginList = tmpMsginList;
    }

    public TmpMsgIn[] getSelectedTmpMsginRecords() {
        return selectedTmpMsginRecords;
    }

    public void setSelectedTmpMsginRecords(TmpMsgIn[] selectedTmpMsginRecords) {
        this.selectedTmpMsginRecords = selectedTmpMsginRecords;
    }

    public TmpMsgOut[] getSelectedTmpMsgoutRecords() {
        return selectedTmpMsgoutRecords;
    }

    public void setSelectedTmpMsgoutRecords(TmpMsgOut[] selectedTmpMsgoutRecords) {
        this.selectedTmpMsgoutRecords = selectedTmpMsgoutRecords;
    }

    public AppMngService getAppMngService() {
        return appMngService;
    }

    public void setAppMngService(AppMngService appMngService) {
        this.appMngService = appMngService;
    }

    public DepService getDepService() {
        return depService;
    }

    public void setDepService(DepService depService) {
        this.depService = depService;
    }
}
