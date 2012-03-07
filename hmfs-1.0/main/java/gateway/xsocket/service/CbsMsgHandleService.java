package gateway.xsocket.service;

import gateway.txn.cbs.DataHead;
import gateway.txn.cbs.PayDetail;
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
public class CbsMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CbsMsgHandleService.class);

    public CbsMsgHandleService() {

    }

    @Override
    public byte[] handleMessage(byte[] bytes) {
        // TODO  ���Խɿ�֪ͨ��ɷ�
        DataHead dataHead = new DataHead();
        dataHead.transBytesToFields(bytes);

        // TODO start from 29
        String datagram = new String(bytes, 29, bytes.length - 29);
        logger.info(" ==== �������ģ�" + datagram);

        // TODO
        // �ɿ�֪ͨ����	18	ά���ʽ�ɿ�֪ͨ����
        // �ɷѽ��	16
        logger.info("�ɿ�֪ͨ���ţ�" + datagram.substring(0, 18) + "    �ɷѽ�" + datagram.substring(18, 34));
        /*for(String detail : datagram.split("\n")) {
            logger.info("=== ������Ŀ���ݣ� " + detail);
            for(String field : detail.split("|")) {
                logger.info("=== �����ֶ����ݣ� " + field);
            }
        }
*/
        PayDetail payDetail = new PayDetail();
        payDetail.setAccountName("����");
        payDetail.setTxAmt("123456.78");
        payDetail.setAddress("�ൺ�б���");
        payDetail.setHouseArea("98.5");
        payDetail.setHouseType("0");
        payDetail.setPhoneNo("89901100");
        payDetail.setProjAmt("1001234567.89");
        payDetail.setPayPart("80%");

        PayDetail detail = new PayDetail();
        detail.setAccountName("����");
        detail.setTxAmt("1234.56");
        detail.setAddress("�ൺ��ɽ��");
        detail.setHouseArea("89.6");
        detail.setHouseType("1");
        detail.setPhoneNo("89931100");
        detail.setProjAmt("1234567.89");
        detail.setPayPart("70%");

        // ���ɷ��ر���ͷ
        DataHead rtnDataHead = new DataHead();
        rtnDataHead.setSerialNo(dataHead.getSerialNo());
        rtnDataHead.setErrorCode("0000");
        rtnDataHead.setLast(true);
        rtnDataHead.setPkgNo("1");
        rtnDataHead.setTxnCode(dataHead.getTxnCode());

        // ��������
        List<PayDetail> payDetailList = new ArrayList<PayDetail>();
        payDetailList.add(payDetail);
        payDetailList.add(detail);

        StringBuilder rtnStrBuilder = new StringBuilder();
        rtnStrBuilder.append(rtnDataHead.toStringByFieldLength());
        rtnStrBuilder.append(datagram.substring(0, 18) + "2   ");
        rtnStrBuilder.append(toDatagramString(payDetailList));
        return (StringUtils.rightPad(String.valueOf(rtnStrBuilder.length() + 6), 6, " ") + rtnStrBuilder.toString()).getBytes();
    }
    
    private String toDatagramString(List<PayDetail> payDetailList) {
        StringBuilder strDatagramBuilder = new StringBuilder();
        for(PayDetail payDetail : payDetailList) {
            strDatagramBuilder.append(payDetail.toStringByDelimiter("|")).append("\n");
        }
        strDatagramBuilder.deleteCharAt(strDatagramBuilder.length() - 1);
        return strDatagramBuilder.toString();
    }
}
