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
 * Time: ����11:47
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
        logger.info("���������ĳ��ȡ�:" + bytes.length + "  �����㻧�˺š���" + tia6001.body.fundActno);

        TOA6001 toa6001 = new TOA6001();
        // ��ѯ���������Ϣ
        HmActFund actFund = hmbActinfoService.qryActfundByActNo(tia6001.body.fundActno);
        // ҵ������
        toa6001.body.accountName = actFund.getInfoName();
        // ���׽��
        //toa6001.body.txAmt = String.format("%.2f", actFund.getHouseTotalAmt()); // hanjianlong 20121226 del
        toa6001.body.txAmt = String.format("%.2f", actFund.getActBal()); // hanjianlong 20121226 add
        // סլ��ַ
        toa6001.body.address = actFund.getInfoAddr();
        // סլ�������
        toa6001.body.houseArea = StringUtils.isEmpty(actFund.getBuilderArea()) ? "" : actFund.getBuilderArea();
        // סլ����
        toa6001.body.houseType = actFund.getHouseDepType();
        // ��ϵ�绰
        toa6001.body.phoneNo = actFund.getHouseCustPhone();
        // ������׼2
        String field83 = actFund.getDepStandard2();
        // ������׼2Ϊ�գ��򹤳���ۺͽ��������Ϊ��
        if (field83 == null) {
            toa6001.body.projAmt = "";
            toa6001.body.payPart = "";
            // ������׼2�е�"|"ǰ��������ۣ���Ϊ�������
        } else if (field83.endsWith("|") || !field83.contains("|")) {
            toa6001.body.projAmt = new StringBuilder(field83).deleteCharAt(field83.length() - 1).toString();
            toa6001.body.payPart = "";
        } else {
            String[] fields83 = field83.split("\\|");
            toa6001.body.projAmt = fields83[0];
            toa6001.body.payPart = fields83[1];
        }
        // ҵ�����㻧�˺�(ά���ʽ��˺�)
        toa6001.body.accountNo = actFund.getFundActno1();
        return toa6001;
    }
}