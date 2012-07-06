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
    def dbparam = [url: 'jdbc:oracle:thin:@localhost:1521:orcl', user: 'hmfs', password: 'hmfs', driver: 'oracle.jdbc.driver.OracleDriver']
    def db = Sql.newInstance(dbparam.url, dbparam.user, dbparam.password, dbparam.driver)
    def mainpath = "c:/temp/"

    static void main(args) {
        MigrationManager mig = new MigrationManager();

        //初始化本地业务表和系统控制表
        //mig.initBizDBTable()
        //mig.initSystemCtrlTable()

        //修改SQL中表名
        //mig.processSqlFile();

        //初始化数据移植表
        //mig.initMigrationDBTable()

        //导入待移植数据到DB中
        //mig.importData()

        //处理帐户表信息
        //mig.tranAcctInfo()

        //处理MSGIN表数据（在途的）
        //mig.tranMsgIn()

        //核对帐户余额表数据
        mig.checkFundAct()

        //核对流水表数据
        //mig.checkFundTxn()

        //核对移植后数据一致性
        mig.checkLocalBizData()

        mig.db.close()
    }

    def initBizDBTable(){
        db.execute("truncate table hm_act_fund")
        db.execute("truncate table hm_act_stl")
        db.execute("truncate table hm_chk_act")
        db.execute("truncate table hm_chk_txn")
        db.execute("truncate table hm_msg_in")
        db.execute("truncate table hm_msg_out")
        db.execute("truncate table hm_msg_cancel")
        db.execute("truncate table hm_txn_fund")
        db.execute("truncate table hm_txn_stl")
        db.execute("truncate table hm_txn_vch")
        db.execute("truncate table tmp_msg_in")
        db.execute("truncate table tmp_msg_out")
        println "\t数据库业务表初始化完成。\n"
    }

    def initMigrationDBTable(){
        db.execute("truncate table mig_real_acct")
        db.execute("truncate table mig_acct")
        db.execute("truncate table mig_acct_water")
        db.execute("truncate table mig_base_info")
        db.execute("truncate table mig_hou_info")
        db.execute("truncate table mig_owner_info")
        db.execute("truncate table mig_pay_detail")
        db.execute("truncate table mig_pay_detail_all")
        println "\t数据库数据移植表初始化完成。\n"
    }

    def initSystemCtrlTable(){
        db.execute("update hm_sys_ctl set sys_sts='0', txnseq=1")
        println "\t数据库系统控制表初始化完成。\n"
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
        println "\t修改SQL文件结束。\n"
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
        println "\t导入数据完成...\n"
    }

    def importData(String sqlfile){
        def process = "cmd /c sqlplus  hmfs/hmfs@orcl < ${mainpath}${sqlfile}.sql > ${mainpath}out_${sqlfile}.txt ".execute()
        println "\t\t ${process.text} ${sqlfile}..."
    }

    def checkFundAct() {
        println "\t开始核对总分余额数据..."

        def totalBal = db.firstRow("select sum(a.bal) as totalbal from mig_acct a where parent_fund  is null").totalbal
        def realAcctBal = db.firstRow("select bal from MIG_REAL_ACCT").bal
        if (totalBal != realAcctBal) {
            println "\t\t结算户余额:${realAcctBal}, 核算户余额合计:${totalBal}, 差额:${realAcctBal-totalBal} "
        }

        db.eachRow("select * from mig_acct where acct_type = '511'") {
            def fund = it.fund
            def bal = it.bal
            def sumbal = 0;
            def count = 0;
            db.eachRow("select count(*) as cnt,sum(bal) as bal from mig_acct where parent_fund = ${fund}") {
                sumbal = it.bal
                count = it.cnt
            }
            if (sumbal == null) {
                sumbal = 0
                count = 0
            }
            if (bal != sumbal) {
                println "\t\t核算户:${fund} 余额:${bal}, 分户余额合计:${sumbal}, 分户数:${count}, 差额:${bal-sumbal} "
            }
            /*
            db.eachRow("select * from mig_acct where parent_fund = ${fund}") {
                db.eachRow("select count(*) cnt from mig_acct where parent_fund = ${it.fund}") {
                    if (it.cnt > 0) {
                        println "存在三级核算户"
                    }
                }
            }
            */
        }
        println "\t核对总分余额数据完成。\n"
    }

    def checkFundTxn() {
        println "\t开始核对帐务流水数据..."
        db.eachRow("select * from mig_acct") {
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
                db.eachRow(sql) {
                    txnamt = it.amt
                }
                if (bal != txnamt) {
                    println "\t\t核算户:${fund} 余额:${bal}, 流水表发生额合计:${txnamt} "
                }
            }
        }
        println "\t核对帐务流水数据完成。\n"
    }

    //数据转换
    def tranAcctInfo(){
        println "\t开始处理帐户数据..."

        //导入结算户数据
        def sql = """
            insert into HM_ACT_STL
              (pkid,
               branch_id,
               bank_name,
               cbs_actno,
               cbs_actname,
               cbs_acttype,
               deposit_type,
               org_id,
               org_type,
               org_name,
               settle_actno1,
               settle_acttype1,
               act_sts,
               act_bal,
               intc_pdt,
               last_txn_dt,
               last_act_bal,
               open_act_date,
               close_act_date,
               frz_act_date,
               recversion)
              select sys_guid(),
                     t.bank_code,
                     t.bank_name,
                     t.real_nmbr,
                     t.account_name,
                     '01',
                     t.saving_type,
                     t.owner,
                     '10',
                     '房管局',
                     t.fund,
                     t.acct_type,
                     t.acct_status,
                     t.bal,
                     t.int_base,
                     to_char(sysdate, 'yyyyMMdd'),
                     0,
                     to_char(sysdate, 'yyyyMMdd'),
                     null,
                     null,
                     0
                from MIG_REAL_ACCT t
        """
        db.execute(sql)

        //导入项目核算户
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
        db.execute(sql)

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
        db.execute(sql)

        //更新项目核算户
        sql = """
            update HM_ACT_FUND t set t.fund_actno2 = '#'  where t.fund_acttype2 = '#'
        """
        db.execute(sql)

        //处理核算户交易明细
        sql = """
            insert into hm_txn_fund a
              (PKID,
               FUND_ACTNO,
               FUND_ACTTYPE,
               CBS_TXN_SN,
               TXN_SN,
               TXN_SUB_SN,
               TXN_AMT,
               DC_FLAG,
               REVERSE_FLAG,
               LAST_ACT_BAL,
               TXN_DATE,
               TXN_TIME,
               TXN_CODE,
               ACTION_CODE,
               TXN_STS,
               OPAC_BRID,
               TXAC_BRID,
               OPR1_NO,
               OPR2_NO,
               REMARK)
              select sys_guid(),
                     b.fund,
                     b.acct_type,
                     0000000000000000,
                     b.batch_no,
                     lpad(b.sub_water, 5, '0'),
                     b.tran_amt,
                     case b.tran_flag
                       when '1' then
                        'D'
                       when '2' then
                        'C'
                     end,
                     b.is_cancel,
                     b.day_lbal,
                     b.tran_date,
                     trim(b.tran_time),
                     b.tran_type,
                     '',
                     '',
                     '',
                     '',
                     '',
                     '',
                     '数据移植初始化'
                from mig_acct_water b
        """
        db.execute(sql)

        println "\t帐户数据处理完成。\n"
    }

    def tranMsgIn(){
        println "\t开始处理MSGIN数据..."

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
                         '20',
                         '5210'
                    from dual
        """
        db.eachRow("select batch_no,sum(tran_amt) sum_tran_amt, count(*) as cnt from mig_pay_detail_all group by batch_no"){
             db.execute(sql, [it.batch_no, it.sum_tran_amt])
        }

        //获取银行账户信息
        def row = db.firstRow("select * from mig_real_acct")

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
                     mpd.tran_amt,
                     '#',
                     '${row.account_name}',
                     to_char(sysdate, 'yyyyMMdd'),
                     to_char(sysdate, 'HH24Miss'),
                     '20',
                     1,
                     '5210'
                from mig_pay_detail_all mpd
                join mig_base_info binfo
                  on mpd.info_id = binfo.info_id
                join mig_acct act
                  on act.owner = mpd.info_id
                join mig_hou_info hinfo
                  on mpd.info_id = hinfo.info_id
        """
        db.execute(sql)

        //修改在途的记录状态
        db.execute("update hm_msg_in a set a.txn_ctl_sts = '00' where exists (select 1 from mig_pay_detail b where  b.batch_no = a.msg_sn)")
        println "\t处理MSGIN数据完成。\n"
    }

    //移植后 数据核对
    def checkLocalBizData(){
        println "\t开始核对移植后本地表中总分余额数据..."

        def totalBal = db.firstRow("select sum(t.act_bal) as totalbal from HM_ACT_FUND t  where t.fund_acttype1='511'").totalbal
        def realAcctBal = db.firstRow("select t.act_bal as bal from HM_ACT_STL t").bal
        if (totalBal != realAcctBal) {
            println "\t\t结算户余额:${realAcctBal}, 核算户余额合计:${totalBal}, 差额:${realAcctBal-totalBal} "
        }

        db.eachRow("select * from HM_ACT_FUND t  where t.fund_actno2 ='#'") {
            def fund = it.fund_actno1
            def bal = it.act_bal
            def sumbal = 0;
            def count = 0;
            db.eachRow("select count(*) as cnt,sum(act_bal) as bal from HM_ACT_FUND where fund_actno2 = ${fund}") {
                sumbal = it.bal
                count = it.cnt
            }
            if (sumbal == null) {
                sumbal = 0
                count = 0
            }
            if (bal != sumbal) {
                println "\t\t核算户:${fund} 余额:${bal}, 分户余额合计:${sumbal}, 分户数:${count}, 差额:${bal-sumbal} "
            }
        }
        println "\t核对移植后本地表中总分余额数据完成。\n"
    }
}
