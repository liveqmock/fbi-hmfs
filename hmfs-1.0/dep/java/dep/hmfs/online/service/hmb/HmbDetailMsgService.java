package dep.hmfs.online.service.hmb;

import common.enums.FundActnoStatus;
import common.repository.hmfs.dao.HmActinfoCbsMapper;
import common.repository.hmfs.dao.HmActinfoFundMapper;
import common.repository.hmfs.model.HmActinfoCbs;
import common.repository.hmfs.model.HmActinfoFund;
import common.repository.hmfs.model.HmActinfoFundExample;
import common.service.HmActinfoFundService;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.*;
import dep.hmfs.online.service.cmb.CmbBookkeepingService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
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

    @Autowired
    private CmbBookkeepingService bookkeepingService;


    @Transactional
    public int createActinfosByMsgList(List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        for (HmbMsg hmbMsg : hmbMsgList) {
            if ("01032".equals(hmbMsg.getMsgType())) {
                Msg032 msg032 = (Msg032) hmbMsg;
                createActinfoCbsByHmbMsg(msg032);
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

    private int createActinfoCbsByHmbMsg(HmbMsg hmbMsg) throws InvocationTargetException, IllegalAccessException {

        HmActinfoCbs actinfoCbs = new HmActinfoCbs();
        actinfoCbs.setPkid(UUID.randomUUID().toString());
        BeanUtils.copyProperties(actinfoCbs, hmbMsg);
        actinfoCbs.setActSts("0");
        actinfoCbs.setActBal(new BigDecimal(0));
        actinfoCbs.setIntcPdt(new BigDecimal(0));
        actinfoCbs.setOpenActDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        actinfoCbs.setRecversion(0);
        return hmActinfoCbsMapper.insert(actinfoCbs);
    }

    private int createActinfoFundByHmbMsg(HmbMsg hmbMsg) throws InvocationTargetException, IllegalAccessException {
        HmActinfoFund actinfoFund = new HmActinfoFund();
        actinfoFund.setPkid(UUID.randomUUID().toString());
        BeanUtils.copyProperties(actinfoFund, hmbMsg);
        actinfoFund.setActSts("0");
        actinfoFund.setActBal(new BigDecimal(0));
        actinfoFund.setIntcPdt(new BigDecimal(0));
        actinfoFund.setOpenActDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        actinfoFund.setRecversion(0);
        return hmActinfoFundMapper.insert(actinfoFund);
    }

    /**
     * 项目拆分合并
     */
    public void splitFundActinfo(String txnCode, List<HmbMsg> hmbMsgList)  {
        //更新核算户信息
        try {
            for (HmbMsg hmbMsg : hmbMsgList.subList(1, hmbMsgList.size())) {
                changeFundActinfo((Msg033)hmbMsg);
            }
        } catch (Exception e) {
            throw  new RuntimeException("核算户信息更新错误！", e);
        }

        Msg009 msg009 = (Msg009) hmbMsgList.get(0);
        String payOutActno = msg009.fundActno1;
        String payInActno = msg009.payinCbsActno;
        String msgSn = msg009.getMsgSn();
        BigDecimal amt = msg009.txnAmt1;

        //余额、流水处理
        try {
            bookkeepingService.fundActBookkeeping(msgSn, payOutActno, amt,  "D", "145");
            bookkeepingService.fundActBookkeeping(msgSn, payInActno, amt, "C", "145");
        } catch (ParseException e) {
            throw  new RuntimeException("帐务处理错误！", e);
        }
    }


    //更新核算帐户信息
    @Transactional
    private void changeFundActinfo(Msg033 msg) throws InvocationTargetException, IllegalAccessException {
        HmActinfoFundExample example = new  HmActinfoFundExample();
        example.createCriteria().andFundActno1EqualTo(msg.fundActno1);
        List<HmActinfoFund> actinfoFundList = hmActinfoFundMapper.selectByExample(example);
        if (actinfoFundList.size() == 0) {
            throw new RuntimeException("账户信息不存在。");
        }
        //TODO  检查多条？
        HmActinfoFund actinfoFund = actinfoFundList.get(0);
        int recversion = actinfoFund.getRecversion() + 1;
        BeanUtils.copyProperties(actinfoFund, msg);
        actinfoFund.setRemark("TXN6210 项目拆分合并" + new Date().toString());
        actinfoFund.setRecversion(recversion);
        hmActinfoFundMapper.updateByPrimaryKey(actinfoFund);
    }
}
