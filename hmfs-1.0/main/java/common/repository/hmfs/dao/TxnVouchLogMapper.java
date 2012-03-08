package common.repository.hmfs.dao;

import common.repository.hmfs.model.TxnVouchLog;
import common.repository.hmfs.model.TxnVouchLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TxnVouchLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_VOUCH_LOG
     *
     * @mbggenerated Thu Mar 08 11:33:14 CST 2012
     */
    int countByExample(TxnVouchLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_VOUCH_LOG
     *
     * @mbggenerated Thu Mar 08 11:33:14 CST 2012
     */
    int deleteByExample(TxnVouchLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_VOUCH_LOG
     *
     * @mbggenerated Thu Mar 08 11:33:14 CST 2012
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_VOUCH_LOG
     *
     * @mbggenerated Thu Mar 08 11:33:14 CST 2012
     */
    int insert(TxnVouchLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_VOUCH_LOG
     *
     * @mbggenerated Thu Mar 08 11:33:14 CST 2012
     */
    int insertSelective(TxnVouchLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_VOUCH_LOG
     *
     * @mbggenerated Thu Mar 08 11:33:14 CST 2012
     */
    List<TxnVouchLog> selectByExample(TxnVouchLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_VOUCH_LOG
     *
     * @mbggenerated Thu Mar 08 11:33:14 CST 2012
     */
    TxnVouchLog selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_VOUCH_LOG
     *
     * @mbggenerated Thu Mar 08 11:33:14 CST 2012
     */
    int updateByExampleSelective(@Param("record") TxnVouchLog record, @Param("example") TxnVouchLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_VOUCH_LOG
     *
     * @mbggenerated Thu Mar 08 11:33:14 CST 2012
     */
    int updateByExample(@Param("record") TxnVouchLog record, @Param("example") TxnVouchLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_VOUCH_LOG
     *
     * @mbggenerated Thu Mar 08 11:33:14 CST 2012
     */
    int updateByPrimaryKeySelective(TxnVouchLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_VOUCH_LOG
     *
     * @mbggenerated Thu Mar 08 11:33:14 CST 2012
     */
    int updateByPrimaryKey(TxnVouchLog record);
}