package common.repository.hmfs.dao.hmfs;

import common.repository.hmfs.model.HmMsgIn;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: 下午2:05
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface HmCmnMapper {
    @Select("select txnseq from hm_sys_ctl where sct_seqno = '1' for update")
    public int selectTxnseq();

    @Update("update hm_sys_ctl set txnseq = #{txnsn}  where sct_seqno = '1'")
    public int updateTxnseq(@Param("txnsn") int txnsn);

    @Update("update hm_msg_in set txn_ctl_sts = #{sts} where " +
            "(msg_type = '00005' and msg_sn = #{msgsn})" +
            " or (msg_type = '01035' and msg_sn = #{msgsn})" +
            " or (msg_type = '01045' and msg_sn = #{msgsn})")
    public int updatePayMsginSts(@Param("msgsn") String msgsn, @Param("sts") String sts);

    @Update("update hm_msg_in set txn_ctl_sts = #{sts} where " +
            "(msg_type = '00007' and msg_sn = #{msgsn})" +
            " or (msg_type = '01041' and msg_sn = #{msgsn})")
    public int updateDrawMsginSts(@Param("msgsn") String msgsn, @Param("sts") String sts);

    @Update("update hm_msg_in set txn_ctl_sts = #{sts} where " +
            "(msg_type = '00005' and msg_sn = #{msgsn})" +
            " or (msg_type = '01039' and msg_sn = #{msgsn})" +
            " or (msg_type = '01043' and msg_sn = #{msgsn})")
    public int updateRefundMsginSts(@Param("msgsn") String msgsn, @Param("sts") String sts);

    /**
     * 校验余额对帐结果
     */

    @Update("update HM_CHK_ACT" +
            "   set chksts = '0'" +
            " where txn_date = #{txnDate}" +
            "   and actno in (select t1.actno" +
            "                   from (select actno, actbal" +
            "                           from HM_CHK_ACT" +
            "                          where send_sys_id = #{sendSysId}" +
            "                            and txn_date = #{txnDate}) t1," +
            "                        (select actno, actbal" +
            "                           from HM_CHK_ACT" +
            "                          where send_sys_id = '00'" +
            "                            and txn_date = #{txnDate}) t2" +
            "                  where t1.actno = t2.actno" +
            "                    and t1.actbal = t2.actbal)")
    public int verifyChkaclResult_0(@Param("txnDate") String txnDate, @Param("sendSysId") String sendSysId);

    @Update("update HM_CHK_ACT" +
            "   set chksts = '1'" +
            " where txn_date = #{txnDate}" +
            "   and send_sys_id = #{sendSysId}" +
            "   and actno not in (select actno" +
            "                       from HM_CHK_ACT" +
            "                      where send_sys_id = '00'" +
            "                        and txn_date = #{txnDate})")
    public int verifyChkaclResult_11(@Param("txnDate") String txnDate, @Param("sendSysId") String sendSysId);

    @Update("update HM_CHK_ACT" +
            "   set chksts = '1'" +
            " where txn_date = #{txnDate}" +
            "   and send_sys_id = '00'" +
            "   and actno not in (select actno" +
            "                       from HM_CHK_ACT" +
            "                      where send_sys_id = #{sendSysId}" +
            "                        and txn_date = #{txnDate})")
    public int verifyChkaclResult_12(@Param("txnDate") String txnDate, @Param("sendSysId") String sendSysId);

    @Update("update HM_CHK_ACT" +
            "   set chksts = '2'" +
            " where txn_date = #{txnDate}" +
            "   and actno in (select t1.actno" +
            "                   from (select actno, actbal" +
            "                           from HM_CHK_ACT" +
            "                          where send_sys_id = #{sendSysId}" +
            "                            and txn_date = #{txnDate}) t1," +
            "                        (select actno, actbal" +
            "                           from HM_CHK_ACT" +
            "                          where send_sys_id = '00'" +
            "                            and txn_date = #{txnDate}) t2" +
            "                  where t1.actno = t2.actno" +
            "                    and t1.actbal != t2.actbal)")
    public int verifyChkaclResult_2(@Param("txnDate") String txnDate, @Param("sendSysId") String sendSysId);

    /**
     * 缴款交易：查询子报文信息
     * @param msgSn
     * @return
     */
    public List<HmMsgIn> selectPendDepositSubMsgList(@Param("msgSn") String msgSn);
}
