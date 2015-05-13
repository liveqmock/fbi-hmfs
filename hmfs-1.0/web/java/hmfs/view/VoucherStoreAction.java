package hmfs.view;

import common.enums.VouchStatus;
import common.repository.hmfs.model.HmVchJrnl;
import common.repository.hmfs.model.HmVchStore;
import hmfs.service.VoucherService;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.advance.utils.PropertyManager;
import pub.platform.security.OperatorManager;
import skyline.common.utils.MessageUtil;
import skyline.service.PlatformService;
import skyline.service.ToolsService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分行及网点的票据库存管理.
 * User: zhanrui
 * Date: 20150423
 */
@ManagedBean
@ViewScoped
public class VoucherStoreAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(VoucherStoreAction.class);

    @ManagedProperty(value = "#{platformService}")
    private PlatformService platformService;
    @ManagedProperty(value = "#{toolsService}")
    private ToolsService toolsService;
    @ManagedProperty(value = "#{voucherService}")
    private VoucherService voucherService;

    private List<HmVchStore> vchStoreList = new ArrayList<HmVchStore>();
    private List<HmVchStore> vchStoreList2 = new ArrayList<HmVchStore>();
    private List<HmVchJrnl> vchJrnlList = new ArrayList<HmVchJrnl>();
    private HmVchStore selectedStoreRecord;
    private VouchStatus vouchStatus = VouchStatus.USED;

    private int totalCount;

    private String startno;
    private String endno;
    private List<SelectItem> vchStsList = new ArrayList<SelectItem>();
    private String operation = "input"; // input output
    private List<SelectItem> branchList;
    private String fromBranchId;    //拨出机构
    private String toBranchId;      //拨入机构
    private String branchId;      //登录柜员机构

    private VouchStatus vchStatus = VouchStatus.RECEIVED;
    private Map<String, String> operMap;
    private Map<String, String> deptMap;

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String txnType = request.getParameter("type");
        if (txnType == null) {
            txnType = "qryjrnl";
        }

        OperatorManager om = platformService.getOperatorManager();
        branchId = om.getOperator().getDeptid();

        if ("headoffice".equalsIgnoreCase(txnType)) {
            branchId = voucherService.selectHoInstNo();
            initHeadOfficeDataList();
        }
        this.branchList = toolsService.selectBranchList(branchId);

        vchStsList.add(new SelectItem(VouchStatus.USED.getCode(), VouchStatus.USED.getTitle()));
        vchStsList.add(new SelectItem(VouchStatus.CANCEL.getCode(), VouchStatus.CANCEL.getTitle()));
        selectedStoreRecord = new HmVchStore();
        selectedStoreRecord.setVchCount(0L);

        operMap = voucherService.selectPtoperMap();
        deptMap = voucherService.selectPtdeptMap();
    }

    public void onQuery() {
        initHeadOfficeDataList();
    }

    private void initHeadOfficeDataList() {
        String headOfficeInstNo = voucherService.selectHoInstNo();
        this.vchStoreList = voucherService.selectInstitutionVoucherStoreList(headOfficeInstNo);

    }

    public void onRecvFromHmb() {
        try {
            int vchnoLen = PropertyManager.getIntProperty("voucher_no_length");
            String startNo = this.selectedStoreRecord.getVchStartNo();
            String endNo = this.selectedStoreRecord.getVchEndNo();
            if (startNo.length() != vchnoLen || endNo.length() != vchnoLen) {
                MessageUtil.addError("票号长度错误。");
                return;
            }
            long cnt = this.selectedStoreRecord.getVchCount();
            if (cnt != Long.parseLong(endNo) - Long.parseLong(startNo) + 1) {
                MessageUtil.addError("输入的票据数量与按照起止号计算的数量不符，请重新输入..");
                return;
            } else {
                voucherService.processHoInstVchInput(cnt, startNo, endNo);
            }

            selectedStoreRecord = new HmVchStore();
            selectedStoreRecord.setVchCount(0L);
            initHeadOfficeDataList();
            RequestContext.getCurrentInstance().execute("document.forms['form']['form:tabview:vchstartno'].focus;");
        } catch (Exception e) {
            logger.error("操作处理失败。" + e.getMessage(), e);
            MessageUtil.addError("操作处理失败。" + e.getMessage());
        }
    }


    public void startSendToHmb() {
        operation = "output";
        disableInputtext();
    }

    //缴回处理
    public String onSendToHmb() {
        try {
            voucherService.processHoInstVchOutput(selectedStoreRecord);
            selectedStoreRecord = new HmVchStore();
            operation = "input";
            enableInputtext();
            initHeadOfficeDataList();
        } catch (Exception e) {
            logger.error("操作处理失败。" + e.getMessage(), e);
            MessageUtil.addError("操作处理失败。" + e.getMessage());
        }
        return null;
    }

    public void onTestVoucher(){

        voucherService.processVchUseOrCancel(branchId, VouchStatus.USED, "011", "020");

    }


    //==============================================================
    public void startTransfer() {
        operation = "output";
        disableInputtext();
    }

    //调拨处理时的库存查询
    public void onTransferQuery() {
        if (fromBranchId.equals(toBranchId)) {
            MessageUtil.addError("请选择不同的机构。");
        } else {
            initTransferdataList();
        }
    }

    private void initTransferdataList() {
        this.vchStoreList = voucherService.selectInstitutionVoucherStoreList(fromBranchId);
        this.vchStoreList2 = voucherService.selectInstitutionVoucherStoreList(toBranchId);
    }

    //票据调拨
    public void onTransfer() {
        if (StringUtils.isEmpty(selectedStoreRecord.getVchStartNo())
                || StringUtils.isEmpty(selectedStoreRecord.getVchEndNo())
                || selectedStoreRecord.getVchCount() == 0) {
            MessageUtil.addError("票据号不能为空，数量不可为零。");
            return;
        }

        try {
            int vchnoLen = PropertyManager.getIntProperty("voucher_no_length");
            String startNo = this.selectedStoreRecord.getVchStartNo();
            String endNo = this.selectedStoreRecord.getVchEndNo();
            if (startNo.length() != vchnoLen || endNo.length() != vchnoLen) {
                MessageUtil.addError("票号长度错误。");
                return;
            }
            long cnt = this.selectedStoreRecord.getVchCount();
            BigDecimal start = new BigDecimal(startNo);
            BigDecimal end = new BigDecimal(endNo);
            if (cnt != end.subtract(start).intValue() + 1) {
                MessageUtil.addError("输入的票据数量与按照起止号计算的数量不符，请重新输入..");
                return;
            } else {
                voucherService.processVchTransfer(fromBranchId, toBranchId, selectedStoreRecord);
            }
            selectedStoreRecord = new HmVchStore();
            selectedStoreRecord.setVchCount(0L);
            initTransferdataList();
        } catch (Exception e) {
            logger.error("操作处理失败。" + e.getMessage(), e);
            MessageUtil.addError("操作处理失败。" + e.getMessage());
        }
    }

    public void  onQryJrnl(){
        try {
            initvchJrnlList();
        } catch (Exception e) {
            logger.error("操作处理失败。" + e.getMessage(), e);
            MessageUtil.addError("操作处理失败。" + e.getMessage());
        }
    }

    private void initvchJrnlList() {
        this.vchStoreList = voucherService.selectInstitutionVoucherStoreList(fromBranchId);
        this.vchJrnlList = voucherService.selectVchJrnl(fromBranchId);
    }

    public String findOperName(String operid){
        String opername = voucherService.selectPtoperMap().get(operid);
        if (opername == null) {
            opername = "";
        }
        return opername;
    }
    //==============================================================
    private void disableInputtext() {
        RequestContext.getCurrentInstance().execute("document.forms['form']['form:tabview:vchstartno'].disabled = true;");
        //RequestContext.getCurrentInstance().execute("document.forms['form']['form:tabview:vchendno'].disabled = true;");
        //RequestContext.getCurrentInstance().execute("document.forms['form']['form:tabview:vchcnt'].disabled = true;");
    }

    private void enableInputtext() {
        RequestContext.getCurrentInstance().execute("document.forms['form']['form:tabview:vchstartno'].disabled = false;");
        RequestContext.getCurrentInstance().execute("document.forms['form']['form:tabview:vchendno'].disabled = false;");
        RequestContext.getCurrentInstance().execute("document.forms['form']['form:tabview:vchcnt'].disabled = false;");
    }

    //==============================================================
    public PlatformService getPlatformService() {
        return platformService;
    }

    public void setPlatformService(PlatformService platformService) {
        this.platformService = platformService;
    }

    public List<HmVchStore> getVchStoreList() {
        return vchStoreList;
    }

    public void setVchStoreList(List<HmVchStore> vchStoreList) {
        this.vchStoreList = vchStoreList;
    }

    public HmVchStore getSelectedStoreRecord() {
        return selectedStoreRecord;
    }

    public void setSelectedStoreRecord(HmVchStore selectedStoreRecord) {
        this.selectedStoreRecord = selectedStoreRecord;
    }

    public VouchStatus getVouchStatus() {
        return vouchStatus;
    }

    public void setVouchStatus(VouchStatus vouchStatus) {
        this.vouchStatus = vouchStatus;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
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

    public List<SelectItem> getVchStsList() {
        return vchStsList;
    }

    public void setVchStsList(List<SelectItem> vchStsList) {
        this.vchStsList = vchStsList;
    }

    public VoucherService getVoucherService() {
        return voucherService;
    }

    public void setVoucherService(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    public List<HmVchJrnl> getVchJrnlList() {
        return vchJrnlList;
    }

    public void setVchJrnlList(List<HmVchJrnl> vchJrnlList) {
        this.vchJrnlList = vchJrnlList;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<SelectItem> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<SelectItem> branchList) {
        this.branchList = branchList;
    }

    public ToolsService getToolsService() {
        return toolsService;
    }

    public void setToolsService(ToolsService toolsService) {
        this.toolsService = toolsService;
    }

    public String getFromBranchId() {
        return fromBranchId;
    }

    public void setFromBranchId(String fromBranchId) {
        this.fromBranchId = fromBranchId;
    }

    public String getToBranchId() {
        return toBranchId;
    }

    public void setToBranchId(String toBranchId) {
        this.toBranchId = toBranchId;
    }

    public List<HmVchStore> getVchStoreList2() {
        return vchStoreList2;
    }

    public void setVchStoreList2(List<HmVchStore> vchStoreList2) {
        this.vchStoreList2 = vchStoreList2;
    }

    public VouchStatus getVchStatus() {
        return vchStatus;
    }

    public void setVchStatus(VouchStatus vchStatus) {
        this.vchStatus = vchStatus;
    }

    public Map<String, String> getOperMap() {
        return operMap;
    }

    public void setOperMap(Map<String, String> operMap) {
        this.operMap = operMap;
    }

    public Map<String, String> getDeptMap() {
        return deptMap;
    }

    public void setDeptMap(Map<String, String> deptMap) {
        this.deptMap = deptMap;
    }
}

