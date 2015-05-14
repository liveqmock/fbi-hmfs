package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmActFund;
import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmTxnStl;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA1001;
import dep.hmfs.online.processor.cbs.domain.txn.TOA1001;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.hmfs.online.service.hmb.TxnVouchService;
import dep.util.PropertyManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CbsTxn1001Processor extends CbsAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CbsTxn1001Processor.class);
    @Autowired
    private HmbActinfoService hmbActinfoService;
    @Autowired
    private TxnVouchService txnVouchService;

    @Override
    public TOA process(TIAHeader tiaHeader, byte[] bytes) throws Exception {
        TIA1001 tia1001 = new TIA1001();
        tia1001.body.payApplyNo = new String(bytes, 0, 18).trim();
        logger.info("【报文正文长度】:" + bytes.length + "  【申请单号】：" + tia1001.body.payApplyNo);
        logger.info("【网点编号】:" + tiaHeader.deptCode + "  【柜员编号】：" + tiaHeader.operCode);

        TOA1001 toa1001 = null;
        // 查询交款汇总信息
        HmMsgIn totalPayInfo = hmbBaseService.qryTotalMsgByMsgSn(tia1001.body.payApplyNo, "00005");

        if (totalPayInfo != null) {
            toa1001 = new TOA1001();
            toa1001.body.payApplyNo = tia1001.body.payApplyNo;
            toa1001.body.payAmt = String.format("%.2f", totalPayInfo.getTxnAmt1());
            // 更新交款汇总信息和明细信息状态为：处理中 -- 更新完成后交款信息不可撤销
            // String[] payMsgTypes = {"01035", "01045"};
            if (!TxnCtlSts.SUCCESS.getCode().equals(totalPayInfo.getTxnCtlSts())) {
                toa1001.body.payFlag = "0";
                hmbBaseService.updateMsginSts(tia1001.body.payApplyNo, TxnCtlSts.HANDLING);
            } else {
                toa1001.body.payFlag = "1";
            }
            //  建行 1001 交易返回报文
            if ("05".equals(PropertyManager.getProperty("SEND_SYS_ID"))) {
                if (checkVoucherIsHandlerByDept(tiaHeader)||checkVoucherIsHandlerByOper(tiaHeader)){
                    throw new RuntimeException(CbsErrorCode.VOUCHER_NOT_HANDLER.getCode());
                }else{
                    String[] payMsgTypes = {"01035", "01045"};
                    List<HmMsgIn> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia1001.body.payApplyNo, payMsgTypes);
                    logger.info("查询交款交易子报文。查询到笔数：" + payInfoList.size());
                    if (payInfoList.size() > 0) {
                        toa1001.body.payDetailNum = String.valueOf(payInfoList.size());
                        for (HmMsgIn hmMsgIn : payInfoList) {
                            HmActFund actFund = hmbActinfoService.qryHmActfundByActNo(hmMsgIn.getFundActno1());
                            TOA1001.Body.Record record = new TOA1001.Body.Record();
                            record.accountName = hmMsgIn.getInfoName();   //21
                            record.txAmt = String.format("%.2f", hmMsgIn.getTxnAmt1());
                            record.address = hmMsgIn.getInfoAddr();    //22
                            record.houseArea = StringUtils.isEmpty(hmMsgIn.getBuilderArea()) ? "" : hmMsgIn.getBuilderArea();

                            record.houseType = actFund.getHouseDepType();
                            record.phoneNo = actFund.getHouseCustPhone();
                            String field83 = actFund.getDepStandard2();
                            if (field83 == null) {
                                record.projAmt = "";
                                record.payPart = "";
                            } else if (field83.endsWith("|") || !field83.contains("|")) {
                                record.projAmt = new StringBuilder(field83).deleteCharAt(field83.length() - 1).toString();
                                record.payPart = "";
                            } else {
                                String[] fields83 = field83.split("\\|");
                                record.projAmt = fields83[0];
                                record.payPart = fields83[1];
                            }
                            record.accountNo = hmMsgIn.getFundActno1();  // 业主核算户账号(维修资金账号)
                            toa1001.body.recordList.add(record);
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException(CbsErrorCode.QRY_NO_RECORDS.getCode());
        }
        return toa1001;
    }
    //2015-05-08 linyong 根据日期查询机构是否存在未进行票据维护的缴款书
    private boolean checkVoucherIsHandlerByDept(TIAHeader tiaHeader){
        Calendar calendar = Calendar.getInstance();
        //得到前一天
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        String prevDate = new SimpleDateFormat("yyyyMMdd").format(date);
        String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String deptCode = tiaHeader.deptCode;
        List<HmTxnStl> listTxn = txnVouchService.checkVoucherIsHandlerByDept(prevDate,currentDate,deptCode);
        if (listTxn.size()>0){
            return true;
        }else {
            return false;
        }
    }
    //2015-05-08 linyong 根据日期查询柜员是否存在未进行票据维护的缴款书
    private boolean checkVoucherIsHandlerByOper(TIAHeader tiaHeader){
        String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String operCode = tiaHeader.operCode;
        List<HmTxnStl> listTxn = txnVouchService.checkVoucherIsHandlerByOper(currentDate, operCode);
        if (listTxn.size()>0){
            return true;
        }else {
            return false;
        }
    }
}