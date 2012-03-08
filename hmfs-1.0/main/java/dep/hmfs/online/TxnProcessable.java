package dep.hmfs.online;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-12-20
 * Time: обнГ2:48
 * To change this template use File | Settings | File Templates.
 */
public interface TxnProcessable {
    List<Map<String, String>> process(String bizCode, String postCode, List<String> paramList) throws Exception;
}
