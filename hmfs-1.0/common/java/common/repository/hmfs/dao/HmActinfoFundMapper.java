package common.repository.hmfs.dao;

import common.repository.hmfs.model.HmActinfoFund;
import common.repository.hmfs.model.HmActinfoFundExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HmActinfoFundMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACTINFO_FUND
     *
     * @mbggenerated Thu Mar 15 21:49:57 CST 2012
     */
    int countByExample(HmActinfoFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACTINFO_FUND
     *
     * @mbggenerated Thu Mar 15 21:49:57 CST 2012
     */
    int deleteByExample(HmActinfoFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACTINFO_FUND
     *
     * @mbggenerated Thu Mar 15 21:49:57 CST 2012
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACTINFO_FUND
     *
     * @mbggenerated Thu Mar 15 21:49:57 CST 2012
     */
    int insert(HmActinfoFund record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACTINFO_FUND
     *
     * @mbggenerated Thu Mar 15 21:49:57 CST 2012
     */
    int insertSelective(HmActinfoFund record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACTINFO_FUND
     *
     * @mbggenerated Thu Mar 15 21:49:57 CST 2012
     */
    List<HmActinfoFund> selectByExample(HmActinfoFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACTINFO_FUND
     *
     * @mbggenerated Thu Mar 15 21:49:57 CST 2012
     */
    HmActinfoFund selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACTINFO_FUND
     *
     * @mbggenerated Thu Mar 15 21:49:57 CST 2012
     */
    int updateByExampleSelective(@Param("record") HmActinfoFund record, @Param("example") HmActinfoFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACTINFO_FUND
     *
     * @mbggenerated Thu Mar 15 21:49:57 CST 2012
     */
    int updateByExample(@Param("record") HmActinfoFund record, @Param("example") HmActinfoFundExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACTINFO_FUND
     *
     * @mbggenerated Thu Mar 15 21:49:57 CST 2012
     */
    int updateByPrimaryKeySelective(HmActinfoFund record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_ACTINFO_FUND
     *
     * @mbggenerated Thu Mar 15 21:49:57 CST 2012
     */
    int updateByPrimaryKey(HmActinfoFund record);
}