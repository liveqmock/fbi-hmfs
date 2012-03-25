package common.repository.hmfs.dao;

import common.repository.hmfs.model.HmTxnStl;
import common.repository.hmfs.model.HmTxnStlExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HmTxnStlMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_STL
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int countByExample(HmTxnStlExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_STL
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int deleteByExample(HmTxnStlExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_STL
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_STL
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int insert(HmTxnStl record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_STL
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int insertSelective(HmTxnStl record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_STL
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    List<HmTxnStl> selectByExample(HmTxnStlExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_STL
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    HmTxnStl selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_STL
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int updateByExampleSelective(@Param("record") HmTxnStl record, @Param("example") HmTxnStlExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_STL
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int updateByExample(@Param("record") HmTxnStl record, @Param("example") HmTxnStlExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_STL
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int updateByPrimaryKeySelective(HmTxnStl record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table HMFS.HM_TXN_STL
     *
     * @mbggenerated Sun Mar 25 09:56:53 CST 2012
     */
    int updateByPrimaryKey(HmTxnStl record);
}