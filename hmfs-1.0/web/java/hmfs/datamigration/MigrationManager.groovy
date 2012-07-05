package hmfs.datamigration

import groovy.sql.Sql

/**
 * Created with IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-7-2
 * Time: 下午4:27
 * To change this template use File | Settings | File Templates.
 */
class MigrationManager {
    def localdbdef = [url: 'jdbc:oracle:thin:@localhost:1521:orcl', user: 'hmfs', password: 'hmfs', driver: 'oracle.jdbc.driver.OracleDriver']
    def localdb = Sql.newInstance(localdbdef.url, localdbdef.user, localdbdef.password, localdbdef.driver)
    def mainpath = "c:/temp/"

    static void main(args) {
        MigrationManager mig = new MigrationManager();

        //初始化本地业务表和系统控制表
        mig.initBizDBTable()
        mig.initSystemCtrlTable()

        //修改SQL中表名
        //mig.processSqlFile();

        //初始化数据移植表
        //mig.initMigrationDBTable()

        //导入待移植数据到DB中
        //mig.importData()

        //处理帐户表信息
        mig.tranAcctInfo()

        //处理MSGIN表数据（在途的）
        mig.tranMsgIn()

        //核对帐户余额表数据
        mig.checkFundAct()

        //核对流水表数据
        mig.checkFundTxn()

        mig.localdb.close()
    }

    def initBizDBTable(){
        localdb.execute("truncate table hm_act_fund")
        localdb.execute("truncate table hm_act_stl")
        localdb.execute("truncate table hm_chk_act")
        localdb.execute("truncate table hm_chk_txn")
        localdb.execute("truncate table hm_msg_in")
        localdb.execute("truncate table hm_msg_out")
        localdb.execute("truncate table hm_msg_cancel")
        localdb.execute("truncate table hm_txn_fund")
        localdb.execute("truncate table hm_txn_stl")
        localdb.execute("truncate table hm_txn_vch")
        localdb.execute("truncate table tmp_msg_in")
        localdb.execute("truncate table tmp_msg_out")
        println "\t数据库业务表初始化完成。"
    }

    def initMigrationDBTable(){
        localdb.execute("truncate table mig_real_acct")
        localdb.execute("truncate table mig_acct")
        localdb.execute("truncate table mig_acct_water")
        localdb.execute("truncate table mig_base_info")
        localdb.execute("truncate table mig_hou_info")
        localdb.execute("truncate table mig_owner_info")
        localdb.execute("truncate table mig_pay_detail")
        localdb.execute("truncate table mig_pay_detail_all")
        println "\t数据库数据移植表初始化完成。"
    }

    def initSystemCtrlTable(){
        localdb.execute("update hm_sys_ctl set sys_sts='0', txnseq=1")
        println "\t数据库系统控制表初始化完成。"
    }

    def processSqlFile(){
        println "\t开始修改SQL文件..."
        processSqlFile("real_acct.sql", "mig_real_acct.sql", "Insert into QDHMFMS.REAL_ACCT", "Insert into MIG_REAL_ACCT");
        processSqlFile("acct.sql", "mig_acct.sql", "Insert into QDHMFMS.ACCT", "Insert into MIG_ACCT");
        processSqlFile("acct_water.sql", "mig_acct_water.sql", "Insert into QDHMFMS.ACCT_WATER", "Insert into MIG_ACCT_WATER");
        processSqlFile("base_info.sql", "mig_base_info.sql", "Insert into QDHMFMS.BASE_INFO", "Insert into MIG_BASE_INFO");
        processSqlFile("hou_info.sql", "mig_hou_info.sql", "Insert into QDHMFMS.HOU_INFO", "Insert into MIG_HOU_INFO");
        processSqlFile("owner_info.sql", "mig_owner_info.sql", "Insert into QDHMFMS.OWNER_INFO", "Insert into MIG_OWNER_INFO");
        processSqlFile("pay_detail(全部清册，包括已发银行未交款 未解锁的).sql", "mig_pay_detail_all.sql", "Insert into QDHMFMS.PAY_DETAIL", "Insert into MIG_PAY_DETAIL_ALL");
        processSqlFile("pay_detail(在途的).sql", "mig_pay_detail.sql", "Insert into PAY_DETAIL.TRADE", "Insert into MIG_PAY_DETAIL");
        println "\t修改SQL文件结束。"
    }

