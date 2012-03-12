package dep.hmfs.online.cmb;

import common.service.HisMsginLogService;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA5001;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn5001Processor extends AbstractTxnProcessor {
    private static final Logger logger = LoggerFactory.getLogger(Txn5001Processor.class);

    @Autowired
    private HisMsginLogService hisMsginLogService;
    @Override
    public TOA process(String txnSerialNo, byte[] bytes) {
        /*
        开户账号	30	维修资金监管部门账号
        账户金额	16	账号当日余额
        日期	8	yyyyMMdd
         */
        TIA5001 tia5001 = new TIA5001();
        tia5001.body.hmbAccountNo = new String(bytes, 0, 30).trim();
        tia5001.body.accountBalance = new String(bytes, 30, 16).trim();
        tia5001.body.txnDate = new String(bytes, 46, 54).trim();
        if(bytes.length > 54) {
            byte[] detailBytes = new byte[bytes.length - 54];
            System.arraycopy(bytes, 54, detailBytes, 0, detailBytes.length);
            String detailStr = new String(detailBytes);
            String[] details = detailStr.split("\n");
            for(String detail : details) {
                TIA5001.Body.Record record = new TIA5001.Body.Record();
                String[] fields = detail.split("\\|");
                record.txnSerialNo = fields[0];
                record.txnAmt = fields[1];
                record.txnType = fields[2];
                tia5001.body.recordList.add(record);
            }
        }
        // TODO 对账业务 交易成功则返回空报文体
        for(TIA5001.Body.Record r : tia5001.body.recordList) {
            logger.info("流水号：" + r.txnSerialNo + " ==交易金额： " + r.txnAmt + " ==记账方向： " + r.txnType);
        }
        return null;
    }
   }
