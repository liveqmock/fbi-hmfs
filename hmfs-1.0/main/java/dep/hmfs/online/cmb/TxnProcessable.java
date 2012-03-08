package dep.hmfs.online.cmb;

import dep.hmfs.online.cmb.domain.base.TOA;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-12-20
 * Time: обнГ2:48
 * To change this template use File | Settings | File Templates.
 */
public interface TxnProcessable {
    TOA process(byte[] bytes) throws Exception;
}
