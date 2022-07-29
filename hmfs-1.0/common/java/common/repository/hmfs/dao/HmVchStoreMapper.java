package common.repository.hmfs.dao;

import common.repository.hmfs.model.HmVchStore;
import common.repository.hmfs.model.HmVchStoreExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HmVchStoreMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_VCH_STORE
     *
     * @mbggenerated Wed May 13 18:53:12 CST 2015
     */
    int countByExample(HmVchStoreExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_VCH_STORE
     *
     * @mbggenerated Wed May 13 18:53:12 CST 2015
     */
    int deleteByExample(HmVchStoreExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_VCH_STORE
     *
     * @mbggenerated Wed May 13 18:53:12 CST 2015
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_VCH_STORE
     *
     * @mbggenerated Wed May 13 18:53:12 CST 2015
     */
    int insert(HmVchStore record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_VCH_STORE
     *
     * @mbggenerated Wed May 13 18:53:12 CST 2015
     */
    int insertSelective(HmVchStore record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_VCH_STORE
     *
     * @mbggenerated Wed May 13 18:53:12 CST 2015
     */
    List<HmVchStore> selectByExample(HmVchStoreExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_VCH_STORE
     *
     * @mbggenerated Wed May 13 18:53:12 CST 2015
     */
    HmVchStore selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_VCH_STORE
     *
     * @mbggenerated Wed May 13 18:53:12 CST 2015
     */
    int updateByExampleSelective(@Param("record") HmVchStore record, @Param("example") HmVchStoreExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_VCH_STORE
     *
     * @mbggenerated Wed May 13 18:53:12 CST 2015
     */
    int updateByExample(@Param("record") HmVchStore record, @Param("example") HmVchStoreExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_VCH_STORE
     *
     * @mbggenerated Wed May 13 18:53:12 CST 2015
     */
    int updateByPrimaryKeySelective(HmVchStore record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_VCH_STORE
     *
     * @mbggenerated Wed May 13 18:53:12 CST 2015
     */
    int updateByPrimaryKey(HmVchStore record);
}