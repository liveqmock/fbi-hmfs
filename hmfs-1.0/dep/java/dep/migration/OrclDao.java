package dep.migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-7-4
 * Time: ÉÏÎç10:49
 * To change this template use File | Settings | File Templates.
 */
public class OrclDao {

    public static void executeSql(String sql) {

        Connection con = null;
        Statement statement = null;
        try {
            con = ConnectionManager.getConnection();
            statement = con.createStatement();
            statement.executeUpdate(sql);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionManager.closeStatement(statement);
        }
    }

    public static Map<String, String> qrySql(String sql) {
        Connection con = null;
        Statement statement = null;
        ResultSet rs = null;
        Map<String, String> actamtMap = null;
        try {
            con = ConnectionManager.getConnection();
            statement = con.createStatement();
            rs = statement.executeQuery(sql);
            if(rs != null) {
                actamtMap = new HashMap<String, String>();
                while (rs.next()) {
                    actamtMap.put(rs.getString(1), rs.getString(2));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionManager.closeResultSet(rs);
            ConnectionManager.closeStatement(statement);
        }
        return actamtMap;
    }
}
