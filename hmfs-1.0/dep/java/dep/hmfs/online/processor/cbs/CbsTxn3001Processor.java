package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA3001;
import dep.hmfs.online.processor.cbs.domain.txn.TOA3001;
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
public class CbsTxn3001Processor extends CbsAbstractTxnProcessor {

    @Override
    public TOA process(TIAHeader tiaHeader, byte[] bytes) {
        TIA3001 tia3001 = new TIA3001();
        tia3001.body.refundApplyNo = new String(bytes, 0, 18).trim();

        TOA3001 toa3001 = null;
        // 查询汇总信息
        HmMsgIn totalRefundInfo = hmbBaseService.qryTotalMsgByMsgSn(tia3001.body.refundApplyNo, "00005");

        if (totalRefundInfo != null) {
            toa3001 = new TOA3001();
            toa3001.body.refundApplyNo = tia3001.body.refundApplyNo;
            toa3001.body.refundAmt = String.format("%.2f", totalRefundInfo.getTxnAmt1());
            if (TxnCtlSts.SUCCESS.getCode().equals(totalRefundInfo.getTxnCtlSts())) {
                toa3001.body.refundFlag = "1";
            } else {
                toa3001.body.refundFlag = "0";
                hmbBaseService.updateMsginSts(tia3001.body.refundApplyNo, TxnCtlSts.HANDLING);
            }

            // 建行-明细
            if ("05".equals(PropertyManager.getProperty("SEND_SYS_ID"))) {
                String[] payMsgTypes = {"01039", "01043"};
                List<HmMsgIn> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia3001.body.refundApplyNo, payMsgTypes);
                if (payInfoList.size() > 0) {
                    toa3001.body.refundDetailNum = String.valueOf(payInfoList.size());
                    for (HmMsgIn hmMsgIn : payInfoList) {
                        TOA3001.Body.Record record = new TOA3001.Body.Record();
                        record.accountName = hmMsgIn.getInfoName();   //21
                        record.txAmt = String.format("%.2f", hmMsgIn.getTxnAmt1());
                        record.address = hmMsgIn.getInfoAddr();    //22
                        record.houseArea = StringUtils.isEmpty(hmMsgIn.getBuilderArea()) ? "" : hmMsgIn.getBuilderArea();
                        /*   record.fundActno1 = hmMsgIn.getFundActno1();
                      record.fundActno2 = hmMsgIn.getFundActno2();
                      record.balAmt = String.format("%.2f", hmMsgIn.getActBal());
                      record.toAccountNo = hmMsgIn.getPayinCbsActno();
                      record.toAccountName = hmMsgIn.getPayinCbsActname();
                      record.idType = hmMsgIn.getCertType();
                      record.idCode = hmMsgIn.getCertId();*/
                        // 退款明细和交款明细改为一致 未赋值字段为空
                        // 20120824 电话字段改为-转入账号 工程造价字段改为：转入账户名  缴存比例改为：证件号码
                        record.toAcctNo = hmMsgIn.getPayinCbsActno();
                        record.toAcctName = hmMsgIn.getPayinCbsActname();
                        record.certID = hmMsgIn.getCertId();
                        //--------------------------------------

                        record.accountNo = hmMsgIn.getFundActno1();

                        toa3001.body.recordList.add(record);
                    }
                }
            }

        } else {
            throw new RuntimeException(CbsErrorCode.QRY_NO_RECORDS.getCode());
        }

        return toa3001;
    }
}
