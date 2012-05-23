package dep.hmfs.online.processor.hmb;

import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HmMsgInMapper;
import common.repository.hmfs.dao.TmpMsgInMapper;
import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmMsgInExample;
import common.repository.hmfs.model.TmpMsgIn;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg099;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
public class HmbTxn7002Processor extends HmbSyncAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn7002Processor.class);
    private static final String[] CAN_CANCEL_CODES = {"01035", "01039", "01041", "01043", "01045"};

    @Autowired
    private HmMsgInMapper hmMsgInMapper;
    @Autowired
    private TmpMsgInMapper tmpMsgInMapper;

    @Override
    public int process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        Msg099 msg099 = (Msg099) hmbMsgList.get(1);
        HmMsgInExample example = new HmMsgInExample();
//        for (String msgType : CAN_CANCEL_CODES) {
//            example.or().andMsgTypeEqualTo(msgType).andMsgSnEqualTo(msg099.origMsgSn)
//                    .andTxnCtlStsEqualTo(TxnCtlSts.INIT.getCode());
//        }
        example.createCriteria().andMsgSnEqualTo(msg099.origMsgSn)
                    .andTxnCtlStsEqualTo(TxnCtlSts.INIT.getCode());
        List<HmMsgIn> msginLogList = hmMsgInMapper.selectByExample(example);
        if (msginLogList.size() == 0) {
            throw new RuntimeException("该交易报文不存在，或已进入业务处理流程。");
        }
        for (HmMsgIn record : msginLogList) {
            // TODO 撤销的交易报文表
            TmpMsgIn tmpMsgIn = new TmpMsgIn();
            //TODO 2012-05-22
            //BeanUtils.copyProperties(tmpMsgIn, record);
            try {
                PropertyUtils.copyProperties(tmpMsgIn, record);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                logger.error("PropertyUtils.copyProperties(tmpMsgIn, record)", e);
            }
            tmpMsgInMapper.insert(tmpMsgIn);
            hmMsgInMapper.deleteByPrimaryKey(record.getPkid());
        }
        return msginLogList.size();
    }
}
