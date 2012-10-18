package dep.hmfs.online.processor.web;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.enums.VouchStatus;
import common.repository.hmfs.model.HmMsgIn;
import common.service.SystemService;
import dep.hmfs.common.HmbTxnsnGenerator;
import dep.hmfs.online.processor.cbs.CbsAbstractTxnProcessor;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA4001;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import dep.hmfs.online.service.hmb.TxnVouchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn1005610Processor extends WebAbstractHmbProductBizTxnProcessor {

    private static Logger logger = LoggerFactory.getLogger(WebTxn1005610Processor.class);

    @Autowired
    private TxnVouchService txnVouchService;
    @Autowired
    private HmbClientReqService hmbClientReqService;
    @Autowired
    private HmbTxnsnGenerator hmbTxnsnGenerator;

    @Override
    protected String process(String request) {
        try {
            processRequest(request);
        } catch (Exception e) {
            logger.error("票据发送失败", e);
            throw new RuntimeException("票据发送失败。" + CbsErrorCode.valueOf(e.getMessage()).getTitle() , e);
        }
        return "0000|票据发送成功";
    }

    @Transactional
    private void processRequest(String request) throws Exception {
        //发送报文
        String[] fields = request.split("\\|");
        //String txnCode = fields[0];
        String applyno = fields[1];
        String deptCode = fields[2];
        String operCode = fields[3];
        String vchStatus = fields[4];
        long startNo = Long.parseLong(fields[5]);
        long endNo = Long.parseLong(fields[6]);

        if (VouchStatus.USED.getCode().equals(vchStatus)) {
            List<HmMsgIn> payInfoList = hmbClientReqService.qrySubMsgsByMsgSnAndTypes(applyno,
                    new String[]{"01035", "01045"});
            // 检查是否存在此申请单号
            if (payInfoList.size() <= 0) {
                throw new RuntimeException(CbsErrorCode.QRY_NO_RECORDS.getCode());
            }
            // 2012-08-01 检查是否已交款成功
            HmMsgIn oriMsgIn = payInfoList.get(0);
            if (!TxnCtlSts.SUCCESS.getCode().equals(oriMsgIn.getTxnCtlSts())) {
                throw new RuntimeException(CbsErrorCode.MSG_IN_SN_NOT_SUCCESS.getCode());
            }
            //  2012-10-17 [检查]：系统内已记录使用票据数 + 当前使用数 > 该申请单缴款户数
            int usedVchCnt = txnVouchService.qryUsedVchCntByMsgsn(applyno);
            if ((endNo - startNo + 1) + usedVchCnt > payInfoList.size()) {
                throw new RuntimeException(CbsErrorCode.VOUCHER_NUM_ERROR.getCode());
            }
        }

        // 检查票据是否已使用
        for (long i = startNo; i <= endNo; i++) {
            if (txnVouchService.isUsedVchNo(String.valueOf(i))) {
                throw new RuntimeException(CbsErrorCode.VOUCHER_USED.getCode());
            }
        }

        boolean isSendOver = false;
        String msgSn = hmbTxnsnGenerator.generateTxnsn("5610");
        try {
            isSendOver = hmbClientReqService.sendVouchsToHmb(msgSn, startNo, endNo, applyno, vchStatus);
            if (isSendOver) {
                txnVouchService.insertVouchsByNo(msgSn, startNo, endNo, SystemService.formatTodayByPattern("yyMMddHHMMSSsss"),
                        deptCode, operCode, applyno, vchStatus);
            }
        } catch (Exception e) {
            logger.error("发送报文至国土局时出现异常。" + e.getMessage(), e);
            if (e.getMessage().startsWith("700")) {
                throw new RuntimeException(CbsErrorCode.NET_COMMUNICATE_ERROR.getCode());
            } else
                throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
        if (!isSendOver) {
            logger.error("票据管理交易发送过程出现异常");
            throw new RuntimeException(CbsErrorCode.VOUCHER_SEND_ERROR.getCode());
        }
    }
}
