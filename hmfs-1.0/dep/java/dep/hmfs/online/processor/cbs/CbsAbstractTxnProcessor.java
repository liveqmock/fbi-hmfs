package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.SysCtlSts;
import common.repository.hmfs.dao.HmSysCtlMapper;
import common.repository.hmfs.model.HmSysCtl;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.service.hmb.HmbBaseService;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class CbsAbstractTxnProcessor {

    @Resource
    protected HmbBaseService hmbBaseService;
    @Resource
    protected HmSysCtlMapper hmSysCtlMapper;

    public abstract TOA process(String txnSerialNo, byte[] bytes) throws Exception;

    public TOA run(String txnCode, String serialNo, byte[] datagramBytes) throws Exception {
        HmSysCtl hmSysCtl = hmSysCtlMapper.selectByPrimaryKey("1");
        String sysSts = hmSysCtl.getSysSts();

        /*
            ������������Ľ��ף���WEB�㷢��Ľ��ף����˴������ǩ��ǩ�˽���ʱϵͳ��״̬��
            ǩ��ǩ�˽��ף������Զ�����Ľ��ף��ɷ����������ĵ�ϵͳ���ơ�
         */
        if ("5001".equals(txnCode)) { //���ʽ���
            if (!SysCtlSts.SIGNOUT.getCode().equals(sysSts) && !SysCtlSts.HMB_CHK_OVER.getCode().equals(sysSts)) {
                throw new RuntimeException(CbsErrorCode.SYS_NOT_SIGN_OUT.getCode());
            }
        }else{ //�������ף�������WEB�㷢���ǩ��ǩ�ˣ�
            if (!SysCtlSts.SIGNON.getCode().equals(sysSts)) {
                throw new RuntimeException(CbsErrorCode.SYS_NOT_SIGN_ON.getCode());
            }
        }
        return process(serialNo, datagramBytes);
    }
}
