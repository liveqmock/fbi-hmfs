package hmfs.service;

import common.enums.SysCtlSts;
import common.repository.hmfs.dao.HmSctMapper;
import common.repository.hmfs.model.HmSct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-12
 * Time: 下午9:55
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppMngService {
    @Resource
    private HmSctMapper hmSctMapper;

    public HmSct getAppSysStatus(){
         return hmSctMapper.selectByPrimaryKey("1");
    }

    /**
     *   向国土局签到
     */
    @Transactional
    public void processSignon(){
        //TODO
        HmSct hmSct = getAppSysStatus();
        hmSct.setSysSts(SysCtlSts.SIGNON.getCode());
        hmSct.setSignonDt(new Date());
        hmSctMapper.updateByPrimaryKey(hmSct);

    }
}
