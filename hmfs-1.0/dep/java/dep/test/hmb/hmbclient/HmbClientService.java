package dep.test.hmb.hmbclient;

import common.repository.hmfs.dao.TmpMsginLogMapper;
import common.repository.hmfs.model.TmpMsginLog;
import common.repository.hmfs.model.TmpMsginLogExample;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: ÏÂÎç5:21
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbClientService{

    @Resource
    private TmpMsginLogMapper tmpMsginLogMapper;

    protected HmbMessageFactory messageFactory = new HmbMessageFactory();

    public byte[] getTxnbuf_5210()  {
        TmpMsginLogExample example =  new TmpMsginLogExample();
        example.or().andMsgSnEqualTo("120314000001521000").andMsgTypeEqualTo("00005");
//        example.or().andMsgSnEqualTo("120314000001521000").andMsgTypeEqualTo("01035");
//        example.or().andMsgSnEqualTo("120314000001521000").andMsgTypeEqualTo("01045");
        example.setOrderByClause("msg_type, submsg_num");

        List<TmpMsginLog> msginLogList = tmpMsginLogMapper.selectByExample(example);
        //int subMsgNum = msginLogList.size();
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        try {
            String pkgName = HmbMsg.class.getPackage().getName();
            for (TmpMsginLog msginLog : msginLogList) {
                String msgCode = msginLog.getMsgType().substring(2);
                HmbMsg detailMsg = (HmbMsg) Class.forName(pkgName + ".Msg" + msgCode).newInstance();
//                BeanUtils.copyProperties(detailMsg, msginLog);
                PropertyUtils.copyProperties(detailMsg, msginLog);
                hmbMsgList.add(detailMsg);
            }
            Map<String, List<HmbMsg>> outMap = new HashMap<String, List<HmbMsg>>();
            String txnCode = "5210";
            outMap.put(txnCode, hmbMsgList);
            return  messageFactory.marshal(txnCode, hmbMsgList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getTxnbuf(String txnCode, String msgSn)  {
        TmpMsginLogExample example =  new TmpMsginLogExample();
        //example.or().andMsgSnEqualTo(msgSn).andMsgTypeLike("00%");
        //example.or().andMsgSnEqualTo(msgSn).andMsgTypeEqualTo("01%");
        example.createCriteria().andMsgSnEqualTo(msgSn);
        example.setOrderByClause("msg_type, submsg_num");

        List<TmpMsginLog> msginLogList = tmpMsginLogMapper.selectByExample(example);
        //int subMsgNum = msginLogList.size();
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        try {
            String pkgName = HmbMsg.class.getPackage().getName();
            for (TmpMsginLog msginLog : msginLogList) {
                String msgCode = msginLog.getMsgType().substring(2);
                HmbMsg detailMsg = (HmbMsg) Class.forName(pkgName + ".Msg" + msgCode).newInstance();
//                BeanUtils.copyProperties(detailMsg, msginLog);
                PropertyUtils.copyProperties(detailMsg, msginLog);
                hmbMsgList.add(detailMsg);
            }
            Map<String, List<HmbMsg>> outMap = new HashMap<String, List<HmbMsg>>();
            outMap.put(txnCode, hmbMsgList);
            return  messageFactory.marshal(txnCode, hmbMsgList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
