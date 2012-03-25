package common.repository.hmfs.dao;

import common.repository.hmfs.model.HmTxnFund;
import common.repository.hmfs.model.HmTxnFundExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HmTxnFundMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_FUND
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int countByExample(HmTxnFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_FUND
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int deleteByExample(HmTxnFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_FUND
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_FUND
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int insert(HmTxnFund record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_FUND
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int insertSelective(HmTxnFund record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_FUND
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    List<HmTxnFund> selectByExample(HmTxnFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_FUND
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    HmTxnFund selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_FUND
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int updateByExampleSelective(@Param("record") HmTxnFund record, @Param("example") HmTxnFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_FUND
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int updateByExample(@Param("record") HmTxnFund record, @Param("example") HmTxnFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_FUND
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int updateByPrimaryKeySelective(HmTxnFund record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_FUND
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int updateByPrimaryKey(HmTxnFund record);
}