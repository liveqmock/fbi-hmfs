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
        // 数据库表清理 hm_act_fund, hm_msg_in
        // 修改监管中心sql脚本中表名为hmfs.mig_***
        // 导入5个sql中的数据

        // 注意 结算户账号、户名 [监管中心提供]
        // 总分账户核对
        OrclDao.executeSql(MigSql.INSERT_ACT_FUND1);    // 导入 项目核算户
        OrclDao.executeSql(MigSql.INSERT_ACT_FUND2);    // 导入 分户核算户
        OrclDao.executeSql(MigSql.UPDATE_ACT_FUND1);    // 更新 项目核算户

        insert5210Msgin(MigSql.QRY_5210_MSG_NO);

    }

    public static void insert5210Msgin(String qrySql) throws Exception {
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
