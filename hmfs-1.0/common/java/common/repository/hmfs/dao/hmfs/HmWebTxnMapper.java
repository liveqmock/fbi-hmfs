package common.repository.hmfs.dao.hmfs;

import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.hmfs.HmChkActVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * web�㽻�״���.
 * User: zhanrui
 * Date: 12-3-14
 * Time: ����2:05
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface HmWebTxnMapper {

    /**
     * �ɿ�ף���ѯ�ӱ�����Ϣ
     *
     * @param msgSn
     * @return
     */
    public List<HmMsgIn> selectSubMsgListByMsgSn(@Param("msgSn") String msgSn);

    /**
     * �����ʽ����ѯ
     *
     * @param sendSysId1
     * @param sendSysId2
     * @return
     */
    public List<HmChkActVO> selectChkActResult(@Param("sendSysId1") String sendSysId1
            , @Param("sendSysId2") String sendSysId2
            , @Param("startDate") String startDate);
}
