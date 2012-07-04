package dep.migration;

public class MigSql {
    // 结算户账号、户名
    static String HMFS_SETTLE_ACTNO = "120000000003";
    static String HMFS_SETTLE_ACTNAM = "管理中心建设银行";

    static final String INSERT_ACT_FUND1 = " insert into HM_ACT_FUND (pkid, " +
            " fund_actno1, fund_acttype1, fund_actno2, fund_acttype2, " +
            " cbs_actno, info_id1, info_id_type1, info_code, info_name, info_addr," +
            " cell_num, builder_area, " +
            " act_sts, act_bal, intc_pdt, last_txn_dt, last_act_bal, open_act_date)" +
            " select sys_guid(),act.fund as actfund, act.acct_type as actfundtype,0,'#'," +
            " act.real_nmbr as cbs_actno,act.owner,binfo.INFO_TYPE,binfo.info_code," +
            " binfo.info_name,binfo.info_addr,binfo.own_count,binfo.info_area," +
            " act.acct_status,act.bal,act.int_base,to_char(sysdate, 'yyyyMMdd')," +
            " act.day_lbal, to_char(sysdate, 'yyyyMMdd')" +
            " from  mig_acct act " +
            " join mig_base_info binfo" +
            " on act.owner = binfo.info_id" +
            " where act.parent_fund is null";

    static final String INSERT_ACT_FUND2 = " insert into HM_ACT_FUND (pkid, " +
            " fund_actno1, fund_acttype1, fund_actno2, fund_acttype2, " +
            " cbs_actno, info_id1, info_id_type1, info_code, info_name, info_addr," +
            " cell_num, builder_area, dev_org_name, " +
            " house_dep_type, sell_flag, cert_type, cert_id, " +
            " org_phone, house_cont_no, house_cust_phone, elevator_type, house_total_amt, " +
            " act_sts, act_bal, intc_pdt, last_txn_dt, last_act_bal, open_act_date)" +
            " select sys_guid(),act1.fund as actfund, act1.acct_type as actfundtype," +
            " act1.parent_fund as parent_actfund, act2.acct_type parent_actfundtype," +
            " act1.real_nmbr as cbs_actno,act1.owner,binfo.INFO_TYPE,binfo.info_code," +
            " binfo.info_name,binfo.info_addr,binfo.own_count,binfo.info_area,hinfo.dev_name," +
            " hinfo.hou_pay_type,hinfo.sale_flag,hinfo.cert_type,hinfo.cert_code,hinfo.own_tel," +
            " hinfo.buy_pact_no,hinfo.own_tel,hinfo.exist_flag,hinfo.buy_money, " +
            " act1.acct_status,act1.bal,act1.int_base,to_char(sysdate, 'yyyyMMdd')," +
            " act1.day_lbal, to_char(sysdate, 'yyyyMMdd')" +
            " from mig_acct act1 " +
            " join mig_acct act2 " +
            " on act1.parent_fund = act2.fund" +
            " join mig_base_info binfo" +
            " on act1.owner = binfo.info_id" +
            " join mig_hou_info hinfo" +
            " on binfo.info_id = hinfo.info_id";

    static final String UPDATE_ACT_FUND1 = "update HM_ACT_FUND t set t.fund_actno2 = '#' " +
            " where t.fund_acttype2 = '#'";

    static final String INSERT_5210_MSG_035 = "insert into hm_msg_in(" +
                    "PKID,MSG_TYPE,MSG_SN,ACTION_CODE,INFO_ID1,INFO_ID_TYPE1," +
                    "INFO_CODE,INFO_NAME,INFO_ADDR,CELL_NUM,BUILDER_AREA," +
                    "DISTRICT_ID,FUND_ACTNO1,FUND_ACTTYPE1,FUND_ACTNO2," +
                    "FUND_ACTTYPE2,SETTLE_ACTNO1,SETTLE_ACTTYPE1,TXN_AMT1," +
                    "DEV_ORG_NAME,PAYIN_ACT_NAME, MSG_PROC_DATE,MSG_PROC_TIME," +
                    "TXN_CTL_STS,MSG_SUB_SN,TXN_CODE)" +
                    " select sys_guid(),'01035',mpd.batch_no,'115',binfo.info_id,binfo.info_type," +
                    " binfo.info_code, binfo.info_name,binfo.info_addr,binfo.own_count,binfo.info_area," +
                    " '2',act.fund, act.acct_type, act.parent_fund, '511', '" + HMFS_SETTLE_ACTNO + "', '210'," +
                    " mpd.own_amt, '#', '" + HMFS_SETTLE_ACTNAM + "',to_char(sysdate, 'yyyyMMdd')," +
                    " to_char(sysdate, 'HH24Miss'),'00',1,'5210'" +
                    " from mig_pay_detail mpd" +
                    " join mig_base_info binfo" +
                    " on mpd.info_id = binfo.info_id" +
                    " join mig_acct act" +
                    " on act.owner = mpd.info_id" +
                    " join mig_hou_info hinfo" +
                    " on mpd.info_id = hinfo.info_id";

    static final String QRY_5210_MSG_NO = " select batch_no,sum(own_amt) sumownamt from mig_pay_detail " +
                                                " group by batch_no ";
                                              // having count(*) > 1";

    public static String assem5210Msg005(String batchno, String ownamt) {
       return  "insert into hm_msg_in(" +
                       "PKID,MSG_TYPE,MSG_SN,SUBMSG_NUM,SEND_SYS_ID," +
                       "ORIG_SYS_ID,MSG_DT,MSG_END_DATE,TXN_TYPE,BIZ_TYPE," +
                       "ORIG_TXN_CODE,INFO_ID1,INFO_ID_TYPE1,DISTRICT_ID," +
                       "FUND_ACTNO1,FUND_ACTTYPE1,SETTLE_ACTNO1," +
                       "SETTLE_ACTTYPE1,TXN_AMT1,DEP_TYPE,MSG_PROC_DATE,MSG_PROC_TIME," +
                       "TXN_CTL_STS,TXN_CODE)" +
                       " select sys_guid(),'00005','" + batchno + "',1,'00'," +
                       "'00',to_char(sysdate, 'yyyyMMddHH24Miss'),'#','1','2'," +
                       "'3330','#','#','#','#','#','#','#','" + ownamt + "','05',to_char(sysdate, 'yyyyMMdd')," +
                       " to_char(sysdate, 'HH24Miss'),'00','5210'" +
                       " from dual ";
    }

}
