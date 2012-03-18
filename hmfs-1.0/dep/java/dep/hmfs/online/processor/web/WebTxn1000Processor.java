package dep.hmfs.online.processor.web;

import dep.hmfs.online.service.hmb.HmbSysTxnService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * ����.
 * User: zhanrui
 * Date: 12-3-12
 * Time: ����11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn1000Processor extends WebAbstractTxnProcessor {

    @Resource
    private HmbSysTxnService hmbSysTxnService;

    @Resource
    private WebTxn7003Processor webTxn7003Processor;

    @Override
    public String process(String request) throws Exception{
        String actionCode = request.split("\\|")[1];
        if (actionCode == null || "".equals(actionCode)) {
            throw  new RuntimeException("�޶����룡");
        }
        if ("signon".equalsIgnoreCase(actionCode)) {
            return doSignon();
        }else if ("signout".equalsIgnoreCase(actionCode)) {
            return doSignout();
        }else if ("chkbal".equalsIgnoreCase(actionCode)) {
            return doChkbal();
        }else if ("chkdetl".equalsIgnoreCase(actionCode)) {
            return doChkdetl();
        }else if ("openact".equalsIgnoreCase(actionCode)) {
            return doOpenact(request);
        }else{
            return "9999|���������";
        }
    }

    private String doSignon() {
        hmbSysTxnService.processSignon();
        return "0000"; //�ɹ�
    }
    private String doSignout() {
        hmbSysTxnService.processSignout();
        return "0000"; //�ɹ�
    }
    private String doChkbal() {
        //hmbCmnTxnService.processChkActBal();
        webTxn7003Processor.process(null);
        return "0000"; //�ɹ�
    }

    private String doChkdetl() {
        hmbSysTxnService.processChkActDetl();
        return "0000"; //�ɹ�
    }
    private String doOpenact(String request) {
        hmbSysTxnService.processOpenactRequest(request);
        return "0000"; //�ɹ�
    }
}