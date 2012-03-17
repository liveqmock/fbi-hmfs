package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg003;
import dep.hmfs.online.processor.hmb.domain.Msg004;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HmbTxn5120Processor extends HmbAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn5120Processor.class);

    @Override
    public byte[] process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) {

        Msg004 msg004 = createRtnMsg004(msgSn);
        try {
            hmbBaseService.updateOrInsertMsginsByHmbMsgList(txnCode, hmbMsgList);
            Msg003 msg003 = (Msg003)hmbMsgList.get(0);
            msg004.infoId1 = msg003.infoId1;
            msg004.infoIdType1 = msg003.infoIdType1;
            msg004.districtId = msg003.districtId;
            List<HmbMsg> subMsgList = hmbMsgList.subList(1, hmbMsgList.size());
            int cnt = hmActinfoFundService.createActinfoFundsByMsgList(subMsgList);
            msg004.rtnInfo = cnt + "笔项目核算户开户完成";

        } catch (Exception e) {
            logger.error("5120交易处理异常！", e);
            msg004.rtnInfoCode = "99";
            msg004.rtnInfo = "报文接收处理失败";
        }
        // 响应
        List<HmbMsg> rtnHmbMsgList = new ArrayList<HmbMsg>();
        rtnHmbMsgList.add(msg004);
        return mf.marshal("5120", rtnHmbMsgList);
    }
}
