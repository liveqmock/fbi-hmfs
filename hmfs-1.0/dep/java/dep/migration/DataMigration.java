package dep.migration;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-7-4
 * Time: 上午10:32
 * To change this template use File | Settings | File Templates.
 */
public class DataMigration {
    public static void main(String[] args) throws Exception {
        // 结算户账号、户名

        // OrclDao.executeSql(MigSql.INSERT_ACT_FUND1);    // 项目核算户
        // OrclDao.executeSql(MigSql.INSERT_ACT_FUND2);    // 分户核算户
        // OrclDao.executeSql(MigSql.UPDATE_ACT_FUND1);    // 更新项目核算户

        insertMultiMsgin(MigSql.QRY_5210_MSG_NO);

    }

    public static void insertMultiMsgin(String qrySql) throws Exception {
        Map<String, String> actamtMap = OrclDao.qrySql(MigSql.QRY_5210_MSG_NO);
        if (actamtMap != null) {
            int i = 1;

            for (Map.Entry<String, String> entry : actamtMap.entrySet()) {
                String batchNo = entry.getKey();
                String sumamt = entry.getValue();

                System.out.println("NO:" + (i++) + " -[" + batchNo + "] " + sumamt);

                OrclDao.executeSql(MigSql.assem5210Msg005(batchNo, sumamt));
            }
            OrclDao.executeSql(MigSql.INSERT_5210_MSG_035);
        } else {
            // TODO
        }
    }
}
