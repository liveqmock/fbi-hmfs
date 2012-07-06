package hmfs.view;

import common.repository.hmfs.model.HmMsgIn;
import hmfs.service.ActInfoService;
import hmfs.service.AppMngService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.util.List;

/**
 * 基本的票据查询 状态更新处理.
 * User: zhanrui
 * Date: 12-4-22
 * Time: 上午10:07
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
public class VoucherAction {
    private static final Logger logger = LoggerFactory.getLogger(VoucherAction.class);

    private HmMsgIn  hmMsgIn;
    private List<HmMsgIn> subMsgList;

    @ManagedProperty(value = "#{appMngService}")
    private AppMngService appMngService;
    @ManagedProperty(value = "#{actInfoService}")
    private ActInfoService actInfoService;

    @PostConstruct
    public void init() {
        hmMsgIn = new HmMsgIn();
        hmMsgIn.setBankName("aaaaaaa111");
    }
    public HmMsgIn getHmMsgIn() {

        return hmMsgIn;
    }

    public void setHmMsgIn(HmMsgIn hmMsgIn) {
        this.hmMsgIn = hmMsgIn;
    }

    public List<HmMsgIn> getSubMsgList() {
        return subMsgList;
    }

    public void setSubMsgList(List<HmMsgIn> subMsgList) {
        this.subMsgList = subMsgList;
    }

    public AppMngService getAppMngService() {
        return appMngService;
    }

    public void setAppMngService(AppMngService appMngService) {
        this.appMngService = appMngService;
    }

    public ActInfoService getActInfoService() {
        return actInfoService;
    }

    public void setActInfoService(ActInfoService actInfoService) {
        this.actInfoService = actInfoService;
    }
}
