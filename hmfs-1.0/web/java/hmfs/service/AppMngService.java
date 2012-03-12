package hmfs.service;

import common.repository.hmfs.dao.HmSctMapper;
import common.repository.hmfs.model.HmSct;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-12
 * Time: обнГ9:55
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppMngService {
    @Resource
    private HmSctMapper hmSctMapper;

    public HmSct getAppSysStatus(){
         return hmSctMapper.selectByPrimaryKey("1");
    }
}
