package common.repository.hmfs.dao.hmfs;

import common.repository.hmfs.model.HmMsgIn;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * web层交易处理.
 * User: zhanrui
 * Date: 12-3-14
 * Time: 下午2:05
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface HmWebTxnMapper {

    /**
     * 缴款交易：查询子报文信息
     * @param msgSn
     * @return
     */
    public List<HmMsgIn> selectSubMsgListByMsgSn(@Param("msgSn") String msgSn);
}
