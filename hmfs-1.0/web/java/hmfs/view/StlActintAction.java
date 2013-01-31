package hmfs.view;

import common.enums.FundActnoStatus;
import common.enums.TxnCtlSts;
import common.enums.VouchStatus;
import common.repository.hmfs.model.HmActStl;
import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmTxnStl;
import common.repository.hmfs.model.HmTxnVch;
import common.service.SystemService;
import hmfs.common.util.JxlsManager;
import hmfs.service.ActInfoService;
import hmfs.service.DepService;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.commandbutton.CommandButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.system.manage.dao.PtOperBean;
import skyline.common.utils.MessageUtil;
import skyline.service.PlatformService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ��Ϣ
 */

@ManagedBean
@ViewScoped
public class StlActintAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(StlActintAction.class);
    @ManagedProperty(value = "#{actInfoService}")
    private ActInfoService actInfoService;
    private String cbsActno;         // �����˺�
    private BigDecimal intamt;       // ���μ�Ϣ���
    private String intDate;          // ��Ϣʱ��
    private FundActnoStatus fundActnoStatus = FundActnoStatus.NORMAL;

    private List<HmActStl> actStlList;
    private List<HmTxnStl> intTxnStlList;

    private HmTxnStl hmTxnStl;
    private String startDate;
    private String endDate;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        String pkid = (String) context.getExternalContext().getRequestParameterMap().get("pkid");
        cbsActno = actInfoService.selectStlActno();
        endDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        startDate = endDate.substring(0, 6) + "01";
        if (!StringUtils.isEmpty(pkid)) {
            hmTxnStl = actInfoService.selectTxnStlByPkid(pkid);
            intamt = hmTxnStl.getTxnAmt();
            intDate = hmTxnStl.getRemark().substring(0, 10);
        } else {
            intDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
    }

    public String onAcctualInt() {
        try {
            //  ���¼�Ϣ���
            //  ����������ϸ  ��ע��Ϣ����Ϣʱ�� ����Ա ����ʱ��
            actInfoService.intAcctual(SystemService.formatTodayByPattern("yyMMddHHMMSSsss"), cbsActno, intDate, intamt);
        } catch (Exception e) {
            MessageUtil.addError("����ʧ�ܡ�" + e.getMessage());
            logger.error("��Ϣʧ��", e);
            return null;
        }
        actStlList = actInfoService.qryActStlsByCbsActno(cbsActno);
        return null;
    }

    public String onQueryIntTxnStls() {
        try {
            intTxnStlList = actInfoService.selectStlIntTxns(cbsActno, startDate, endDate);
            if(intTxnStlList.isEmpty()) {
                MessageUtil.addWarn("���ݲ�����...");
            }
        } catch (Exception e) {
            MessageUtil.addError("����ʧ�ܡ�" + e.getMessage());
            logger.error("��Ϣ��ϸ��ѯʧ��", e);
        }
        return null;
    }

    public String onIntTxnEdit() {
        try {
            int rtn = actInfoService.updateNewIntacc(hmTxnStl, intamt, intDate);
            if (rtn == 2) {
                UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
                CommandButton saveBtn = (CommandButton) viewRoot.findComponent("form:saveBtn");
                saveBtn.setDisabled(true);
                MessageUtil.addInfo("��Ϣ�޸���ɣ�");
            } else {
                MessageUtil.addError("��Ϣ�޸�ʧ�ܣ�");
            }
        } catch (Exception e) {
            MessageUtil.addError("����ʧ�ܡ�" + e.getMessage());
            logger.error("��Ϣ�޸�ʧ��", e);
            return null;
        }
        actStlList = actInfoService.qryActStlsByCbsActno(cbsActno);
        return null;
    }

    // ----------------------------------------------------------

    public List<HmTxnStl> getIntTxnStlList() {
        return intTxnStlList;
    }

    public void setIntTxnStlList(List<HmTxnStl> intTxnStlList) {
        this.intTxnStlList = intTxnStlList;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public HmTxnStl getHmTxnStl() {
        return hmTxnStl;
    }

    public void setHmTxnStl(HmTxnStl hmTxnStl) {
        this.hmTxnStl = hmTxnStl;
    }

    public FundActnoStatus getFundActnoStatus() {
        return fundActnoStatus;
    }

    public void setFundActnoStatus(FundActnoStatus fundActnoStatus) {
        this.fundActnoStatus = fundActnoStatus;
    }

    public List<HmActStl> getActStlList() {
        return actStlList;
    }

    public void setActStlList(List<HmActStl> actStlList) {
        this.actStlList = actStlList;
    }

    public BigDecimal getIntamt() {
        return intamt;
    }

    public void setIntamt(BigDecimal intamt) {
        this.intamt = intamt;
    }

    public String getIntDate() {
        return intDate;
    }

    public void setIntDate(String intDate) {
        this.intDate = intDate;
    }

    public ActInfoService getActInfoService() {
        return actInfoService;
    }

    public void setActInfoService(ActInfoService actInfoService) {
        this.actInfoService = actInfoService;
    }

    public String getCbsActno() {
        return cbsActno;
    }

    public void setCbsActno(String cbsActno) {
        this.cbsActno = cbsActno;
    }
}
