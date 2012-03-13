package hmfs.service;

import common.repository.hmfs.dao.HmSctMapper;
import common.repository.hmfs.model.HmSct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-12
 * Time: ÏÂÎç9:55
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppMngService {
    private static final Logger logger = LoggerFactory.getLogger(AppMngService.class);

    @Resource
    private HmSctMapper hmSctMapper;

    @Resource
    private  DepService depService;

    public HmSct getAppSysStatus(){
         return hmSctMapper.selectByPrimaryKey("1");
    }

}
