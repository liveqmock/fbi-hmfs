package dep.hmfs.online.processor.web;

import dep.hmfs.online.processor.hmb.domain.Msg100;
import org.springframework.stereotype.Component;

/**
 * 维修资金缴款.
 * User: zhanrui
 * Date: 12-3-25
 * Time: 下午1:45
 */
@Component
public class WebTxn1005210Processor extends WebAbstractHmbProductTxnProcessor{

    public String process(String request)  {
        processRequest(request);
        return "0000|缴款成功";
    }

    private void processRequest(String request) {
        //发送报文
        Msg100 msg100 = new Msg100();
        msg100.setRtnInfoCode("00");

        if (!msg100.rtnInfoCode.equals("00")) {
            throw new RuntimeException("国土局返回错误信息：" + msg100.rtnInfo);
        }
    }

}
