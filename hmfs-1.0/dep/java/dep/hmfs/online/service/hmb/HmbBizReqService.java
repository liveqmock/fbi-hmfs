package dep.hmfs.online.service.hmb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-26
 * Time: обнГ7:35
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbBizReqService {
    private static final Logger logger = LoggerFactory.getLogger(HmbBizReqService.class);
    
    @Autowired
    private HmbClientReqService hmbClientReqService;


    
}
