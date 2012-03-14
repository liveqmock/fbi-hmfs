package dep.hmfs.online.service.hmb;

import common.repository.hmfs.dao.HmActinfoCbsMapper;
import common.repository.hmfs.dao.HmActinfoFundMapper;
import common.repository.hmfs.model.HmActinfoCbs;
import common.repository.hmfs.model.HmActinfoFund;
import common.service.HmActinfoFundService;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg032;
import dep.hmfs.online.processor.hmb.domain.Msg033;
import dep.hmfs.online.processor.hmb.domain.Msg034;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-14
 * Time: ����7:58
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
                throw new RuntimeException("�����а���δ������ӱ��ģ��ӱ�����Ŵ���");
            }
        }
        return hmbMsgList.size();
    }

    public int updateActinfosByMsgList(List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        for (HmbMsg hmbMsg : hmbMsgList) {
            if ("01033".equals(hmbMsg.getMsgType())) {
                Msg033 msg033 = (Msg033) hmbMsg;
                HmActinfoFund hmActinfoFund = hmActinfoFundService.qryHmActinfoFundByFundActNo(msg033.fundActno1);
                BeanUtils.copyProperties(hmActinfoFund, msg033);
                hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
            } else {
                throw new RuntimeException("�����а���δ������ӱ��ģ��ӱ�����Ŵ���");
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
