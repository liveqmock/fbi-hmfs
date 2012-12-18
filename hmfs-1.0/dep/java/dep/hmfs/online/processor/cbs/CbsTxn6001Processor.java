package dep.hmfs.online.processor.cbs;

import common.repository.hmfs.model.HmActFund;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA6001;
import dep.hmfs.online.processor.cbs.domain.txn.TOA6001;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import org.apache.commons.lang.StringUtils;
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
public class CbsTxn6001Processor extends CbsAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CbsTxn6001Processor.class);
    @Autowired
    private HmbActinfoService hmbActinfoService;

    @Override
    public TOA process(TIAHeader tiaHeader, byte[] bytes) throws Exception {
        TIA6001 tia6001 = new TIA6001();
        tia6001.body.fundActno = new String(bytes, 0, 12).trim();
        logger.info("【报文正文长度】:" + bytes.length + "  【核算户账号】：" + tia6001.body.fundActno);

        TOA6001 toa6001 = new TOA6001();
        // 查询交款汇总信息
        HmActFund actFund = hmbActinfoService.qryActfundByActNo(tia6001.body.fundActno);

        toa6001.body.accountName = actFund.getInfoName();
        toa6001.body.txAmt = String.format("%.2f", actFund.getHouseTotalAmt());
        toa6001.body.address = actFund.getInfoAddr();    //22
        toa6001.body.houseArea = StringUtils.isEmpty(actFund.getBuilderArea()) ? "" : actFund.getBuilderArea();

        toa6001.body.houseType = actFund.getHouseDepType();
        toa6001.body.phoneNo = actFund.getHouseCustPhone();
        String field83 = actFund.getDepStandard2();
        if (field83 == null) {
            toa6001.body.projAmt = "";
            toa6001.body.payPart = "";
        } else if (field83.endsWith("|") || !field83.contains("|")) {
            toa6001.body.projAmt = new StringBuilder(field83).deleteCharAt(field83.length() - 1).toString();
            toa6001.body.payPart = "";
        } else {
            String[] fields83 = field83.split("\\|");
            toa6001.body.projAmt = fields83[0];
            toa6001.body.payPart = fields83[1];
        }
        toa6001.body.accountNo = actFund.getFundActno1();  // 业主核算户账号(维修资金账号)
        return toa6001;
    }
}