package dep.hmfs.online.web;

import dep.hmfs.service.hmb.AppBaseService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-12
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn1000Processor extends AbstractWebTxnProcessor {

    @Resource
    private AppBaseService appBaseService;

    @Override
    public String process(String request) throws Exception{
        String actionCode = request.split("\\|")[1];
        if (actionCode == null || "".equals(actionCode)) {
            throw  new RuntimeException("无动作码！");
        }
        if ("signon".equalsIgnoreCase(actionCode)) {
            return doSignon();
        }else if ("signout".equalsIgnoreCase(actionCode)) {
            return doSignout();
        }else if ("chkbal".equalsIgnoreCase(actionCode)) {
            return doChkbal();
        }else if ("chkdetl".equalsIgnoreCase(actionCode)) {
            return doChkdetl();
        }else{
            return "9999|动作码错误！";
        }
    }

    private String doSignon() {
        appBaseService.processSignon();
        return "0000"; //成功
    }
    private String doSignout() {
        appBaseService.processSignout();
        return "0000"; //成功
    }
    private String doChkbal() {
        appBaseService.processSignon();
        return "0000"; //成功
    }
    private String doChkdetl() {
        appBaseService.processSignon();
        return "0000"; //成功
    }
}