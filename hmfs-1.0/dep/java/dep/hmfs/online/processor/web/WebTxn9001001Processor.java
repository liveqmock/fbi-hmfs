package dep.hmfs.online.processor.web;

import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.TmpMsgInMapper;
import common.repository.hmfs.dao.TmpMsgOutMapper;
import common.repository.hmfs.model.TmpMsgIn;
import common.repository.hmfs.model.TmpMsgInExample;
import common.repository.hmfs.model.TmpMsgOut;
import common.repository.hmfs.model.TmpMsgOutExample;
import common.service.SystemService;
import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.service.hmb.HmbSysTxnService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MOCK 模拟房产局发送报文 （接收web层请求).
 * User: zhanrui
 * Date: 12-3-15
 * Time: 下午1:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn9001001Processor extends WebAbstractHmbDevelopTxnProcessor {
    @Resource
    private HmbSysTxnService hmbSysTxnService;

    private XSocketBlockClient socketBlockClient;

    @Resource
    private TmpMsgOutMapper tmpMsgOutMapper;

    @Resource
    protected TmpMsgInMapper tmpMsgInMapper;

    @Override
    public String process(String request) {
        String[] fields = request.split("\\|");
        String txnCode = fields[1];
        String msgSn = fields[2];

        //发送报文
        Map<String, List<HmbMsg>> responseMap = sendDataUntilRcv(getTxnbuf(txnCode, msgSn));

        //处理返回报文
        String rtnTxnCode = (String) responseMap.keySet().toArray()[0];
        List<HmbMsg> msgList = responseMap.get(rtnTxnCode);
/*
        if (msgList == null || msgList.size() == 0) {
            throw new RuntimeException("接收DEP报文出错，报文为空");
        }
*/
        //保存到本地数据库
        if ("9999".equals(rtnTxnCode)) {
            rtnTxnCode = txnCode;
        }
        try {
            deleteAndInsertMsginsByHmbMsgList(rtnTxnCode, msgList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return "0000|完成交易，可查询交易结果。";
    }

    //=============
    @Override
    protected Map<String, List<HmbMsg>> sendDataUntilRcv(byte[] bytes) {
        byte[] hmfsDatagram;
        try {
            socketBlockClient = new XSocketBlockClient("127.0.0.1", 41014, 50000);
            hmfsDatagram = socketBlockClient.sendDataUntilRcvToHmb(bytes);
            return messageFactory.unmarshal(hmfsDatagram);
        } catch (Exception e) {
            throw new RuntimeException("通讯或解包错误.", e);
        } finally {
            try {
                socketBlockClient.close();
            } catch (IOException e) {
                //
            }
        }
    }


    private byte[] getTxnbuf(String txnCode, String msgSn) {
        TmpMsgOutExample example = new TmpMsgOutExample();
        example.createCriteria().andMsgSnEqualTo(msgSn);
        example.setOrderByClause("msg_type, submsg_num");
        List<TmpMsgOut> msgOutList = tmpMsgOutMapper.selectByExample(example);

        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        try {
            String pkgName = HmbMsg.class.getPackage().getName();
            for (TmpMsgOut msgOut : msgOutList) {
                String msgCode = msgOut.getMsgType().substring(2);
                HmbMsg detailMsg = (HmbMsg) Class.forName(pkgName + ".Msg" + msgCode).newInstance();
                PropertyUtils.copyProperties(detailMsg, msgOut);
                hmbMsgList.add(detailMsg);
            }
            return messageFactory.marshal(txnCode, hmbMsgList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    private void deleteAndInsertMsginsByHmbMsgList(String txnCode, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        String msgSn = "";
        TmpMsgInExample example = new TmpMsgInExample();
        example.createCriteria().andMsgSnEqualTo(msgSn).andMsgTypeLike("00%");
        List<TmpMsgIn> msgInList = tmpMsgInMapper.selectByExample(example);
        tmpMsgInMapper.deleteByExample(example);
        insertMsginsByHmbMsgList(txnCode, hmbMsgList);
    }

    private int insertMsginsByHmbMsgList(String txnCode, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        int index = 0;
        String msgSn = "";
        for (HmbMsg hmbMsg : hmbMsgList) {
            TmpMsgIn msgIn = new TmpMsgIn();
            BeanUtils.copyProperties(msgIn, hmbMsg);
            String guid = UUID.randomUUID().toString();
            msgIn.setPkid(guid);
            msgIn.setTxnCode(txnCode);
            msgIn.setMsgProcDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            msgIn.setMsgProcTime(SystemService.formatTodayByPattern("HHmmss"));

            index++;
            if (index == 1) {
                msgSn = msgIn.getMsgSn();
            } else {
                msgIn.setMsgSn(msgSn);
            }
            msgIn.setMsgSubSn(index);
            msgIn.setTxnCtlSts(TxnCtlSts.INIT.getCode());

            tmpMsgInMapper.insert(msgIn);
        }

        return hmbMsgList.size();
    }

}
