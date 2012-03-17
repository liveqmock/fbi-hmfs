package dep.hmfs.online.service;

import common.repository.hmfs.dao.HmActinfoCbsMapper;
import common.repository.hmfs.model.HmActinfoCbs;
import common.repository.hmfs.model.HmActinfoCbsExample;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
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
 * Date: 12-3-11
 * Time: ÏÂÎç12:44
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmActinfoCbsService {

    @Autowired
    private HmActinfoCbsMapper hmActinfoCbsMapper;

    public HmActinfoCbs getFirstHmActinfoCbs() {
        return hmActinfoCbsMapper.selectByExample(new HmActinfoCbsExample()).get(0);
    }

    public HmActinfoCbs qryHmActinfoCbsByNo(String cbsActNo) {
        HmActinfoCbsExample example = new HmActinfoCbsExample();
        example.createCriteria().andCbsActnoEqualTo(cbsActNo);
        List<HmActinfoCbs> actinfoCbsList = hmActinfoCbsMapper.selectByExample(example);
        return actinfoCbsList.size() > 0 ? actinfoCbsList.get(0) : null;
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
}
