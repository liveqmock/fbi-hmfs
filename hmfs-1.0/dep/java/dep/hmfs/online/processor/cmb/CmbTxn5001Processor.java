package dep.hmfs.online.processor.cmb;

import common.repository.hmfs.model.HmActinfoCbs;
import common.repository.hmfs.model.TxnCbsLog;
import common.service.HmActinfoCbsService;
import common.service.TxnCbsLogService;
import dep.hmfs.online.processor.cmb.domain.base.TOA;
import dep.hmfs.online.processor.cmb.domain.txn.TIA5001;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CmbTxn5001Processor extends CmbAbstractTxnProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CmbTxn5001Processor.class);

    @Autowired
    private HmActinfoCbsService hmActinfoCbsService;
    @Autowired
    private TxnCbsLogService txnCbsLogService;

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) {
        /*
        开户账号	30	维修资金监管部门账号
        账户金额	16	账号当日余额
        日期	8	yyyyMMdd
         */
        TIA5001 tia5001 = new TIA5001();
        tia5001.body.cbsActNo = new String(bytes, 0, 30).trim();
        tia5001.body.accountBalance = new String(bytes, 30, 16).trim();
        tia5001.body.txnDate = new String(bytes, 46, 8).trim();

        logger.info("【前台】账号：" + tia5001.body.cbsActNo);
        logger.info("【前台】余额：" + tia5001.body.accountBalance);
        logger.info("【前台】交易日期：" + tia5001.body.txnDate);

        HmActinfoCbs hmActinfoCbs = hmActinfoCbsService.qryHmActinfoCbsByNo(tia5001.body.cbsActNo);
        if (hmActinfoCbs == null) {
            throw new RuntimeException("该账户不存在！");
        } else if (new BigDecimal(tia5001.body.accountBalance).compareTo(hmActinfoCbs.getActBal()) != 0) {
            throw new RuntimeException("账户余额不一致！");
        } else {
            // TODO 发起国土局余额对账交易
            // 清余额对账表，明细对账表
            // TODO 新增-会计账号余额信息  -- 余额对账表

            // TODO 新增-会计账号明细    ---  明细对账表
            // 获取明细，开始主机对账
            if (bytes.length > 54) {
                byte[] detailBytes = new byte[bytes.length - 54];
                System.arraycopy(bytes, 54, detailBytes, 0, detailBytes.length);
                String detailStr = new String(detailBytes);
                String[] details = detailStr.split("\n");
                for (String detail : details) {
                    TIA5001.Body.Record record = new TIA5001.Body.Record();
                    String[] fields = detail.split("\\|");
                    record.txnSerialNo = fields[0];
                    record.txnAmt = fields[1];
                    record.txnType = fields[2];
                    tia5001.body.recordList.add(record);
                }
            }
            List<TxnCbsLog> txnCbsLogList = txnCbsLogService.qryTxnCbsLogsByDate(tia5001.body.txnDate);
            if (txnCbsLogList.size() != tia5001.body.recordList.size()) {
                throw new RuntimeException("账户交易明细数不一致！【本地】交易数：" + txnCbsLogList.size() + "【前台】交易数：" + tia5001.body.recordList.size());
            } else {
                int index = 0;
                for (TIA5001.Body.Record r : tia5001.body.recordList) {
                    logger.info("流水号：" + r.txnSerialNo + " ==交易金额： " + r.txnAmt + " ==记账方向： " + r.txnType);
                    TxnCbsLog txnCbsLog = txnCbsLogList.get(index);
                    if (!r.txnSerialNo.equals(txnCbsLog.getTxnSn())
                            || txnCbsLog.getTxnAmt().compareTo(new BigDecimal(r.txnAmt)) != 0
                            || !r.txnType.equals(txnCbsLog.getDcFlag())) {
                        throw new RuntimeException("账户交易明细内容不一致！");
                    }
                    index++;
                }
                // 至此，本地对账结束
                // TODO 发起国土局明细对账
            }
        }

        return null;
    }
}
