package common.repository.hmfs.dao;

import common.repository.hmfs.model.HmJoblog;
import common.repository.hmfs.model.HmJoblogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface HmJoblogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_JOBLOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int countByExample(HmJoblogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_JOBLOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int deleteByExample(HmJoblogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_JOBLOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_JOBLOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int insert(HmJoblog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_JOBLOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int insertSelective(HmJoblog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_JOBLOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    List<HmJoblog> selectByExample(HmJoblogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_JOBLOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    HmJoblog selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_JOBLOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int updateByExampleSelective(@Param("record") HmJoblog record, @Param("example") HmJoblogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_JOBLOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int updateByExample(@Param("record") HmJoblog record, @Param("example") HmJoblogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_JOBLOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int updateByPrimaryKeySelective(HmJoblog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_JOBLOG
     *
     * @mbggenerated Thu Mar 08 18:40:05 CST 2012
     */
    int updateByPrimaryKey(HmJoblog record);
}