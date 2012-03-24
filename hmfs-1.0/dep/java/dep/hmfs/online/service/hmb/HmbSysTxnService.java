package dep.hmfs.online.service.hmb;

import common.enums.FundActnoStatus;
import common.repository.hmfs.dao.*;
import common.repository.hmfs.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * ��������ǩ��ǩ�� ����.
 * User: zhanrui
 * Date: 12-3-12
 * Time: ����9:55
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbSysTxnService extends HmbBaseService {
    private static final Logger logger = LoggerFactory.getLogger(HmbSysTxnService.class);

    @Resource
    private HmActFundMapper hmActFundMapper;

    @Resource
    private HmActStlMapper hmActStlMapper;


    @Resource
    private HmChkTxnMapper hmChkTxnMapper;
    @Resource
    private HmTxnFundMapper hmTxnFundMapper;
    @Resource
    private HmTxnStlMapper hmTxnStlMapper;


    /**
     * �����˻����
     * @return
     */
    public List<HmActFund> selectFundActinfo(){
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode()).andFundActno1NotEqualTo("120000000003");
        return hmActFundMapper.selectByExample(example);
    }

    /**
     * �����˻����
     * @return
     */
    public List<HmActStl> selectCbsActinfo(){
        HmActStlExample example = new HmActStlExample();
        example.createCriteria().andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActStlMapper.selectByExample(example);
    }
    /**
     * �����˻���ˮ
     * @return
     */
    public List<HmTxnFund> selectFundTxnDetl(String yyyymmdd){
        HmTxnFundExample exampleHm = new HmTxnFundExample();
        exampleHm.createCriteria().andTxnDateEqualTo(yyyymmdd);
        return hmTxnFundMapper.selectByExample(exampleHm);
    }

}
