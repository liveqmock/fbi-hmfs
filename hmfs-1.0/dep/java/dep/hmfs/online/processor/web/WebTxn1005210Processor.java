package dep.hmfs.online.processor.web;

import dep.hmfs.online.processor.hmb.domain.Msg100;
import org.springframework.stereotype.Component;

/**
 * ά���ʽ�ɿ�.
 * User: zhanrui
 * Date: 12-3-25
 * Time: ����1:45
 */
@Component
public class WebTxn1005210Processor extends WebAbstractHmbProductTxnProcessor{

    public String process(String request)  {
        processRequest(request);
        return "0000|�ɿ�ɹ�";
    }

    private void processRequest(String request) {
        //���ͱ���
        Msg100 msg100 = new Msg100();
        msg100.setRtnInfoCode("00");

        if (!msg100.rtnInfoCode.equals("00")) {
            throw new RuntimeException("�����ַ��ش�����Ϣ��" + msg100.rtnInfo);
        }
    }

}
