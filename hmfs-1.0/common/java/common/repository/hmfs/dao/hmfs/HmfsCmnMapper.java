package common.repository.hmfs.dao.hmfs;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: ÏÂÎç2:05
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface HmfsCmnMapper {
    @Select("select txnseq from hm_sct where sct_seqno = '1' for update")
    public int  selectTxnseq();

    @Update("update hm_sct set txnseq = #{txnsn}  where sct_seqno = '1'")
    public int  updateTxnseq(@Param("txnsn") int txnsn);
}
