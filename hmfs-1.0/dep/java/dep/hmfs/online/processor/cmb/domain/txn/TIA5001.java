package dep.hmfs.online.processor.cmb.domain.txn;

import dep.hmfs.online.processor.cmb.domain.base.TIA;
import dep.hmfs.online.processor.cmb.domain.base.TIABody;
import dep.hmfs.online.processor.cmb.domain.base.TIAHeader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TIA5001 extends TIA implements Serializable {

    public Header header = new Header();
    public Body body = new Body();

    @Override
    public TIAHeader getHeader() {
        return header;
    }

    @Override
    public TIABody getBody() {
        return body;
    }

    //====================================================================
    public static class Header extends TIAHeader {
    }

    public static class Body extends TIABody {

        /*
        开户账号	30	维修资金监管部门账号
        账户金额	16	账号当日余额
        日期	8	yyyyMMdd
         */
        public String cbsActNo = "";                // 开户账号
        public String accountBalance = "";              // 账户金额
        public String txnDate = "";                     // 日期
        
        public List<Record> recordList = new ArrayList<Record>();

        /*
        交易流水号|交易金额|记账方向\n交易流水号|交易金额|记账方向
        记账方向 D 支取或退款 C 缴款
         */
        public static class Record {
            public String txnSerialNo = "";            // 交易流水号
            public String txnAmt = "";                 // 交易金额
            public String txnType = "";                // 记账方向
        }
    }
}