    def processSqlFile(String infile, String outfile, String srcStr, String destStr){
        BufferedWriter writer = new File(mainpath + outfile).newWriter()
        new File(mainpath + infile).eachLine {line->
            writer.write(line.replace(srcStr, destStr) + '\n')
        }
        writer.close()
    }


    def importData(){
        println "\t开始导入数据..."
        importData("mig_real_acct")
        importData("mig_acct")
        importData("mig_acct_water")
        importData("mig_base_info")
        importData("mig_hou_info")
        importData("mig_owner_info")
        importData("mig_pay_detail_all")
        importData("mig_pay_detail")
        println "\t导入数据完成..."
    }

    def importData(String sqlfile){
        def process = "cmd /c sqlplus  hmfs/hmfs@orcl < ${mainpath}${sqlfile}.sql > ${mainpath}out_${sqlfile}.txt ".execute()
        println "\t\t ${process.text} ${sqlfile}..."
    }

    def checkFundAct() {
        println "\t开始核对总分余额数据..."
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
                println "\t\t核算户:${fund} 余额:${bal}, 分户余额合计:${sumbal}, 分户数:${count} "
            }
            /*
            localdb.eachRow("select * from mig_acct where parent_fund = ${fund}") {
                localdb.eachRow("select count(*) cnt from mig_acct where parent_fund = ${it.fund}") {
                    if (it.cnt > 0) {
                        println "存在三级核算户"
                    }
                }
            }
            */
        }
        println "\t核对总分余额数据完成。"
    }

    def checkFundTxn() {
        println "\t开始核对帐务流水数据..."
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
                    println "\t\t核算户:${fund} 余额:${bal}, 流水表发生额合计:${txnamt} "
                }
            }
        }
        println "\t核对帐务流水数据完成。"
    }

    //数据转换
    def tranAcctInfo(){
        //导入项目核算户
        def sql = """
            insert into HM_ACT_FUND
              (pkid,
               fund_actno1,
               fund_acttype1,
               fund_actno2,
               fund_acttype2,
               cbs_actno,
               info_id1,
               info_id_type1,
               info_code,
               info_name,
               info_addr,
               cell_num,
               builder_area,
               act_sts,
               act_bal,
               intc_pdt,
               last_txn_dt,
               last_act_bal,
               open_act_date)
              select sys_guid(),
                     act.fund as actfund,
                     act.acct_type as actfundtype,
                     0,
                     '#',
                     act.real_nmbr as cbs_actno,
                     act.owner,
                     binfo.INFO_TYPE,
                     binfo.info_code,
                     binfo.info_name,
                     binfo.info_addr,
                     binfo.own_count,
                     binfo.info_area,
                     act.acct_status,
                     act.bal,
                     act.int_base,
                     to_char(sysdate, 'yyyyMMdd'),
                     act.day_lbal,
                     to_char(sysdate, 'yyyyMMdd')
                from mig_acct act
                join mig_base_info binfo
                  on act.owner = binfo.info_id
               where act.parent_fund is null
        """
        localdb.execute(sql)

        //导入分户核算户
        sql = """
            insert into HM_ACT_FUND
              (pkid,
               fund_actno1,
               fund_acttype1,
               fund_actno2,
               fund_acttype2,
               cbs_actno,
               info_id1,
               info_id_type1,
               info_code,
               info_name,
               info_addr,
               cell_num,
               builder_area,
               dev_org_name,
               house_dep_type,
               sell_flag,
               cert_type,
               cert_id,
               org_phone,
               house_cont_no,
               house_cust_phone,
               elevator_type,
               house_total_amt,
               act_sts,
               act_bal,
               intc_pdt,
               last_txn_dt,
               last_act_bal,
               open_act_date)
              select sys_guid(),
                     act1.fund as actfund,
                     act1.acct_type as actfundtype,
                     act1.parent_fund as parent_actfund,
                     act2.acct_type parent_actfundtype,
                     act1.real_nmbr as cbs_actno,
                     act1.owner,
                     binfo.INFO_TYPE,
                     binfo.info_code,
                     binfo.info_name,
                     binfo.info_addr,
                     binfo.own_count,
                     binfo.info_area,
                     hinfo.dev_name,
                     hinfo.hou_pay_type,
                     hinfo.sale_flag,
                     hinfo.cert_type,
                     hinfo.cert_code,
                     hinfo.own_tel,
                     hinfo.buy_pact_no,
                     hinfo.own_tel,
                     hinfo.exist_flag,
                     hinfo.buy_money,
                     act1.acct_status,
                     act1.bal,
                     act1.int_base,
                     to_char(sysdate, 'yyyyMMdd'),
                     act1.day_lbal,
                     to_char(sysdate, 'yyyyMMdd')
                from mig_acct act1
                join mig_acct act2
                  on act1.parent_fund = act2.fund
                join mig_base_info binfo
                  on act1.owner = binfo.info_id
                join mig_hou_info hinfo
                  on binfo.info_id = hinfo.info_id
        """
        localdb.execute(sql)

        //更新项目核算户
        sql = """
            update HM_ACT_FUND t set t.fund_actno2 = '#'  where t.fund_acttype2 = '#'
        """
        localdb.execute(sql)
    }

    def tranMsgIn(){
        //生成汇总报文记录 MSG_005
        def sql = """
                insert into hm_msg_in
                  (PKID,
                   MSG_TYPE,
                   MSG_SN,
                   SUBMSG_NUM,
                   SEND_SYS_ID,
                   ORIG_SYS_ID,
                   MSG_DT,
                   MSG_END_DATE,
                   TXN_TYPE,
                   BIZ_TYPE,
                   ORIG_TXN_CODE,
                   INFO_ID1,
                   INFO_ID_TYPE1,
                   DISTRICT_ID,
                   FUND_ACTNO1,
                   FUND_ACTTYPE1,
                   SETTLE_ACTNO1,
                   SETTLE_ACTTYPE1,
                   TXN_AMT1,
                   DEP_TYPE,
                   MSG_PROC_DATE,
                   MSG_PROC_TIME,
                   TXN_CTL_STS,
                   TXN_CODE)
                  select sys_guid(),
                         '00005',
                         ?,
                         1,
                         '00',
                         '00',
                         to_char(sysdate, 'yyyyMMddHH24Miss'),
                         '#',
                         '1',
                         '2',
                         '3330',
                         '#',
                         '#',
                         '#',
                         '#',
                         '#',
                         '#',
                         '#',
                         ?,
                         '05',
                         to_char(sysdate, 'yyyyMMdd'),
                         to_char(sysdate, 'HH24Miss'),
                         '00',
                         '5210'
                    from dual
        """
        localdb.eachRow("select batch_no,sum(own_amt) sumownamt, count(*) as cnt from mig_pay_detail group by batch_no"){
             localdb.execute(sql, [it.batch_no, it.sumownamt])
        }

        //获取银行账户信息
        def row = localdb.firstRow("select * from mig_real_acct")

        //生成明细报文记录 MSG_035
        sql = """
            insert into hm_msg_in
              (PKID,
               MSG_TYPE,
               MSG_SN,
               ACTION_CODE,
               INFO_ID1,
               INFO_ID_TYPE1,
               INFO_CODE,
               INFO_NAME,
               INFO_ADDR,
               CELL_NUM,
               BUILDER_AREA,
               DISTRICT_ID,
               FUND_ACTNO1,
               FUND_ACTTYPE1,
               FUND_ACTNO2,
               FUND_ACTTYPE2,
               SETTLE_ACTNO1,
               SETTLE_ACTTYPE1,
               TXN_AMT1,
               DEV_ORG_NAME,
               PAYIN_ACT_NAME,
               MSG_PROC_DATE,
               MSG_PROC_TIME,
               TXN_CTL_STS,
               MSG_SUB_SN,
               TXN_CODE)
              select sys_guid(),
                     '01035',
                     mpd.batch_no,
                     '115',
                     binfo.info_id,
                     binfo.info_type,
                     binfo.info_code,
                     binfo.info_name,
                     binfo.info_addr,
                     binfo.own_count,
                     binfo.info_area,
                     '2',
                     act.fund,
                     act.acct_type,
                     act.parent_fund,
                     '511',
                     '${row.fund}',
                     '210',
                     mpd.own_amt,
                     '#',
                     '${row.account_name}',
                     to_char(sysdate, 'yyyyMMdd'),
                     to_char(sysdate, 'HH24Miss'),
                     '00',
                     1,
                     '5210'
                from mig_pay_detail mpd
                join mig_base_info binfo
                  on mpd.info_id = binfo.info_id
                join mig_acct act
                  on act.owner = mpd.info_id
                join mig_hou_info hinfo
                  on mpd.info_id = hinfo.info_id
        """
        localdb.execute(sql)
    }

    //TODO 移植后 数据核对
}
