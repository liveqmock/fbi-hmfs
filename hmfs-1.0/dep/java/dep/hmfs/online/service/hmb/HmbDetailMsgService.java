package dep.hmfs.online.service.hmb;

import common.enums.FundActnoStatus;
import common.repository.hmfs.dao.HmActinfoCbsMapper;
import common.repository.hmfs.dao.HmActinfoFundMapper;
import common.repository.hmfs.model.HmActinfoCbs;
import common.repository.hmfs.model.HmActinfoFund;
import common.service.HmActinfoFundService;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.*;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-14
 * Time: 下午7:58
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbDetailMsgService extends HmbBaseService {

    @Autowired
    private HmActinfoCbsMapper hmActinfoCbsMapper;
    @Autowired
    private HmActinfoFundMapper hmActinfoFundMapper;
    @Autowired
    private HmActinfoFundService hmActinfoFundService;

    @Transactional
    public int createActinfosByMsgList(List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        for (HmbMsg hmbMsg : hmbMsgList) {
            if ("01032".equals(hmbMsg.getMsgType())) {
                Msg032 msg032 = (Msg032) hmbMsg;

                HmActinfoCbs actinfoCbs = new HmActinfoCbs();
                actinfoCbs.setPkid(UUID.randomUUID().toString());
                BeanUtils.copyProperties(actinfoCbs, msg032);
                actinfoCbs.setActSts("0");
                actinfoCbs.setActBal(new BigDecimal(0));
                actinfoCbs.setIntcPdt(new BigDecimal(0));
                actinfoCbs.setOpenActDate(SystemService.formatTodayByPattern("yyyyMMdd"));
                actinfoCbs.setRecversion(0);

                hmActinfoCbsMapper.insert(actinfoCbs);
            } else if ("01033".equals(hmbMsg.getMsgType())) {
                Msg033 msg033 = (Msg033) hmbMsg;
                createActinfoFundByHmbMsg(msg033);
            } else if ("01034".equals(hmbMsg.getMsgType())) {
                Msg034 msg034 = (Msg034) hmbMsg;
                createActinfoFundByHmbMsg(msg034);
            } else {
                throw new RuntimeException("报文中包含未定义的子报文，子报文序号错误！");
            }
        }
        return hmbMsgList.size();
    }

    @Transactional
    public int updateActinfosByMsgList(List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        for (HmbMsg hmbMsg : hmbMsgList) {
            if ("01033".equals(hmbMsg.getMsgType())) {
                Msg033 msg033 = (Msg033) hmbMsg;
                HmActinfoFund hmActinfoFund = hmActinfoFundService.qryHmActinfoFundByFundActNo(msg033.fundActno1);
                BeanUtils.copyProperties(hmActinfoFund, msg033);
                hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
            } else {
                throw new RuntimeException("报文中包含未定义的子报文，子报文序号错误！");
            }
        }
        return hmbMsgList.size();
    }

    @Transactional
    public int cancelActinfoFundsByMsgList(List<HmbMsg> hmbMsgList) {
        for (HmbMsg hmbMsg : hmbMsgList) {
            if ("01051".equals(hmbMsg.getMsgType())) {
                Msg051 msg051 = (Msg051) hmbMsg;
                HmActinfoFund hmActinfoFund = hmActinfoFundService.qryHmActinfoFundByFundActNo(msg051.fundActno1);
                hmActinfoFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
            } else {
                throw new RuntimeException("报文中包含未定义的子报文，子报文序号错误！");
            }
        }
        return hmbMsgList.size();
    }

    private void createActinfoFundByHmbMsg(HmbMsg hmbMsg) throws InvocationTargetException, IllegalAccessException {
        HmActinfoFund actinfoFund = new HmActinfoFund();
        actinfoFund.setPkid(UUID.randomUUID().toString());
        BeanUtils.copyProperties(actinfoFund, hmbMsg);
        actinfoFund.setActSts("0");
        actinfoFund.setActBal(new BigDecimal(0));
        actinfoFund.setIntcPdt(new BigDecimal(0));
        actinfoFund.setOpenActDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        actinfoFund.setRecversion(0);
        hmActinfoFundMapper.insert(actinfoFund);
    }
}
