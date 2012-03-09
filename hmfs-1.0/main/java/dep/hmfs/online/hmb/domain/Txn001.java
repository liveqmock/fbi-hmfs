package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����7:05
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("001")
public class Txn001 extends SummaryMsg{
    //F12:���׷�ʽ  1��������2������
    @Hmb8583Field(12)
    public String txnType;

    //F13:ҵ������  1����λ��2��ҵ����3��������4��Ĩ��
    @Hmb8583Field(13)
    public String bizType;

    //F14:�������� ���𷽵Ľ���������
    @Hmb8583Field(14)
    public String origTxnCode;
}
