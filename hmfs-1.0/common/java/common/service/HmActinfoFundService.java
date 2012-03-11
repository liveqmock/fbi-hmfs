package common.service;

import common.repository.hmfs.dao.HmActinfoFundMapper;
import common.repository.hmfs.model.HmActinfoFund;
import common.repository.hmfs.model.HmActinfoFundExample;
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
public class HmActinfoFundService {
    
    @Autowired
    private HmActinfoFundMapper hmActinfoFundMapper;
    
    public HmActinfoFund qryHmActinfoFundByFundActNo(String fundActNo) {
        HmActinfoFundExample example = new HmActinfoFundExample();
        example.createCriteria().andFundActno1EqualTo(fundActNo);
        List<HmActinfoFund> actinfoFundList = hmActinfoFundMapper.selectByExample(example);
        return actinfoFundList.size() > 0 ? actinfoFundList.get(0) : null;
    }
}
