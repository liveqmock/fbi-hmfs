package dep.gateway.service;

import dep.hmfs.online.cmb.domain.DataHead;
import dep.hmfs.online.cmb.domain.PayDetail;
import dep.hmfs.online.cmb.domain.base.TIAHeader;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: ����2:27
 * To change this template use File | Settings | File Templates.
 */
/*
A2��ˮ��	��16λ��
A3������	��4λ��
A4��������	��4λ��
A5�����	��4λ��
A6���һ����־	��1λ��
 */

@Service
public class CmbMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CmbMsgHandleService.class);

    public CmbMsgHandleService() {

    }

    @Override
    public byte[] handleMessage(byte[] bytes) {
        // TODO  ���Խɿ�֪ͨ��ɷ�

        TIAHeader tiaHeader = new TIAHeader();
        tiaHeader.initFields(bytes);

        byte[] datagramBytes = new byte[bytes.length - 24];
        System.arraycopy(bytes, 24, datagramBytes, 0, datagramBytes.length);

        String datagram = new String(datagramBytes);
        logger.info(" ==== �������ģ�" + datagram);

        // �ɿ�֪ͨ����	18	ά���ʽ�ɿ�֪ͨ����
        // �ɷѽ��	16
        logger.info("�ɿ�֪ͨ���ţ�" + datagram.substring(0, 18) + "    �ɷѽ�" + datagram.substring(18, 34));

        PayDetail payDetail = new PayDetail();
        payDetail.setAccountName("����");
        payDetail.setTxAmt("123456.78");
        payDetail.setAddress("�ൺ�б���");
        payDetail.setHouseArea("98.5");
        payDetail.setHouseType("0");
        payDetail.setPhoneNo("89901100");
        payDetail.setProjAmt("1001234567.89");
        payDetail.setPayPart("30%");
        payDetail.setAccountNo("32001001");

        PayDetail detail = new PayDetail();
        detail.setAccountName("����");
        detail.setTxAmt("1234.56");
        detail.setAddress("�ൺ��ɽ��ɽ��ɽ");
        detail.setHouseArea("89.6");
        detail.setHouseType("1");
        detail.setPhoneNo("89931100");
        detail.setProjAmt("1234588.88");
        detail.setPayPart("99%");
        detail.setAccountNo("32001002");


        // ���ɷ��ر���ͷ
        DataHead rtnDataHead = new DataHead();
        rtnDataHead.setSerialNo(tiaHeader.serialNo);
        rtnDataHead.setErrorCode("0000");
        rtnDataHead.setTxnCode(tiaHeader.txnCode);

        // ��������
        List<PayDetail> payDetailList = new ArrayList<PayDetail>();
        payDetailList.add(payDetail);
        payDetailList.add(detail);

        StringBuilder rtnStrBuilder = new StringBuilder();
        rtnStrBuilder.append(rtnDataHead.toStringByFieldLength());
        rtnStrBuilder.append(datagram.substring(0, 18) + "2   ");
        rtnStrBuilder.append(toDatagramString(payDetailList));

        String rtnDatagram = rtnStrBuilder.toString();
        int totalLength = rtnDatagram.getBytes().length + 6;
        return (StringUtils.rightPad(String.valueOf(totalLength), 6, " ") + rtnDatagram).getBytes();
    }

    private String toDatagramString(List<PayDetail> payDetailList) {
        StringBuilder strDatagramBuilder = new StringBuilder();
        for (PayDetail payDetail : payDetailList) {
            strDatagramBuilder.append(payDetail.toStringByDelimiter("|")).append("\n");
        }
        strDatagramBuilder.deleteCharAt(strDatagramBuilder.length() - 1);
        return strDatagramBuilder.toString();
    }
}
