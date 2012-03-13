package dep.hmfs.online.cmb;

import dep.gateway.hmb8583.HmbMessageFactory;
import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.hmb.domain.HmbMsg;
import dep.util.PropertyManager;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: обнГ7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTxnProcessor {



    public abstract TOA process(String txnSerialNo, byte[] bytes) throws Exception;


}
