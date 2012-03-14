package dep.hmfs.online.service.hmb;

import common.repository.hmfs.dao.HmActinfoCbsMapper;
import common.repository.hmfs.dao.HmActinfoFundMapper;
import common.repository.hmfs.model.HmActinfoCbs;
import common.repository.hmfs.model.HmActinfoFund;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg032;
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
 * Time: 下午7:58
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbDetailMsgService extends HmbBaseService {

    @Autowired
    private HmActinfoCbsMapper hmActinfoCbsMapper;
    @Autowired
    private HmActinfoFundMapper hmActinfoFundMapper;

    public int createFundActByMsgList(List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
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
            } else if ("01034".equals(hmbMsg.getMsgType())) {
                Msg034 msg034 = (Msg034) hmbMsg;

                HmActinfoFund actinfoFund = new HmActinfoFund();
                actinfoFund.setPkid(UUID.randomUUID().toString());
                BeanUtils.copyProperties(actinfoFund, msg034);
                actinfoFund.setActSts("0");
                actinfoFund.setActBal(new BigDecimal(0));
                actinfoFund.setIntcPdt(new BigDecimal(0));
                actinfoFund.setOpenActDate(SystemService.formatTodayByPattern("yyyyMMdd"));
                actinfoFund.setRecversion(0);

                hmActinfoFundMapper.insert(actinfoFund);
            } else {
                throw new RuntimeException("报文中包含未定义的子报文，子报文序号错误！");
            }
        }
        return hmbMsgList.size();
    }
}
