package common.service;

import common.repository.hmfs.dao.HmActinfoCbsMapper;
import common.repository.hmfs.model.HmActinfoCbs;
import common.repository.hmfs.model.HmActinfoCbsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    
    public HmActinfoCbs qryHmActinfoCbsBySettleActNo(String settleActNo) {
        HmActinfoCbsExample example = new HmActinfoCbsExample();
        example.createCriteria().andSettleActno1EqualTo(settleActNo);
        List<HmActinfoCbs> actinfoCbsList = hmActinfoCbsMapper.selectByExample(example);
        return actinfoCbsList.size() > 0 ? actinfoCbsList.get(0) : null;
    }
}
