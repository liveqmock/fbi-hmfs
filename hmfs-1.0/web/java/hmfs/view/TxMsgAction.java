package hmfs.view;

import common.enums.SysCtlSts;
import common.repository.hmfs.model.*;
import hmfs.service.AppMngService;
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
public class TxMsgAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(TxMsgAction.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String sysDate;
    private String sysTime;
    private String txnDate;
    private String sysSts;

    private String txnCode;
    private String msgSn;

    private List<HisMsgoutLog> hisMsgoutList;
    private List<HisMsginLog> hisMsginList;
    private HisMsginLog[] selectedHisMsginRecords;
    private HisMsgoutLog[] selectedHisMsgoutRecords;

    private List<TmpMsgoutLog> tmpMsgoutList;
    private List<TmpMsginLog> tmpMsginList;
    private TmpMsginLog[] selectedTmpMsginRecords;
    private TmpMsgoutLog[] selectedTmpMsgoutRecords;

    @ManagedProperty(value = "#{appMngService}")
    private AppMngService appMngService;


    @PostConstruct
    public void init() {
        this.sysDate = new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
        this.txnDate = this.sysDate;
        this.sysTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());

        HmSct hmSct = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        this.sysSts = sysCtlSts.getTitle();

        //initList();
    }

    private void initList(){
        this.hisMsginList = appMngService.selectHisMsginList(txnCode, msgSn);
        this.hisMsgoutList = appMngService.selectHisMsgoutList(txnCode, msgSn);
        this.tmpMsginList = appMngService.selectTmpMsginList(txnCode, msgSn);
        this.tmpMsgoutList = appMngService.selectTmpMsgoutList(txnCode, msgSn);
    }


    public String onQueryHisMsgin() {
        try {
            this.hisMsginList = appMngService.selectHisMsginList(txnCode, msgSn);
            if (hisMsginList.size() == 0) {
                MessageUtil.addWarn("未查询到记录.");
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }
    public String onQueryHisMsgout() {
        try {
            this.hisMsgoutList = appMngService.selectHisMsgoutList(txnCode, msgSn);
            if (hisMsgoutList.size() == 0) {
                MessageUtil.addWarn("未查询到记录.");
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }
    public String onQueryTmpMsgin() {
        try {
            this.tmpMsginList = appMngService.selectTmpMsginList(txnCode, msgSn);
            if (tmpMsginList.size() == 0) {
                MessageUtil.addWarn("未查询到记录.");
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }
    public String onQueryTmpMsgout() {
        try {
            this.tmpMsgoutList = appMngService.selectTmpMsgoutList(txnCode, msgSn);
            if (tmpMsgoutList.size() == 0) {
                MessageUtil.addWarn("未查询到记录.");
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    /**
     * 编辑模拟国土局发送报文
     * @return
     */
    public String onSaveTmpMsgoutData(RowEditEvent event) {
        TmpMsgoutLog tmpMsgoutLog = (TmpMsgoutLog) event.getObject();
        try {
            appMngService.updateTmpMsgoutRecord(tmpMsgoutLog);
            MessageUtil.addInfo("记录处理成功");
        } catch (Exception e) {
            logger.error("修改记录时出现错误", e);
            MessageUtil.addError("修改记录时出现错误,可能帐号重复。");
        }
        return null;
    }


    //=============================

    public static SimpleDateFormat getSdf() {
        return sdf;
    }

    public static void setSdf(SimpleDateFormat sdf) {
        TxMsgAction.sdf = sdf;
    }

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

    public List<HisMsgoutLog> getHisMsgoutList() {
        return hisMsgoutList;
    }

    public void setHisMsgoutList(List<HisMsgoutLog> hisMsgoutList) {
        this.hisMsgoutList = hisMsgoutList;
    }

    public List<HisMsginLog> getHisMsginList() {
        return hisMsginList;
    }

    public void setHisMsginList(List<HisMsginLog> hisMsginList) {
        this.hisMsginList = hisMsginList;
    }

    public HisMsginLog[] getSelectedHisMsginRecords() {
        return selectedHisMsginRecords;
    }

    public void setSelectedHisMsginRecords(HisMsginLog[] selectedHisMsginRecords) {
        this.selectedHisMsginRecords = selectedHisMsginRecords;
    }

    public HisMsgoutLog[] getSelectedHisMsgoutRecords() {
        return selectedHisMsgoutRecords;
    }

    public void setSelectedHisMsgoutRecords(HisMsgoutLog[] selectedHisMsgoutRecords) {
        this.selectedHisMsgoutRecords = selectedHisMsgoutRecords;
    }

    public List<TmpMsgoutLog> getTmpMsgoutList() {
        return tmpMsgoutList;
    }

    public void setTmpMsgoutList(List<TmpMsgoutLog> tmpMsgoutList) {
        this.tmpMsgoutList = tmpMsgoutList;
    }

    public List<TmpMsginLog> getTmpMsginList() {
        return tmpMsginList;
    }

    public void setTmpMsginList(List<TmpMsginLog> tmpMsginList) {
        this.tmpMsginList = tmpMsginList;
    }

    public TmpMsginLog[] getSelectedTmpMsginRecords() {
        return selectedTmpMsginRecords;
    }

    public void setSelectedTmpMsginRecords(TmpMsginLog[] selectedTmpMsginRecords) {
        this.selectedTmpMsginRecords = selectedTmpMsginRecords;
    }

    public TmpMsgoutLog[] getSelectedTmpMsgoutRecords() {
        return selectedTmpMsgoutRecords;
    }

    public void setSelectedTmpMsgoutRecords(TmpMsgoutLog[] selectedTmpMsgoutRecords) {
        this.selectedTmpMsgoutRecords = selectedTmpMsgoutRecords;
    }

    public AppMngService getAppMngService() {
        return appMngService;
    }

    public void setAppMngService(AppMngService appMngService) {
        this.appMngService = appMngService;
    }
}
