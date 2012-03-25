package common.repository.hmfs.dao;

import common.repository.hmfs.model.HmMsgCancel;
import common.repository.hmfs.model.HmMsgCancelExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HmMsgCancelMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_CANCEL
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int countByExample(HmMsgCancelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_CANCEL
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int deleteByExample(HmMsgCancelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_CANCEL
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_CANCEL
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int insert(HmMsgCancel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_CANCEL
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int insertSelective(HmMsgCancel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_CANCEL
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    List<HmMsgCancel> selectByExample(HmMsgCancelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_CANCEL
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    HmMsgCancel selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_CANCEL
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int updateByExampleSelective(@Param("record") HmMsgCancel record, @Param("example") HmMsgCancelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_CANCEL
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int updateByExample(@Param("record") HmMsgCancel record, @Param("example") HmMsgCancelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_CANCEL
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int updateByPrimaryKeySelective(HmMsgCancel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_MSG_CANCEL
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int updateByPrimaryKey(HmMsgCancel record);
}