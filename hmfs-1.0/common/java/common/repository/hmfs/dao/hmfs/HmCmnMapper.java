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

    @Select("select txnseq from hmfs.hm_sys_ctl where sct_seqno = '1' for update")
    public int selectTxnseq();

    @Update("update hmfs.hm_sys_ctl set txnseq = #{txnsn}  where sct_seqno = '1'")
    public int updateTxnseq(@Param("txnsn") int txnsn);

    @Update("update hm_msg_in set txn_ctl_sts = #{sts} where " +
            " msg_sn = #{msgsn} ")
    public int updateMsginSts(@Param("msgsn") String msgsn, @Param("sts") String sts);


    //===========校验余额对帐结果====================================================================
    //双方帐户都存在 核对 平账
    @Update("update HM_CHK_ACT" +
            "   set chksts = '0'" +
            " where txn_date = #{txnDate}" +
            "   and actno in (select t1.actno" +
            "                   from (select * " +
            "                           from HM_CHK_ACT" +
            "                          where send_sys_id = #{sendSysId1}" +
            "                            and acttype = #{actType}" +
            "                            and txn_date = #{txnDate}) t1," +
            "                        (select * " +
            "                           from HM_CHK_ACT" +
            "                          where send_sys_id = #{sendSysId2}" +
            "                            and acttype = #{actType}" +
            "                            and txn_date = #{txnDate}) t2" +
            "                  where t1.actno = t2.actno" +
//20120913 zhanrui 不核对面积
//            "                    and cast(t1.builder_area as decimal(8,2)) = cast(t2.builder_area as decimal(8,2))" +
            "                    and cast(t1.cell_num as decimal(8,2)) = cast(t2.cell_num as decimal(8,2))" +
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
            "                   from (select * " +
            "                           from HM_CHK_ACT" +
            "                          where send_sys_id = #{sendSysId1}" +
            "                            and acttype = #{actType}" +
            "                            and txn_date = #{txnDate}) t1," +
            "                        (select * " +
            "                           from HM_CHK_ACT" +
            "                          where send_sys_id = #{sendSysId2}" +
            "                            and acttype = #{actType}" +
            "                            and txn_date = #{txnDate}) t2" +
            "                  where t1.actno = t2.actno" +
            "                    and (cast(t1.cell_num as decimal(8,2)) != cast(t2.cell_num as decimal(8,2))" +
//20120913 zhanrui 不核对面积
//            "                    or cast(t1.builder_area as decimal(8,2)) != cast(t2.builder_area as decimal(8,2))" +
            "                    or t1.actbal != t2.actbal))")
    public int verifyChkActResult_2(@Param("txnDate") String txnDate,
                                    @Param("sendSysId1") String sendSysId1,
                                    @Param("sendSysId2") String sendSysId2,
                                    @Param("actType") String actType);


    //===========校验流水对帐结果====================================================================
    //双方帐户都存在 核对 平账
    @Update("update HM_CHK_TXN" +
            "   set chksts = '0'" +
            " where txn_date = #{txnDate}" +
            "   and msg_sn in (select t1.msg_sn" +
            "                   from (select * " +
            "                           from HM_CHK_TXN" +
            "                          where send_sys_id = #{sendSysId1}" +
            "                            and txn_date = #{txnDate}) t1," +
            "                        (select * " +
            "                           from HM_CHK_TXN" +
            "                          where send_sys_id = #{sendSysId2}" +
            "                            and txn_date = #{txnDate}) t2" +
            "                  where t1.msg_sn = t2.msg_sn" +
            "                    and t1.txnamt = t2.txnamt" +
            "                    and t1.dc_flag = t2.dc_flag)")
    public int verifyChkTxnResult_0(@Param("txnDate") String txnDate,
                                    @Param("sendSysId1") String sendSysId1,
                                    @Param("sendSysId2") String sendSysId2);

    //核对我有他无
    @Update("update HM_CHK_TXN" +
            "   set chksts = '1'" +
            " where txn_date = #{txnDate}" +
            "   and send_sys_id = #{sendSysId1}" +
            "   and msg_sn not in (select msg_sn" +
            "                       from HM_CHK_TXN" +
            "                      where send_sys_id = #{sendSysId2}" +
            "                        and txn_date = #{txnDate})")
    public int verifyChkTxnResult_11(@Param("txnDate") String txnDate,
                                     @Param("sendSysId1") String sendSysId1,
                                     @Param("sendSysId2") String sendSysId2);

    //核对我无他有
    @Update("update HM_CHK_TXN" +
            "   set chksts = '1'" +
            " where txn_date = #{txnDate}" +
            "   and send_sys_id = #{sendSysId2}" +
            "   and msg_sn not in (select msg_sn" +
            "                       from HM_CHK_TXN" +
            "                      where send_sys_id = #{sendSysId1}" +
            "                        and txn_date = #{txnDate})")
    public int verifyChkTxnResult_12(@Param("txnDate") String txnDate,
                                     @Param("sendSysId1") String sendSysId1,
                                     @Param("sendSysId2") String sendSysId2);

    //双方帐户都存在 核对不平的情况
    @Update("update HM_CHK_TXN" +
            "   set chksts = '2'" +
            " where txn_date = #{txnDate}" +
            "   and msg_sn in (select t1.msg_sn" +
            "                   from (select * " +
            "                           from HM_CHK_TXN" +
            "                          where send_sys_id = #{sendSysId1}" +
            "                            and txn_date = #{txnDate}) t1," +
            "                        (select * " +
            "                           from HM_CHK_TXN" +
            "                          where send_sys_id = #{sendSysId2}" +
            "                            and txn_date = #{txnDate}) t2" +
            "                  where t1.msg_sn = t2.msg_sn" +
            "                    and (t1.txnamt != t2.txnamt" +
            "                    or t1.dc_flag != t2.dc_flag))")
    public int verifyChkTxnResult_2(@Param("txnDate") String txnDate,
                                    @Param("sendSysId1") String sendSysId1,
                                    @Param("sendSysId2") String sendSysId2);

}
