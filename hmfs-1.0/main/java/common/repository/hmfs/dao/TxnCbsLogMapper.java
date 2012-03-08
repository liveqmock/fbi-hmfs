package common.repository.hmfs.dao;

import common.repository.hmfs.model.TxnCbsLog;
import common.repository.hmfs.model.TxnCbsLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TxnCbsLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_CBS_LOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int countByExample(TxnCbsLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_CBS_LOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int deleteByExample(TxnCbsLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_CBS_LOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_CBS_LOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int insert(TxnCbsLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_CBS_LOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int insertSelective(TxnCbsLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_CBS_LOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    List<TxnCbsLog> selectByExample(TxnCbsLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_CBS_LOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    TxnCbsLog selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_CBS_LOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int updateByExampleSelective(@Param("record") TxnCbsLog record, @Param("example") TxnCbsLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_CBS_LOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int updateByExample(@Param("record") TxnCbsLog record, @Param("example") TxnCbsLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_CBS_LOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int updateByPrimaryKeySelective(TxnCbsLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TXN_CBS_LOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int updateByPrimaryKey(TxnCbsLog record);
}