package common.repository.hmfs.dao;

import common.repository.hmfs.model.HmActFund;
import common.repository.hmfs.model.HmActFundExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HmActFundMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACT_FUND
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int countByExample(HmActFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACT_FUND
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int deleteByExample(HmActFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACT_FUND
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACT_FUND
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int insert(HmActFund record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACT_FUND
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int insertSelective(HmActFund record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACT_FUND
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    List<HmActFund> selectByExample(HmActFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACT_FUND
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    HmActFund selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACT_FUND
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int updateByExampleSelective(@Param("record") HmActFund record, @Param("example") HmActFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACT_FUND
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int updateByExample(@Param("record") HmActFund record, @Param("example") HmActFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACT_FUND
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int updateByPrimaryKeySelective(HmActFund record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACT_FUND
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int updateByPrimaryKey(HmActFund record);
}