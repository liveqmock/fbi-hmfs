package common.repository.hmfs.dao;

import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmMsgInExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HmMsgInMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_IN
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int countByExample(HmMsgInExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_IN
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int deleteByExample(HmMsgInExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_IN
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_IN
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int insert(HmMsgIn record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_IN
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int insertSelective(HmMsgIn record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_IN
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    List<HmMsgIn> selectByExample(HmMsgInExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_IN
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    HmMsgIn selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_IN
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int updateByExampleSelective(@Param("record") HmMsgIn record, @Param("example") HmMsgInExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_IN
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int updateByExample(@Param("record") HmMsgIn record, @Param("example") HmMsgInExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_IN
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int updateByPrimaryKeySelective(HmMsgIn record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_IN
     *
     * @mbggenerated Sat Mar 24 20:15:49 CST 2012
     */
    int updateByPrimaryKey(HmMsgIn record);
}