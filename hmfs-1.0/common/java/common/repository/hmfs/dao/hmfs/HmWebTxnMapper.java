package common.repository.hmfs.dao.hmfs;

import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.hmfs.HmChkActVO;
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
     *
     * @param msgSn
     * @return
     */
    public List<HmMsgIn> selectSubMsgListByMsgSn(@Param("msgSn") String msgSn);

    /**
     * 余额对帐结果查询
     *
     * @param sendSysId1
     * @param sendSysId2
     * @return
     */
    public List<HmChkActVO> selectChkActResult(@Param("sendSysId1") String sendSysId1
            , @Param("sendSysId2") String sendSysId2
            , @Param("startDate") String startDate);
}
