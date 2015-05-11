package common.repository.hmfs.dao.hmfs;

import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmVchStore;
import common.repository.hmfs.model.hmfs.HmChkActVO;
import common.repository.hmfs.model.hmfs.HmChkTxnVO;
import common.repository.hmfs.model.hmfs.HmFundTxnVO;
import common.repository.hmfs.model.hmfs.HmVchStoreSumVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 新票据管理. 主要是出入库管理.
 * User: zhanrui
 * Date: 20150423
 */
@Repository
public interface HmVoucherMapper {

    //按机构查询票据库存情况
    public List<HmVchStore> selectInstVoucherStoreList(@Param("instNo") String instNo);

    //public List<HmVchStoreSumVO> selectInstVoucherStoreSumInfo(@Param("instNo") String instNo);

    //按机构查找当前库存的最大票据号
    public String selectInstVchMaxEndNo(@Param("instNo") String instNo);



    //比需入库的票据号大的库存记录中的起号
    public String selectStoreStartno_GreaterThanVchno( @Param("vchNo") String vchNo);

    //比需入库的票据号小的库存记录中的止号
    public String selectStoreEndno_LessThanVchno(@Param("vchNo") String vchNo);

    //按照起止号查找库存记录中的在此范围内的记录数，非票据数
    public int selectStoreRecordnumBetweenStartnoAndEndno(@Param("startNo") String startNo, @Param("endNo") String endNo);

    //按照起止号查找库存记录中的在此范围内的记录集
    public List<HmVchStore> selectStoreRecordListBetweenStartnoAndEndno(@Param("startNo") String startNo, @Param("endNo") String endNo);



    //出库删除
    public int deleteVoucherByPkid(@Param("pkid") String pkid, @Param("recversion") int recversion);

    //出库更新 (只更新止号)
    public int updateVoucherStoreRecordEndnoByPkid(@Param("pkid") String pkid, @Param("recversion") int recversion, @Param("endNo") String endNo);
}
