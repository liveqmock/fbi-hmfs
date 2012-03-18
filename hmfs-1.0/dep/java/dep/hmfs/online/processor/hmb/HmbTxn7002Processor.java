package dep.hmfs.online.processor.hmb;

import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HisMsginLogMapper;
import common.repository.hmfs.dao.TmpMsginLogMapper;
import common.repository.hmfs.model.HisMsginLog;
import common.repository.hmfs.model.HisMsginLogExample;
import common.repository.hmfs.model.TmpMsginLog;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg099;
import org.apache.commons.beanutils.BeanUtils;
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
    private HisMsginLogMapper hisMsginLogMapper;
    @Autowired
    private TmpMsginLogMapper tmpMsginLogMapper;

    @Override
    public int process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        Msg099 msg099 = (Msg099) hmbMsgList.get(1);
        HisMsginLogExample example = new HisMsginLogExample();
        for (String msgType : CAN_CANCEL_CODES) {
            example.or().andMsgTypeEqualTo(msgType).andMsgSnEqualTo(msg099.origMsgSn)
                    .andTxnCtlStsEqualTo(TxnCtlSts.INIT.getCode());
        }
        List<HisMsginLog> msginLogList = hisMsginLogMapper.selectByExample(example);
        if (msginLogList.size() == 0) {
            throw new RuntimeException("该交易报文不存在，或已进入业务处理流程。");
        }
        for (HisMsginLog record : msginLogList) {
            // TODO 撤销的交易报文表
            TmpMsginLog tmpMsginLog = new TmpMsginLog();
            BeanUtils.copyProperties(tmpMsginLog, record);
            tmpMsginLogMapper.insert(tmpMsginLog);
            hisMsginLogMapper.deleteByPrimaryKey(record.getPkid());
        }
        return msginLogList.size();
    }
}
