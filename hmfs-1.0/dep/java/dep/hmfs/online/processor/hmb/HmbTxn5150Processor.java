package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg005;
import dep.hmfs.online.processor.hmb.domain.Msg006;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Deprecated
public class HmbTxn5150Processor extends HmbAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn5150Processor.class);

    @Override
    public byte[] process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) {
        Msg006 msg006 = new Msg006();
        List<HmbMsg> detailHmbMsgList = hmbMsgList.subList(1, hmbMsgList.size());
        try {
            hmbBaseService.updateOrInsertMsginsByHmbMsgList(txnCode, hmbMsgList);
            Msg005 msg005 = (Msg005) hmbMsgList.get(0);
            BeanUtils.copyProperties(msg006, msg005);
            msg006.msgSn = msgSn;
            msg006.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
            msg006.origSysId = "00";
            msg006.origMsgSn = msg005.msgSn;
            msg006.rtnInfoCode = "00";

            int cnt = hmbActinfoService.createActinfoFundsByMsgList(detailHmbMsgList);
            msg006.rtnInfo = cnt + "��5150���Ľ�����ɡ�";
        } catch (Exception e) {
            logger.error("5150���״����쳣��", e);
            msg006.rtnInfoCode = "99";
            msg006.rtnInfo = "����ʧ��,ԭ��" + e.getMessage();
        }
        // ��Ӧ
        List<HmbMsg> rtnHmbMsgList = new ArrayList<HmbMsg>();
        rtnHmbMsgList.add(msg006);
        rtnHmbMsgList.addAll(detailHmbMsgList);
        return mf.marshal("5150", rtnHmbMsgList);
    }
}
