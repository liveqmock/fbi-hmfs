package dep.hmfs.online.cmb;

import dep.hmfs.online.cmb.domain.txn.TOA1002;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA1002;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn1002Processor extends AbstractTxnProcessor {
    @Override
    public TOA process(byte[] bytes) throws Exception {
        TIA1002 tia1002 = new TIA1002();
        tia1002.body.payApplyNo = new String(bytes, 0, 18).trim();
        tia1002.body.payAmt = new String(bytes, 18, 16).trim();

        // TODO �����ݿ��ѯ������ϸ
        TOA1002.Body.Record record = new TOA1002.Body.Record();
        record.accountName = "����";
        record.txAmt = "123456.78";
        record.address = "�ൺ�б���";
        record.houseArea = "98.5";
        record.houseType = "0";
        record.phoneNo = "89901100";
        record.projAmt = "1001234567.89";
        record.payPart = "30%";
        record.accountNo = "32001001";

        TOA1002 toa1002 = new TOA1002();
        toa1002.body.recordList.add(record);

        record = new TOA1002.Body.Record();
        record.accountName = "����";
        record.txAmt = "123456.78";
        record.address = "�ൺ�б���";
        record.houseArea = "98.5";
        record.houseType = "0";
        record.phoneNo = "89901100";
        record.projAmt = "1001234567.89";
        record.payPart = "30%";
        record.accountNo = "320010888";
        toa1002.body.recordList.add(record);

        return toa1002;
    }
}
