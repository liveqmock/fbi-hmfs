package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.repository.hmfs.model.*;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA5001;
import dep.hmfs.online.processor.web.WebTxn1007003Processor;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.hmfs.online.service.hmb.HmbSysTxnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 对帐.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 */
@Component
public class CbsTxn5001Processor extends CbsAbstractTxnProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CbsTxn5001Processor.class);

    @Autowired
    private HmbActinfoService hmbActinfoService;

    @Resource
    protected HmbSysTxnService hmbSysTxnService;

    @Autowired
    private WebTxn1007003Processor webTxn7003Processor;

    /**
     * 20120719 zhanrui
     * 整理主机发起对帐程序流程
     * 修改事务传播属性：REQUIRES_NEW  在对帐失败，抛出异常的情况下，仍保留历史供查询。
     */
    @Override
    @Transactional
    public TOA process(String txnSerialNo, byte[] bytes) {
        /*
        开户账号	30	维修资金监管部门账号
        账户金额	16	账号当日余额
        日期	8	yyyyMMdd
         */
        String cbsActNo = new String(bytes, 0, 30).trim();
        String accountBalance = new String(bytes, 30, 16).trim();
        String txnDate = new String(bytes, 46, 8).trim();
        logger.info("【主机】账号：" + cbsActNo + "【主机】余额：" + accountBalance + "【主机】交易日期：" + txnDate);

        HmSysCtl hmSysCtl = hmbActinfoService.getSysCtl();
        String bankId = hmSysCtl.getBankId();

        clearTodayChkData(cbsActNo, txnDate, bankId);

        // 发起国土局余额对账
        String hmbChkResponse = null;
        try {
            hmbChkResponse = webTxn7003Processor.process(null);
        } catch (Exception e) {
            hmbChkResponse = "9999|与国土局对账不平！";
            logger.error("与国土局对帐处理异常", e);
        }

        //新增本地余额对帐流水
        try {
            appendLocalActBalRecord(cbsActNo, accountBalance, txnDate, bankId);
        } catch (Exception e) {
            throw new RuntimeException(CbsErrorCode.CBS_ACT_NOT_EXIST.getCode());
        }

        if (bytes.length > 54) {
            boolean chktxnResult = false;
            try {
                //新增主机对帐流水记录
                appendCbsChkTxnRecord(bytes, cbsActNo, txnDate, bankId);
                //增加结算户交易明细到明细对账表
                appendLocalChkTxnRecord(txnDate);
                //校验对帐数据
                chktxnResult = hmbSysTxnService.verifyChkTxnData(txnDate, bankId);
            } catch (Exception e) {
                logger.error("处理错误", e);
                throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
            }
            if (!chktxnResult){
                throw new RuntimeException(CbsErrorCode.CBS_ACT_TXNS_ERROR.getCode());
            }
        }else{
            logger.error("TIA报文错误" + new String(bytes));
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }

        //注意：建行对帐策略：不进行主机余额对帐
        if (hmbChkResponse == null || !hmbChkResponse.startsWith("0000")) {
            throw new RuntimeException(CbsErrorCode.FUND_ACT_CHK_ERROR.getCode());
        }
        return null;
    }

    private void clearTodayChkData(String cbsActNo, String txnDate, String bankId) {
        // 删除日期为txnDate的会计账户余额对账记录
        hmbSysTxnService.deleteCbsChkActByDate(txnDate, cbsActNo, bankId);
        hmbSysTxnService.deleteCbsChkActByDate(txnDate, cbsActNo, "99");
        // 删除会计账号交易明细对账记录
        hmbSysTxnService.deleteCbsChkTxnByDate(txnDate, cbsActNo, bankId);
        hmbSysTxnService.deleteCbsChkTxnByDate(txnDate, cbsActNo, "99");
    }

    private void appendLocalChkTxnRecord(String txnDate) {
        List<HmTxnStl> hmTxnStlList = hmbActinfoService.qryHmTxnStlForChkAct(txnDate);

        for (HmTxnStl txnStl : hmTxnStlList) {
            HmChkTxn hmChkTxn = new HmChkTxn();
            hmChkTxn.setPkid(UUID.randomUUID().toString());
            hmChkTxn.setTxnDate(txnDate);
            hmChkTxn.setSendSysId("99");
            hmChkTxn.setActno(txnStl.getCbsActno());

            hmChkTxn.setTxnamt(txnStl.getTxnAmt());
            hmChkTxn.setMsgSn(txnStl.getCbsTxnSn());
            hmChkTxn.setDcFlag(txnStl.getDcFlag());
            hmbSysTxnService.insertChkTxnWithNewTx(hmChkTxn);
        }
    }

    private void appendLocalActBalRecord(String cbsActNo, String accountBalance, String txnDate, String bankId) {
        // 新增 日期为txnDate的会计账户余额对账记录
        HmChkAct hmChkAct = new HmChkAct();
        hmChkAct.setPkid(UUID.randomUUID().toString());
        hmChkAct.setTxnDate(txnDate);
        hmChkAct.setSendSysId(bankId);
        hmChkAct.setActno(cbsActNo);
        hmChkAct.setActbal(new BigDecimal(accountBalance));
        hmbSysTxnService.insertChkActWithNewTx(hmChkAct);
        // DEP 会计(结算)账户余额
        HmActStl hmActStl = hmbActinfoService.qryHmActstlByCbsactNo(cbsActNo);
        hmChkAct = new HmChkAct();
        hmChkAct.setPkid(UUID.randomUUID().toString());
        hmChkAct.setTxnDate(txnDate);
        hmChkAct.setSendSysId("99");
        hmChkAct.setActno(hmActStl.getCbsActno());
        hmChkAct.setActbal(hmActStl.getActBal());
        hmbSysTxnService.insertChkActWithNewTx(hmChkAct);
    }

    private void appendCbsChkTxnRecord(byte[] bytes, String cbsActNo, String txnDate, String bankId) {
        TIA5001 tia5001 = new TIA5001();
        byte[] detailBytes = new byte[bytes.length - 54];
        System.arraycopy(bytes, 54, detailBytes, 0, detailBytes.length);
        String detailStr = new String(detailBytes);
        String[] details = detailStr.split("\n");
        // 保存主机明细数据
        for (String detail : details) {
            logger.info("====" + detail);
            String[] fields = detail.split("\\|");
            int recordCnt = fields.length / 3;
            for (int i = 0; i < recordCnt; i++) {
                TIA5001.Body.Record record = new TIA5001.Body.Record();
                record.txnSerialNo = fields[i * 3 + 0].trim();
                record.txnAmt = fields[i * 3 + 1].trim();
                record.txnType = fields[i * 3 + 2].trim();
                tia5001.body.recordList.add(record);
                HmChkTxn hmChkTxn = new HmChkTxn();
                hmChkTxn.setPkid(UUID.randomUUID().toString());
                hmChkTxn.setTxnDate(txnDate);
                hmChkTxn.setSendSysId(bankId);
                hmChkTxn.setActno(cbsActNo);
                hmChkTxn.setTxnamt(new BigDecimal(record.txnAmt));
                hmChkTxn.setMsgSn(record.txnSerialNo);
                hmChkTxn.setDcFlag(record.txnType);
                hmbSysTxnService.insertChkTxnWithNewTx(hmChkTxn);
            }
        }
    }
}
