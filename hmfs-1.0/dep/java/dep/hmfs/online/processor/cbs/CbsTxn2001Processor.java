package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA2001;
import dep.hmfs.online.processor.cbs.domain.txn.TOA2001;
import dep.util.PropertyManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CbsTxn2001Processor extends CbsAbstractTxnProcessor {

    @Override
    public TOA process(TIAHeader tiaHeader, byte[] bytes) {
        TIA2001 tia2001 = new TIA2001();
        tia2001.body.drawApplyNo = new String(bytes, 0, 18).trim();

        TOA2001 toa2001 = null;
        // 查询汇总信息
        HmMsgIn totalDrawInfo = hmbBaseService.qryTotalMsgByMsgSn(tia2001.body.drawApplyNo, "00007");

        if (totalDrawInfo != null) {
            toa2001 = new TOA2001();
            toa2001.body.drawApplyNo = tia2001.body.drawApplyNo;
            toa2001.body.drawAmt = String.format("%.2f", totalDrawInfo.getTxnAmt1());
            if (TxnCtlSts.SUCCESS.getCode().equals(totalDrawInfo.getTxnCtlSts())) {
                toa2001.body.drawFlag = "1";
            } else {
                hmbBaseService.updateMsginSts(tia2001.body.drawApplyNo, TxnCtlSts.HANDLING);
                toa2001.body.drawFlag = "0";
            }
            toa2001.body.rcvAccountNo = totalDrawInfo.getPayinActno();
            toa2001.body.rcvAccountName = totalDrawInfo.getPayinActName();

            // 建行-明细
            if ("05".equals(PropertyManager.getProperty("SEND_SYS_ID"))) {
                String[] payMsgTypes = {"01041"};
                List<HmMsgIn> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia2001.body.drawApplyNo, payMsgTypes);
                if (payInfoList.size() > 0) {
                    toa2001.body.drawDetailNum = String.valueOf(payInfoList.size());
                    for (HmMsgIn hmMsgIn : payInfoList) {
                        TOA2001.Body.Record record = new TOA2001.Body.Record();
                        record.accountName = hmMsgIn.getInfoName();   //21
                        record.txAmt = String.format("%.2f", hmMsgIn.getTxnAmt1());
                        record.address = hmMsgIn.getInfoAddr();    //22
                        record.houseArea = StringUtils.isEmpty(hmMsgIn.getBuilderArea()) ? "" : hmMsgIn.getBuilderArea();
                        record.fundActno1 = hmMsgIn.getFundActno1();
                        record.fundActno2 = hmMsgIn.getFundActno2();
                        record.balAmt = String.format("%.2f", hmMsgIn.getActBal());
                        toa2001.body.recordList.add(record);
                    }
                }
            }
        } else {
            throw new RuntimeException(CbsErrorCode.QRY_NO_RECORDS.getCode());
        }

        return toa2001;
    }
}
