package dep.hmfs.online.hmb.domain;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����6:50
 * To change this template use File | Settings | File Templates.
 */
public class Txn037 extends SubMsg{
    //F8����������
    public String actionCode;

    //F16����ϢID1
    public String infoId1;

    //F17����ϢID1����
    public String infoIdType1;

    //F21����Ϣ����
    public String infoName;

    //F22����Ϣ��ַ
    public String infoAddr;

    //F24���������
    public String builderArea;

    //F28�����㻧�˺�1��
    public String fundActno1;

    //F29�����㻧�˺�1����
    public String fundActtype1;

    //F45�����׽��1
    public BigDecimal txnAmt1;

    private String orgId;
    private String orgType;

    private String orgName;

}

