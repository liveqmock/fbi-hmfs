package dep.mocktool.hmb.hmbserver;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: обнГ1:22
 * To change this template use File | Settings | File Templates.
 */
public interface TxnProcessor {
    byte[] process(byte[] msgin);
}
