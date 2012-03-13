package dep.hmfs.online.web;

import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-12
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn1000Processor extends AbstractWebTxnProcessor {

    @Override
    public String process(String request) throws Exception{
        String actionCode = request.split("\\|")[1];
        if (actionCode == null || "".equals(actionCode)) {
            throw  new RuntimeException("无动作码！");
        }
        if ("signon".equalsIgnoreCase(actionCode)) {
            return doSignon();
        }else{
            return "9999|动作码错误！";
        }
    }

    private String doSignon() {
        return "0000"; //成功
    }
}