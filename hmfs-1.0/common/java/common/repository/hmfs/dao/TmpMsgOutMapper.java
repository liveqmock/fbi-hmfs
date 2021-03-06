package common.repository.hmfs.dao;

import common.repository.hmfs.model.TmpMsgOut;
import common.repository.hmfs.model.TmpMsgOutExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TmpMsgOutMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TMP_MSG_OUT
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int countByExample(TmpMsgOutExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TMP_MSG_OUT
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int deleteByExample(TmpMsgOutExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TMP_MSG_OUT
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TMP_MSG_OUT
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int insert(TmpMsgOut record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TMP_MSG_OUT
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int insertSelective(TmpMsgOut record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TMP_MSG_OUT
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    List<TmpMsgOut> selectByExample(TmpMsgOutExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TMP_MSG_OUT
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    TmpMsgOut selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TMP_MSG_OUT
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int updateByExampleSelective(@Param("record") TmpMsgOut record, @Param("example") TmpMsgOutExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TMP_MSG_OUT
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int updateByExample(@Param("record") TmpMsgOut record, @Param("example") TmpMsgOutExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TMP_MSG_OUT
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int updateByPrimaryKeySelective(TmpMsgOut record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.TMP_MSG_OUT
     *
     * @mbggenerated Sun Mar 25 21:53:14 CST 2012
     */
    int updateByPrimaryKey(TmpMsgOut record);
}