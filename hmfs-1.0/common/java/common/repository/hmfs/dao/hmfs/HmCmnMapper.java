package common.repository.hmfs.dao.hmfs;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * DEP通用处理.
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
            " msg_sn = #{msgsn} ")
    public int updateMsginSts(@Param("msgsn") String msgsn, @Param("sts") String sts);

    /**
     * 校验余额对帐结果
     */
    //双方帐户都存在 核对 平账
    @Update("update HM_CHK_ACT" +
            "   set chksts = '0'" +
            " where txn_date = #{txnDate}" +
            "   and actno in (select t1.actno" +
            "                   from (select actno, actbal" +
            "                           from HM_CHK_ACT" +
            "                          where send_sys_id = #{sendSysId1}" +
            "                            and acttype = #{actType}" +
            "                            and txn_date = #{txnDate}) t1," +
            "                        (select actno, actbal" +
            "                           from HM_CHK_ACT" +
            "                          where send_sys_id = #{sendSysId2}" +
            "                            and acttype = #{actType}" +
            "                            and txn_date = #{txnDate}) t2" +
            "                  where t1.actno = t2.actno" +
            "                    and t1.cell_num = t2.cell_num" +
            "                    and t1.builder_area = t2.builder_area" +
            //"                    and t1.acttype = t2.acttype" +
            //"                    and t1.info_id1 = t2.info_id1" +
            //"                    and t1.info_id1_type1 = t2.info_id1_type1" +
            "                    and t1.actbal = t2.actbal)")
    public int verifyChkActResult_0(@Param("txnDate") String txnDate,
                                    @Param("sendSysId1") String sendSysId1,
                                    @Param("sendSysId2") String sendSysId2,
                                    @Param("actType") String actType);

    //核对我有他无
    @Update("update HM_CHK_ACT" +
            "   set chksts = '1'" +
            " where txn_date = #{txnDate}" +
            "   and send_sys_id = #{sendSysId1}" +
            "   and actno not in (select actno" +
            "                       from HM_CHK_ACT" +
            "                      where send_sys_id = #{sendSysId2}" +
            "                        and txn_date = #{txnDate})")
    public int verifyChkActResult_11(@Param("txnDate") String txnDate,
                                     @Param("sendSysId1") String sendSysId1,
                                     @Param("sendSysId2") String sendSysId2);

    //核对我无他有
    @Update("update HM_CHK_ACT" +
            "   set chksts = '1'" +
            " where txn_date = #{txnDate}" +
            "   and send_sys_id = #{sendSysId2}" +
            "   and actno not in (select actno" +
            "                       from HM_CHK_ACT" +
            "                      where send_sys_id = #{sendSysId1}" +
            "                        and txn_date = #{txnDate})")
    public int verifyChkActResult_12(@Param("txnDate") String txnDate,
                                     @Param("sendSysId1") String sendSysId1,
                                     @Param("sendSysId2") String sendSysId2);

    //双方帐户都存在 核对不平的情况
    @Update("update HM_CHK_ACT" +
            "   set chksts = '2'" +
            " where txn_date = #{txnDate}" +
            "   and actno in (select t1.actno" +
            "                   from (select actno, actbal" +
            "                           from HM_CHK_ACT" +
            "                          where send_sys_id = #{sendSysId1}" +
            "                            and acttype = #{actType}" +
            "                            and txn_date = #{txnDate}) t1," +
            "                        (select actno, actbal" +
            "                           from HM_CHK_ACT" +
            "                          where send_sys_id = #{sendSysId2}" +
            "                            and acttype = #{actType}" +
            "                            and txn_date = #{txnDate}) t2" +
            "                  where t1.actno = t2.actno" +
            "                    and t1.cell_num != t2.cell_num" +
            "                    and t1.builder_area != t2.builder_area" +
            "                    and t1.actbal != t2.actbal)")
    public int verifyChkActResult_2(@Param("txnDate") String txnDate,
                                    @Param("sendSysId1") String sendSysId1,
                                    @Param("sendSysId2") String sendSysId2,
                                    @Param("actType") String actType);

}
