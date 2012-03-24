package dep.hmfs.common;

import common.repository.hmfs.dao.hmfs.HmfsCmnMapper;
import dep.util.PropertyManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang.StringUtils.leftPad;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: ÏÂÎç2:03
 * To change this template use File | Settings | File Templates.
 */
@Component
public class HmbTxnsnGenerator {

    private  static String SEND_SYS_ID =  PropertyManager.getProperty("SEND_SYS_ID");

    @Resource
    HmfsCmnMapper cmnMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateTxnsn(String txnCode) {
        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        int sn = cmnMapper.selectTxnseq();
        cmnMapper.updateTxnseq(sn + 1);
        return date + leftPad("" + sn, 6, '0') + txnCode + SEND_SYS_ID;
    }
}
