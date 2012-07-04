package hmfs.datamigration

import groovy.sql.Sql

/**
 * Created with IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-7-2
 * Time: ����4:27
 * To change this template use File | Settings | File Templates.
 */
class MigrationManager {
    def localdbdef = [url: 'jdbc:oracle:thin:@localhost:1521:orcl', user: 'hmfs', password: 'hmfs', driver: 'oracle.jdbc.driver.OracleDriver']
    def localdb = Sql.newInstance(localdbdef.url, localdbdef.user, localdbdef.password, localdbdef.driver)

    static void main(args) {
        MigrationManager mig = new MigrationManager();
        mig.checkFundAct()
        mig.checkFundTxn()
    }

    //�˶��ʻ���������
    def checkFundAct() {
        localdb.eachRow("select * from mig_acct where parent_fund is null") {
            def fund = it.fund
            def bal = it.bal
            def sumbal = 0;
            def count = 0;
            localdb.eachRow("select count(*) as cnt,sum(bal) as bal from mig_acct where parent_fund = ${fund}") {
                sumbal = it.bal
                count = it.cnt
            }
            if (sumbal == null) {
                sumbal = 0
                count = 0
            }
            if (bal != sumbal) {
                println "======���㻧:${fund} ���:${bal}, �ֻ����ϼ�:${sumbal}, �ֻ���:${count} "
            }
            /*
            localdb.eachRow("select * from mig_acct where parent_fund = ${fund}") {
                localdb.eachRow("select count(*) cnt from mig_acct where parent_fund = ${it.fund}") {
                    if (it.cnt > 0) {
                        println "�����������㻧"
                    }
                }
            }
            */
        }
    }

    //�˶���ˮ������
    def checkFundTxn() {
        localdb.eachRow("select * from mig_acct") {
            def fund = it.fund
            def bal = it.bal
            def txnamt = 0

            if (bal != 0) {
                def sql = """
                        select sum(case
                                     when (t.is_cancel = '0' and t.tran_flag = '2') then
                                      t.tran_amt
                                     when (t.is_cancel = '0' and t.tran_flag = '1') then
                                      -t.tran_amt
                                     when (t.is_cancel = '1' and t.tran_flag = '2') then
                                      -t.tran_amt
                                     when (t.is_cancel = '1' and t.tran_flag = '1') then
                                      t.tran_amt
                                   end) as amt
                          from MIG_ACCT_WATER t
                         where t.fund = ${fund}
                    """
                localdb.eachRow(sql) {
                    txnamt = it.amt
                }
                if (bal != txnamt) {
                    println "======���㻧:${fund} ���:${bal}, ��ˮ������ϼ�:${txnamt} "
                }
            }
        }
    }

    //TODO
    //TODO ��ֲ�� ���ݺ˶�
}
