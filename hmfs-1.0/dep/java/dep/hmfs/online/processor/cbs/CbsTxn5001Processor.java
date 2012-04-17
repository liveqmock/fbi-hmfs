package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.repository.hmfs.model.HmActStl;
import common.repository.hmfs.model.HmChkAct;
import common.repository.hmfs.model.HmChkTxn;
import common.repository.hmfs.model.HmTxnStl;
import common.service.SystemService;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA5001;
import dep.hmfs.online.processor.web.WebTxn1007003Processor;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CbsTxn5001Processor extends CbsAbstractTxnProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CbsTxn5001Processor.class);

    @Autowired
    private HmbActinfoService hmbActinfoService;
    @Autowired
    private WebTxn1007003Processor webTxn7003Processor;

    @Override
    @Transactional
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

        // 如果对账日期不是系统当前日期，则返回错误。
        if (!SystemService.formatTodayByPattern("yyyyMMdd").equals(tia5001.body.txnDate)) {
            throw new RuntimeException(CbsErrorCode.CBS_ACT_CHK_DATE_ERROR.getCode());
        }

        logger.info("【主机】账号：" + tia5001.body.cbsActNo);
        logger.info("【主机】余额：" + tia5001.body.accountBalance);
        logger.info("【主机】交易日期：" + tia5001.body.txnDate);

        // 删除日期为txnDate的会计账户余额对账记录
        hmbActinfoService.deleteCbsChkActByDate(tia5001.body.txnDate, tia5001.body.cbsActNo, "05");
        // 新增 日期为txnDate的会计账户余额对账记录
        HmChkAct hmChkAct = new HmChkAct();
        hmChkAct.setPkid(UUID.randomUUID().toString());
        hmChkAct.setTxnDate(tia5001.body.txnDate);
        hmChkAct.setSendSysId("05");
        hmChkAct.setActno(tia5001.body.cbsActNo);
        hmChkAct.setActbal(new BigDecimal(tia5001.body.accountBalance));
        hmbActinfoService.insertChkAct(hmChkAct);

        hmbActinfoService.deleteCbsChkActByDate(tia5001.body.txnDate, tia5001.body.cbsActNo, "99");
        // DEP 会计(结算)账户余额
        HmActStl hmActStl = hmbActinfoService.qryHmActstlByCbsactNo(tia5001.body.cbsActNo);

        if (hmActStl == null) {
            throw new RuntimeException(CbsErrorCode.CBS_ACT_NOT_EXIST.getCode());
        }

        // TODO 因存在利息问题，余额对账终将不平，需求待定
        /*else if (new BigDecimal(tia5001.body.accountBalance).compareTo(hmActStl.getActBal()) != 0) {
            throw new RuntimeException(CbsErrorCode.CBS_ACT_BAL_ERROR.getCode());
        }*/

        hmChkAct = new HmChkAct();
        hmChkAct.setPkid(UUID.randomUUID().toString());
        hmChkAct.setTxnDate(tia5001.body.txnDate);
        hmChkAct.setSendSysId("99");
        hmChkAct.setActno(hmActStl.getCbsActno());
        hmChkAct.setActbal(hmActStl.getActBal());
        hmbActinfoService.insertChkAct(hmChkAct);

        // 获取明细，开始主机对账
        // 删除会计账号交易明细对账记录
        hmbActinfoService.deleteCbsChkTxnByDate(tia5001.body.txnDate, tia5001.body.cbsActNo, "05");
        hmbActinfoService.deleteCbsChkTxnByDate(tia5001.body.txnDate, tia5001.body.cbsActNo, "99");

        // 有明细数据
        if (bytes.length > 54) {
            byte[] detailBytes = new byte[bytes.length - 54];
            System.arraycopy(bytes, 54, detailBytes, 0, detailBytes.length);
            String detailStr = new String(detailBytes);
            String[] details = detailStr.split("\n");
            // 保存主机明细数据
            for (String detail : details) {
                TIA5001.Body.Record record = new TIA5001.Body.Record();
                String[] fields = detail.split("\\|");
                record.txnSerialNo = fields[0];
                record.txnAmt = fields[1];
                record.txnType = fields[2];
                tia5001.body.recordList.add(record);

                HmChkTxn hmChkTxn = new HmChkTxn();
                hmChkTxn.setPkid(UUID.randomUUID().toString());
                hmChkTxn.setTxnDate(tia5001.body.txnDate);
                hmChkTxn.setSendSysId("05");
                hmChkTxn.setActno(tia5001.body.cbsActNo);
                hmChkTxn.setTxnamt(new BigDecimal(record.txnAmt));
                hmChkTxn.setMsgSn(record.txnSerialNo);
                hmChkTxn.setDcFlag(record.txnType);
                hmbActinfoService.insertChkTxn(hmChkTxn);
            }
            // 查询结算户交易明细到明细对账表
            List<HmTxnStl> hmTxnStlList = hmbActinfoService.qryTxnstlsByDate(tia5001.body.txnDate);
            for (HmTxnStl txnStl : hmTxnStlList) {
                HmChkTxn hmChkTxn = new HmChkTxn();
                hmChkTxn.setPkid(UUID.randomUUID().toString());
                hmChkTxn.setTxnDate(tia5001.body.txnDate);
                hmChkTxn.setSendSysId("99");
                hmChkTxn.setActno(txnStl.getCbsActno());
                hmChkTxn.setTxnamt(txnStl.getTxnAmt());
                hmChkTxn.setMsgSn(txnStl.getTxnSn());
                hmChkTxn.setDcFlag(txnStl.getDcFlag());
                hmbActinfoService.insertChkTxn(hmChkTxn);
            }

            // 明细对账
            if (hmTxnStlList.size() != tia5001.body.recordList.size()) {
                logger.error("账户交易明细数不一致！【本地】交易数：" + hmTxnStlList.size() + "【主机】交易数：" + tia5001.body.recordList.size());
                throw new RuntimeException(CbsErrorCode.CBS_ACT_TXNS_ERROR.getCode());
            }

            int index = 0;
            for (TIA5001.Body.Record r : tia5001.body.recordList) {
                logger.info("【主机端】流水号：" + r.txnSerialNo + " ==交易金额： " + r.txnAmt + " ==记账方向： " + r.txnType);
                HmTxnStl hmTxnStl = hmTxnStlList.get(index);
                logger.info("【本地会计账户】流水号：" + hmTxnStl.getTxnSn() + " ==交易金额： " + hmTxnStl.getTxnAmt() +
                        " ==记账方向： " + hmTxnStl.getDcFlag());

                if (!r.txnSerialNo.equals(hmTxnStl.getTxnSn())
                        || hmTxnStl.getTxnAmt().compareTo(new BigDecimal(r.txnAmt)) != 0
                        || !r.txnType.equals(hmTxnStl.getDcFlag())) {
                    logger.error("账户交易明细内容不一致！");
                    throw new RuntimeException(CbsErrorCode.CBS_ACT_TXNS_ERROR.getCode());
                }
                index++;
            }
            // 主机-本地对账结束
        }

        // 发起国土局余额对账
        // TODO 发起国土局明细对账【暂无此交易】
        if (true) {
            return null;
        }
        String res = null;
        try {
            res = webTxn7003Processor.process(null);
        } catch (Exception e) {
            logger.error("对账异常", e);
            throw new RuntimeException(CbsErrorCode.FUND_ACT_CHK_ERROR.getCode());
        }
        if (res != null && res.startsWith("0000")) {
            return null;
        } else {
            throw new RuntimeException(CbsErrorCode.FUND_ACT_CHK_ERROR.getCode());
        }
    }
}
